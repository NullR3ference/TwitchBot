package org.aytsan_lex.twitchbot;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.reactor.ReactorEventHandler;

public class TwitchBot
{
    // TODO: Make it static

    public static final Logger LOGGER = LoggerFactory.getLogger(TwitchBot.class);
    private static TwitchBot twitchBotInstance = null;

    private TwitchClient twitchClient;
    private boolean isInitialized;
    private boolean isRunning;

    private TwitchBot()
    {
        this.isRunning = false;
    }

    public static synchronized TwitchBot instance()
    {
        if (twitchBotInstance == null) { twitchBotInstance = new TwitchBot(); }
        return twitchBotInstance;
    }

    public TwitchBot initialize(String clientId, String accessToken)
    {
        if (!this.isInitialized)
        {
            LOGGER.info("Initializing...");

            TwitchClientBuilder client_builder = TwitchClientBuilder.builder()
                    .withClientId(clientId)
                    .withEnableChat(true)
                    .withEnableHelix(true)
                    .withTimeout(1000)
                    .withChatMaxJoinRetries(2)
                    .withDefaultEventHandler(ReactorEventHandler.class);

            client_builder = client_builder
                    .withChatAccount(
                            new OAuth2Credential(
                                    "twitch",
                                    accessToken,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null
                            )
                    );

            this.twitchClient = client_builder.build();
            this.isInitialized = true;
        }
        else
        {
            LOGGER.warn("Cannot initialize, already initialized!");
        }
        return this;
    }

    public void start()
    {
        if (!isRunning)
        {
            this.twitchClient.getEventManager()
                    .getEventHandler(ReactorEventHandler.class)
                    .onEvent(IRCMessageEvent.class, IrcMessageHandler::handleIrcMessage);

            final ArrayList<String> channels = BotConfigManager.getConfig().getChannels();
            final ArrayList<String> owners = BotConfigManager.getConfig().getOwners();

            LOGGER.info("Connecting to owners channels: {}", owners);
            owners.forEach(this::joinToChat);

            LOGGER.info("Connecting to channels: {}", channels);
            channels.forEach(this::joinToChat);

            this.isRunning = true;
            LOGGER.info("Started");
        }
        else
        {
            LOGGER.warn("Cannot start, already running!");
        }
    }

    public void stop()
    {
        if (this.isRunning)
        {
            this.isRunning = false;
        }
        else
        {
            LOGGER.warn("Cannot stop, already stopped!");
        }
    }

    public void joinToChat(final String channelName)
    {
        this.twitchClient.getChat().joinChannel(channelName);
    }

    public void leaveFromChat(final String channelName)
    {
        this.twitchClient.getChat().leaveChannel(channelName);
    }

    public boolean channelExists(final String channelName)
    {
        // TODO: Implement channel exist checking
        return true;
    }
}