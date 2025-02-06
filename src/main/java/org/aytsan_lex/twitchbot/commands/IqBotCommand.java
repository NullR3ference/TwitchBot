package org.aytsan_lex.twitchbot.commands;

import java.util.ArrayList;
import java.util.Random;

import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

import org.aytsan_lex.twitchbot.BotConfigManager;

public class IqBotCommand extends BotCommandBase
{
    @Override
    public void execute(IRCMessageEvent event, ArrayList<String> args)
    {
        if (!super.isMuted())
        {
            final int iqValue = new Random().nextInt(0, 250);

            super.replyToMessageWithDelay(
                    event.getChannel(),
                    event.getUser().getId(),
                    event.getMessageId().get(),
                    event.getTwitchChat(),
                    "@%s У тебя %d IQ".formatted(event.getUserName(), iqValue),
                    BotConfigManager.getConfig().getDelayBetweenMessages()
            );
        }
    }
}
