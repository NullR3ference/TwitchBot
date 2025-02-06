package org.aytsan_lex.twitchbot.commands;

import java.util.ArrayList;

import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

import org.aytsan_lex.twitchbot.TwitchBot;
import org.aytsan_lex.twitchbot.BotConfigManager;

public class MsgDelayBotCommand extends BotCommandBase
{
    private enum SubCommand
    {
        SET
    }

    @Override
    public void execute(final IRCMessageEvent event, final ArrayList<String> args)
    {
        if (args.isEmpty())
        {
            throw new BotCommandError("Args are required for this command!");
        }

        final String userName = event.getUser().getName();
        final int permissionLevel = BotConfigManager.getPermissionLevel(userName);

        if (permissionLevel >= this.getRequiredPermissionLevel())
        {
            if (!args.isEmpty())
            {
                final String subCommand = args.get(0).trim().toUpperCase();
                this.handleSubCommand(event, subCommand, new ArrayList<>(args.subList(1, args.size())));
            }
            else
            {
                final int delayValue = BotConfigManager.getConfig().getDelayBetweenMessages();
                super.replyToMessageWithDelay(
                        event.getChannel(),
                        event.getUser().getId(),
                        event.getMessageId().get(),
                        event.getTwitchChat(),
                        "Задержка между сообщениями: %dms".formatted(delayValue),
                        delayValue
                );
            }
        }
        else
        {
            TwitchBot.LOGGER.warn("{}: permission denied: {}/{}", userName, permissionLevel, super.getRequiredPermissionLevel());
        }
    }

    private void handleSubCommand(final IRCMessageEvent event,
                                  final String cmd,
                                  final ArrayList<Object> args)
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

                    TwitchBot.LOGGER.info("Delay between messages set: {} ms", BotCommandBase.DEFAULT_MESSAGE_DELAY);

                    super.replyToMessageWithDelay(
                            event.getChannel(),
                            event.getUser().getId(),
                            event.getMessageId().get(),
                            event.getTwitchChat(),
                            "Задержка между сообщениями установлена: %d ms".formatted(value),
                            value
                    );
                }
            }
        }
        catch (IllegalArgumentException e)
        {
            TwitchBot.LOGGER.warn("Invalid sub-command for '{}': '{}'", this.getClass().getSimpleName(), cmd);
        }
    }
}
