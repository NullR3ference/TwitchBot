package org.aytsan_lex.twitchbot.BotCommands;

import java.util.regex.Pattern;
import org.aytsan_lex.twitchbot.BotConfig;
import org.aytsan_lex.twitchbot.ollama.OllamaMira;
import org.aytsan_lex.twitchbot.BotCommands.filters.MiraPreFilter;
import org.aytsan_lex.twitchbot.BotCommands.filters.MiraPostFilter;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

public class MiraBotCommand extends BotCommandBase
{
    private static final int MAX_TWITCH_MESSAGE_LEN = 400;

    public MiraBotCommand()
    {
        super(1);
    }

    @Override
    public void execute(Object... args)
    {
        // TODO: Make AI to able read chat in real time

        if (!(args[0] instanceof String message) || !(args[1] instanceof IRCMessageEvent event))
        {
            throw new BotCommandError("Invalid args classes");
        }

        final String channelName = event.getChannel().getName();
        final String userId = event.getUser().getId();
        final String userName = event.getUser().getName();
        final String messageId = event.getMessageId().get();
        final int userPermLevel = BotConfig.instance().getPermissionLevel(userId);

        if (!OllamaMira.instance().checkConnection())
        {
            super.replyToMessageWithDelay(
                    channelName,
                    userId,
                    messageId,
                    event.getTwitchChat(),
                    "Я Мирочка, мой создатель отключил меня, пока я не обучусь. Прости зайка <3",
                    BotCommandBase.DEFAULT_MESSAGE_DELAY
            );
            return;
        }

        if (userPermLevel < this.getRequiredPermissionLevel())
        {
            super.replyToMessage(
                    channelName,
                    userId,
                    messageId,
                    event.getTwitchChat(),
                    "Ой, прости зайка, я пока не могу общаться с тобой, создателю надо выдать разрешение, прежде чем я смогу тебе отвечать (("
            );
            return;
        }

        if (!miraPreFilter(message))
        {
            super.replyToMessageWithDelay(
                    channelName,
                    userId,
                    messageId,
                    event.getTwitchChat(),
                    "Даже не пытайся ))",
                    BotCommandBase.DEFAULT_MESSAGE_DELAY
            );
            return;
        }

        final String response = OllamaMira.instance().question(userName, message);

        if (response.length() >= MAX_TWITCH_MESSAGE_LEN)
        {
            this.replyToMessage(
                    channelName,
                    userId,
                    messageId,
                    event.getTwitchChat(),
                    "Ой, прости зайка, я слишком много букв написала (( Не буду так много говорить"
            );
            return;
        }

        if (!miraPostFilter(response))
        {
            this.replyToMessage(
                    channelName,
                    userId,
                    messageId,
                    event.getTwitchChat(),
                    "Ой, прости зайка, кажется я написала бредик (( Мне запретили такое говорить"
            );
            return;
        }

        this.replyToMessage(channelName, userId, messageId, event.getTwitchChat(), response);
    }

    private boolean miraPreFilter(String messageText)
    {
        for (final Pattern pattern : MiraPreFilter.VALUES)
        {
            if (pattern.matcher(messageText).find())
            {
                return false;
            }
        }
        return true;
    }

    private boolean miraPostFilter(String modelResponse)
    {
        for (final Pattern pattern: MiraPostFilter.VALUES)
        {
            if (pattern.matcher(modelResponse).find())
            {
                return false;
            }
        }
        return true;
    }
}
