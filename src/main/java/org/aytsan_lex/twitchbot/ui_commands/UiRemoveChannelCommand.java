package org.aytsan_lex.twitchbot.ui_commands;

import java.util.ArrayList;

import org.java_websocket.WebSocket;

import org.aytsan_lex.twitchbot.TwitchBot;

public class UiRemoveChannelCommand implements IUiCommand
{
    @Override
    public void execute(ArrayList<String> args, WebSocket client)
    {
        if (!args.isEmpty())
        {
            final String targetChannelName = args.get(0);
            final String targetChannelId = TwitchBot.getTwitchChat().getChannelNameToChannelId().get(targetChannelName);

            if (targetChannelId != null)
            {
                if (TwitchBot.channelExists(targetChannelName) &&
                    TwitchBot.getConfigManager().removeChannel(targetChannelName))
                {
                    TwitchBot.leaveFromChat(targetChannelName);
                    TwitchBot.getConfigManager().removeChannel(targetChannelName);
                    TwitchBot.getConfigManager().saveFile();
                }
            }
        }
    }
}
