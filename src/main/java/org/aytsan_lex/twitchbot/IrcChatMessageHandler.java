package org.aytsan_lex.twitchbot;

import java.util.HashMap;
import java.util.Optional;
import java.time.LocalDateTime;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;
import com.github.twitch4j.common.events.domain.EventUser;

public class IrcChatMessageHandler
{
    private enum IrcCommandType
    {
        PRIVMSG,
        CLEARCHAT
    }

    public IrcChatMessageHandler()
    {
    }

    public void handleIrcMessage(IRCMessageEvent event)
    {
        final String commandType = event.getCommandType();

        // TODO: Handle other types of command type from Helix API
        // TODO: Handle timeout and other messages from Helix API

        try
        {
            switch (IrcCommandType.valueOf(commandType))
            {
                case PRIVMSG -> handlePrivmsgIrcCommand(event);
                case CLEARCHAT -> handleClearchatIrcCommand(event);
            }
        }
        catch (IllegalArgumentException ignored)
        {
        }
    }

    private void handlePrivmsgIrcCommand(IRCMessageEvent event)
    {
        final EventUser user = event.getUser();
        final Optional<String> optionalMessage = event.getMessage();

        if (user != null && optionalMessage.isPresent())
        {
            final String channelName = event.getChannel().getName();
            final String message = optionalMessage.get();

            // TODO: Handle command via @tag of bot
            // @bot <command> [args...]

            if (message.startsWith("%"))
            {
                if (BotConfigManager.instance().isTimedOutChannel(channelName))
                {
                    final LocalDateTime currentDateTime = LocalDateTime.now();
                    final LocalDateTime expiredIn = BotConfigManager.instance().getTimeoutExpiredIn(channelName);

                    if (currentDateTime.isBefore(expiredIn))
                    {
                        TwitchBot.LOGGER.warn("Failed to handle command on '{}': timed out", channelName);
                        return;
                    }

                    BotConfigManager.instance().removeTimedOutChannel(channelName);
                    BotConfigManager.instance().saveChanges();
                }

                CommandHandler.handleCommand(message, event);
            }
        }
    }

    private void handleClearchatIrcCommand(IRCMessageEvent event)
    {
        final String rawMessage = event.getRawMessage();
        if (rawMessage.contains("@ban-duration"))
        {
            final HashMap<String, String> values = this.parseRawMessageForClearchatCommand(rawMessage);
            final String targetId = values.get("target-id");
            if (targetId.equals(BotConfigManager.instance().getConfig().getRunningOnChannelId()))
            {
                final String channelName = event.getChannel().getName();
                final int seconds = Integer.parseInt(values.get("ban-duration"));

                final LocalDateTime expiredIn = LocalDateTime.now().plusSeconds(seconds);
                BotConfigManager.instance().setChannelIsTimedOut(channelName, expiredIn);
                BotConfigManager.instance().saveChanges();

                TwitchBot.LOGGER.warn("You`ve been timed out for {} seconds on channel: '{}'", seconds, channelName);
            }
        }
    }

    private HashMap<String, String> parseRawMessageForClearchatCommand(final String rawMessage)
    {
        // TODO: Implement parseRawMessageForClearchatCommand()
        return new HashMap<>();
    }
}
