package org.aytsan_lex.twitchbot.commands;

import java.time.LocalDateTime;
import java.util.ArrayList;

import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

import org.aytsan_lex.twitchbot.BotConfigManager;

public class BotCommandBase implements IBotCommand
{
    public static final int DEFAULT_MESSAGE_DELAY = 1100; // ms

    @Override
    public void execute(final IRCMessageEvent event, final ArrayList<String> args)
    {
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return BotConfigManager.getCommandRequiredPermissionLevel(this.getClass().getSimpleName());
    }

    @Override
    public int getCooldown()
    {
        return BotConfigManager.getCommandCooldown(this.getClass().getSimpleName());
    }

    @Override
    public boolean isMuted()
    {
        return BotConfigManager.commandIsMuted(this.getClass());
    }

    protected boolean isTimedOutOnChannelOrModify(final String channelName)
    {
        if (BotConfigManager.isTimedOutOnChannel(channelName))
        {
            final LocalDateTime now = LocalDateTime.now();
            final LocalDateTime timeoutEndsAt = BotConfigManager.getTimeoutEndsAt(channelName);

            if (now.isBefore(timeoutEndsAt)) { return true; }

            BotConfigManager.removeTimedOutOnChannel(channelName);
            BotConfigManager.saveConfig();
        }
        return false;
    }
}
