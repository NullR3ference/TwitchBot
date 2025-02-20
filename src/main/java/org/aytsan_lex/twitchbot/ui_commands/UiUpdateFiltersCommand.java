package org.aytsan_lex.twitchbot.ui_commands;

import java.util.ArrayList;

import org.java_websocket.WebSocket;

import org.aytsan_lex.twitchbot.TwitchBot;

public class UiUpdateFiltersCommand implements IUiCommand
{
    @Override
    public void execute(ArrayList<String> args, WebSocket client)
    {
        if (!args.isEmpty())
        {
            final String filtersData = args.get(0);
            TwitchBot.getFiltersManager().writeData(filtersData);
            TwitchBot.getFiltersManager().readFile();
        }
    }
}
