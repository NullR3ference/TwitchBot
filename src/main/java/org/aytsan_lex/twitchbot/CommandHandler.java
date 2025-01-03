package org.aytsan_lex.twitchbot;

import java.util.ArrayList;
import java.util.Arrays;
import org.aytsan_lex.twitchbot.botcommands.BotCommandDispatcher;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

public class CommandHandler
{
    public enum Commands
    {
        STATUS,
        LINES,
        RESTART,
    };

    public enum MultiargCommands
    {
        LINES,
        ADD,
        DEL,
        BEN,
    }

    public static void handleCommand(final String message, final IRCMessageEvent event)
    {
        final ArrayList<String> cmdArgs = new ArrayList<>(
                Arrays.asList(message.replaceFirst("^%", "").split(" "))
        );

        final String cmd = cmdArgs.get(0).toUpperCase();

        if (cmdArgs.size() > 1)
        {
            cmdArgs.remove(0);
            BotCommandDispatcher.dispatch(cmd, cmdArgs, event);
        }
        else
        {
            BotCommandDispatcher.dispatch(cmd, event);
        }
    }

    private static String handleMultiargCommand(final ArrayList<String> cmdArgs,
                                                final String userId,
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
            }
        }
        catch (IllegalArgumentException e)
        {
            return "Unknown command: '%s'".formatted(cmd.toLowerCase());
        }

        return "";
    }
}
