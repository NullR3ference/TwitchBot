package org.aytsan_lex.twitchbot.ui_commands;

import java.util.ArrayList;

import org.java_websocket.WebSocket;

public class UiSendMessageCommand implements IUiCommand
{
    @Override
    public void execute(final ArrayList<String> args, final WebSocket client)
    {
        if (args.size() < 2)
        {
            return;
        }

        final String channelName = args.get(0);
        final String message = args.get(1);
    }
}
