package org.aytsan_lex.twitchbot.botcommands;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import com.github.twitch4j.chat.TwitchChat;
import org.aytsan_lex.twitchbot.CommandHandler;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;
import org.aytsan_lex.twitchbot.IrcChatMessageHandler;

public class BotCommandDispatcher
{
    public static void dispatch(final String cmd,
                                final IRCMessageEvent messageEvent,
                                final IrcChatMessageHandler ircMessageHandler)
    {
        try
        {
            switch (CommandHandler.Commands.valueOf(cmd))
            {
                case STATUS -> new StatusBotCommand().execute(messageEvent);
                case LINES -> new LinesBotCommand().execute(messageEvent, ircMessageHandler);

                case IQ -> {
                    new IqBotCommand().execute("", messageEvent);
                }
            }
        }
        catch (IllegalArgumentException e)
        {
            BotCommandDispatcher.sendMessage(
                    messageEvent.getChannel().getName(),
                    messageEvent.getUser().getId(),
                    messageEvent.getMessageId().get(),
                    messageEvent.getTwitchChat(),
                    "Неизвестная команда: '%s'".formatted(cmd)
            );
            System.err.println("Unknown command '%s'".formatted(cmd));
        }
        catch (BotCommandError e)
        {
            BotCommandDispatcher.sendMessage(
                    messageEvent.getChannel().getName(),
                    messageEvent.getUser().getId(),
                    messageEvent.getMessageId().get(),
                    messageEvent.getTwitchChat(),
                    "Ошкибка команды '%s': %s".formatted(cmd, e.getMessage())
            );
            System.err.println("Bot command error '%s': %s".formatted(cmd, e.getMessage()));
        }
    }

    public static void dispatch(final String cmd,
                                final ArrayList<String> args,
                                final IRCMessageEvent messageEvent,
                                final IrcChatMessageHandler ircMessageHandler)
    {
        try
        {
            switch (CommandHandler.MultiargCommands.valueOf(cmd))
            {
                case BEN -> {
                    final String messageText = String.join(" ", args);
                    new BenBotCommand().execute(messageText, messageEvent);
                }

                case MIRA -> {
                    final String messageText = String.join(" ", args);
                    new MiraBotCommand().execute(messageText, messageEvent);
                }

                case IQ -> {
                    final String messageText = String.join(" ", args);
                    new IqBotCommand().execute(messageText, messageEvent);
                }

                case ADD -> new AddBotCommand().execute(messageEvent, args.get(0), ircMessageHandler);
                case DEL -> new DelBotCommand().execute(messageEvent, args.get(0), ircMessageHandler);
            }
        }
        catch (IllegalArgumentException e)
        {
            BotCommandDispatcher.sendMessage(
                    messageEvent.getChannel().getName(),
                    messageEvent.getUser().getId(),
                    messageEvent.getMessageId().get(),
                    messageEvent.getTwitchChat(),
                    "Неизвестная команда: '%s'".formatted(cmd)
            );
            System.err.println("Unknown command '%s'".formatted(cmd));
        }
        catch (BotCommandError e)
        {
            BotCommandDispatcher.sendMessage(
                    messageEvent.getChannel().getName(),
                    messageEvent.getUser().getId(),
                    messageEvent.getMessageId().get(),
                    messageEvent.getTwitchChat(),
                    "Ошкибка команды '%s': %s".formatted(cmd, e.getMessage())
            );
            System.err.println("Bot command error '%s': %s".formatted(cmd, e.getMessage()));
        }
    }

    private static void sendMessage(String channelName,
                                    String userId,
                                    String messageId,
                                    TwitchChat chat,
                                    String message)
    {
        final String channelId = chat.getChannelNameToChannelId().get(channelName);

        if (!channelId.equals(userId))
        {
            try { TimeUnit.MILLISECONDS.sleep(1100); }
            catch (InterruptedException e) { }
        }

        chat.sendMessage(channelName, message,null, messageId);
    }
}
