package org.aytsan_lex.twitchbot.commands;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.github.twitch4j.chat.TwitchChat;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;
import com.github.twitch4j.common.events.domain.EventChannel;

import org.aytsan_lex.twitchbot.BotConfigManager;

public class BotCommandBase implements IBotCommand
{
    public static final int DEFAULT_MESSAGE_DELAY = 1100; // ms
    public static final Lock messageSendMutex = new ReentrantLock(true);

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
        return BotConfigManager.commandIsMuted(this.getClass().getSimpleName());
    }

    protected boolean isTimedOutOnChannelOrModify(final String channelName)
    {
        if (BotConfigManager.isTimedOutOnChannel(channelName))
        {
            final LocalDateTime now = LocalDateTime.now();
            final LocalDateTime timeoutEndsAt = BotConfigManager.getTimeoutEndsAt(channelName);

            if (now.isBefore(timeoutEndsAt)) { return true; }

            BotConfigManager.removeTimedOutOnChannel(channelName);
            BotConfigManager.writeConfig();
        }
        return false;
    }

    protected void replyToMessage(final EventChannel channel,
                                  final TwitchChat chat,
                                  final String messageId,
                                  final String message,
                                  final int delay)
    {
        messageSendMutex.lock();
        try
        {
            try { TimeUnit.MILLISECONDS.sleep(delay); }
            catch (InterruptedException ignored) { }
            chat.sendMessage(channel.getName(), message,null, messageId);
        }
        catch (Exception ignored) { }
        finally { messageSendMutex.unlock(); }
    }

    protected void sendMessage(final EventChannel channel,
                               final TwitchChat chat,
                               final String message,
                               final int delay)
    {
        messageSendMutex.lock();
        try
        {
            try { TimeUnit.MILLISECONDS.sleep(delay); }
            catch (InterruptedException ignored) { }
            chat.sendMessage(channel.getName(), message);
        }
        catch (Exception ignored) { }
        finally { messageSendMutex.unlock(); }
    }
}
