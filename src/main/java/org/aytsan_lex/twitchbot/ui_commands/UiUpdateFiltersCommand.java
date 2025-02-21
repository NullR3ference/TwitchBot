package org.aytsan_lex.twitchbot.ui_commands;

import java.util.ArrayList;

import com.google.gson.Gson;
import org.java_websocket.WebSocket;

import org.aytsan_lex.twitchbot.filters.MiraFilters;
import org.aytsan_lex.twitchbot.TwitchBot;

public class UiUpdateFiltersCommand implements IUiCommand
{
    @Override
    public void execute(ArrayList<String> args, WebSocket client) throws UiCommandError
    {
        if (!args.isEmpty())
        {
            final String filtersData = args.get(0);
            final MiraFilters.Adapter adapter = new Gson().fromJson(filtersData, MiraFilters.Adapter.class);
            final MiraFilters newMiraFilters = MiraFilters.fromAdapter(adapter);
            TwitchBot.getFiltersManager().writeAndUpdate(newMiraFilters);
        }
        else
        {
            throw new UiCommandError("Args are required!");
        }
    }
}
