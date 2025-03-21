package org.aytsan_lex.twitchbot;

import java.util.Optional;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.twitch4j.common.events.domain.EventUser;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

public class IrcMessageHandler
{
    private static final Logger LOG = LoggerFactory.getLogger(IrcMessageHandler.class);

    private enum IrcCommandType
    {
        // https://dev.twitch.tv/docs/chat/irc/#irc-command-reference
        PRIVMSG,
        CLEARCHAT,
        PART
    }

    public static void handleIrcMessage(final IRCMessageEvent event)
    {
        try
        {
            switch (IrcCommandType.valueOf(event.getCommandType()))
            {
                case PRIVMSG -> handlePrivmsgIrcCommand(event);
                case CLEARCHAT -> handleClearchatIrcCommand(event);
                case PART -> handlePartIrcCommand(event);
            }
        }
        catch (IllegalArgumentException ignored)
        { }
    }

    private static void handlePrivmsgIrcCommand(final IRCMessageEvent event)
    {
        // TODO: Handle command via @tag of bot
        // @bot <command> [args...]

        final EventUser user = event.getUser();
        final Optional<String> eventMessage = event.getMessage();

        if (user != null && eventMessage.isPresent())
        {
            final String message = eventMessage.get();
            if (message.startsWith("%"))
            {
                BotCommandHandler.handleCommand(message, event);
            }
        }
    }

    private static void handleClearchatIrcCommand(final IRCMessageEvent event)
    {
        final Optional<String> banDurationTag = event.getTagValue("ban-duration");
        final Optional<String> targetUserIdTag = event.getTagValue("target-user-id");

        if (banDurationTag.isPresent() && targetUserIdTag.isPresent())
        {
            final String channelName = event.getChannel().getName();
            final String targetUserId = targetUserIdTag.get();
            final int banDuration = Integer.parseInt(banDurationTag.get());

            if (targetUserId.equals(TwitchBot.getCredentialsManager().getCredentials().userId()))
            {
                TwitchBot.getConfigManager().setIsTimedOutOnChannel(
                        channelName,
                        LocalDateTime.now().plusSeconds(banDuration)
                );
                TwitchBot.getConfigManager().saveFile();

                LOG.warn("[{}] You`ve been timed out for {} seconds", channelName, banDuration);
            }
        }
    }

    private static void handlePartIrcCommand(final IRCMessageEvent event)
    {
    }
}
