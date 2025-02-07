package org.aytsan_lex.twitchbot;

import java.util.HashMap;
import java.util.Optional;
import java.time.LocalDateTime;
import com.github.twitch4j.common.events.domain.EventUser;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

public class IrcChatMessageHandler
{
    private enum IrcCommandType
    {
        PRIVMSG,
        CLEARCHAT
    }

    public static void handleIrcMessage(IRCMessageEvent event)
    {
        final String commandType = event.getCommandType();
        try
        {
            switch (IrcCommandType.valueOf(commandType))
            {
                case PRIVMSG -> handlePrivmsgIrcCommand(event);
                case CLEARCHAT -> handleClearchatIrcCommand(event);
            }
        }
        catch (IllegalArgumentException ignored) { }
    }

    private static void handlePrivmsgIrcCommand(IRCMessageEvent event)
    {
//        final EventUser user = event.getUser();
//        final Optional<String> optionalMessage = event.getMessage();
//
//        if (user != null && optionalMessage.isPresent())
//        {
//            final String channelName = event.getChannel().getName();
//            final String message = optionalMessage.get();
//
//            // TODO: Handle command via @tag of bot
//            // @bot <command> [args...]
//
//            if (message.startsWith("%"))
//            {
//                if (BotConfigManager.isTimedOutChannel(channelName))
//                {
//                    final LocalDateTime currentDateTime = LocalDateTime.now();
//                    final LocalDateTime expiredIn = BotConfigManager.getTimeoutExpiredIn(channelName);
//
//                    if (currentDateTime.isBefore(expiredIn))
//                    {
//                        TwitchBot.LOGGER.warn("Failed to handle command on '{}': timed out", channelName);
//                        return;
//                    }
//
//                    BotConfigManager.removeTimedOutChannel(channelName);
//                    BotConfigManager.writeConfig();
//                }
//
//                CommandHandler.handleCommand(message, event);
//            }
//        }
    }

    private static void handleClearchatIrcCommand(IRCMessageEvent event)
    {
        final String rawMessage = event.getRawMessage();
        if (rawMessage.contains("@ban-duration"))
        {
            final HashMap<String, String> values = parseRawMessageForClearchatCommand(rawMessage);
            final String targetId = values.get("target-id");

            if (BotConfigManager.getConfig().getRunningOnChannelId().equals(targetId))
            {
                final String channelName = event.getChannel().getName();
                final int seconds = Integer.parseInt(values.get("ban-duration"));

                final LocalDateTime expiredIn = LocalDateTime.now().plusSeconds(seconds);
                BotConfigManager.setTimedOutOnChannel(channelName, expiredIn);
                BotConfigManager.writeConfig();

                TwitchBot.LOGGER.warn("You`ve been timed out for {} seconds on channel: '{}'", seconds, channelName);
            }
            // targetId == null ~ banned
        }
    }

    private static HashMap<String, String> parseRawMessageForClearchatCommand(final String rawMessage)
    {
        // TODO: Implement parseRawMessageForClearchatCommand()
        return new HashMap<>();
    }
}
