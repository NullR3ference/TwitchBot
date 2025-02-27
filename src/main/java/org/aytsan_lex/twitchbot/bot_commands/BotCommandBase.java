package org.aytsan_lex.twitchbot.bot_commands;

import java.time.LocalDateTime;
import java.util.ArrayList;

import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

import org.aytsan_lex.twitchbot.TwitchBot;

public abstract class BotCommandBase implements IBotCommand
{
    public static final int DEFAULT_MESSAGE_DELAY = 1100; // ms

    public abstract void execute(final IRCMessageEvent event, final ArrayList<String> args);

    @Override
    public int getRequiredPermissionLevel()
    {
        return TwitchBot.getConfigManager().getRequiredPermissionLevel(this.getClass());
    }

    @Override
    public int getCooldown()
    {
        return TwitchBot.getConfigManager().getCommandCooldown(this.getClass());
    }

    @Override
    public boolean isMuted()
    {
        return TwitchBot.getConfigManager().commandIsMuted(this.getClass());
    }

    protected boolean isTimedOutOnChannelOrModify(final String channelName)
    {
        if (TwitchBot.getConfigManager().isTimedOutOnChannel(channelName))
        {
            final LocalDateTime now = LocalDateTime.now();
            final LocalDateTime timeoutEndsAt = TwitchBot.getConfigManager().getTimeoutEndsAt(channelName);

            if (now.isBefore(timeoutEndsAt)) { return true; }

            TwitchBot.getConfigManager().removeTimedOutOnChannel(channelName);
            TwitchBot.getConfigManager().saveFile();
        }
        return false;
    }
}
