package org.aytsan_lex.twitchbot.bot_commands;

import java.util.ArrayList;

import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

import org.aytsan_lex.twitchbot.TwitchBot;

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
        final int permissionLevel = TwitchBot.getConfigManager().getPermissionLevel(userName);

        if (permissionLevel >= this.getRequiredPermissionLevel())
        {
            if (args.size() >= 2)
            {
                final String subCommand = args.get(0).toUpperCase();
                this.handleSubCommand(event, subCommand, new ArrayList<>(args.subList(1, args.size())));
            }
            else
            {
                final int delayValue = TwitchBot.getConfigManager().getConfig().getDelayBetweenMessages();
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
            TwitchBot.LOG.warn("{}: permission denied: {}/{}", userName, permissionLevel, super.getRequiredPermissionLevel());
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
                    TwitchBot.getConfigManager().setDelayBetweenMessages(value);
                    TwitchBot.getConfigManager().saveFile();

                    TwitchBot.LOG.info("Delay between messages set: {} ms", value);

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
            TwitchBot.LOG.warn("Invalid sub-command for '{}': '{}'", this.getClass().getSimpleName(), cmd);
        }
    }
}
