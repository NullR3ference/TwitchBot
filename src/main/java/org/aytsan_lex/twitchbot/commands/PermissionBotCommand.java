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
        final int delay = BotConfigManager.getConfig().getDelayBetweenMessages();

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

                    final String targetUserName = args.get(0).trim().toLowerCase();
                    final int permissionLevel = BotConfigManager.getPermissionLevel(targetUserName);

                    super.replyToMessage(
                            event.getChannel(),
                            event.getTwitchChat(),
                            event.getMessageId().get(),
                            "Уровень доступа '%s': %d".formatted(targetUserName, permissionLevel),
                            delay
                    );
                }

                case SET ->
                {
                    if (args.size() < 2)
                    {
                        throw new BotCommandError("Args are required for this command!");
                    }

                    final String targetUserName = args.get(0).toLowerCase();
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

                        super.replyToMessage(
                                event.getChannel(),
                                event.getTwitchChat(),
                                event.getMessageId().get(),
                                "Нельзя изменить уровень доступа для пользователя с уровнем выше твоего: %d против %d"
                                        .formatted(senderPermissionLevel, targetUserCurrentLevel),
                                delay
                        );
                        return;
                    }

                    if (!targetUserName.equals(senderUserName))
                    {
                        BotConfigManager.setPermissionLevel(targetUserName.toLowerCase(), targetLevel);
                        BotConfigManager.writeConfig();

                        if (!super.isTimedOutOnChannelOrModify(channelName))
                        {
                            new Thread(() -> {
                                try
                                {
                                    final String modelMessage =
                                            "(системное сообщение): для пользователя '%s' установлен уровень доступа: %d (реагируй на него, издевайся, кратко, упомянай его по имени)"
                                                    .formatted(targetUserName, targetLevel);

                                    TwitchBot.LOGGER.info("Sending info to the model:\n{}", modelMessage);

                                    final String filteredResponse = FiltersManager.getMiraFilters().runPostFilter(
                                            OllamaModelsManager.getMiraModel().chatWithModel(modelMessage)
                                                    .trim()
                                                    .replaceAll("\\s{2,}", " ")
                                                    .replaceAll("—+", "-")
                                    );

                                    TwitchBot.LOGGER.info("Response: {}", filteredResponse);

                                    super.sendMessage(
                                            event.getChannel(),
                                            event.getTwitchChat(),
                                            filteredResponse,
                                            BotConfigManager.getConfig().getDelayBetweenMessages()
                                    );
                                }
                                catch (Exception e)
                                {
                                    e.printStackTrace();
                                    Thread.currentThread().interrupt();
                                }
                            }).start();
                        }

                        super.replyToMessage(
                                event.getChannel(),
                                event.getTwitchChat(),
                                event.getMessageId().get(),
                                "Уровень доступа установлен '%s' -> %d".formatted(targetUserName, targetLevel),
                                delay
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
