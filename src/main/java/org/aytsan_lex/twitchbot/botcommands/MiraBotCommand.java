package org.aytsan_lex.twitchbot.botcommands;

import org.aytsan_lex.twitchbot.BotConfig;
import org.aytsan_lex.twitchbot.botcommands.filters.MiraPostFilter;
import org.aytsan_lex.twitchbot.ollama.OllamaMira;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

import java.util.regex.Pattern;

public class MiraBotCommand extends BotCommandBase
{
    private static final int MAX_TWITCH_MESSAGE_LEN = 450;

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

        if (true)
        {
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

            for (final Pattern pattern: MiraPostFilter.VALUES)
            {
                if (pattern.matcher(response).find())
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
            }

            this.replyToMessage(channelName, userId, messageId, event.getTwitchChat(), response);
        }
        else
        {
            super.replyToMessageWithDelay(
                    channelName,
                    userId,
                    messageId,
                    event.getTwitchChat(),
                    "Я Мирочка, мой создатель отключил меня, пока я не обучусь. Прости зайка <3",
                    BotCommandBase.DEFAULT_MESSAGE_DELAY
            );
        }
    }
}
