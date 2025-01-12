package org.aytsan_lex.twitchbot.BotCommands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.aytsan_lex.twitchbot.BotConfig;
import org.aytsan_lex.twitchbot.TwitchBot;
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
                    "Мой создатель отключил меня, пока я не обучусь. Прости зайка <3",
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
                    "Прости зайка, я пока не могу общаться с тобой, создателю надо выдать разрешение, прежде чем я смогу тебе отвечать (("
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

        if (!this.miraResponseLengthFilter(response))
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

        final String clearedResponse = MiraPostFilter.URL_PATTERN.matcher(response).replaceAll("(***)");
        TwitchBot.LOGGER.info("Raw response from model: {}", response);
        TwitchBot.LOGGER.info("Cleared response from model: {}", clearedResponse);

        this.replyToMessage(
                channelName,
                userId,
                messageId,
                event.getTwitchChat(),
                clearedResponse
        );
    }

    private boolean miraPreFilter(String messageText)
    {
        for (final Pattern pattern : MiraPreFilter.VALUES)
        {
            if (pattern.matcher(messageText).find())
            {
                TwitchBot.LOGGER.warn("Mira pre-filter failed: {}", messageText);
                return false;
            }
        }
        return true;
    }

    private boolean miraResponseLengthFilter(String modelResponse)
    {
        if (modelResponse.length() > MAX_TWITCH_MESSAGE_LEN)
        {
            TwitchBot.LOGGER.warn("Mira length filter failed: {}", modelResponse);
        }
        return modelResponse.length() <= MAX_TWITCH_MESSAGE_LEN;
    }

    private boolean miraPostFilter(String modelResponse)
    {
        for (final Pattern pattern : MiraPostFilter.VALUES)
        {
            final Matcher matcher = pattern.matcher(modelResponse);
            if (matcher.find())
            {
                TwitchBot.LOGGER.warn("Mira post-filter failed: '{}': {}", matcher.pattern(), modelResponse);
                return false;
            }
        }
        return true;
    }
}
