package org.aytsan_lex.twitchbot.commands;

import java.util.ArrayList;
import java.util.Random;

import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

import org.aytsan_lex.twitchbot.TwitchBot;

public class IqBotCommand extends BotCommandBase
{
    @Override
    public void execute(final IRCMessageEvent event, final ArrayList<String> args)
    {
        if (!super.isMuted() && !super.isTimedOutOnChannelOrModify(event.getChannel().getName()))
        {
            // TODO: Add args support: < > <= >= != ==
            // Message: %iq < 10: get value between 0 and 10
            // Message: %iq > 100: get value between 100 and 250
            // Etc...

            final int iqValue = new Random().nextInt(0, 250);

            TwitchBot.replyToMessage(
                    event.getChannel().getName(),
                    event.getMessageId().get(),
                    "@%s У тебя %d IQ".formatted(event.getUserName(), iqValue)
            );
        }
    }
}
