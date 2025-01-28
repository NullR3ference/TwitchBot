package org.aytsan_lex.twitchbot.commands;

import org.aytsan_lex.twitchbot.BotConfigManager;
import org.aytsan_lex.twitchbot.TwitchBot;
import com.github.twitch4j.chat.TwitchChat;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

public class RemoveChannelBotCommand extends BotCommandBase
{
    public RemoveChannelBotCommand()
    {
        super(777);
    }

    @Override
    public void execute(Object... args)
    {
        if (!(args[0] instanceof String targetChannelName) || !(args[1] instanceof IRCMessageEvent event))
        {
            throw new BotCommandError("Invalid args classes");
        }

        final String userName = event.getUser().getName();
        final TwitchChat chat = event.getTwitchChat();
        final int permissionLevel = BotConfigManager.instance().getPermissionLevel(userName);

        if (permissionLevel >= super.getRequiredPermissionLevel())
        {
            final String targetChannelId = chat.getChannelNameToChannelId().get(targetChannelName);
            if ((targetChannelId != null) && !BotConfigManager.instance().isOwner(targetChannelId))
            {
                if (TwitchBot.instance().channelExists(targetChannelName))
                {
                    if (BotConfigManager.instance().removeChannel(targetChannelName))
                    {
                        super.replyToMessageWithDelay(
                                event.getChannel(),
                                event.getUser().getId(),
                                event.getMessageId().get(),
                                chat,
                                "Канал удален: [%s]".formatted(targetChannelName),
                                BotCommandBase.DEFAULT_MESSAGE_DELAY
                        );
                        TwitchBot.instance().leaveFromChat(targetChannelName);
                        BotConfigManager.instance().saveChanges();
                    }
                }
            }
        }
        else
        {
            TwitchBot.LOGGER.warn("{}: permission denied: {}/{}", userName, permissionLevel, super.getRequiredPermissionLevel());
        }
    }
}
