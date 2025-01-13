package org.aytsan_lex.twitchbot.BotCommands;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;

import org.aytsan_lex.twitchbot.BotConfig;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;
import org.aytsan_lex.twitchbot.TwitchBotLauncher;

public class StatusBotCommand extends BotCommandBase
{
    public StatusBotCommand()
    {
        super(777);
    }

    @Override
    public void execute(Object... args)
    {
        if (!(args[0] instanceof IRCMessageEvent event))
        {
            throw new BotCommandError("Invalid args classes");
        }

        final String channelName = event.getChannel().getName();
        final String userId = event.getUser().getId();
        final String messageId = event.getMessageId().get();
        final int userPermLevel = BotConfig.instance().getPermissionLevel(userId);

        if (userPermLevel >= this.getRequiredPermissionLevel())
        {
            final Duration uptime = Duration.between(TwitchBotLauncher.getStartTime(), Instant.now());

            final String reply = "Uptime: %02d:%02d:%02d"
                    .formatted(uptime.toHoursPart(), uptime.toMinutesPart(), uptime.toSecondsPart());

            super.replyToMessageWithDelay(
                    channelName,
                    userId,
                    messageId,
                    event.getTwitchChat(),
                    reply,
                    BotCommandBase.DEFAULT_MESSAGE_DELAY
            );
        }
        else
        {
            super.replyToMessageWithDelay(
                    channelName,
                    userId,
                    messageId,
                    event.getTwitchChat(),
                    "Требуется %d+ уровень доступа, у тебя: %d SOSI"
                            .formatted(this.getRequiredPermissionLevel(), userPermLevel),
                    BotCommandBase.DEFAULT_MESSAGE_DELAY
            );
        }
    }
}
