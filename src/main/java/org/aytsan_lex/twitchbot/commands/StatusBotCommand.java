package org.aytsan_lex.twitchbot.commands;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

import org.aytsan_lex.twitchbot.BotConfigManager;
import org.aytsan_lex.twitchbot.TwitchBot;
import org.aytsan_lex.twitchbot.TwitchBotLauncher;

public class StatusBotCommand extends BotCommandBase
{
    @Override
    public void execute(final IRCMessageEvent event, final ArrayList<String> args)
    {
        final String userName = event.getUser().getName();
        final int permissionLevel = BotConfigManager.getPermissionLevel(userName);

        if (permissionLevel >= this.getRequiredPermissionLevel())
        {
            super.replyToMessage(
                    event.getChannel(),
                    event.getTwitchChat(),
                    event.getMessageId().get(),
                    this.createStatusMessage(),
                    BotConfigManager.getConfig().getDelayBetweenMessages()
            );
        }
        else
        {
            TwitchBot.LOGGER.warn("{}: permission denied: {}/{}", userName, permissionLevel, super.getRequiredPermissionLevel());
        }
    }

    private String createStatusMessage()
    {
        final MemoryMXBean memMXBean = ManagementFactory.getMemoryMXBean();
        final Duration uptime = Duration.between(TwitchBotLauncher.getStartTime(), Instant.now());
        final float heapUsedMib = (float)memMXBean.getHeapMemoryUsage().getUsed() / (1024 * 1024);
        final float heapMaxMib = (float)memMXBean.getHeapMemoryUsage().getMax() / (1024 * 1024);

        return "%02d:%02d:%02d | %.2f MiB / %.2f MiB | Channels: %d".formatted(
                uptime.toHoursPart(),
                uptime.toMinutesPart(),
                uptime.toSecondsPart(),
                heapUsedMib,
                heapMaxMib,
                BotConfigManager.getConfig().getChannels().size()
        );
    }
}
