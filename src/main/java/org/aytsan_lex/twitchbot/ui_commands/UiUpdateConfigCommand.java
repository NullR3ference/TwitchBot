package org.aytsan_lex.twitchbot.ui_commands;

import java.util.ArrayList;

import org.java_websocket.WebSocket;

import org.aytsan_lex.twitchbot.TwitchBot;

public class UiUpdateConfigCommand implements IUiCommand
{
    @Override
    public void execute(ArrayList<String> args, WebSocket client)
    {
        if (!args.isEmpty())
        {
            final String configData = args.get(0);
            TwitchBot.getConfigManager().writeData(configData);
        }
    }
}
