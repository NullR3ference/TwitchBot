package org.aytsan_lex.twitchbot.ui_commands;

import java.util.ArrayList;

import org.aytsan_lex.twitchbot.TwitchBot;
import org.java_websocket.WebSocket;

public class UiSendMessageCommand implements IUiCommand
{
    @Override
    public void execute(final ArrayList<String> args, final WebSocket client) throws UiCommandError
    {
        if (args.size() < 2)
        {
            throw new UiCommandError("Args are required!");
        }

        final String channelName = args.get(0).trim();
        final String message = args.get(1).trim();

        if (TwitchBot.isConnectedToChat(channelName))
        {
            TwitchBot.sendMessage(channelName, message);
        }
    }
}
