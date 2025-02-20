package org.aytsan_lex.twitchbot.ui_commands;

import java.util.ArrayList;

import org.java_websocket.WebSocket;

import org.aytsan_lex.twitchbot.TwitchBot;

public class UiRequestFiltersCommand implements IUiCommand
{
    @Override
    public void execute(ArrayList<String> args, WebSocket client)
    {
        if (client.isOpen())
        {
            final String filtersData = TwitchBot.getFiltersManager().getMiraFilters().toJsonString();
            client.send(filtersData);
        }
    }
}
