package org.aytsan_lex.twitchbot.BotCommands;

import org.aytsan_lex.twitchbot.BotConfig;
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

        final String userId = event.getUser().getId();
        final String messageId = event.getMessageId().get();
        final String channelName = event.getChannel().getName();
        final TwitchChat chat = event.getTwitchChat();
        final int permissionLevel = BotConfig.instance().getPermissionLevel(userId);

        if (permissionLevel >= super.getRequiredPermissionLevel())
        {
            final String targetChannelId = chat.getChannelNameToChannelId().get(targetChannelName);
            if ((targetChannelId != null) && !BotConfig.instance().isOwner(targetChannelId))
            {
                if (TwitchBot.instance().channelExists(targetChannelName))
                {
                    if (BotConfig.instance().removeChannel(targetChannelName))
                    {
                        super.replyToMessageWithDelay(
                                channelName,
                                userId,
                                messageId,
                                chat,
                                "Канал удален: [%s]".formatted(targetChannelName),
                                BotCommandBase.DEFAULT_MESSAGE_DELAY
                        );
                        TwitchBot.instance().leaveFromChat(targetChannelName);
                        BotConfig.instance().saveChanges();
                    }
                }
            }
        }
        else
        {
            super.replyToMessageWithDelay(
                    channelName,
                    userId,
                    messageId,
                    chat,
                    "Требуется %d+ уровень доступа, у тебя: %d SOSI"
                            .formatted(this.getRequiredPermissionLevel(), permissionLevel),
                    BotCommandBase.DEFAULT_MESSAGE_DELAY
            );
        }
    }
}
