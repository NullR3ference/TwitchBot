package org.aytsan_lex.twitchbot.commands;

import java.util.concurrent.TimeUnit;
import com.github.twitch4j.chat.TwitchChat;

public class BotCommandBase implements IBotCommand
{
    public static final int DEFAULT_MESSAGE_DELAY = 1100; // ms

    private final int requiredPermissionLevel;

    public BotCommandBase(int level)
    {
        this.requiredPermissionLevel = level;
    }

    @Override
    public void execute(Object... args)
    {
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return this.requiredPermissionLevel;
    }

    public void replyToMessage(String channelName,
                               String userId,
                               String messageId,
                               TwitchChat chat,
                               String message)
    {
        this.replyToMessageWithDelay(channelName, userId, messageId, chat, message, 0);
    }

    public void replyToMessageWithDelay(String channelName,
                                        String userId,
                                        String messageId,
                                        TwitchChat chat,
                                        String message,
                                        int delay)
    {
        this.sendMessage(channelName, userId, messageId, chat, message, delay);
    }

    public void sendMessage(String channelName,
                            String userId,
                            String messageId,
                            TwitchChat chat,
                            String message,
                            int delay)
    {
        final String channelId = chat.getChannelNameToChannelId().get(channelName);

        if (!channelId.equals(userId))
        {
            try { TimeUnit.MILLISECONDS.sleep(delay); }
            catch (InterruptedException e) { }
        }

        chat.sendMessage(channelName, message,null, messageId);
    }
}
