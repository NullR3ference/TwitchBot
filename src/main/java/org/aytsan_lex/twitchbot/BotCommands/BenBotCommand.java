package org.aytsan_lex.twitchbot.BotCommands;

import java.util.Random;
import java.util.regex.Pattern;
import org.aytsan_lex.twitchbot.BotCommands.filters.BenFilter;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

public class BenBotCommand extends BotCommandBase
{
    public BenBotCommand()
    {
        super(0);
    }

    @Override
    public void execute(Object... args)
    {
        if ((!(args[0] instanceof String messageText)) || (!(args[1] instanceof IRCMessageEvent event)))
        {
            throw new BotCommandError("Invalid args classes");
        }

        final String channelName = event.getChannel().getName();
        final String userId = event.getUserId();
        final String messageId = event.getMessageId().get();

        for (final Pattern pattern : BenFilter.VALUES)
        {
            if (pattern.matcher(messageText).find())
            {
                super.replyToMessageWithDelay(
                        channelName,
                        userId,
                        messageId,
                        event.getTwitchChat(),
                        "Я не отвечаю на такие вопросы",
                        BotCommandBase.DEFAULT_MESSAGE_DELAY
                );
                return;
            }
        }

        final boolean result = new Random().nextBoolean();

        super.replyToMessageWithDelay(
                channelName,
                userId,
                messageId,
                event.getTwitchChat(),
                (result) ? "yes" : "no",
                BotCommandBase.DEFAULT_MESSAGE_DELAY
        );
    }
}
