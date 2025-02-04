package org.aytsan_lex.twitchbot.commands;

import java.util.Arrays;
import java.util.ArrayList;
import org.aytsan_lex.twitchbot.TwitchBot;
import org.aytsan_lex.twitchbot.BotConfigManager;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

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

                this.handleSubCommand(
                        event,
                        subCommand.trim().toUpperCase(),
                        new ArrayList<>(Arrays.asList(args).subList(2, args.length))
                );
            }
            else
            {
                final int delayValue = BotConfigManager.getConfig().getDelayBetweenMessages();
                super.replyToMessageWithDelay(
                        event.getChannel(),
                        event.getUser().getId(),
                        event.getMessageId().get(),
                        event.getTwitchChat(),
                        "Задержка м/сообщениями: %dms".formatted(delayValue),
                        delayValue
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

                    TwitchBot.LOGGER.info("Delay between messages set -> {}ms", value);

                    super.replyToMessageWithDelay(
                            event.getChannel(),
                            event.getUser().getId(),
                            event.getMessageId().get(),
                            event.getTwitchChat(),
                            "Задержка м/сообщениями установлена -> %dms".formatted(value),
                            value
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
