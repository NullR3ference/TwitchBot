package org.aytsan_lex.twitchbot.BotCommands;

import com.github.twitch4j.chat.events.channel.IRCMessageEvent;
import org.aytsan_lex.twitchbot.BotConfig;

public class MiraMuteBotCommand extends BotCommandBase
{
    public MiraMuteBotCommand()
    {
        super(4);
    }

    @Override
    public void execute(Object... args)
    {
        if (!(args[0] instanceof Boolean isMuted) || !(args[1] instanceof IRCMessageEvent event))
        {
            throw new BotCommandError("Invalid args classes");
        }

        final String userId = event.getUser().getId();
        final String messageId = event.getMessageId().get();
        final String currentChannelName = event.getChannel().getName();
        final int permissionLevel = BotConfig.instance().getPermissionLevel(userId);

        if (permissionLevel >= super.getRequiredPermissionLevel())
        {
            System.out.println("Mira command muted = %b".formatted(isMuted));
            BotCommandSharedState.setMiraCommandIsMuted(isMuted);
        }
        else
        {
            super.replyToMessageWithDelay(
                    currentChannelName,
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
