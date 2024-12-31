package org.aytsan_lex.twitchbot;

import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.time.format.DateTimeFormatter;

import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.common.util.ChatReply;

public class ChatMessagesHandler
{
    private static final ArrayList<String> m_Channels = BotConfig.instance().getChannels();

    private final HashMap<String, FileWriter> m_FileWriters;

    public ChatMessagesHandler()
    {
        this.m_FileWriters = new HashMap<>();

        m_Channels.forEach(channel_name -> {
            try
            {
                final File file = new File(String.format(
                        "%s_chat_log_%s.csv",
                        channel_name,
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss")))
                );
                System.out.printf("[*] Opening (or creating) log file: %s\n", file.getName());

                if (!file.exists())
                {
                    file.createNewFile();
                }

                this.m_FileWriters.put(channel_name, new FileWriter(file));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        });
    }

    public void handleChatMessage(ChannelMessageEvent event)
    {
        try
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

            final FileWriter current_writer = this.m_FileWriters.get(channel);
            current_writer.write(String.format(
                    "%s|%s|%s|%s|%s|%s|%s\n",
                    timestamp,
                    channel,
                    user_id,
                    user_name,
                    message,
                    reply != null,
                    (reply == null) ? "" : String.format(
                            "Body='%s', UserLogin=%s, UserID=%s, ThrUserLogin=%s, MsgID=%s, ThrMsgID=%s",
                            reply.getMessageBody(),
                            reply.getUserLogin(),
                            reply.getUserId(),
                            reply.getThreadUserLogin(),
                            reply.getMessageId(),
                            reply.getThreadMessageId()
                    )
            ));
            current_writer.flush();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
