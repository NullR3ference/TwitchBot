package org.aytsan_lex.twitchbot;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.reactor.ReactorEventHandler;

// TODO: Implement WebSocket client to interact with frontend
// https://github.com/TooTallNate/Java-WebSocket

public class TwitchBot
{
    public static final Logger LOGGER = LoggerFactory.getLogger(TwitchBot.class);

    private static final Object chatMessageSync = new Object();
    private static TwitchClient twitchClient = null;
    private static boolean isRunning = false;

    public static void initialize(String clientId, String accessToken)
    {
        LOGGER.info("Initializing...");

        twitchClient = TwitchClientBuilder.builder()
                .withClientId(clientId)
                .withEnableChat(true)
                .withEnablePubSub(true)
                .withTimeout(1000)
                .withChatMaxJoinRetries(2)
                .withChatAccount(new OAuth2Credential("twitch", accessToken))
                .withDefaultEventHandler(ReactorEventHandler.class)
                .build();
    }

    public static void shutdown()
    {
        twitchClient.close();
        twitchClient = null;
    }

    public static void start()
    {
        if (!isRunning)
        {
            twitchClient.getEventManager()
                    .getEventHandler(ReactorEventHandler.class)
                    .onEvent(IRCMessageEvent.class, IrcMessageHandler::handleIrcMessage);

            final ArrayList<String> channels = BotConfigManager.getConfig().getChannels();
            final ArrayList<String> owners = BotConfigManager.getConfig().getOwners();

            LOGGER.info("Connecting to owners channels: {}", owners);
            owners.forEach(TwitchBot::joinToChat);

            LOGGER.info("Connecting to channels: {}", channels);
            channels.forEach(TwitchBot::joinToChat);

            isRunning = true;
            LOGGER.info("Started");
        }
        else
        {
            LOGGER.warn("Cannot start, already running!");
        }
    }

    public static void stop()
    {
        if (isRunning)  { isRunning = false; }
        else            { LOGGER.warn("Cannot stop, already stopped!"); }
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
        // TODO: Implement channel exist checking
        return true;
    }
}