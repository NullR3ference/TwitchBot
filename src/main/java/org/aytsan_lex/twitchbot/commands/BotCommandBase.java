package org.aytsan_lex.twitchbot.commands;

import java.util.concurrent.TimeUnit;
import com.github.twitch4j.chat.TwitchChat;
import com.github.twitch4j.common.events.domain.EventChannel;
import org.aytsan_lex.twitchbot.BotConfigManager;

public class BotCommandBase implements IBotCommand
{
    public static final int DEFAULT_MESSAGE_DELAY = 1100; // ms

    public BotCommandBase()
    {
    }

    @Override
    public void execute(Object... args)
    {
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return BotConfigManager.getCommandRequiredPermissionLevel(this.getClass().getSimpleName());
    }

    public void replyToMessage(EventChannel channel,
                               String userId,
                               String messageId,
                               TwitchChat chat,
                               String message)
    {
        this.replyToMessageWithDelay(channel, userId, messageId, chat, message, 0);
    }

    public void replyToMessageWithDelay(EventChannel channel,
                                        String userId,
                                        String messageId,
                                        TwitchChat chat,
                                        String message,
                                        int delay)
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

        if (!channelId.equals(userId))
        {
            try { TimeUnit.MILLISECONDS.sleep(delay); }
            catch (InterruptedException e) { }
        }

        chat.sendMessage(channelName, message,null, messageId);
    }
}
