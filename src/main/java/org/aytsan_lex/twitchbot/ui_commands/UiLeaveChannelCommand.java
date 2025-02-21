package org.aytsan_lex.twitchbot.ui_commands;

import java.util.ArrayList;

import org.java_websocket.WebSocket;

import org.aytsan_lex.twitchbot.TwitchBot;

public class UiLeaveChannelCommand implements IUiCommand
{
    @Override
    public void execute(ArrayList<String> args, WebSocket client) throws UiCommandError
    {
        if (!args.isEmpty())
        {
            final String targetChannel = args.get(0);
            if (TwitchBot.channelExists(targetChannel) && TwitchBot.isConnectedToChat(targetChannel))
            {
                TwitchBot.leaveFromChat(targetChannel);
            }
        }
        else
        {
            throw new UiCommandError("Args are required!");
        }
    }
}
