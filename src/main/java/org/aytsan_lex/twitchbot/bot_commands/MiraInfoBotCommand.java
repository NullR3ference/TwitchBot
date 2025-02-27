package org.aytsan_lex.twitchbot.bot_commands;

import java.util.ArrayList;

import com.github.twitch4j.chat.events.channel.IRCMessageEvent;
import org.aytsan_lex.twitchbot.TwitchBot;

public class MiraInfoBotCommand extends BotCommandBase
{
    @Override
    public void execute(final IRCMessageEvent event, final ArrayList<String> args)
    {
        final String userName = event.getUser().getName();
        final int userLevel = TwitchBot.getConfigManager().getPermissionLevel(userName);

        if (userLevel >= super.getRequiredPermissionLevel())
        {
            final String params = TwitchBot.getOllamaModelsManager().getMiraModel().getParams().toString();
            TwitchBot.sendMessage(event.getChannel().getName(), params);
        }
        else
        {
            TwitchBot.LOG.warn("{}: permission denied: {}/{}", userName, userLevel, super.getRequiredPermissionLevel());
        }
    }
}
