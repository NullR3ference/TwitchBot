package org.aytsan_lex.twitchbot;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;

public class Utils
{
    public static Path getCurrentWorkingPath()
    {
        return Path.of("").toAbsolutePath();
    }

    /**
     * Build the status message (uptime, memory usage, channels and Ollama status)
     * @return The status message of a bot
     */
    public static String buildStatusMessage()
    {
        final MemoryMXBean memMXBean = ManagementFactory.getMemoryMXBean();
        final Duration uptime = Duration.between(TwitchBot.getTimeSinceInitialize(), Instant.now());
        final float heapUsedMib = (float) memMXBean.getHeapMemoryUsage().getUsed() / (1024 * 1024);
        final float heapMaxMib = (float) memMXBean.getHeapMemoryUsage().getMax() / (1024 * 1024);

        return "%dd %02dh %02dm %02ds | %.2f MiB / %.2f MiB | Channels: %d | Ollama access: %s".formatted(
                uptime.toDaysPart(),
                uptime.toHoursPart(),
                uptime.toMinutesPart(),
                uptime.toSecondsPart(),
                heapUsedMib,
                heapMaxMib,
                TwitchBot.getConfigManager().getConfig().getChannels().size(),
                (TwitchBot.getOllamaModelsManager().checkConnection()) ? "✅" : "❌"
        );
    }
}
