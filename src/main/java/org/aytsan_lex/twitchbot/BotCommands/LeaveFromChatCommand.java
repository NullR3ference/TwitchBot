package org.aytsan_lex.twitchbot.BotCommands;

import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

public class LeaveFromChatCommand extends BotCommandBase
{
    public LeaveFromChatCommand()
    {
        super(4);
    }

    @Override
    public void execute(Object... args)
    {
        if (!(args[0] instanceof String channelName) || !(args[1] instanceof IRCMessageEvent event))
        {
            throw new BotCommandError("Invalid args classes");
        }
    }
}
