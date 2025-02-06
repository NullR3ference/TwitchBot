package org.aytsan_lex.twitchbot.commands;

import java.util.ArrayList;

import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

import org.aytsan_lex.twitchbot.BotConfigManager;
import org.aytsan_lex.twitchbot.BotGlobalState;
import org.aytsan_lex.twitchbot.OllamaModelsManager;
import org.aytsan_lex.twitchbot.TwitchBot;

public class SetPermissionBotCommand extends BotCommandBase
{
    @Override
    public void execute(final IRCMessageEvent event, final ArrayList<String> args)
    {
        if (args.isEmpty())
        {
            throw new BotCommandError("Args are required for this command!");
        }

        final String userName = event.getUser().getName();
        final int permissionLevel = BotConfigManager.getPermissionLevel(userName);
        final int delay = BotConfigManager.getConfig().getDelayBetweenMessages();

        final String targetUserName = args.get(0).trim();
        final int targetLevel = Integer.parseInt(args.get(1).trim());
        final int targetUserCurrentLevel = BotConfigManager.getPermissionLevel(targetUserName);

        if (permissionLevel >= super.getRequiredPermissionLevel())
        {
            if (permissionLevel < targetUserCurrentLevel)
            {
                TwitchBot.LOGGER.warn(
                        "{}: cannot set permission for '{}' with higher level then you: {} vs {}",
                        userName,
                        targetUserName,
                        permissionLevel,
                        targetUserCurrentLevel
                );

                super.replyToMessageWithDelay(
                        event.getChannel(),
                        event.getUser().getId(),
                        event.getMessageId().get(),
                        event.getTwitchChat(),
                        "Нельзя изменить уровень доступа для пользователя с уровнем выше твоего: %d против %d"
                                .formatted(permissionLevel, targetUserCurrentLevel),
                        delay
                );
                return;
            }

            if (!targetUserName.toLowerCase().equals(userName))
            {
                BotConfigManager.setPermissionLevel(targetUserName.toLowerCase(), targetLevel);
                BotConfigManager.writeConfig();

                super.replyToMessageWithDelay(
                        event.getChannel(),
                        event.getUser().getId(),
                        event.getMessageId().get(),
                        event.getTwitchChat(),
                        "Уровень доступа установлен '%s' -> %d".formatted(targetUserName, targetLevel),
                        delay
                );

                TwitchBot.LOGGER.info("Permission level '{}' -> {}", targetUserName, targetLevel);

                try
                {
                    final String modelMessage =
                            "(системное сообщение): для пользователя '%s' установлен уровень доступа: %d (реагируй на него, если 0 - издевайся)"
                                    .formatted(targetUserName, targetLevel);

                    TwitchBot.LOGGER.info("Sending info to the model:\n{}", modelMessage);

                    BotGlobalState.setMiraCommandRunning(true);

                    final String response = OllamaModelsManager.getMiraModel().chatWithModel(modelMessage)
                            .trim()
                            .replaceAll("\\s{2,}", " ")
                            .replaceAll("—+", "-");

                    TwitchBot.LOGGER.info("Response: {}", response);

                    BotGlobalState.setMiraCommandRunning(false);

                    super.sendMessage(
                            event.getChannel(),
                            BotConfigManager.getConfig().getRunningOnChannelId(),
                            null,
                            event.getTwitchChat(),
                            response,
                            BotConfigManager.getConfig().getDelayBetweenMessages()
                    );
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        else
        {
            TwitchBot.LOGGER.warn("{}: permission denied: {}/{}", userName, permissionLevel, super.getRequiredPermissionLevel());
        }
    }
}
