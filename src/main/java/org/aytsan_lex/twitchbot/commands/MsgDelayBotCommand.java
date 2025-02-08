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
        final String userName = event.getUser().getName();
        final int permissionLevel = BotConfigManager.getPermissionLevel(userName);

        if (permissionLevel >= this.getRequiredPermissionLevel())
        {
            if (args.size() >= 2)
            {
                final String subCommand = args.get(0).toUpperCase();
                this.handleSubCommand(event, subCommand, new ArrayList<>(args.subList(1, args.size())));
            }
            else
            {
                final int delayValue = BotConfigManager.getConfig().getDelayBetweenMessages();
                TwitchBot.replyToMessageWithDelay(
                        event.getChannel().getName(),
                        event.getMessageId().get(),
                        "Задержка м/сообщениями: %d ms".formatted(delayValue),
                        BotCommandBase.DEFAULT_MESSAGE_DELAY
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

                    TwitchBot.LOGGER.info("Delay between messages set: {} ms", value);

                    TwitchBot.replyToMessageWithDelay(
                            event.getChannel().getName(),
                            event.getMessageId().get(),
                            "Задержка м/сообщениями установлена: %d ms".formatted(value),
                            BotCommandBase.DEFAULT_MESSAGE_DELAY
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
