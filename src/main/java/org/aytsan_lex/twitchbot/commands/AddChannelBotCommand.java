package org.aytsan_lex.twitchbot.commands;

import java.util.ArrayList;
import org.aytsan_lex.twitchbot.TwitchBot;
import org.aytsan_lex.twitchbot.BotConfigManager;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

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
                            BotConfigManager.getConfig().getDelayBetweenMessages()
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
