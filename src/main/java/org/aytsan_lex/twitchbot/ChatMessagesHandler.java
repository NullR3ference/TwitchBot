package org.aytsan_lex.twitchbot;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.time.format.DateTimeFormatter;

import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.common.util.ChatReply;

public class ChatMessagesHandler
{
    private static final ArrayList<String> m_Channels = BotConfig.instance().getChannels();

    private final HashMap<String, PrintWriter> m_PrintWriters = new HashMap<>();

    public ChatMessagesHandler()
    {
        m_Channels.forEach(channel_name -> {
            try
            {
                final File file = new File(String.format("%s_chat_log.csv", channel_name));
                if (!file.exists()) { file.createNewFile(); }

                this.m_PrintWriters.put(channel_name, new PrintWriter(file));
            }
            catch (IOException e)
            {}
        });
    }

    public void handleChatMessage(ChannelMessageEvent event)
    {
        final String channel = event.getChannel().getName();
        final String user_id = event.getUser().getId();
        final String user_name = event.getUser().getName();
        final String message = event.getMessage();
        final String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss.SSS"));
        final ChatReply reply = event.getReplyInfo();

        System.out.printf(
                "[%s] [%s] (%s)[%s]: %s\n",
                timestamp,
                channel,
                user_id,
                user_name,
                message
        );

        final PrintWriter current_writer = this.m_PrintWriters.get(channel);

        current_writer.printf(
                "%s|%s|%s|%s|%s|%s|%s\n",
                timestamp,
                channel,
                user_id,
                user_name,
                message,
                reply != null,
                String.format(
                        "Body='%s', UserLogin=%s, UserID=%s, ThrUserLogin=%s, MsgID=%s, ThrMsgID=%s",
                        reply.getMessageBody(),
                        reply.getUserLogin(),
                        reply.getUserId(),
                        reply.getThreadUserLogin(),
                        reply.getMessageId(),
                        reply.getThreadMessageId()
                )
        );

        current_writer.flush();
    }
}
