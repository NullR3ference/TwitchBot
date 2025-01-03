package org.aytsan_lex.twitchbot.botcommands;

import java.util.Random;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

public class BenBotCommand extends BotCommandBase
{
    public BenBotCommand()
    {
        super(0);
    }

    @Override
    public int execute(Object... args)
    {
        if ((!(args[0] instanceof String messageText)) || (!(args[1] instanceof IRCMessageEvent event)))
        {
            throw new IllegalArgumentException("Arguments must be instance of String and IRCMessageEvent!");
        }

        final String channelName = event.getChannel().getName();
        final String userId = event.getUserId();
        final String messageId = event.getMessageId().get();
        final boolean result = new Random().nextBoolean();

        super.replyToMessage(
                channelName,
                userId,
                messageId,
                event.getTwitchChat(),
                (result) ? "yes" : "no"
        );

        return 0;
    }
}
