package org.aytsan_lex.twitchbot.bot_commands;

import java.util.ArrayList;

import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

import org.aytsan_lex.twitchbot.TwitchBot;

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
        final int permissionLevel = TwitchBot.getConfigManager().getPermissionLevel(userName);

        if (permissionLevel >= super.getRequiredPermissionLevel())
        {
            final String targetChannelName = args.get(0);

            if (TwitchBot.channelExists(targetChannelName))
            {
                if (TwitchBot.getConfigManager().addChannel(targetChannelName))
                {
                    TwitchBot.joinToChat(targetChannelName);
                    TwitchBot.getConfigManager().addChannel(targetChannelName);
                    TwitchBot.getConfigManager().saveFile();

                    TwitchBot.LOG.info("Channel added: [{}]", targetChannelName);

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
            TwitchBot.LOG.warn("{}: permission denied: {}/{}", userName, permissionLevel, super.getRequiredPermissionLevel());
        }
    }
}
