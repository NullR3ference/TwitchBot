package org.aytsan_lex.twitchbot;

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

    public TwitchBot init(String client_id, @Nullable String access_token)
    {
        TwitchClientBuilder client_builder = TwitchClientBuilder.builder()
                .withEnableChat(true)
                .withEnableHelix(true)
                .withClientId(client_id)
                .withTimeout(1000)
                .withDefaultEventHandler(SimpleEventHandler.class);

        if (access_token != null)
        {
            client_builder = client_builder
                    .withChatAccount(new OAuth2Credential("twitch", access_token));
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
        System.out.println("Channels: " + BotConfig.instance().getChannels());

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

    public boolean isRunning()
    {
        return this.isRunning;
    }
}