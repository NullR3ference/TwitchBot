package org.aytsan_lex.twitchbot;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

public class WsUiServer extends WebSocketServer
{
    public static class Builder
    {
        private String host;
        private int port;

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

        public WsUiServer build()
        {
            return new WsUiServer(this.host, this.port);
        }
    }

    private enum Commands
    {
        // Block of client to server commands to request data
        // syntax: #<command> or #<command_1>:#<command_2>... (as batch)
        requestconfig,
        requestfilters,
        requestmodelmessages,
        requestmodelmessageshistory,

        // Block of client to server commands to accept data
        // syntax: ///<command>###<data>
        updatefilters,
        updateconfig
    }

    private static final Logger LOG = LoggerFactory.getLogger(WsUiServer.class);
    private WebSocket currentClient = null;

    private WsUiServer(String host, int port)
    {
        super(new InetSocketAddress(host, port));
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public void sendMessage(final String message)
    {
        if (this.currentClient != null)
        {
            LOG.info("Sending message to client: {}", message);
            this.currentClient.send(message);
        }
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake)
    {
        final InetSocketAddress addrInfo = webSocket.getRemoteSocketAddress();
        LOG.info("Client connected: {}:{}", addrInfo.getHostString(), addrInfo.getPort());

        this.currentClient = webSocket;
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b)
    {
        final InetSocketAddress addrInfo = webSocket.getRemoteSocketAddress();
        LOG.info("Client disconnected: {}", addrInfo.getHostString());

        if (this.currentClient != null)
        {
            if (this.currentClient.getRemoteSocketAddress().getHostString().equals(addrInfo.getHostString()))
            {
                this.currentClient = null;
            }
        }
    }

    @Override
    public void onMessage(WebSocket webSocket, String message)
    {
        final InetSocketAddress addrInfo = webSocket.getRemoteSocketAddress();
        LOG.info("[{}] Message received: {}", addrInfo.getHostString(), message);

        if (message.startsWith("#"))
        {
            this.handleRequestCommand(webSocket, message);
        }
        else if (message.startsWith("///"))
        {
            this.handleDataCommand(message);
        }
    }

    @Override
    public void onError(WebSocket webSocket, Exception e)
    {
        final InetSocketAddress addrInfo = webSocket.getRemoteSocketAddress();
        LOG.error("[{}] Error: {}", addrInfo.getHostString(), e.getMessage());
    }

    @Override
    public void onStart()
    {
        LOG.info("Started on: {}:{}", super.getAddress().getHostString(), super.getPort());
    }

    private void handleRequestCommand(WebSocket webSocket, final String message)
    {
        final ArrayList<String> response = new ArrayList<>();
        final ArrayList<String> commandBatch = new ArrayList<>(Arrays.asList(message.split(":")));

        for (String command : commandBatch)
        {
            command = command.substring(1);

            try
            {
                switch (Commands.valueOf(command))
                {
                    case requestconfig ->
                    {
                        try { response.add(BotConfigManager.readConfigAdString()); }
                        catch (IOException e) { LOG.error("Failed to read config: {}", e.getMessage()); }
                    }

                    case requestfilters ->
                    {
                        try { response.add(FiltersManager.readFiltersAsString()); }
                        catch (IOException e) { LOG.error("Failed to read filters: {}", e.getMessage()); }
                    }

                    case requestmodelmessages -> { }

                    case requestmodelmessageshistory ->
                    {
                        final ArrayList<String> history = OllamaModelsManager.getMiraModel().getQuestionsHistory();
                        commandBatch.add(String.join("\n", history));
                    }

                    default -> {  }
                }
            }
            catch (IllegalArgumentException e)
            {
                LOG.warn("Invalid request command: '{}', ignored", command);
            }

            response.add("///");
        }

        webSocket.send(String.join("", response));
    }

    private void handleDataCommand(final String message)
    {
        final String[] data = message.split("###");
        final String command = data[0].substring(3);

        if (data.length < 2)
        {
            LOG.error("Invalid data command, received data is empty!");
            return;
        }

        try
        {
            switch (Commands.valueOf(command))
            {
                case updateconfig ->
                {
                    try
                    {
                        BotConfigManager.writeConfig(data[1]);
                    }
                    catch (IOException e)
                    {
                        LOG.error("Failed to save config: {}", e.getMessage());
                    }
                }

                case updatefilters ->
                {
                    try
                    {
                        FiltersManager.saveFilters(data[1]);
                    }
                    catch (IOException e)
                    {
                        LOG.error("Failed to save filters: {}", e.getMessage());
                    }
                }

                default -> { }
            }
        }
        catch (IllegalArgumentException e)
        {
            LOG.warn("Invalid data command: '{}', ignored", command);
        }
    }
}
