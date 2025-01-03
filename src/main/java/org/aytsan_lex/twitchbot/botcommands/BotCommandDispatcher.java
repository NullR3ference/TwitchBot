package org.aytsan_lex.twitchbot.botcommands;

import java.util.ArrayList;
import org.aytsan_lex.twitchbot.CommandHandler;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

public class BotCommandDispatcher
{
    public static void dispatch(final String cmd, final IRCMessageEvent messageEvent)
    {
        try
        {
            int returnCode = 0;
            switch (CommandHandler.Commands.valueOf(cmd))
            {
                case STATUS -> returnCode = new StatusBotCommand().execute(messageEvent);
            }
        }
        catch (IllegalArgumentException e)
        {
            e.printStackTrace();
        }
    }

    public static void dispatch(final String cmd, final ArrayList<String> args, final IRCMessageEvent messageEvent)
    {
        try
        {
            int returnCode = 0;
            switch (CommandHandler.MultiargCommands.valueOf(cmd))
            {
                case BEN -> returnCode = new BenBotCommand().execute(args.get(0), messageEvent);
            }
        }
        catch (IllegalArgumentException e)
        {
            e.printStackTrace();
        }
    }
}
