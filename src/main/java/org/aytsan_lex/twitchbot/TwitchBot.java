package org.aytsan_lex.twitchbot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;
import com.github.twitch4j.eventsub.events.StreamOfflineEvent;
import com.github.twitch4j.eventsub.events.StreamOnlineEvent;
import com.github.philippheuer.events4j.reactor.ReactorEventHandler;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;

import org.aytsan_lex.twitchbot.managers.*;

public class TwitchBot
{
    public static final Logger LOG = LoggerFactory.getLogger(TwitchBot.class);
    private static final Object CHAT_MESSAGE_SYNC = new Object();

    private static final IBotManager configBotManager = new ConfigBotManager();
    private static final IBotManager credentialsBotManager = new CredentialsBotManager();
    private static final IBotManager filtersBotManager = new FiltersBotManager();
    private static final IBotManager commandsBotManager = new CommandsBotManager();
    private static final IBotManager ollamaModelsBotManager = new OllamaModelsBotManager();

    private static TwitchClient twitchClient = null;
    private static WsUiServer wsUiServer = null;
    private static boolean isRunning = false;

    /**
     * Initialize Twitch bot and all sub-systems
     *
     * @return true if initialization was successful, otherwise - false
     */
    public static boolean initialize()
    {
        LOG.info("Initializing...");

        boolean managersIsInitialized = true;
        managersIsInitialized &= configBotManager.initialize();
        managersIsInitialized &= credentialsBotManager.initialize();
        managersIsInitialized &= commandsBotManager.initialize();

        filtersBotManager.initialize();
        ollamaModelsBotManager.initialize();

        if (!managersIsInitialized)
        {
            return false;
        }

        CommandHandler.initialize();

        LOG.info("Building TwitchClient...");
        twitchClient = TwitchClientBuilder.builder()
                .withEnableChat(true)
                .withEnablePubSub(true)
                .withTimeout(1000)
                .withChatMaxJoinRetries(2)
                .withClientId(getCredentialsManager().getCredentials().clientId())
                .withChatAccount(new OAuth2Credential("twitch", getCredentialsManager().getCredentials().accessToken()))
                .withDefaultEventHandler(ReactorEventHandler.class)
                .build();

        twitchClient.getEventManager().getEventHandler(ReactorEventHandler.class)
                .onEvent(IRCMessageEvent.class, IrcMessageHandler::handleIrcMessage);

        twitchClient.getEventManager().getEventHandler(ReactorEventHandler.class)
                .onEvent(StreamOnlineEvent.class, ChannelEventHandler::onStreamOnline);

        twitchClient.getEventManager().getEventHandler(ReactorEventHandler.class)
                .onEvent(StreamOfflineEvent.class, ChannelEventHandler::onStreamOffline);

        LOG.info("Building WebSocket server...");
        wsUiServer = WsUiServer.builder().withHost("0.0.0.0").withPort(8811).build();

        return true;
    }

    public static void shutdown()
    {
        CommandHandler.shutdown();

        configBotManager.shutdown();
        filtersBotManager.shutdown();
        credentialsBotManager.shutdown();
        commandsBotManager.shutdown();
        ollamaModelsBotManager.shutdown();

        twitchClient.close();
    }

    /**
     * Start TwitchBot
     * <p>
     * Connect to all channels within arrays "channels" and "owners"
     * start Websocket Server
     * <p>
     */
    public static void start()
    {
        if (!isRunning)
        {
            LOG.info("Starting WebSocket server...");
            wsUiServer.start();

            final ArrayList<String> channels = Stream.of(
                    getConfigManager().getConfig().getOwners(),
                    getConfigManager().getConfig().getChannels()
            ).flatMap(Collection::stream).collect(Collectors.toCollection(ArrayList::new));

            LOG.info("Connecting to channels: {}", channels);
            channels.forEach(TwitchBot::joinToChat);

            isRunning = true;
            LOG.info("Started");
        }
    }

    /**
     * Stop TwitchBot
     * <p>
     * Disconnect from all channels within arrays "channels" and "owners"
     * Stop WebSocket server
     * <p>
     */
    public static void stop()
    {
        if (isRunning)
        {
            LOG.info("Disconnecting from all channels...");
            getConfigManager().getConfig().getOwners().forEach(TwitchBot::joinToChat);
            getConfigManager().getConfig().getChannels().forEach(TwitchBot::joinToChat);

            LOG.info("Stopping WebSocket server...");
            try { wsUiServer.stop(1000); }
            catch (InterruptedException ignored) { }

            isRunning = false;
            LOG.info("Stopped");
        }
    }

    public static void joinToChat(String channelName)
    {
        twitchClient.getChat().joinChannel(channelName);
    }

    public static void leaveFromChat(String channelName)
    {
        twitchClient.getChat().leaveChannel(channelName);
    }

    public static boolean isConnectedToChat(String channelName)
    {
        return twitchClient.getChat().isChannelJoined(channelName);
    }

    public static void sendMessage(String channelName, String message)
    {
        sendMessageWithDelay(channelName, message, getConfigManager().getDelayBetweenMessages());
    }

    public static void replyToMessage(String channelName, String messageId, String message)
    {
        replyToMessageWithDelay(channelName, messageId, message, getConfigManager().getDelayBetweenMessages());
    }

    public static void sendMessageWithDelay(String channelName, String message, int delay)
    {
        // TODO: Handle identical message timeout, prevent this
        synchronized (CHAT_MESSAGE_SYNC)
        {
            try { TimeUnit.MILLISECONDS.sleep(delay); }
            catch (InterruptedException ignored) { }
        }
        twitchClient.getChat().sendMessage(channelName, message);
    }

    public static void replyToMessageWithDelay(String channelName, String messageId, String message, int delay)
    {
        synchronized (CHAT_MESSAGE_SYNC)
        {
            try { TimeUnit.MILLISECONDS.sleep(delay); }
            catch (InterruptedException ignored) {}
        }
        twitchClient.getChat().sendMessage(channelName, message, null, messageId);
    }

    public static boolean channelExists(String channelName)
    {
        return true;
    }

    public static ConfigBotManager getConfigManager()
    {
        return (ConfigBotManager) configBotManager;
    }

    public static CredentialsBotManager getCredentialsManager()
    {
        return (CredentialsBotManager) credentialsBotManager;
    }

    public static FiltersBotManager getFiltersManager()
    {
        return (FiltersBotManager) filtersBotManager;
    }

    public static CommandsBotManager getCommandsManager()
    {
        return (CommandsBotManager) commandsBotManager;
    }

    public static OllamaModelsBotManager getOllamaModelsManager()
    {
        return (OllamaModelsBotManager) ollamaModelsBotManager;
    }

    public static WsUiServer getWsUiServer()
    {
        return wsUiServer;
    }
}