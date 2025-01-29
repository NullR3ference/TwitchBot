package org.aytsan_lex.twitchbot.commands;

import org.aytsan_lex.twitchbot.BotGlobalState;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

public class VoteBotCommand extends BotCommandBase
{
    public VoteBotCommand()
    {
        super(0);
    }

    @Override
    public void execute(Object... args)
    {
        if (!(args[0] instanceof IRCMessageEvent event))
        {
            throw new BotCommandError("Invalid args classes");
        }

        if (BotGlobalState.votingIsActive())
        {
            final BotGlobalState.VotingContext context = BotGlobalState.getCurrentVotingContext();
            context.addVote();
            if (context.isComplete())
            {
                super.replyToMessageWithDelay(
                        event.getChannel(),
                        event.getUser().getId(),
                        event.getMessageId().get(),
                        event.getTwitchChat(),
                        "Голосование '%s' - завершено! Собрано голосов: %d из %d".formatted(
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
                        "Голосование '%s'. Собрано голосов: %d из %d".formatted(
                                context.getContent(),
                                context.getCurrentVotes(),
                                context.getTargetVotes()
                        ),
                        BotCommandBase.DEFAULT_MESSAGE_DELAY
                );
            }
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
}
