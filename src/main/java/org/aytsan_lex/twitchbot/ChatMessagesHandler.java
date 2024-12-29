package org.aytsan_lex.twitchbot;

import java.sql.Timestamp;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

public class ChatMessagesHandler
{
    private final ChatMessageLogger m_ChatMessageLogger;

    public ChatMessagesHandler()
    {
        this.m_ChatMessageLogger = new ChatMessageLogger();
    }

    public void handleChatMessage(ChannelMessageEvent event)
    {
        final String channel = event.getChannel().getName();
        final String user_id = event.getUser().getId();
        final String user_name = event.getUser().getName();
        final String message = event.getMessage();
        final String timestamp = new Timestamp(System.currentTimeMillis()).toString();

        final String chat_line = String.format(
                "[%s] [%s] (%s)[%s]: %s",
                timestamp,
                channel,
                user_id,
                user_name,
                message
        );

        System.out.println(chat_line);
        this.m_ChatMessageLogger.filteredLog(timestamp, channel, user_id, user_name, message);
    }
}
