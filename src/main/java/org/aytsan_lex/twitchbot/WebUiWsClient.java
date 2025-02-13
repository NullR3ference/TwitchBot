package org.aytsan_lex.twitchbot;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

public class WebUiWsClient extends WebSocketClient
{
    public enum ClientCommands
    {
        requestconfig,
        requestfilters
    }

    public enum ServerCommands
    {
        currentquestion,
        nextquestion,
        returnedresponse
    }

    private static final Logger LOG = LoggerFactory.getLogger(WebUiWsClient.class);
    private static ArrayList<String> currentCommandBatch = null;

    public static class Builder
    {
        private String host = "127.0.0.1";
        private int port = 8812;

        public Builder withHost(String host)
        {
            this.host = host;
            return this;
        }

        public Builder withPort(int port)
        {
            this.port = port;
            return this;
        }

        public WebUiWsClient build()
        {
            try
            {
                return new WebUiWsClient(new URI("ws://%s:%d".formatted(this.host, this.port)));
            }
            catch (URISyntaxException e)
            {
                LOG.error("Invalid URI: {}", e.getMessage());
            }

            return null;
        }
    }

    private WebUiWsClient(final URI serverUri)
    {
        super(serverUri);
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public void sendCommand(String command, String payload)
    {
        if (!this.isOpen())
        {
            LOG.warn("Cannot send server command: socket is closed");
            return;
        }

        try
        {
            final CommandHandler.CommandContext currentContext = CommandHandler.getMiraCommandQueue().peek();

            switch (ServerCommands.valueOf(command))
            {
                case currentquestion, returnedresponse -> { /* just send the message with payload */ }

                case nextquestion ->
                {
                    if (currentContext != null)
                    {
                        payload = String.join(" ", currentContext.getArgs());
                    }
                    else
                    {
                        LOG.warn("Command will not send: queue is empty");
                    }
                }
            }

            if (!payload.isEmpty())
            {
                final String message = "{'%s':'%s'}".formatted(command, payload);
                LOG.info("Sending server command: {}", message);
                super.send(message);
            }
        }
        catch (IllegalArgumentException ignored)
        {
            LOG.error("Invalid server command: {}", command);
        }
    }

    public void beginCommandBatch()
    {
        currentCommandBatch = new ArrayList<>();
        currentCommandBatch.add("{");
    }

    public void executeAndPushCommandToBatch(String command, String payload)
    {
        if (currentCommandBatch == null)
        {
            LOG.error("Cannot add command to batch: call beginCommandBatch() first!");
            return;
        }

        try
        {
            switch (ServerCommands.valueOf(command))
            {
                case currentquestion, returnedresponse -> { /* just send the message with payload */ }

                case nextquestion ->
                {
                    final CommandHandler.CommandContext currentContext = CommandHandler.getMiraCommandQueue().peek();
                    if (currentContext != null)
                    {
                        payload = String.join(" ", currentContext.getArgs());
                    }
                }
            }

            if (!payload.isEmpty())
            {
                final String keyValuePair = "'%s':'%s',".formatted(command, payload);
                currentCommandBatch.add(keyValuePair);
            }
        }
        catch (IllegalArgumentException ignored)
        {
            LOG.warn("Ignoring invalid server command '{}', not added to batch", command);
        }
    }

    public void endCommandBatch()
    {
        if (currentCommandBatch == null)
        {
            LOG.error("Cannot close batch: call beginCommandBatch() first!");
            return;
        }

        // remove , from end to avoid Json invalid syntax
        if (currentCommandBatch.get(currentCommandBatch.size() - 1).equals(","))
        {
            currentCommandBatch.remove(currentCommandBatch.size() - 1);
        }
        currentCommandBatch.add("}");
    }

    public void sendCommandBatch()
    {
        if (!this.isOpen())
        {
            LOG.warn("Cannot send server command batch: socket is closed");
            return;
        }

        if ((currentCommandBatch != null) && !currentCommandBatch.isEmpty())
        {
            final String message = String.join("", currentCommandBatch);
            LOG.info("Sending batch of server commands: {}", message);

            super.send(message);
            currentCommandBatch = null;
        }
        else
        {
            LOG.warn("Cannot send empty batch");
        }
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake)
    {
        LOG.info("WebSocket connected: {}", serverHandshake.getHttpStatusMessage());
    }

    @Override
    public void onMessage(String message)
    {
        if (message.startsWith("#"))
        {
            final String command = message.substring(1);
            try
            {
                switch (ClientCommands.valueOf(command))
                {
                    case requestconfig -> super.send(BotConfigManager.getConfig().asJson());
                    case requestfilters -> super.send(FiltersManager.getMiraFilters().asJson());
                }
                LOG.info("Handling client command: {}", command);
            }
            catch (IllegalArgumentException e)
            {
                LOG.error("Invalid client command: {}", command);
            }
        }
    }

    @Override
    public void onClose(int i, String s, boolean b)
    {
        LOG.info("WebSocket connection closed");
    }

    @Override
    public void onError(Exception e)
    {
        LOG.error("WebSocket error: {}", e.getMessage());
    }
}
