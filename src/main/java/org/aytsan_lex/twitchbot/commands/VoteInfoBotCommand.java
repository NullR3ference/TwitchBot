package org.aytsan_lex.twitchbot.commands;

import org.aytsan_lex.twitchbot.BotConfigManager;
import org.aytsan_lex.twitchbot.BotGlobalState;
import org.aytsan_lex.twitchbot.TwitchBot;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

public class VoteInfoBotCommand extends BotCommandBase
{
    public VoteInfoBotCommand()
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
            super.replyToMessageWithDelay(
                    event.getChannel(),
                    event.getUser().getId(),
                    event.getMessageId().get(),
                    event.getTwitchChat(),
                    this.createVoteInfoMessage(),
                    BotCommandBase.DEFAULT_MESSAGE_DELAY
            );
        }
        else
        {
            TwitchBot.LOGGER.warn("{}: permission denied: {}/{}", userName, permissionLevel, super.getRequiredPermissionLevel());
        }
    }

    private String createVoteInfoMessage()
    {
        if (!BotGlobalState.votingIsActive())
        {
            return "Нет активного голосования";
        }

        final BotGlobalState.VotingContext context = BotGlobalState.getCurrentVotingContext();
        return "Активное голосование '%s' голоса: %d из %d".formatted(
                context.getContent(),
                context.getCurrentVotes(),
                context.getTargetVotes()
        );
    }
}
