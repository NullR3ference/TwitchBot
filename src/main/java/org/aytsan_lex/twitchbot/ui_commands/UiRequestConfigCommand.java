package org.aytsan_lex.twitchbot.ui_commands;

import java.util.ArrayList;

import org.java_websocket.WebSocket;

import org.aytsan_lex.twitchbot.TwitchBot;

public class UiRequestConfigCommand implements IUiCommand
{
    @Override
    public void execute(ArrayList<String> args, WebSocket client)
    {
        final String configData = "#config///" + TwitchBot.getConfigManager().getConfig().toJsonString();
        client.send(configData);
    }
}
