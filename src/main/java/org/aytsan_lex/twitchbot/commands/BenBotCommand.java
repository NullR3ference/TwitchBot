package org.aytsan_lex.twitchbot.commands;

import java.util.ArrayList;
import java.util.Random;
import org.aytsan_lex.twitchbot.BotConfigManager;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

public class BenBotCommand extends BotCommandBase
{
    @Override
    public void execute(final IRCMessageEvent event, final ArrayList<String> args)
    {
        if (!super.isMuted())
        {
            final boolean result = new Random().nextBoolean();

            super.replyToMessageWithDelay(
                    event.getChannel(),
                    event.getUser().getId(),
                    event.getMessageId().get(),
                    event.getTwitchChat(),
                    (result) ? "yes" : "no",
                    BotConfigManager.getConfig().getDelayBetweenMessages()
            );
        }
    }
}
