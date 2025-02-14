package org.aytsan_lex.twitchbot;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.aytsan_lex.twitchbot.commands.RestartBotCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import org.aytsan_lex.twitchbot.commands.BenBotCommand;
import org.aytsan_lex.twitchbot.commands.IqBotCommand;
import org.aytsan_lex.twitchbot.commands.MiraBotCommand;

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
        requestmutestate,

        // Block of client to server commands to accept data
        // syntax: ///<command>###<data>
        updatefilters,
        updateconfig,
        miramute,
        benmute,
        iqmute,
        restart
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
        LOG.info("Handling command from: {}", addrInfo.getHostString());

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
    }

    @Override
    public void onStart()
    {
        LOG.info("Started on: {}:{}", super.getAddress().getHostString(), super.getPort());
    }

    private void handleRequestCommand(WebSocket webSocket, final String message)
    {
        final String command = message.substring(1);

        try
        {
            switch (Commands.valueOf(command))
            {
                case requestconfig ->
                {
                    try { webSocket.send(BotConfigManager.readConfigAdString()); }
                    catch (IOException e) { LOG.error("Failed to read config: {}", e.getMessage()); }
                }

                case requestfilters ->
                {
                    try { webSocket.send(FiltersManager.readFiltersAsString()); }
                    catch (IOException e) { LOG.error("Failed to read filters: {}", e.getMessage()); }
                }

                case requestmodelmessages -> { }

                case requestmodelmessageshistory ->
                {
                    final ArrayList<String> history = OllamaModelsManager.getMiraModel().getQuestionsHistory();
                    webSocket.send(String.join("\n", history));
                }

                case requestmutestate ->
                {
                    final boolean miraIsMuted = BotConfigManager.commandIsMuted(MiraBotCommand.class);
                    final boolean benIsMuted = BotConfigManager.commandIsMuted(BenBotCommand.class);
                    final boolean iqIsMuted = BotConfigManager.commandIsMuted(IqBotCommand.class);
                    final String data = "%b///%b///%b".formatted(miraIsMuted, benIsMuted, iqIsMuted);
                    webSocket.send(data);
                }

                default -> {  }
            }
        }
        catch (IllegalArgumentException e)
        {
            LOG.warn("Invalid request command: '{}', ignored", command);
        }
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

                    try
                    {
                        BotConfigManager.writeConfig(data[1]);
                        BotConfigManager.readConfig();
                    }
                    catch (IOException e)
                    {
                        LOG.error("Failed to save config: {}", e.getMessage());
                    }
                }

                case updatefilters ->
                {
                    if (data.length < 2)
                    {
                        LOG.error("Received data for '{}' command is empty!", command);
                        return;
                    }

                    try
                    {
                        FiltersManager.saveFilters(data[1]);
                        FiltersManager.readFilters();
                    }
                    catch (IOException e)
                    {
                        LOG.error("Failed to save filters: {}", e.getMessage());
                    }
                }

                case miramute ->
                {
                    if (data.length < 2)
                    {
                        LOG.error("Received data for '{}' command is empty!", command);
                        return;
                    }

                    final boolean isMuted = Boolean.parseBoolean(data[1]);
                    BotCommandsManager.setCommandIsMuted(MiraBotCommand.class, isMuted);
                }

                case benmute ->
                {
                    if (data.length < 2)
                    {
                        LOG.error("Received data for '{}' command is empty!", command);
                        return;
                    }

                    final boolean isMuted = Boolean.parseBoolean(data[1]);
                    BotCommandsManager.setCommandIsMuted(BenBotCommand.class, isMuted);
                }

                case iqmute ->
                {
                    if (data.length < 2)
                    {
                        LOG.error("Received data for '{}' command is empty!", command);
                        return;
                    }

                    final boolean isMuted = Boolean.parseBoolean(data[1]);
                    BotCommandsManager.setCommandIsMuted(IqBotCommand.class, isMuted);
                }

                case restart ->
                {
                    new RestartBotCommand().execute(null, new ArrayList<>(List.of("update")));
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
