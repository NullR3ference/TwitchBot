package org.aytsan_lex.twitchbot.commands;

import org.aytsan_lex.twitchbot.TwitchBot;
import org.aytsan_lex.twitchbot.BotConfigManager;
import org.aytsan_lex.twitchbot.BotGlobalState;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

public class VoteInfoBotCommand extends BotCommandBase
{
    private enum SubCommand
    {
        CLEAR
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
        final int delay = BotConfigManager.getConfig().getDelayBetweenMessages();

        if (permissionLevel >= this.getRequiredPermissionLevel())
        {
            if ((args.length >= 2) && (args[1] instanceof String subCommand))
            {
                this.handleSubCommand(event, subCommand.toUpperCase(), delay);
            }
            else
            {
                super.replyToMessageWithDelay(
                        event.getChannel(),
                        event.getUser().getId(),
                        event.getMessageId().get(),
                        event.getTwitchChat(),
                        this.createVoteInfoMessage(),
                        delay
                );
            }
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

    private void handleSubCommand(final IRCMessageEvent event, final String cmd, final int delay)
    {
        try
        {
            switch (SubCommand.valueOf(cmd))
            {
                case CLEAR ->
                {
                    if (BotGlobalState.hasRecentVotingContext())
                    {
                        BotGlobalState.clearRecentContext();
                        super.replyToMessageWithDelay(
                                event.getChannel(),
                                event.getUser().getId(),
                                event.getMessageId().get(),
                                event.getTwitchChat(),
                                "Предыдущий контекст голосования очищен",
                                delay
                        );
                    }
                }
            }
        }
        catch (IllegalArgumentException e)
        {
            TwitchBot.LOGGER.warn("Invalid (or unknown) sub-command for '{}': '{}'", this.getClass().getSimpleName(), cmd);
        }
    }
}
