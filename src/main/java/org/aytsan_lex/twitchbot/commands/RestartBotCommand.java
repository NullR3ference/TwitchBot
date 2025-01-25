package org.aytsan_lex.twitchbot.commands;

import org.aytsan_lex.twitchbot.BotConfig;
import org.aytsan_lex.twitchbot.TwitchBot;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

public class RestartBotCommand extends BotCommandBase
{
    public RestartBotCommand()
    {
        super(777);
    }

    @Override
    public void execute(Object... args)
    {
        if (!(args[0] instanceof IRCMessageEvent event))
        {
            throw new BotCommandError("Invalid args classes");
        }

        final String userName = event.getUser().getName();
        final int permissionLevel = BotConfig.instance().getPermissionLevel(userName);

        if (permissionLevel >= this.getRequiredPermissionLevel())
        {
            // Assumes that user runs this code via JVM and not over Gradle
            // Because Gradle returns 1
            System.exit(10);
        }
        else
        {
            TwitchBot.LOGGER.warn("{}: permission denied: {}/{}", userName, permissionLevel, super.getRequiredPermissionLevel());
        }
    }
}
