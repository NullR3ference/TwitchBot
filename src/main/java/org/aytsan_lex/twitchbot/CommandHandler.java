package org.aytsan_lex.twitchbot;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
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

        public int getPriority()
        {
            return BotConfigManager.getPermissionLevel(this.event.getUser().getName());
        }
    }

    private static class CommandContextComparator implements Comparator<CommandContext>
    {
        @Override
        public int compare(CommandContext lhs, CommandContext rhs)
        {
            return Integer.compare(rhs.getPriority(), lhs.getPriority());
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
                    final CommandContext commandContext = commandQueue.take();
                    try
                    {
                        LOGGER.info(
                                "Taken command with priority: {}, in queue: {}",
                                commandContext.getPriority(), commandQueue.size()
                        );

                        if (commandContext.commandObject instanceof MiraBotCommand)
                        {
                            new Thread(commandContext::execute).start();
                        }
                        else
                        {
                            commandContext.execute();
                        }
                    }
                    catch (BotCommandError e)
                    {
                        final String channelName = commandContext.event.getChannel().getName();
                        final String userName = commandContext.event.getUser().getName();

                        LOGGER.error(
                                "[{}] [{}] '{}': Error: {}",
                                channelName, userName, commandContext.commandObject.getClass().getSimpleName(), e.getMessage()
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

    private static final int COMMAND_QUEUE_SIZE = 30;
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandHandler.class);

    private static Thread commandExecutorThread = null;

    private static final BlockingQueue<CommandContext> commandQueue = new PriorityBlockingQueue<>(
            COMMAND_QUEUE_SIZE,
            new CommandContextComparator()
    );

    public static void initialize()
    {
        LOGGER.info("Initializing...");

        commandExecutorThread = new Thread(new CommandExecutor(), "CommandExecutor");
        commandExecutorThread.start();
    }

    public static void shutdown()
    {
        commandExecutorThread.interrupt();
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
            final CommandContext commandContext = new CommandContext(command, event, cmdArgs);
            if (!commandQueue.contains(commandContext))
            {
                try
                {
                    commandQueue.put(commandContext);
                    LOGGER.info("[{}] [{}]: Command: '{}', args: {}", channelName, userName, cmd, cmdArgs);
                }
                catch (InterruptedException e)
                {
                }
            }
        }
        else
        {
            LOGGER.warn("[{}] [{}]: Invalid command: '{}'", channelName, userName, cmd);
        }
    }
}
