package org.aytsan_lex.twitchbot.BotCommands;

import org.aytsan_lex.twitchbot.BotConfig;
import org.aytsan_lex.twitchbot.TwitchBot;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

public class JoinToChatBotCommand extends BotCommandBase
{
    public JoinToChatBotCommand()
    {
        super(777);
    }

    @Override
    public void execute(Object... args)
    {
        if (!(args[0] instanceof String channelName) || !(args[1] instanceof IRCMessageEvent event))
        {
            throw new BotCommandError("Invalid args classes");
        }

        final String userId = event.getUser().getId();
        final String messageId = event.getMessageId().get();
        final String currentChannelName = event.getChannel().getName();
        final int permissionLevel = BotConfig.instance().getPermissionLevel(userId);

        if (BotConfig.instance().isOwner(userId) || (permissionLevel >= super.getRequiredPermissionLevel()))
        {
            TwitchBot.instance().joinToChat(channelName);
            super.replyToMessageWithDelay(
                    currentChannelName,
                    userId,
                    messageId,
                    event.getTwitchChat(),
                    "Подключен к: [%s]".formatted(channelName),
                    BotCommandBase.DEFAULT_MESSAGE_DELAY
            );
        }
    }
}
