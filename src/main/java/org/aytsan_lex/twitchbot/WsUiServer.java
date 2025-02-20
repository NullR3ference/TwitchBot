package org.aytsan_lex.twitchbot;

import java.net.InetSocketAddress;

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
    private WebSocket currentClient = null;

    private WsUiServer(String host, int port)
    {
        super(new InetSocketAddress(host, port));
        setReuseAddr(true);
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public void sendMessage(final String message)
    {
        if (this.currentClient != null)
        {
            LOG.debug("Sending message to client: {}", message);
            this.currentClient.send(message);
        }
    }

    public boolean clientIsConnected()
    {
        return this.currentClient != null;
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake)
    {
        final InetSocketAddress addrInfo = webSocket.getRemoteSocketAddress();
        LOG.debug("Client connected: {}:{}", addrInfo.getHostString(), addrInfo.getPort());
        this.currentClient = webSocket;
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b)
    {
        final InetSocketAddress addrInfo = webSocket.getRemoteSocketAddress();
        LOG.debug("Client disconnected: {}", addrInfo.getHostString());
        this.currentClient = null;
    }

    @Override
    public void onMessage(WebSocket webSocket, String message)
    {
        final InetSocketAddress addrInfo = webSocket.getRemoteSocketAddress();
        LOG.debug("Handling command from: {}", addrInfo.getHostString());
        UiCommandHandler.handleCommand(webSocket, message);
    }

    @Override
    public void onError(WebSocket webSocket, Exception e)
    {
        final InetSocketAddress addrInfo = webSocket.getRemoteSocketAddress();
        LOG.error("[{}] Error: {}", addrInfo.getHostString(), e.getMessage());
        this.currentClient = null;
    }

    @Override
    public void onStart()
    {
        LOG.info("Started on: {}:{}", super.getAddress().getHostString(), super.getPort());
    }
}
