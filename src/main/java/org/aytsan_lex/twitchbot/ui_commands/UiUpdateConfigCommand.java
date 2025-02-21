package org.aytsan_lex.twitchbot.ui_commands;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.aytsan_lex.twitchbot.managers.BotConfig;
import org.java_websocket.WebSocket;

import org.aytsan_lex.twitchbot.TwitchBot;

public class UiUpdateConfigCommand implements IUiCommand
{
    @Override
    public void execute(ArrayList<String> args, WebSocket client) throws UiCommandError
    {
        if (!args.isEmpty())
        {
            final String configData = args.get(0);
            final BotConfig parsedConfig = new Gson().fromJson(configData, BotConfig.class);
            TwitchBot.getConfigManager().writeAndUpdate(parsedConfig);
        }
        else
        {
            throw new UiCommandError("Args are required!");
        }
    }
}
