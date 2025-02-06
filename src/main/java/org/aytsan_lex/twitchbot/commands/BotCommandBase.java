package org.aytsan_lex.twitchbot.commands;

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
    public void execute(final IRCMessageEvent event, final ArrayList<String> args) { }

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

    public void replyToMessageWithDelay(final EventChannel channel,
                                        final String userId,
                                        final String messageId,
                                        final TwitchChat chat,
                                        final String message,
                                        final int delay)
    {
        this.sendMessage(channel, userId, messageId, chat, message, delay);
    }

    public void sendMessage(EventChannel channel,
                            String userId,
                            String messageId,
                            TwitchChat chat,
                            String message,
                            int delay)
    {
        final String channelName = channel.getName();
        final String channelId = channel.getId();

        messageSendMutex.lock();
        try
        {
            if (!channelId.equals(userId))
            {
                try { TimeUnit.MILLISECONDS.sleep(delay); }
                catch (InterruptedException e) { }
            }
            chat.sendMessage(channelName, message,null, messageId);
        }
        catch (Exception ignored) { }
        finally
        {
            messageSendMutex.unlock();
        }
    }
}
