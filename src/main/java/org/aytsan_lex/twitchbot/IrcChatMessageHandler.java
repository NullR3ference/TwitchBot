package org.aytsan_lex.twitchbot;

import java.util.Optional;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;
import com.github.twitch4j.common.events.domain.EventUser;

public class IrcChatMessageHandler
{
    private enum IrcCommandType
    {
        PRIVMSG,
        CLEARCHAT
    }

    public IrcChatMessageHandler()
    {
    }

    public void handleIrcMessage(IRCMessageEvent event)
    {
        final String commandType = event.getCommandType();

        // TODO: Handle other types of command type from Helix API
        // TODO: Handle timeout and other messages from Helix API

        try
        {
            switch (IrcCommandType.valueOf(commandType))
            {
                case PRIVMSG -> handlePrivmsgIrcCommand(event);
                case CLEARCHAT -> handleClearchatIrcCommand(event);
            }
        }
        catch (IllegalArgumentException ignored)
        {
        }
    }

    private void handlePrivmsgIrcCommand(IRCMessageEvent event)
    {
        final EventUser user = event.getUser();
        final Optional<String> optionalMessage = event.getMessage();

        if (user != null && optionalMessage.isPresent())
        {
            final String channelName = event.getChannel().getName();
            final String userId = user.getId();
            final String userName = user.getName();
            final String message = optionalMessage.get();

            // TODO: Handle command via @tag of bot
            // @bot <command> [args...]

            if (message.startsWith("%"))
            {
                CommandHandler.handleCommand(message, event);
            }
        }
    }

    public void handleClearchatIrcCommand(IRCMessageEvent event)
    {
    }
}
