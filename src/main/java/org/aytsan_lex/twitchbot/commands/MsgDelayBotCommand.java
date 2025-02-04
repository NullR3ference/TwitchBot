package org.aytsan_lex.twitchbot.commands;

import org.aytsan_lex.twitchbot.TwitchBot;
import org.aytsan_lex.twitchbot.BotConfigManager;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

import java.util.ArrayList;
import java.util.Arrays;

public class MsgDelayBotCommand extends BotCommandBase
{
    private enum SubCommand
    {
        SET
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
            if (args.length >= 3)
            {
                if (!(args[1] instanceof String subCommand))
                {
                    throw new BotCommandError("Invalid args classes");
                }

                final int begin = 2;
                final int end = args.length - 1;

                final ArrayList<Object> subCommandArgs = new ArrayList<>(Arrays.asList(args).subList(begin, end));
                this.handleSubCommand(event, subCommand.trim().toUpperCase(), subCommandArgs);
            }
            else
            {
                super.replyToMessageWithDelay(
                        event.getChannel(),
                        event.getUser().getId(),
                        event.getMessageId().get(),
                        event.getTwitchChat(),
                        "Задержка м/сообщениями: %dms".formatted(
                                BotConfigManager.getConfig().getDelayBetweenMessages()
                        ),
                        BotCommandBase.DEFAULT_MESSAGE_DELAY
                );
            }
        }
        else
        {
            TwitchBot.LOGGER.warn("{}: permission denied: {}/{}", userName, permissionLevel, super.getRequiredPermissionLevel());
        }
    }

    private void handleSubCommand(final IRCMessageEvent event, final String cmd, final ArrayList<Object> args)
    {
        try
        {
            switch (SubCommand.valueOf(cmd))
            {
                case SET ->
                {
                    final int value = Integer.parseInt(String.valueOf(args.get(0)));
                    BotConfigManager.setDelayBetweenMessages(value);
                    BotConfigManager.writeConfig();

                    super.replyToMessageWithDelay(
                            event.getChannel(),
                            event.getUser().getId(),
                            event.getMessageId().get(),
                            event.getTwitchChat(),
                            "Задержка м/сообщениями установлена -> %dms".formatted(value),
                            BotCommandBase.DEFAULT_MESSAGE_DELAY
                    );
                }
            }
        }
        catch (IllegalArgumentException e)
        {
            TwitchBot.LOGGER.warn("Invalid (or unknown) sub-command for '{}': '{}'", this.getClass().getSimpleName(), cmd);
        }
    }
}
