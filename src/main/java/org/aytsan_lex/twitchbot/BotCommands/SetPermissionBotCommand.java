package org.aytsan_lex.twitchbot.BotCommands;

import org.aytsan_lex.twitchbot.BotConfig;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

public class SetPermissionBotCommand extends BotCommandBase
{
    public SetPermissionBotCommand()
    {
        super(777);
    }

    @Override
    public void execute(Object... args)
    {
        if (!(args[0] instanceof String targetUserName) ||
            !(args[1] instanceof Integer targetLevel) ||
            !(args[2] instanceof IRCMessageEvent event))
        {
            throw new BotCommandError("Invalid args classes");
        }

        final String channelName = event.getChannel().getName();
        final String userId = event.getUser().getId();
        final String messageId = event.getMessageId().get();
        final int permissionLevel = BotConfig.instance().getPermissionLevel(userId);

        if (permissionLevel >= super.getRequiredPermissionLevel())
        {
            final String targetUserId = event.getTwitchChat().getChannelNameToChannelId().get(targetUserName);
            if (targetUserId != null)
            {
                if (!userId.equals(targetUserId))
                {
                    BotConfig.instance().setPermissionLevel(targetUserId, targetLevel);
                    BotConfig.instance().saveChanges();

                    super.replyToMessageWithDelay(
                            channelName,
                            userId,
                            messageId,
                            event.getTwitchChat(),
                            "Уровень доступа для '%s' установлен -> %d".formatted(targetUserName, targetLevel),
                            BotCommandBase.DEFAULT_MESSAGE_DELAY
                    );

                    System.out.println("Set permission level of '%s' -> %d".formatted(targetUserName, targetLevel));
                }
            }
        }
        else
        {
            super.replyToMessageWithDelay(
                    channelName,
                    userId,
                    messageId,
                    event.getTwitchChat(),
                    "Требуется %d+ уровень доступа, у тебя: %d SOSI"
                            .formatted(this.getRequiredPermissionLevel(), permissionLevel),
                    BotCommandBase.DEFAULT_MESSAGE_DELAY
            );
        }
    }
}
