package org.aytsan_lex.twitchbot.botcommands;

import java.util.concurrent.TimeUnit;
import com.github.twitch4j.chat.TwitchChat;

public class BotCommandBase implements IBotCommand
{
    private final int requiredPermissionLevel;

    public BotCommandBase(int level)
    {
        this.requiredPermissionLevel = level;
    }

    @Override
    public int execute(Object... args)
    {
        return 0;
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return this.requiredPermissionLevel;
    }

    public void sendMessage(String channelName, String userId, TwitchChat chat, String message)
    {
        this.replyToMessage(channelName, userId, null, chat, message);
    }

    public void replyToMessage(String channelName,
                               String userId,
                               String messageId,
                               TwitchChat chat,
                               String message)
    {
        final String channelId = chat.getChannelNameToChannelId().get(channelName);

        if (!channelId.equals(userId))
        {
            try { TimeUnit.MILLISECONDS.sleep(1100); }
            catch (InterruptedException e) { }
        }

        chat.sendMessage(channelName, message,null, messageId);
    }
}
