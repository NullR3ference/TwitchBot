package org.aytsan_lex.twitchbot.ui_commands;

import java.util.ArrayList;

import org.java_websocket.WebSocket;

import org.aytsan_lex.twitchbot.TwitchBot;

public class UiMuteCommand implements IUiCommand
{
    @Override
    public void execute(ArrayList<String> args, WebSocket client)
    {
        if (args.size() < 2)
        {
            return;
        }

        final String commandToMute = args.get(0);
        final var commandClass = TwitchBot.getBotCommandsManager().getCommandClassByName(commandToMute);

        if (commandClass != null)
        {
            final boolean isMuted = Boolean.parseBoolean(args.get(1));
            TwitchBot.getBotCommandsManager().setCommandIsMuted(commandClass, isMuted);
        }
    }
}
