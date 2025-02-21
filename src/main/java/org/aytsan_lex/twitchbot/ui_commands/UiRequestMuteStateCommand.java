package org.aytsan_lex.twitchbot.ui_commands;

import java.util.ArrayList;

import org.aytsan_lex.twitchbot.TwitchBot;
import org.aytsan_lex.twitchbot.bot_commands.BenBotCommand;
import org.aytsan_lex.twitchbot.bot_commands.IqBotCommand;
import org.aytsan_lex.twitchbot.bot_commands.MiraBotCommand;
import org.java_websocket.WebSocket;

public class UiRequestMuteStateCommand implements IUiCommand
{
    @Override
    public void execute(ArrayList<String> args, WebSocket client) throws UiCommandError
    {
        if (client.isOpen())
        {
            final String muteStateData = "#mutestate///%b///%b///%b".formatted(
                    TwitchBot.getConfigManager().commandIsMuted(MiraBotCommand.class),
                    TwitchBot.getConfigManager().commandIsMuted(BenBotCommand.class),
                    TwitchBot.getConfigManager().commandIsMuted(IqBotCommand.class)
            );
            client.send(muteStateData);
        }
    }
}
