package org.aytsan_lex.twitchbot;

import java.util.ArrayList;
import javax.annotation.Nullable;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;

public class TwitchBot
{
    private final ArrayList<String> loggedChannels;
    private final ChatMessagesHandler chatMessageHandler;

    private TwitchClient twitchClient;
    private boolean isRunning;

    private static TwitchBot twitchBotInstance = null;

    private TwitchBot()
    {
        this.loggedChannels = new ArrayList<>();
        this.chatMessageHandler = new ChatMessagesHandler();
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

    public TwitchBot withChannels(ArrayList<String> channels)
    {
        this.loggedChannels.addAll(channels);
        return this;
    }

    public void start()
    {
        this.twitchClient.getEventManager()
                .getEventHandler(SimpleEventHandler.class)
                .onEvent(ChannelMessageEvent.class, this.chatMessageHandler::handleChatMessage);

        this.loggedChannels.forEach(name -> this.twitchClient.getChat().joinChannel(name));

        this.isRunning = true;
    }

    public void stop()
    {
        this.twitchClient.getEventManager()
                .getEventHandler(SimpleEventHandler.class)
                .close();

        this.loggedChannels.forEach(name -> this.twitchClient.getChat().leaveChannel(name));

        this.isRunning = false;
    }

    public TwitchClient getTwitchClient()
    {
        return this.twitchClient;
    }

    public boolean isRunning()
    {
        return this.isRunning;
    }
}