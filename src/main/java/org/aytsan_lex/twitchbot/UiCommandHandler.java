package org.aytsan_lex.twitchbot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.java_websocket.WebSocket;

import org.aytsan_lex.twitchbot.ui_commands.IUiCommand;
import org.aytsan_lex.twitchbot.ui_commands.UiCommandError;
import org.aytsan_lex.twitchbot.ui_commands.UiRequestStatusCommand;

public class UiCommandHandler
{
    private record UiCommandContext(IUiCommand commandObject, ArrayList<String> args, WebSocket webSocket)
    {
        public void execute() { this.commandObject.execute(this.args, this.webSocket); }
    }

    private static class UiCommandsExecutor implements Runnable
    {
        @Override
        public void run()
        {
            while (true)
            {
                try
                {
                    final UiCommandContext context = uiCommandsQueue.take();

                    if (!(context.commandObject instanceof UiRequestStatusCommand))
                    {
                        // Skip log for repeating commands
                        LOG.info("Executing command: '{}'", context.commandObject.getClass().getSimpleName());
                    }

                    context.execute();
                }
                catch (InterruptedException e)
                {
                    Thread.currentThread().interrupt();
                    break;
                }
                catch (UiCommandError e)
                {
                    LOG.error("Ui command error: {}", e.getMessage());
                }
                catch (Exception e)
                {
                    LOG.error("Error: {}", e.getMessage());
                }
            }
        }
    }

    private static final int UI_COMMANDS_QUEUE_SIZE = 10;
    private static final Logger LOG = LoggerFactory.getLogger(UiCommandHandler.class);

    private static final BlockingQueue<UiCommandContext> uiCommandsQueue = new ArrayBlockingQueue<>(UI_COMMANDS_QUEUE_SIZE);
    private static Thread uiCommandsExecutionThread;

    public static void initialize()
    {
        uiCommandsExecutionThread = new Thread(new UiCommandsExecutor(), "UiCommandExecutor");
        uiCommandsExecutionThread.start();
    }

    public static void shutdown()
    {
        uiCommandsExecutionThread.interrupt();
        uiCommandsExecutionThread = null;
        uiCommandsQueue.clear();
    }

    public static void handleCommand(final WebSocket webSocketFrom, final String rawData)
    {
        final ArrayList<String> rawCmd = new ArrayList<>(Arrays.stream(rawData.split("###")).toList());

        final String cmd = rawCmd.get(0).trim().replaceFirst("^[#|/]", "");
        final IUiCommand command = TwitchBot.getUiCommandsManager().getCommandByName(cmd);

        if (command != null)
        {
            ArrayList<String> args = new ArrayList<>();

            if (rawCmd.size() >= 2)
            {
                args = rawCmd.subList(1, rawCmd.size())
                        .stream().map(String::trim).collect(Collectors.toCollection(ArrayList::new));
            }

            try
            {
                LOG.debug("Command: '{}', args: {}", cmd, args);
                uiCommandsQueue.put(new UiCommandContext(command, args, webSocketFrom));
            }
            catch (InterruptedException ignored) { }
        }
        else
        {
            LOG.error("Invalid Ui command: '{}'", cmd);
        }
    }
}
