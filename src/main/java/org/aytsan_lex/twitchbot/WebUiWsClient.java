package org.aytsan_lex.twitchbot;

import java.net.URI;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

public class WebUiWsClient extends WebSocketClient
{
    private enum Commands
    {
        requestconfig,
        requestfilters
    }

    private static final Logger LOG = LoggerFactory.getLogger(WebUiWsClient.class);

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

    @Override
    public void onOpen(ServerHandshake serverHandshake)
    {
        LOG.info("WebSocket connected");
    }

    @Override
    public void onMessage(String message)
    {
        if (message.startsWith("#"))
        {
            final String command = message.substring(1);
            try
            {
                LOG.info("Handling command: {}", command);
                switch (Commands.valueOf(command))
                {
                    case requestconfig -> send(BotConfigManager.getConfig().asJson());
                    case requestfilters -> send(FiltersManager.getMiraFilters().asJson());
                }
            }
            catch (IllegalArgumentException e)
            {
                LOG.error("Invalid command: {}", command);
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
