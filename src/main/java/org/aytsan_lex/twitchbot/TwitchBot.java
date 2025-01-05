package org.aytsan_lex.twitchbot;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

public class TwitchBot
{
    private final IrcChatMessageHandler ircChatMessageHandler;

    private TwitchClient twitchClient;
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

    public TwitchBot init(String clientId)
    {
        return this.init(clientId, null, null, null);
    }

    public TwitchBot init(String clientId, String accessToken)
    {
        return this.init(clientId, accessToken, null, null);
    }

    public TwitchBot init(String clientId,
                          @Nullable String accessToken,
                          @Nullable String refreshToken,
                          @Nullable ArrayList<String> scopes)
    {
        TwitchClientBuilder client_builder = TwitchClientBuilder.builder()
                .withEnableChat(true)
                .withEnableHelix(true)
                .withClientId(clientId)
                .withTimeout(1000)
                .withChatMaxJoinRetries(2)
                .withDefaultEventHandler(SimpleEventHandler.class);

        if (accessToken != null)
        {
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
        }

        this.twitchClient = client_builder.build();
        return this;
    }

    public void start()
    {
        this.twitchClient.getEventManager()
                .getEventHandler(SimpleEventHandler.class)
                .onEvent(IRCMessageEvent.class, this.ircChatMessageHandler::handleIrcMessage);

        BotConfig.instance().getChannels().forEach(this::joinToChat);
        this.isRunning = true;
    }

    public void stop()
    {
        BotConfig.instance().getChannels().forEach(this::leaveFromChat);
        this.isRunning = false;
    }

    public boolean joinToChat(String channelName)
    {
        this.twitchClient.getChat().joinChannel(channelName);
        return this.twitchClient.getChat().isChannelJoined(channelName);
    }

    public boolean leaveFromChat(String channelName)
    {
        this.twitchClient.getChat().leaveChannel(channelName);
        return !this.twitchClient.getChat().isChannelJoined(channelName);
    }

    public boolean channelExists(String channelName)
    {
        return true;
    }

    public boolean isRunning()
    {
        return this.isRunning;
    }
}