package org.aytsan_lex.twitchbot;

import java.util.ArrayList;
import javax.annotation.Nullable;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.reactor.ReactorEventHandler;

public class TwitchBot
{
    private final TwitchClient m_TwitchClient;
    private final ArrayList<String> m_ManagedChannels;
    private final ChatMessagesHandler m_ChatMessageHandler;
    private boolean m_IsRunning;

    public TwitchBot(String client_id, String access_token, @Nullable String refresh_token)
    {
        this.m_TwitchClient = TwitchClientBuilder.builder()
                .withEnableChat(true)
                .withEnableHelix(true)
                .withClientId(client_id)
                .withChatAccount(new OAuth2Credential("twitch", access_token))
                .withDefaultEventHandler(ReactorEventHandler.class)
                .build();

        this.m_ManagedChannels = new ArrayList<>();
        this.m_ChatMessageHandler = new ChatMessagesHandler();
        this.m_IsRunning = false;
    }

    public TwitchBot withChannels(ArrayList<String> channels)
    {
        this.m_ManagedChannels.addAll(channels);
        return this;
    }

    public TwitchBot start()
    {
        this.m_TwitchClient.getEventManager()
                .getEventHandler(ReactorEventHandler.class)
                .onEvent(ChannelMessageEvent.class, this.m_ChatMessageHandler::handleChatMessage);

        this.m_ManagedChannels.forEach(name -> this.m_TwitchClient.getChat().joinChannel(name));

        this.m_IsRunning = true;
        return this;
    }

    public void stop()
    {
        this.m_TwitchClient.getEventManager()
                .getEventHandler(ReactorEventHandler.class)
                .close();

        this.m_ManagedChannels.forEach(name -> this.m_TwitchClient.getChat().leaveChannel(name));

        this.m_IsRunning = false;
    }

    public boolean isRunning()
    {
        return this.m_IsRunning;
    }
}