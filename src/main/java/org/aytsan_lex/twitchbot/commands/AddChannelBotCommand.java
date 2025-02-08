package org.aytsan_lex.twitchbot.commands;

import java.util.ArrayList;

import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

import org.aytsan_lex.twitchbot.TwitchBot;
import org.aytsan_lex.twitchbot.BotConfigManager;

public class AddChannelBotCommand extends BotCommandBase
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
        final String targetChannelName = args.get(0);

        if (permissionLevel >= super.getRequiredPermissionLevel())
        {
            if (TwitchBot.channelExists(targetChannelName))
            {
                if (BotConfigManager.addChannel(targetChannelName))
                {
                    TwitchBot.joinToChat(targetChannelName);
                    BotConfigManager.addChannel(targetChannelName);
                    BotConfigManager.writeConfig();

                    TwitchBot.LOGGER.info("Channel added: [{}]", targetChannelName);

                    TwitchBot.replyToMessage(
                            event.getChannel().getName(),
                            event.getMessageId().get(),
                            "Канал добавлен: [%s]".formatted(targetChannelName)
                    );
                }
            }
        }
        else
        {
            TwitchBot.LOGGER.warn("{}: permission denied: {}/{}", userName, permissionLevel, super.getRequiredPermissionLevel());
        }
    }
}
