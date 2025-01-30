package org.aytsan_lex.twitchbot.commands;

import java.util.Random;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

public class BenBotCommand extends BotCommandBase
{
    public BenBotCommand()
    {
        super();
    }

    @Override
    public void execute(Object... args)
    {
        if ((!(args[0] instanceof String messageText)) || (!(args[1] instanceof IRCMessageEvent event)))
        {
            throw new BotCommandError("Invalid args classes");
        }

        final boolean result = new Random().nextBoolean();

        super.replyToMessageWithDelay(
                event.getChannel(),
                event.getUser().getId(),
                event.getMessageId().get(),
                event.getTwitchChat(),
                (result) ? "yes" : "no",
                BotCommandBase.DEFAULT_MESSAGE_DELAY
        );
    }
}
