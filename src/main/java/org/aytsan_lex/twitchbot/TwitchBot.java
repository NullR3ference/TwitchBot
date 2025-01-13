package org.aytsan_lex.twitchbot;

import javax.annotation.Nullable;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TwitchBot
{
    public static final Logger LOGGER = LoggerFactory.getLogger(TwitchBot.class);

    private final IrcChatMessageHandler ircChatMessageHandler;

    private TwitchClient twitchClient;
    private boolean isInitialized;
    private boolean isRunning;

    private static TwitchBot twitchBotInstance = null;

    private TwitchBot()
    {
        this.ircChatMessageHandler = new IrcChatMessageHandler();
        this.isRunning = false;
    }

    public static synchronized TwitchBot instance()
    {
        if (twitchBotInstance == null) { twitchBotInstance = new TwitchBot(); }
        return twitchBotInstance;
    }

    public TwitchBot init(@NotNull String clientId,
                          @NotNull String clientSecret,
                          @NotNull String accessToken,
                          @Nullable String refreshToken,
                          @Nullable ArrayList<String> scopes)
    {
        if (!this.isInitialized)
        {
            LOGGER.info("Initializing TwitchBot...");

            TwitchClientBuilder client_builder = TwitchClientBuilder.builder()
                    .withClientId(clientId)
                    .withClientSecret(clientSecret)
                    .withEnableChat(true)
                    .withEnableHelix(true)
                    .withTimeout(1000)
                    .withChatMaxJoinRetries(1)
                    .withDefaultEventHandler(SimpleEventHandler.class);

            client_builder = client_builder
                    .withChatAccount(
                            new OAuth2Credential(
                                    "twitch",
                                    accessToken,
                                    refreshToken,
                                    null,
                                    null,
                                    null,
                                    scopes
                            )
                    );

            this.twitchClient = client_builder.build();
            this.isInitialized = true;
        }
        return this;
    }

    public void start()
    {
        LOGGER.info("Connecting TwitchBot to channels: {}", BotConfig.instance().getChannels());

        this.twitchClient.getEventManager()
                .getEventHandler(SimpleEventHandler.class)
                .onEvent(IRCMessageEvent.class, this.ircChatMessageHandler::handleIrcMessage);

        BotConfig.instance().getChannels().forEach(this::joinToChat);
        this.isRunning = true;

        LOGGER.info("Started");
    }

    public void stop()
    {
        BotConfig.instance().getChannels().forEach(this::leaveFromChat);
        this.isRunning = false;
    }

    public void joinToChat(String channelName)
    {
        this.twitchClient.getChat().joinChannel(channelName);
    }

    public void leaveFromChat(String channelName)
    {
        this.twitchClient.getChat().leaveChannel(channelName);
    }

    public boolean channelExists(String channelName)
    {
        // TODO: Implement channel exist checking
        return true;
    }

    public boolean isRunning()
    {
        return this.isRunning;
    }
}