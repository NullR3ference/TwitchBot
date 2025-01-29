package org.aytsan_lex.twitchbot.commands;

import org.aytsan_lex.twitchbot.BotConfigManager;
import org.aytsan_lex.twitchbot.BotGlobalState;
import org.aytsan_lex.twitchbot.TwitchBot;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

public class CancelVoteBotCommand extends BotCommandBase
{
    public CancelVoteBotCommand()
    {
        super(777);
    }

    @Override
    public void execute(Object... args)
    {
        if (!(args[0] instanceof IRCMessageEvent event))
        {
            throw new BotCommandError("Invalid args classes");
        }

        final String userName = event.getUser().getName();
        final int permissionLevel = BotConfigManager.getPermissionLevel(userName);

        if (permissionLevel >= this.getRequiredPermissionLevel())
        {
            if (BotGlobalState.votingIsActive())
            {
                final BotGlobalState.VotingContext context = BotGlobalState.getCurrentVotingContext();
                super.replyToMessageWithDelay(
                        event.getChannel(),
                        event.getUser().getId(),
                        event.getMessageId().get(),
                        event.getTwitchChat(),
                        "Голосование '%s' - остановлено. Голосов %d из %d".formatted(
                                context.getContent(),
                                context.getCurrentVotes(),
                                context.getTargetVotes()
                        ),
                        BotCommandBase.DEFAULT_MESSAGE_DELAY
                );
                BotGlobalState.stopVoting();
            }
            else
            {
                super.replyToMessageWithDelay(
                        event.getChannel(),
                        event.getUser().getId(),
                        event.getMessageId().get(),
                        event.getTwitchChat(),
                        "Нет активного голосования",
                        BotCommandBase.DEFAULT_MESSAGE_DELAY
                );
            }
        }
        else
        {
            TwitchBot.LOGGER.warn("{}: permission denied: {}/{}", userName, permissionLevel, super.getRequiredPermissionLevel());
        }
    }
}
