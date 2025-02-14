package org.aytsan_lex.twitchbot.commands;

import java.util.ArrayList;

import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

import org.aytsan_lex.twitchbot.*;

public class PermissionBotCommand extends BotCommandBase
{
    private enum SubCommand
    {
        GET,
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

        if (permissionLevel >= super.getRequiredPermissionLevel())
        {
            final String subCommand = args.get(0).toUpperCase();
            this.handleSubCommand(event, subCommand, new ArrayList<>(args.subList(1, args.size())));
        }
        else
        {
            TwitchBot.LOGGER.warn("{}: permission denied: {}/{}", userName, permissionLevel, super.getRequiredPermissionLevel());
        }
    }

    private void handleSubCommand(final IRCMessageEvent event,
                                  final String subCmd,
                                  final ArrayList<String> args) throws BotCommandError
    {
        final String channelName = event.getChannel().getName();
        final String senderUserName = event.getUser().getName();
        final int senderPermissionLevel = BotConfigManager.getPermissionLevel(senderUserName);

        try
        {
            switch (SubCommand.valueOf(subCmd))
            {
                case GET ->
                {
                    if (args.isEmpty())
                    {
                        throw new BotCommandError("Args are required for this command!");
                    }

                    final String targetUserName = args.get(0).toLowerCase();
                    final int permissionLevel = BotConfigManager.getPermissionLevel(targetUserName);

                    TwitchBot.replyToMessage(
                            channelName,
                            event.getMessageId().get(),
                            "Уровень доступа '%s': %d".formatted(targetUserName, permissionLevel)
                    );
                }

                case SET ->
                {
                    if (args.size() < 2)
                    {
                        throw new BotCommandError("Args are required for this command!");
                    }

                    final String targetUserName = args.get(0).toLowerCase();
                    final String targetUserId = event.getTwitchChat().getChannelNameToChannelId().get(targetUserName);
                    final int targetLevel = Integer.parseInt(args.get(1));
                    final int targetUserCurrentLevel = BotConfigManager.getPermissionLevel(targetUserName);

                    if (senderPermissionLevel < targetUserCurrentLevel)
                    {
                        TwitchBot.LOGGER.warn(
                                "{}: cannot set permission for '{}' with higher level then you: {} vs {}",
                                senderUserName,
                                targetUserName,
                                senderPermissionLevel,
                                targetUserCurrentLevel
                        );

                        TwitchBot.replyToMessage(
                                channelName,
                                event.getMessageId().get(),
                                "Нельзя изменить уровень доступа для пользователя с уровнем выше твоего: %d против %d"
                                        .formatted(senderPermissionLevel, targetUserCurrentLevel)
                        );
                        return;
                    }

                    if (!targetUserName.equals(senderUserName) && !BotConfigManager.getConfig().getRunningOnChannelId().equals(targetUserId))
                    {
                        BotConfigManager.setPermissionLevel(targetUserName.toLowerCase(), targetLevel);
                        BotConfigManager.saveConfig();

                        TwitchBot.replyToMessage(
                                channelName,
                                event.getMessageId().get(),
                                "Уровень доступа установлен '%s' -> %d".formatted(targetUserName, targetLevel)
                        );

                        TwitchBot.LOGGER.info("Permission level '{}' -> {}", targetUserName, targetLevel);
                    }
                }
            }
        }
        catch (IllegalArgumentException e)
        {
            TwitchBot.LOGGER.warn("Invalid sub-command for '{}': '{}'", this.getClass().getSimpleName(), subCmd);
        }
    }
}
