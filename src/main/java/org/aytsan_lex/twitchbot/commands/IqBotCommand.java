package org.aytsan_lex.twitchbot.commands;

import java.util.Random;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

public class IqBotCommand extends BotCommandBase
{
    public IqBotCommand()
    {
        super(0);
    }

    @Override
    public void execute(Object... args)
    {
        if ((!(args[0] instanceof String)) || (!(args[1] instanceof IRCMessageEvent event)))
        {
            throw new BotCommandError("Invalid args classes");
        }

        final int iqValue = new Random().nextInt(1, 250);

        super.replyToMessageWithDelay(
                event.getChannel(),
                event.getUser().getId(),
                event.getMessageId().get(),
                event.getTwitchChat(),
                "@%s у тебя %d IQ".formatted(event.getUserName(), iqValue),
                BotCommandBase.DEFAULT_MESSAGE_DELAY
        );
    }
}
