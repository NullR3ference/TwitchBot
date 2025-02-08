package org.aytsan_lex.twitchbot.commands;

import java.util.ArrayList;
import java.util.Random;

import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

import org.aytsan_lex.twitchbot.TwitchBot;

public class BenBotCommand extends BotCommandBase
{
    @Override
    public void execute(final IRCMessageEvent event, final ArrayList<String> args)
    {
        if (!super.isMuted() && !super.isTimedOutOnChannelOrModify(event.getChannel().getName()))
        {
            final boolean result = new Random().nextBoolean();

            TwitchBot.replyToMessage(
                    event.getChannel().getName(),
                    event.getMessageId().get(),
                    (result) ? "yes" : "no"
            );
        }
    }
}
