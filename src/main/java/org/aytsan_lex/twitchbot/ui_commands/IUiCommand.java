package org.aytsan_lex.twitchbot.ui_commands;

import java.util.ArrayList;

import org.java_websocket.WebSocket;

public interface IUiCommand
{
    void execute(ArrayList<String> args, WebSocket client) throws UiCommandError;
}
