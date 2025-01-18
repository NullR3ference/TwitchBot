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

        final String channelName = event.getChannel().getName();
        final String userId = event.getUserId();
        final String userName = event.getUserName();
        final String messageId = event.getMessageId().get();

        final int iqValue = new Random().nextInt(0, 250);

        super.replyToMessageWithDelay(
                channelName,
                userId,
                messageId,
                event.getTwitchChat(),
                "@%s у тебя %d IQ".formatted(userName, iqValue),
                BotCommandBase.DEFAULT_MESSAGE_DELAY
        );
    }
}
