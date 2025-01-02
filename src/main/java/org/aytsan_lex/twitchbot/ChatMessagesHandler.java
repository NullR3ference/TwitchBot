package org.aytsan_lex.twitchbot;

import java.util.HashMap;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

public class ChatMessagesHandler
{
    private final HashMap<String, ChannelMessageLogger> channelLoggers = new HashMap<>();

    public ChatMessagesHandler()
    {
        BotConfig.instance().getChannels().forEach(channelName -> {
            this.channelLoggers.put(channelName, new ChannelMessageLogger(channelName));
        });
    }

    public void handleChatMessage(ChannelMessageEvent event)
    {
        final String channelName = event.getChannel().getName();
        final String message = event.getMessage();

        if (message.startsWith("!"))
        {
            final String cmd = message.substring(1);
            if (cmd.equals("lines"))
            {
            }
        }

        final String messageTimestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
        final String userId = event.getUser().getId();
        final String userName = event.getUser().getName();

        System.out.printf(
                "[%s] [%s] (%s)[%s]: %s\n",
                messageTimestamp,
                channelName,
                userId,
                userName,
                message
        );

        this.channelLoggers.get(event.getChannel().getName()).addMessage(event);
    }
}
