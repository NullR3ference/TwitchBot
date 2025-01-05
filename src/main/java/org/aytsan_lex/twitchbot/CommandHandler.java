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
        EH,
    };

    public enum MultiargCommands
    {
        LINES,
        ADD,
        DEL,
        BEN,
    }

    public static void handleCommand(final String message,
                                     final IRCMessageEvent event,
                                     final IrcChatMessageHandler ircMessageHandler)
    {
        final ArrayList<String> cmdArgs = new ArrayList<>(
                Arrays.asList(message.replaceFirst("^%", "").split(" "))
        );

        System.out.println("message: " + message);
        System.out.println("cmdArgs: " + cmdArgs);

        final String cmd = cmdArgs.get(0).toUpperCase();

        if (cmdArgs.size() > 1)
        {
            cmdArgs.remove(0);
            BotCommandDispatcher.dispatch(cmd, cmdArgs, event, ircMessageHandler);
        }
        else
        {
            BotCommandDispatcher.dispatch(cmd, event, ircMessageHandler);
        }
    }
}
