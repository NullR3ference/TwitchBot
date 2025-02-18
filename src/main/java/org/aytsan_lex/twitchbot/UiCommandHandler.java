package org.aytsan_lex.twitchbot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.java_websocket.WebSocket;

import org.aytsan_lex.twitchbot.ui_commands.IUiCommand;

public class UiCommandHandler
{
    private static final Logger LOG = LoggerFactory.getLogger(UiCommandHandler.class);

    public static void handleCommand(final WebSocket webSocketFrom, final String rawData)
    {
        final ArrayList<String> rawCmd = new ArrayList<>(Arrays.stream(rawData.split("///")).toList());

        final String cmd = rawCmd.get(0).trim();
        ArrayList<String> args = new ArrayList<>();

        if (rawCmd.size() >= 2)
        {
            args = rawCmd.subList(1, rawCmd.size())
                    .stream().map(String::trim).collect(Collectors.toCollection(ArrayList::new));
        }

        final IUiCommand command = TwitchBot.getUiCommandsManager().getCommandByName(cmd);

        if (command != null)
        {
            command.execute(args, webSocketFrom);
        }
        else
        {
            LOG.error("Invalid Ui command: '{}'", command);
        }
    }
}
