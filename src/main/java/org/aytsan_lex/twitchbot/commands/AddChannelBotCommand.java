package org.aytsan_lex.twitchbot.commands;

import org.aytsan_lex.twitchbot.BotConfigManager;
import org.aytsan_lex.twitchbot.TwitchBot;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

public class AddChannelBotCommand extends BotCommandBase
{
    public AddChannelBotCommand()
    {
        super();
    }

    @Override
    public void execute(Object... args)
    {
        if (!(args[0] instanceof String targetChannelName) || !(args[1] instanceof IRCMessageEvent event))
        {
            throw new BotCommandError("Invalid args classes");
        }

        final String userName = event.getUser().getName();
        final int permissionLevel = BotConfigManager.getPermissionLevel(userName);

        if (permissionLevel >= super.getRequiredPermissionLevel())
        {
            if (TwitchBot.instance().channelExists(targetChannelName))
            {
                if (BotConfigManager.addChannel(targetChannelName))
                {
                    TwitchBot.instance().joinToChat(targetChannelName);
                    BotConfigManager.writeConfig();

                    super.replyToMessageWithDelay(
                            event.getChannel(),
                            event.getUser().getId(),
                            event.getMessageId().get(),
                            event.getTwitchChat(),
                            "Канал добавлен: [%s]".formatted(targetChannelName),
                            BotCommandBase.DEFAULT_MESSAGE_DELAY
                    );

                    TwitchBot.LOGGER.info("Channel added: [{}]", targetChannelName);
                }
            }
        }
        else
        {
            TwitchBot.LOGGER.warn("{}: permission denied: {}/{}", userName, permissionLevel, super.getRequiredPermissionLevel());
        }
    }
}
