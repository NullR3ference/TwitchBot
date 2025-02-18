package org.aytsan_lex.twitchbot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

import org.aytsan_lex.twitchbot.bot_commands.IBotCommand;
import org.aytsan_lex.twitchbot.bot_commands.MiraBotCommand;
import org.aytsan_lex.twitchbot.bot_commands.BotCommandError;

public class BotCommandHandler
{
    public static class CommandContext
    {
        private final IBotCommand commandObject;
        private final IRCMessageEvent event;
        private final ArrayList<String> args;

        public CommandContext(final IBotCommand command,
                              final IRCMessageEvent event,
                              final ArrayList<String> args)
        {
            this.commandObject = command;
            this.event = event;
            this.args = args;
        }

        public IBotCommand getCommandObject()
        {
            return this.commandObject;
        }

        public IRCMessageEvent getEvent()
        {
            return this.event;
        }

        public ArrayList<String> getArgs()
        {
            return this.args;
        }

        public void execute()
        {
            commandObject.execute(event, args);
        }

        public int getPriority()
        {
            return TwitchBot.getConfigManager().getPermissionLevel(this.event.getUser().getName());
        }
    }

    private static class CommandPriorityComparator implements Comparator<CommandContext>
    {
        @Override
        public int compare(final CommandContext lhs, final CommandContext rhs)
        {
            return Integer.compare(rhs.getPriority(), lhs.getPriority());
        }
    }

    private static class CommandExecutor implements Runnable
    {
        private CommandContext currentContext = null;

        @Override
        public void run()
        {
            if (this.currentContext != null)
            {
                try
                {
                    currentContext.execute();
                }
                catch (BotCommandError e)
                {
                    final String channelName = currentContext.getEvent().getChannel().getName();
                    final String userName = currentContext.getEvent().getUser().getName();

                    LOG.error(
                            "[{}] [{}] '{}': Error: {}",
                            channelName, userName, currentContext.getCommandObject().getClass().getSimpleName(), e.getMessage()
                    );
                }
                this.currentContext = null;
            }
        }

        public void setCurrentContext(final CommandContext context)
        {
            this.currentContext = context;
        }
    }

    private static class BotCommandExecutor extends CommandExecutor
    {
        @Override
        public void run()
        {
            while (true)
            {
                try
                {
                    final CommandContext commandContext = botCommandQueue.take();

                    LOG.info(
                            "Executing '{}' with priority: {}; in queue: {}",
                            commandContext.getCommandObject().getClass().getSimpleName(),
                            commandContext.getPriority(),
                            botCommandQueue.size()
                    );

                    super.setCurrentContext(commandContext);
                    super.run();
                }
                catch (Exception e)
                {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    private static class MiraCommandExecutor extends CommandExecutor
    {
        @Override
        public void run()
        {
            while (true)
            {
                try
                {
                    final CommandContext commandContext = miraCommandQueue.take();

                    LOG.info(
                            "Executing Mira command with priority: {}; in queue: {}",
                            commandContext.getPriority(),
                            miraCommandQueue.size()
                    );

                    super.setCurrentContext(commandContext);
                    super.run();
                }
                catch (Exception e)
                {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    private static final Logger LOG = LoggerFactory.getLogger(BotCommandHandler.class);

    private static final int BOT_COMMAND_QUEUE_SIZE = 10;
    private static final int MIRA_COMMANDS_QUEUE_SIZE = 5;
    private static final Comparator<CommandContext> commandPriorityComparator = new CommandPriorityComparator();

    private static final BlockingQueue<CommandContext> botCommandQueue = new PriorityBlockingQueue<>(
            BOT_COMMAND_QUEUE_SIZE, commandPriorityComparator
    );

    private static final BlockingQueue<CommandContext> miraCommandQueue = new PriorityBlockingQueue<>(
            MIRA_COMMANDS_QUEUE_SIZE, commandPriorityComparator
    );

    private static Thread botCommandExecutorThread = null;
    private static Thread miraCommandExecutorThread = null;

    public static void initialize()
    {
        LOG.info("Initializing...");

        botCommandExecutorThread = new Thread(new BotCommandExecutor(), "BotCommandExecutor");
        miraCommandExecutorThread = new Thread(new MiraCommandExecutor(), "MiraCommandExecutor");

        botCommandExecutorThread.start();
        miraCommandExecutorThread.start();
    }

    public static void shutdown()
    {
        LOG.info("Shutting down...");

        botCommandExecutorThread.interrupt();
        miraCommandExecutorThread.interrupt();

        botCommandQueue.clear();
        miraCommandQueue.clear();
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

    public static BlockingQueue<CommandContext> getBotCommandQueue()
    {
        return botCommandQueue;
    }

    public static BlockingQueue<CommandContext> getMiraCommandQueue()
    {
        return miraCommandQueue;
    }

    private static void putCommandInQueue(final IRCMessageEvent event,
                                          final String cmd,
                                          final ArrayList<String> cmdArgs)
    {
        final String channelName = event.getChannel().getName();
        final String userName = event.getUser().getName();
        final IBotCommand command = TwitchBot.getBotCommandsManager().getCommandByName(cmd);

        if (command != null)
        {
            LOG.info("[{}] [{}]: Command: '{}', args: {}", channelName, userName, cmd, cmdArgs);
            final CommandContext commandContext = new CommandContext(command, event, cmdArgs);

            try
            {
                boolean commandAccepted = false;

                if (command instanceof MiraBotCommand)
                {
                    if (miraCommandQueue.size() < MIRA_COMMANDS_QUEUE_SIZE)
                    {
                        miraCommandQueue.put(commandContext);
                        commandAccepted = true;
                    }
                }
                else if (botCommandQueue.size() < BOT_COMMAND_QUEUE_SIZE)
                {
                    botCommandQueue.put(commandContext);
                    commandAccepted = true;
                }

                if (!commandAccepted)
                {
                    LOG.warn("Command rejected: queue is full");
                }
            }
            catch (InterruptedException e)
            {
                LOG.warn("[{}] [{}]: Command: '{}': {}", channelName, userName, cmd, e.getMessage());
            }
        }
        else
        {
            LOG.warn("[{}] [{}]: Invalid command: '{}'", channelName, userName, cmd);
        }
    }
}
