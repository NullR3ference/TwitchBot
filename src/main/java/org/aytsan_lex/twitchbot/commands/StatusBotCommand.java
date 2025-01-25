package org.aytsan_lex.twitchbot.commands;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.time.Duration;
import java.time.Instant;
import org.aytsan_lex.twitchbot.BotConfig;
import org.aytsan_lex.twitchbot.TwitchBot;
import org.aytsan_lex.twitchbot.TwitchBotLauncher;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

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
        final String userName = event.getUser().getName();
        final String messageId = event.getMessageId().get();
        final int permissionLevel = BotConfig.instance().getPermissionLevel(userName);

        if (permissionLevel >= this.getRequiredPermissionLevel())
        {
            super.replyToMessageWithDelay(
                    channelName,
                    userId,
                    messageId,
                    event.getTwitchChat(),
                    this.createStatusMessage(),
                    BotCommandBase.DEFAULT_MESSAGE_DELAY
            );
        }
        else
        {
            TwitchBot.LOGGER.warn("{}: permission denied: {}/{}", userName, permissionLevel, super.getRequiredPermissionLevel());
        }
    }

    private String createStatusMessage()
    {
        final MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        final Duration uptime = Duration.between(TwitchBotLauncher.getStartTime(), Instant.now());
        final float heapUsedMib = (float)memoryMXBean.getHeapMemoryUsage().getUsed() / (1024 * 1024);
        final float nonHeapUsedMib = (float)memoryMXBean.getNonHeapMemoryUsage().getUsed() / (1024 * 1024);

        return "Uptime: %02d:%02d:%02d | Heap: %.2f MiB | Non-Heap: %.2f MiB".formatted(
                uptime.toHoursPart(),
                uptime.toMinutesPart(),
                uptime.toSecondsPart(),
                heapUsedMib,
                nonHeapUsedMib
        );
    }
}
