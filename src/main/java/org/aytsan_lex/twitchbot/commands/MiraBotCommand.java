package org.aytsan_lex.twitchbot.commands;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import org.aytsan_lex.twitchbot.*;
import com.github.twitch4j.chat.TwitchChat;
import com.github.twitch4j.common.events.domain.EventChannel;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;
import org.aytsan_lex.twitchbot.filters.MiraFilters;

public class MiraBotCommand extends BotCommandBase
{
    private enum MessageSendingMode
    {
        MSG_SINGLE,
        MSG_BLOCKS;

        public static MessageSendingMode ofIntValue(int val)
        {
            switch (val)
            {
                case 0 -> { return MSG_SINGLE; }
                case 1 -> { return MSG_BLOCKS; }
            }
            return (val < 1) ? MSG_SINGLE : MSG_BLOCKS;
        }
    }

    private LocalDateTime cooldownExpiresIn;

    public MiraBotCommand()
    {
        super();
        this.cooldownExpiresIn = null;
    }

    @Override
    public void execute(final IRCMessageEvent event, final ArrayList<String> args)
    {
        if (args.isEmpty())
        {
            throw new BotCommandError("Args are required for this command!");
        }

        final String userId = event.getUser().getId();
        final String runningOnChannelId = BotConfigManager.getConfig().getRunningOnChannelId();

        if (this.cooldownExpiresIn != null && LocalDateTime.now().isBefore(this.cooldownExpiresIn))
        {
            if (!userId.equals(runningOnChannelId))
            {
                TwitchBot.LOGGER.warn("Command will not execute: cooldown");
                return;
            }
        }

        final EventChannel channel = event.getChannel();
        final String userName = event.getUser().getName();
        final String messageId = event.getMessageId().get();
        final TwitchChat chat = event.getTwitchChat();
        final int permissionLevel = BotConfigManager.getPermissionLevel(userName);
        final int delay = BotConfigManager.getConfig().getDelayBetweenMessages();
        final MiraFilters miraFilters = FiltersManager.getMiraFilters();
        final String message = String.join(" ", args).trim().replaceAll("\\s{2,}", "");

        if (!OllamaModelsManager.checkConnection())
        {
            TwitchBot.LOGGER.warn("Ollama connection failed: {}", BotConfigManager.getConfig().getOllamaHost());
            super.replyToMessageWithDelay(
                    channel,
                    userId,
                    messageId,
                    chat,
                    "Ошибка сети. Возможно я отключена. Прости зайка <3",
                    delay
            );
            return;
        }

        if (permissionLevel < this.getRequiredPermissionLevel())
        {
            TwitchBot.LOGGER.warn("{}: permission denied: {}/{}", userName, permissionLevel, super.getRequiredPermissionLevel());
            super.replyToMessageWithDelay(
                    channel,
                    userId,
                    messageId,
                    chat,
                    "Прости зайка, создателю надо выдать разрешение, прежде чем я смогу тебе отвечать ((",
                    delay
            );
            return;
        }

        if (super.isMuted())
        {
            TwitchBot.LOGGER.warn("Command will not execute: command is muted!");
            super.sendMessage(
                    channel,
                    runningOnChannelId,
                    null,
                    chat,
                    "@%s Прости зайка, я сплю spit".formatted(userName),
                    delay
            );
            return;
        }

        if (!miraFilters.testPreFilter(message))
        {
            super.replyToMessageWithDelay(
                    channel,
                    userId,
                    messageId,
                    chat,
                    "Даже не пытайся, зайка )",
                    delay
            );
            return;
        }

        final String finalMessage = this.buildModelMessage(userName, message).trim();
        if (finalMessage.isEmpty())
        {
            TwitchBot.LOGGER.warn("Command fill not execute: request message is empty!");
            return;
        }

        BotGlobalState.setMiraCommandRunning(true);
        this.cooldownExpiresIn = LocalDateTime.now().plusSeconds(super.getCooldown());

        final String response = OllamaModelsManager.getMiraModel()
                .chatWithModel(finalMessage)
                .trim()
                .replaceAll("\\s{2,}", " ")
                .replaceAll("—+", "-");

        final String filteredResponse = String.join(
                " ",
                miraFilters.splitWideWords(miraFilters.runPostFilter(response))
        );

        TwitchBot.LOGGER.info("Raw model response:\n{}", response);
        TwitchBot.LOGGER.info("Filtered model response:\n{}", filteredResponse);

        if (super.isMuted())
        {
            TwitchBot.LOGGER.warn("Message will not send: command is muted!");
            return;
        }

        switch (MessageSendingMode.ofIntValue(BotConfigManager.getConfig().getMessageSendingMode()))
        {
            case MSG_SINGLE ->
                    super.sendMessage(
                            channel,
                            runningOnChannelId,
                            null,
                            chat,
                            miraFilters.truncateLength(filteredResponse),
                            delay
                    );

            case MSG_BLOCKS ->
                    this.sendBlocks(channel, runningOnChannelId, chat, delay, filteredResponse);
        }

        if (!miraFilters.testMuteCommandsFilter(filteredResponse))
        {
            TwitchBot.LOGGER.warn("Detected Mute context word, Mira will be muted!");
            BotCommandsManager.setCommandIsMuted(this.getClass().getSimpleName(), true);
        }

        BotGlobalState.setMiraCommandRunning(false);
    }

    private void sendBlocks(final EventChannel channel,
                            final String userId,
                            final TwitchChat chat,
                            final int delay,
                            final String response)
    {
        final ArrayList<String> messageBlocks = FiltersManager.getMiraFilters().splitMessageByBlocks(response);
        TwitchBot.LOGGER.info("Sending {} message block(s)...", messageBlocks.size());
        for (final String msg : messageBlocks)
        {
            this.sendMessage(
                    channel,
                    userId,
                    null,
                    chat,
                    msg,
                    delay
            );
        }
        TwitchBot.LOGGER.info("Message block(s) was sent");
    }

    private String buildModelMessage(final String userName, final String message)
    {
        String finalMessage = BotConfigManager.getConfig().getModelMessageTemplate();

        final String dateTimeStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        finalMessage = finalMessage.replaceAll("(<)datetime(>)", dateTimeStr);
        finalMessage = finalMessage.replaceAll("(<)username(>)", userName);
        finalMessage = finalMessage.replaceAll("(<)permlvl(>)", Integer.toString(BotConfigManager.getPermissionLevel(userName)));
        finalMessage = finalMessage.replaceAll("(<)message(>)", message.trim().replaceAll("\\s{2,}", " "));

        return finalMessage;
    }
}
