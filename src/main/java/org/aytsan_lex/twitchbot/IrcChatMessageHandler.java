package org.aytsan_lex.twitchbot;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Optional;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;
import com.github.twitch4j.common.events.domain.EventUser;

public class IrcChatMessageHandler
{
    private final HashMap<String, ChannelMessageLogger> channelLoggers;

    public IrcChatMessageHandler()
    {
        this.channelLoggers = new HashMap<>();

        BotConfig.instance().getChannels().forEach(channelName -> {
            this.channelLoggers.put(channelName, new ChannelMessageLogger(channelName));
        });
    }

    public void handleIrcMessage(IRCMessageEvent event)
    {
        final String commandType = event.getCommandType();

        // TODO: Handle other types of command type from Helix API
        // TODO: Handle timeout and other messages from Helix API
        if (commandType.equals("PRIVMSG"))
        {
            final EventUser user = event.getUser();
            final Optional<String> optionalMessage = event.getMessage();

            if (user != null && optionalMessage.isPresent())
            {
                final String channelName = event.getChannel().getName();
                final String userId = user.getId();
                final String userName = user.getName();
                final String message = optionalMessage.get();

                // FIXME: Pass the bot reply message to log

                // TODO: Handle command via @tag of bot
                // @bot <command> [args...]

                if (message.startsWith("%"))
                {
                    CommandHandler.handleCommand(message, event, this);
                }

                final String messageTimestamp =
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));

                System.out.printf(
                        "[%s] [%s] (%s)[%s]: %s\n",
                        messageTimestamp,
                        channelName,
                        userId,
                        userName,
                        message
                );

                final ChannelMessageLogger currentLogger = this.channelLoggers.get(event.getChannel().getName());
                if (currentLogger != null)
                {
                    currentLogger.addMessage(event);
                }
            }
        }
    }

    public ChannelMessageLogger getLogger(final String channelName)
    {
        return this.channelLoggers.get(channelName);
    }

    public void addLogger(final String channelName)
    {
        this.channelLoggers.put(channelName, new ChannelMessageLogger(channelName));
    }

    public void removeLogger(final String channelName)
    {
        this.channelLoggers.remove(channelName);
    }
}
