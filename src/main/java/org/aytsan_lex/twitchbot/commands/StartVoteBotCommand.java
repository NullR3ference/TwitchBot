package org.aytsan_lex.twitchbot.commands;

import com.github.twitch4j.chat.events.channel.IRCMessageEvent;
import org.aytsan_lex.twitchbot.BotConfigManager;
import org.aytsan_lex.twitchbot.BotGlobalState;
import org.aytsan_lex.twitchbot.TwitchBot;

public class StartVoteBotCommand extends BotCommandBase
{
    public StartVoteBotCommand()
    {
        super();
    }

    @Override
    public void execute(Object... args)
    {
        if (!(args[0] instanceof IRCMessageEvent event) ||
            !(args[1] instanceof Integer targetVotes)   ||
            !(args[2] instanceof String voteContent))
        {
            throw new BotCommandError("Invalid args classes");
        }

        final String userName = event.getUser().getName();
        final int permissionLevel = BotConfigManager.getPermissionLevel(userName);

        if (permissionLevel >= this.getRequiredPermissionLevel())
        {
            if (!BotGlobalState.votingIsActive())
            {
                BotGlobalState.startVoting(voteContent, targetVotes);
                super.replyToMessageWithDelay(
                        event.getChannel(),
                        event.getUser().getId(),
                        event.getMessageId().get(),
                        event.getTwitchChat(),
                        "Голосование запущено: '%s'; Необходимо %d голосов(са)"
                                .formatted(voteContent, targetVotes),
                        BotCommandBase.DEFAULT_MESSAGE_DELAY
                );
            }
            else
            {
                super.replyToMessageWithDelay(
                        event.getChannel(),
                        event.getUser().getId(),
                        event.getMessageId().get(),
                        event.getTwitchChat(),
                        "Нельзя начать новое голосование, уже активно: '%s'"
                                .formatted(BotGlobalState.getCurrentVotingContext().getContent()),
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
