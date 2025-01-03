package org.aytsan_lex.twitchbot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

public class CommandHandler
{
    private enum Commands
    {
        STATUS,
        LINES,
    };

    private enum MultiargCommands
    {
        LINES,
        ADD,
        DEL,
        RAND
    }

    public static void handleCommand(final String channelName,
                                     final String userId,
                                     final String userName,
                                     final String message,
                                     final IRCMessageEvent event,
                                     final IrcChatMessageHandler ircMessageHandler)
    {
        final ArrayList<String> cmdArgs = new ArrayList<>(
                Arrays.asList(message.replaceFirst("^%", "").split(" "))
        );

        String replyMessage = "";

        if (cmdArgs.size() > 1)
        {
            replyMessage = handleMultiargCommand(cmdArgs, ircMessageHandler);
        }
        else
        {
            replyMessage = handleSingleargCommand(
                    cmdArgs.get(0).toUpperCase(),
                    ircMessageHandler.getLogger(channelName)
            );
        }

        if (!replyMessage.isEmpty())
        {
            if (!event.getChannel().getId().equals(userId))
            {
                try { TimeUnit.MILLISECONDS.sleep(1100); }
                catch (InterruptedException e) { }
            }

            event.getTwitchChat().sendMessage(
                    channelName,
                    replyMessage,
                    null,
                    event.getMessageId().get()
            );
        }
    }

    private static String handleSingleargCommand(final String cmd,
                                                 final ChannelMessageLogger logger)
    {
        try
        {
            switch (Commands.valueOf(cmd))
            {
                case STATUS -> {
                    return "Active channels: " + BotConfig.instance().getChannels().size();
                }

                case LINES -> {
                    if (logger != null)
                    {
                        return "Current log lines: " + logger.getCurrentLines();
                    }
                }
            }
        }
        catch (IllegalArgumentException e)
        {
            return "Unknown command: '%s'".formatted(cmd.toLowerCase());
        }

        return "";
    }

    private static String handleMultiargCommand(final ArrayList<String> cmdArgs,
                                                final IrcChatMessageHandler ircMessageHandler)
    {
        final String cmd = cmdArgs.get(0).toUpperCase();

        try
        {
            switch (MultiargCommands.valueOf(cmd))
            {
                case LINES -> {
                    final String channelName = cmdArgs.get(1);
                    if (BotConfig.instance().getChannels().contains(channelName))
                    {
                        return "Log lines for [%s]: %d"
                                .formatted(channelName, ircMessageHandler.getLogger(channelName).getCurrentLines());
                    }
                    else
                    {
                        return "No logs for channel: " + channelName;
                    }
                }
                case ADD -> {
                    final String newChannelName = cmdArgs.get(1);
                    if (TwitchBot.instance().joinToChat(newChannelName))
                    {
                        if (BotConfig.instance().addChannel(newChannelName))
                        {
                            ircMessageHandler.addLogger(newChannelName);
                            BotConfig.instance().saveChanges();
                            return "Channel added [%s]".formatted(newChannelName);
                        }
                        else
                        {
                            return "Channel [%s] already added".formatted(newChannelName);
                        }
                    }
                }
                case DEL -> {
                    final String channelName = cmdArgs.get(1);
                    if (TwitchBot.instance().leaveFromChat(channelName))
                    {
                        if (BotConfig.instance().removeChannel(channelName))
                        {
                            ircMessageHandler.removeLogger(channelName);
                            BotConfig.instance().saveChanges();
                            return "Channel removed [%s]".formatted(channelName);
                        }
                        else
                        {
                            return "Channel [%s] already removed".formatted(channelName);
                        }
                    }
                }

                case RAND -> {
                    if (cmdArgs.size() < 3)
                    {
                        return "Required the second argument: %rand <start> <end>";
                    }

                    final int start = Integer.parseInt(cmdArgs.get(1));
                    final int end = Integer.parseInt(cmdArgs.get(2));

                    if (start == end)
                    {
                        return "Args required to be '%d' != '%d'".formatted(start, end);
                    }

                    return "Random: " + new Random().nextInt(start, end);
                }
            }
        }
        catch (IllegalArgumentException e)
        {
            return "Unknown command: '%s'".formatted(cmd.toLowerCase());
        }

        return "";
    }
}
