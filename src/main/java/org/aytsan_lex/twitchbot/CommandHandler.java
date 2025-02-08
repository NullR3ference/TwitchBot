package org.aytsan_lex.twitchbot;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.stream.Collectors;

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
        ArrayList<String> args = new ArrayList<>();

        if (rawMessage.size() >= 2)
        {
            args = rawMessage.subList(1, rawMessage.size())
                    .stream()
                    .map(String::trim)
                    .map(s -> s.replaceAll("\\s+", ""))
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        handleCommandsInNewThread(event, cmd, args);
    }

    private static void handleCommandsInNewThread(final IRCMessageEvent event,
                                                  final String cmd,
                                                  final ArrayList<String> cmdArgs)
    {
        final String channelName = event.getChannel().getName();
        final String userName = event.getUser().getName();
        final IBotCommand command = BotCommandsManager.getCommandByName(cmd);

        if (command != null)
        {
            LOGGER.info("[{}] [{}]: Command: '{}', args: {}", channelName, userName, cmd, cmdArgs);

            new Thread(() -> {
                try
                {
                    command.execute(event, cmdArgs);
                }
                catch (BotCommandError e)
                {
                    LOGGER.error(
                            "[{}] [{}] '{}': Error: {}",
                            channelName, userName, command.getClass().getSimpleName(), e.getMessage()
                    );
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
            LOGGER.warn("[{}] [{}]: Invalid command: '{}'", channelName, userName, cmd);
        }
    }
}
