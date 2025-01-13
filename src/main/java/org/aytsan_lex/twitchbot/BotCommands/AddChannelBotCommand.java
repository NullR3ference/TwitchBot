package org.aytsan_lex.twitchbot.BotCommands;

import org.aytsan_lex.twitchbot.BotConfig;
import org.aytsan_lex.twitchbot.TwitchBot;
import com.github.twitch4j.chat.TwitchChat;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

public class AddChannelBotCommand extends BotCommandBase
{
    public AddChannelBotCommand()
    {
        super(777);
    }

    @Override
    public void execute(Object... args)
    {
        if (!(args[0] instanceof String targetChannelName) || !(args[1] instanceof IRCMessageEvent event))
        {
            throw new BotCommandError("Invalid args classes");
        }

        final String userId = event.getUser().getId();
        final String messageId = event.getMessageId().get();
        final String channelName = event.getChannel().getName();
        final TwitchChat chat = event.getTwitchChat();
        final int permissionLevel = BotConfig.instance().getPermissionLevel(userId);

        if (permissionLevel >= super.getRequiredPermissionLevel())
        {
            if (TwitchBot.instance().channelExists(targetChannelName))
            {
                if (BotConfig.instance().addChannel(targetChannelName))
                {
                    TwitchBot.instance().joinToChat(targetChannelName);
                    BotConfig.instance().saveChanges();

                    super.replyToMessageWithDelay(
                            channelName,
                            userId,
                            messageId,
                            chat,
                            "Канал добавлен: [%s]".formatted(targetChannelName),
                            BotCommandBase.DEFAULT_MESSAGE_DELAY
                    );
                }
            }
        }
    }
}
