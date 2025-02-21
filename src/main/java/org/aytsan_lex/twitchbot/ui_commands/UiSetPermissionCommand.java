package org.aytsan_lex.twitchbot.ui_commands;

import java.util.ArrayList;

import org.java_websocket.WebSocket;

import org.aytsan_lex.twitchbot.TwitchBot;

public class UiSetPermissionCommand implements IUiCommand
{
    @Override
    public void execute(ArrayList<String> args, WebSocket client) throws UiCommandError
    {
        if (args.size() < 2)
        {
            throw new UiCommandError("Args are required!");
        }

        final String targetUserName = args.get(0);
        final int targetLevel = Integer.parseInt(args.get(1));

        final String targetUserId = TwitchBot.getTwitchChat().getChannelNameToChannelId().get(targetUserName);
        final String selfUserId = TwitchBot.getCredentialsManager().getCredentials().userId();

        final boolean targetIsNotSelf =
                !TwitchBot.getConfigManager().isOwner(targetUserName)
                && (targetUserId != null && !targetUserId.equals(selfUserId));

        if (targetIsNotSelf)
        {
            TwitchBot.getConfigManager().setPermissionLevel(targetUserName, targetLevel);
            TwitchBot.getConfigManager().saveFile();
        }
    }
}
