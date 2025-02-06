package org.aytsan_lex.twitchbot;

import java.util.Arrays;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

import org.aytsan_lex.twitchbot.commands.*;

public class CommandHandler
{
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandHandler.class);

    public static void handleCommand(final String message, final IRCMessageEvent event)
    {
        final ArrayList<String> rawMessage = new ArrayList<>(
                Arrays.asList(message.replaceFirst("^%", "").split(" "))
        );

        final String cmd = rawMessage.get(0).trim().replaceAll("\\s+", "").toLowerCase();

        handleCommandsInNewThread(
                event,
                cmd,
                (rawMessage.size() >= 2)
                        ? new ArrayList<>(rawMessage.subList(1, rawMessage.size()))
                        : new ArrayList<>()
        );
    }

    private static void handleCommandsInNewThread(final IRCMessageEvent event,
                                                  final String cmd,
                                                  final ArrayList<String> cmdArgs)
    {
        final IBotCommand command = BotCommandsManager.getCommandByName(cmd);
        if (command != null)
        {
            LOGGER.info("[{}] Command: '{}', args: {}", event.getUser().getName(), cmd, cmdArgs);
            new Thread(() -> {
                try
                {
                    command.execute(event, cmdArgs);
                }
                catch (BotCommandError e)
                {
                    LOGGER.error("{} error: {}", command.getClass().getSimpleName(), e.getMessage());
                    Thread.currentThread().interrupt();
                }
                catch (Exception e)
                {
                    LOGGER.error("Error: {}", e.getMessage());
                    Thread.currentThread().interrupt();
                }
            }).start();
        }
        else
        {
            LOGGER.warn("Invalid (or unknown) command: '{}'", cmd);
        }
    }
}
