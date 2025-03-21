package org.aytsan_lex.twitchbot.ui_commands;

import java.util.ArrayList;

import org.java_websocket.WebSocket;

public class UiRestartCommand implements IUiCommand
{
    @Override
    public void execute(ArrayList<String> args, WebSocket client) throws UiCommandError
    {
        System.exit(10);
    }
}
