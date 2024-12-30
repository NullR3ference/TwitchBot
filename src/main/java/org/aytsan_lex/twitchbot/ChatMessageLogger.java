package org.aytsan_lex.twitchbot;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ChatMessageLogger
{
    private static final ArrayList<String> m_Channels = BotConfig.instance().getChannels();
    private final HashMap<String, FileWriter> m_FileWriters = new HashMap<>();

    public ChatMessageLogger()
    {
        m_Channels.forEach(channel_name -> {
            try
            {
                this.m_FileWriters.put(channel_name, new FileWriter(String.format("%s_chat_log.csv", channel_name)));
            }
            catch (IOException e)
            {}
        });
    }

    public void filteredLog(String timestamp, String channel, String user_id, String user_name, String message)
    {
        final String chat_csv_line = String.format(
                "%s|%s|%s|%s|%s\n",
                timestamp, channel, user_id, user_name, message
        );

        try
        {
            final FileWriter current_writer = this.m_FileWriters.get(channel);
            current_writer.write(chat_csv_line);
            current_writer.flush();
        }
        catch (IOException e)
        {}
    }
}
