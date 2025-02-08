package org.aytsan_lex.twitchbot;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

import org.aytsan_lex.twitchbot.commands.*;

public class CommandHandler
{
    private static class CommandContext
    {
        public final IBotCommand commandObject;
        public final IRCMessageEvent event;
        public final ArrayList<String> args;

        public CommandContext(final IBotCommand command, final IRCMessageEvent event, final ArrayList<String> args)
        {
            this.commandObject = command;
            this.event = event;
            this.args = args;
        }

        public void execute()
        {
            commandObject.execute(event, args);
        }
    }

    private static class CommandExecutor implements Runnable
    {
        @Override
        public void run()
        {
            while (true)
            {
                try
                {
                    final CommandContext context = commandQueue.take();
                    final String channelName = context.event.getChannel().getName();
                    final String userName = context.event.getUser().getName();

                    try
                    {
                        if (context.commandObject instanceof MiraBotCommand)
                        {
                            new Thread(context::execute).start();
                        }
                        else
                        {
                            context.execute();
                        }
                    }
                    catch (BotCommandError e)
                    {
                        LOGGER.error(
                                "[{}] [{}] '{}': Error: {}",
                                channelName, userName, context.commandObject.getClass().getSimpleName(), e.getMessage()
                        );
                    }
                }
                catch (InterruptedException e)
                {
                    Thread.currentThread().interrupt();
                    break;
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    private static final int COMMAND_QUEUE_SIZE = 15;
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandHandler.class);

    private static final BlockingQueue<CommandContext> commandQueue = new ArrayBlockingQueue<>(COMMAND_QUEUE_SIZE);
    private static final Thread commandExecutorThread = new Thread(new CommandExecutor(), "CommandExecutor");

    public static void initialize()
    {
        LOGGER.info("Initializing...");
        commandExecutorThread.start();
    }

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

        putCommandInQueue(event, cmd, args);
    }

    private static void putCommandInQueue(final IRCMessageEvent event,
                                          final String cmd,
                                          final ArrayList<String> cmdArgs)
    {
        final String channelName = event.getChannel().getName();
        final String userName = event.getUser().getName();
        final IBotCommand command = BotCommandsManager.getCommandByName(cmd);

        if (command != null)
        {
            LOGGER.info("[{}] [{}]: Command: '{}', args: {}", channelName, userName, cmd, cmdArgs);
            try
            {
                commandQueue.put(new CommandContext(command, event, cmdArgs));
            }
            catch (InterruptedException e)
            {
                LOGGER.warn("Interrupted: {}", e.getMessage());
            }
        }
        else
        {
            LOGGER.warn("[{}] [{}]: Invalid command: '{}'", channelName, userName, cmd);
        }
    }
}
