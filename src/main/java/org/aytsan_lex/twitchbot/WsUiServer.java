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

    private static final Logger LOG = LoggerFactory.getLogger(WsUiServer.class);

    private WsUiServer(String host, int port)
    {
        super(new InetSocketAddress(host, port));
    }

    public static Builder builder()
    {
        return new Builder();
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake)
    {
        final InetSocketAddress addrInfo = webSocket.getRemoteSocketAddress();
        LOG.info("Client connected: {}:{}", addrInfo.getHostString(), addrInfo.getPort());
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b)
    {
        final InetSocketAddress addrInfo = webSocket.getRemoteSocketAddress();
        LOG.info("Client disconnected: {}", addrInfo.getHostString());
    }

    @Override
    public void onMessage(WebSocket webSocket, String message)
    {
        final InetSocketAddress addrInfo = webSocket.getRemoteSocketAddress();
        LOG.info("[{}] Message received: {}", addrInfo.getHostString(), message);

        final ArrayList<String> commands = new ArrayList<>(Arrays.asList(message.split(":")));
        final ArrayList<String> response = new ArrayList<>();

        for (final String command : commands)
        {
            if (command.equals("#requestconfig"))
            {
                try
                {
                    response.add(BotConfigManager.readConfigAdString());
                }
                catch (IOException e)
                {
                    LOG.error("Failed to read config: {}", e.getMessage());
                }
            }
            else if (command.equals("#requestfilters"))
            {
                try
                {
                    response.add(FiltersManager.readFiltersAsString());
                }
                catch (IOException e)
                {
                    LOG.error("Failed to read filters: {}", e.getMessage());
                }
            }

            response.add("///");
        }

        webSocket.send(String.join("", response));
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
}
