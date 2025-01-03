package org.aytsan_lex.twitchbot;

import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ChannelMessageLogger
{
    private final String channelName;
    private final String channelLogDir;

    private String currentDate;
    private FileWriter fileWriter;
    private long currentLines;

    public ChannelMessageLogger(String channelName)
    {
        this.channelName = channelName;
        this.channelLogDir = BotConfig.LOG_BASE_PATH + "/" + this.channelName;
        this.currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        this.currentLines = 0;

        try
        {
            if (!Files.exists(Paths.get(this.channelLogDir)))
            {
                Files.createDirectory(Paths.get(this.channelLogDir));
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        this.openFile();
    }

    public void addMessage(IRCMessageEvent messageEvent)
    {
        final String messageDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        final String messageTimestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));

        if (!this.currentDate.equals(messageDate))
        {
            this.currentDate = messageDate;
            this.openFile();
        }

        final String line = String.format(
                "%s|%s|%s|%s\n",
                messageTimestamp,
                messageEvent.getUser().getId(),
                messageEvent.getUser().getName(),
                messageEvent.getMessage()
        );
        this.writeLine(line);
    }

    public void close()
    {
        try
        {
            this.fileWriter.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public long getCurrentLines()
    {
        return this.currentLines;
    }

    private void openFile()
    {
        try
        {
            final Path filepath = Path.of(
                    String.format("%s/%s_log_%s.csv", this.channelLogDir, this.channelName, this.currentDate)
            );

            this.fileWriter = new FileWriter(filepath.toString(), true);
            this.currentLines = Files.lines(filepath).count();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void writeLine(String line)
    {
        try
        {
            this.fileWriter.write(line);
            this.fileWriter.flush();
            this.currentLines++;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
