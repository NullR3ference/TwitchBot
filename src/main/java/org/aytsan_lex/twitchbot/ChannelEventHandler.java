package org.aytsan_lex.twitchbot;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.eventsub.events.ChannelBanEvent;
import com.github.twitch4j.eventsub.events.ChannelUnbanEvent;

public class ChannelEventHandler
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelEventHandler.class);

    public static void handleChannelMessage(final ChannelMessageEvent event)
    {
        final String channelName = event.getChannel().getName();
        final String message = event.getMessage().trim();

        if (message.startsWith("%"))
        {
            if (BotConfigManager.isBannedOnChannel(channelName))
            {
                LOGGER.warn("Command will not handle: banned in chat of '{}'", channelName);
                return;
            }

            if (BotConfigManager.isTimedOutOnChannel(channelName))
            {
                final LocalDateTime currentDateTime = LocalDateTime.now();
                final LocalDateTime expiredIn = BotConfigManager.getTimeoutEndsAt(channelName);

                if (currentDateTime.isBefore(expiredIn))
                {
                    LOGGER.warn("Command will not handle: timed out in chat of '{}'", channelName);
                    return;
                }

                BotConfigManager.removeTimedOutOnChannel(channelName);
                BotConfigManager.writeConfig();
            }

            CommandHandler.handleCommand(message, event.getMessageEvent());
        }
    }

    public static void handleBanEvent(final ChannelBanEvent event)
    {
        final String userId = event.getUserId();
        final String userName = event.getUserName();
        final String broadcasterUserName = event.getBroadcasterUserName();

        if (userId.equals(BotConfigManager.getConfig().getRunningOnChannelId()))
        {
            if (event.isPermanent())
            {
                BotConfigManager.setBannedOnChannel(broadcasterUserName, true);
                BotConfigManager.writeConfig();
            }
            else
            {
                final Instant endsAt = event.getEndsAt();
                final Duration duration = Duration.between(event.getBannedAt(), endsAt);

                BotConfigManager.setTimedOutOnChannel(
                        broadcasterUserName,
                        LocalDateTime.ofInstant(endsAt, ZoneId.systemDefault())
                );
                BotConfigManager.writeConfig();

                LOGGER.warn("[{}] You has been timed out for {} seconnds", broadcasterUserName, duration.getSeconds());
            }
        }
        else
        {
            if (event.isPermanent())
            {
                LOGGER.info("[{}] User '{}' has been permanently banned", broadcasterUserName, userName);
            }
            else
            {
                final Duration duration = Duration.between(event.getBannedAt(), event.getEndsAt());
                LOGGER.info("[{}] User '{}' has been timed out for {} seconds", broadcasterUserName, userName, duration.getSeconds());
            }
        }
    }

    public static void handleUnbanEvent(final ChannelUnbanEvent event)
    {
        // Remove info from config
    }
}
