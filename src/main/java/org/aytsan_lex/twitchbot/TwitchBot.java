package org.aytsan_lex.twitchbot;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;
import com.github.twitch4j.eventsub.events.StreamOfflineEvent;
import com.github.twitch4j.eventsub.events.StreamOnlineEvent;
import com.github.philippheuer.events4j.reactor.ReactorEventHandler;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;

public class TwitchBot
{
    public static final Logger LOGGER = LoggerFactory.getLogger(TwitchBot.class);

    private static final Object chatMessageSync = new Object();
    private static TwitchClient twitchClient = null;
    private static boolean isRunning = false;

    private static final WsUiServer wsUiServer = WsUiServer.builder()
            .withHost("0.0.0.0")
            .withPort(8811)
            .build();

    public static void initialize()
    {
        LOGGER.info("Initializing...");

        twitchClient = TwitchClientBuilder.builder()
                .withEnableChat(true)
                .withEnablePubSub(true)
                .withTimeout(1000)
                .withChatMaxJoinRetries(2)
                .withClientId(BotCredentialManager.getCredentials().getClientId())
                .withChatAccount(new OAuth2Credential("twitch", BotCredentialManager.getCredentials().getAccessToken()))
                .withDefaultEventHandler(ReactorEventHandler.class)
                .build();
    }

    public static void shutdown()
    {
        twitchClient.close();
    }

    public static void start()
    {
        if (!isRunning)
        {
            twitchClient.getEventManager().getEventHandler(ReactorEventHandler.class)
                    .onEvent(IRCMessageEvent.class, IrcMessageHandler::handleIrcMessage);

            twitchClient.getEventManager().getEventHandler(ReactorEventHandler.class)
                    .onEvent(StreamOnlineEvent.class, ChannelEventHandler::onStreamOnline);

            twitchClient.getEventManager().getEventHandler(ReactorEventHandler.class)
                    .onEvent(StreamOfflineEvent.class, ChannelEventHandler::onStreamOffline);

            final ArrayList<String> channels = BotConfigManager.getConfig().getChannels();
            final ArrayList<String> owners = BotConfigManager.getConfig().getOwners();

            LOGGER.info("Connecting to owners channels: {}", owners);
            owners.forEach(channel -> {
                TwitchBot.joinToChat(channel);
//                twitchClient.getClientHelper().enableStreamEventListener(channel);
            });

            LOGGER.info("Connecting to channels: {}", channels);
            channels.forEach(channel -> {
                TwitchBot.joinToChat(channel);
//                twitchClient.getClientHelper().enableStreamEventListener(channel);
            });

            wsUiServer.setReuseAddr(true);
            wsUiServer.start();

            isRunning = true;
            LOGGER.info("Started");
        }
    }

    public static void stop()
    {
        if (isRunning)
        {
            try { wsUiServer.stop(); }
            catch (InterruptedException ignored) { }

            isRunning = false;
            LOGGER.info("Stopped");
        }
    }

    public static boolean isInitialized()
    {
        return twitchClient != null;
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
        sendMessageWithDelay(channelName, message, BotConfigManager.getConfig().getDelayBetweenMessages());
    }

    public static void replyToMessage(String channelName, String messageId, String message)
    {
        replyToMessageWithDelay(channelName, messageId, message, BotConfigManager.getConfig().getDelayBetweenMessages());
    }

    public static void sendMessageWithDelay(String channelName, String message, int delay)
    {
        // TODO: Handle identical message timeout, prevent this
        synchronized (chatMessageSync)
        {
            try { TimeUnit.MILLISECONDS.sleep(delay); }
            catch (InterruptedException ignored) { }
        }
        twitchClient.getChat().sendMessage(channelName, message);
    }

    public static void replyToMessageWithDelay(String channelName, String messageId, String message, int delay)
    {
        synchronized (chatMessageSync)
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

    public static WsUiServer getWsUiServer()
    {
        return wsUiServer;
    }
}