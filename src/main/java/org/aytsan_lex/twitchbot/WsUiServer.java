package org.aytsan_lex.twitchbot;

import java.util.ArrayList;
import java.util.List;
import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import org.aytsan_lex.twitchbot.bot_commands.BenBotCommand;
import org.aytsan_lex.twitchbot.bot_commands.IqBotCommand;
import org.aytsan_lex.twitchbot.bot_commands.MiraBotCommand;

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
        // syntax: #<command> or #<command_1>///#<command_2>... (as batch)
        requestconfig,
        requestfilters,
        requestmodelmessages,
        requestmodelmessageshistory,
        requestmutestate,
        requeststatus,

        // Block of client to server commands to accept data
        // syntax: /<command>###<data>
        updateconfig,
        updatefilters,
        miramute,
        benmute,
        iqmute,
        addchannel,
        rmchannel,
        join,
        leave,
        restart
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

    public void sendBytes(final byte[] bytes)
    {
        if (this.currentClient != null)
        {
            this.currentClient.send(bytes);
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

        if (message.startsWith("#"))
        {
            this.handleRequestCommand(webSocket, message);
        }
        else if (message.startsWith("/"))
        {
            this.handleDataCommand(message);
        }
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

    private void handleRequestCommand(WebSocket webSocket, final String message)
    {
        final ArrayList<String> batch = new ArrayList<>(List.of(message.split("///")));

        if (batch.size() >= 2)
        {
            this.handleRequestCommandBatched(batch, webSocket);
        }
        else
        {
            final String command = batch.get(0).substring(1);

            try
            {
                switch (Commands.valueOf(command))
                {
                    case requestconfig -> webSocket.send(TwitchBot.getConfigManager().getConfig().toJsonString());
                    case requestfilters -> webSocket.send(TwitchBot.getFiltersManager().getMiraFilters().toJsonString());

                    case requestmodelmessages -> { }

                    case requestmodelmessageshistory ->
                    {
                        final ArrayList<String> history = TwitchBot.getOllamaModelsManager().getMiraModel().getQuestionsHistory();
                        webSocket.send(String.join("\n", history));
                    }

                    case requestmutestate ->
                    {
                        final boolean miraIsMuted = TwitchBot.getConfigManager().commandIsMuted(MiraBotCommand.class);
                        final boolean benIsMuted = TwitchBot.getConfigManager().commandIsMuted(BenBotCommand.class);
                        final boolean iqIsMuted = TwitchBot.getConfigManager().commandIsMuted(IqBotCommand.class);
                        final String data = "%b///%b///%b".formatted(miraIsMuted, benIsMuted, iqIsMuted);
                        webSocket.send(data);
                    }

                    case requeststatus ->
                    {
                        final String response = "#status///%s".formatted(Utils.buildStatusMessage());
                        webSocket.send(response);
                    }

                    default -> {  }
                }
            }
            catch (IllegalArgumentException e)
            {
                LOG.warn("Invalid request command: '{}', ignored", command);
            }
        }
    }

    private void handleRequestCommandBatched(final ArrayList<String> commandBatch, WebSocket webSocket)
    {
        // TODO: Handle batch of commands
    }

    private void handleDataCommand(final String message)
    {
        final String[] data = message.split("###");
        final String command = data[0].replaceFirst("^/", "");

        try
        {
            switch (Commands.valueOf(command))
            {
                case updateconfig ->
                {
                    if (data.length < 2)
                    {
                        LOG.error("Received data for '{}' command is empty!", command);
                        return;
                    }

                    TwitchBot.getConfigManager().writeData(data[1]);
                    TwitchBot.getConfigManager().readFile();
                }

                case updatefilters ->
                {
                    if (data.length < 2)
                    {
                        LOG.error("Received data for '{}' command is empty!", command);
                        return;
                    }

                    TwitchBot.getFiltersManager().writeData(data[1]);
                    TwitchBot.getFiltersManager().readFile();
                }

                case miramute ->
                {
                    if (data.length < 2)
                    {
                        LOG.error("Received data for '{}' command is empty!", command);
                        return;
                    }

                    final boolean isMuted = Boolean.parseBoolean(data[1]);
                    TwitchBot.getCommandsManager().setCommandIsMuted(MiraBotCommand.class, isMuted);
                }

                case benmute ->
                {
                    if (data.length < 2)
                    {
                        LOG.error("Received data for '{}' command is empty!", command);
                        return;
                    }

                    final boolean isMuted = Boolean.parseBoolean(data[1]);
                    TwitchBot.getCommandsManager().setCommandIsMuted(BenBotCommand.class, isMuted);
                }

                case iqmute ->
                {
                    if (data.length < 2)
                    {
                        LOG.error("Received data for '{}' command is empty!", command);
                        return;
                    }

                    final boolean isMuted = Boolean.parseBoolean(data[1]);
                    TwitchBot.getCommandsManager().setCommandIsMuted(IqBotCommand.class, isMuted);
                }

                case addchannel ->
                {
                    if (data.length < 2)
                    {
                        LOG.error("Received data for '{}' command is empty!", command);
                        return;
                    }

                    final String channelName = data[1].toLowerCase().trim();
                    TwitchBot.getConfigManager().addChannel(channelName);
                    TwitchBot.getConfigManager().saveFile();
                }

                case rmchannel ->
                {
                    if (data.length < 2)
                    {
                        LOG.error("Received data for '{}' command is empty!", command);
                        return;
                    }

                    final String channelName = data[1].toLowerCase().trim();
                    TwitchBot.getConfigManager().removeChannel(channelName);
                    TwitchBot.getConfigManager().saveFile();
                }

                case join ->
                {
                    if (data.length < 2)
                    {
                        LOG.error("Received data for '{}' command is empty!", command);
                        return;
                    }

                    final String channelName = data[1].toLowerCase().trim();
                    TwitchBot.joinToChat(channelName);
                }

                case leave ->
                {
                    if (data.length < 2)
                    {
                        LOG.error("Received data for '{}' command is empty!", command);
                        return;
                    }

                    final String channelName = data[1].toLowerCase().trim();
                    TwitchBot.leaveFromChat(channelName);
                }

                case restart -> System.exit(10);

                default -> { }
            }
        }
        catch (IllegalArgumentException e)
        {
            LOG.warn("Invalid data command: '{}', ignored", command);
        }
    }
}
