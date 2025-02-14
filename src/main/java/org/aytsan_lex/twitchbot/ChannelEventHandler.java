package org.aytsan_lex.twitchbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.twitch4j.eventsub.events.StreamOnlineEvent;
import com.github.twitch4j.eventsub.events.StreamOfflineEvent;

public class ChannelEventHandler
{
    private static final Logger LOG = LoggerFactory.getLogger(ChannelEventHandler.class);

    public static void onStreamOnline(final StreamOnlineEvent event)
    {
        LOG.info("Stream is online: '{}'", event.getBroadcasterUserName());
        TwitchBot.leaveFromChat(event.getBroadcasterUserName());
    }

    public static void onStreamOffline(final StreamOfflineEvent event)
    {
        LOG.info("Stream is offline: '{}'", event.getBroadcasterUserName());
        TwitchBot.joinToChat(event.getBroadcasterUserName());
    }
}
