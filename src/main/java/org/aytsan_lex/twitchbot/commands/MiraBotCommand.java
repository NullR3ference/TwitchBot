package org.aytsan_lex.twitchbot.commands;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.aytsan_lex.twitchbot.*;
import com.github.twitch4j.chat.TwitchChat;
import com.github.twitch4j.common.events.domain.EventChannel;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

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
    public void execute(Object... args)
    {
        if (!(args[0] instanceof String message) || !(args[1] instanceof IRCMessageEvent event))
        {
            throw new BotCommandError("Invalid args classes");
        }

        if (BotConfigManager.commandIsMuted(CommandHandler.Commands.MIRA.name()))
        {
            TwitchBot.LOGGER.warn("Command will not execute: command is muted!");
            return;
        }

        if (this.cooldownExpiresIn != null && LocalDateTime.now().isBefore(this.cooldownExpiresIn))
        {
            TwitchBot.LOGGER.warn("Command will not execute: cooldown");
            return;
        }

        final EventChannel channel = event.getChannel();
        final String userId = event.getUser().getId();
        final String userName = event.getUser().getName();
        final String messageId = event.getMessageId().get();
        final TwitchChat chat = event.getTwitchChat();
        final int permissionLevel = BotConfigManager.getPermissionLevel(userName);
        final int delay = BotConfigManager.getConfig().getDelayBetweenMessages();

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

        if (BotGlobalState.votingIsActive())
        {
            TwitchBot.LOGGER.warn("Cannot interact with Mira while voting is active");
            super.replyToMessageWithDelay(
                    channel,
                    userId,
                    messageId,
                    chat,
                    "Прости зайка, я не могу отвечать, пока голосование активно ((",
                    delay
            );
            return;
        }

        if (!miraPreFilter(message))
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

        final String filteredResponse = this.splitWideWords(this.miraPostFilter(response));

        TwitchBot.LOGGER.info("Raw model response:\n{}", response);
        TwitchBot.LOGGER.info("Filtered model response:\n{}", filteredResponse);

        if (BotConfigManager.commandIsMuted(CommandHandler.Commands.MIRA.name()))
        {
            TwitchBot.LOGGER.warn("Message will not send: command is muted!");
            return;
        }

        switch (MessageSendingMode.ofIntValue(BotConfigManager.getConfig().getMessageSendingMode()))
        {
            case MSG_SINGLE ->
                    super.replyToMessageWithDelay(
                            channel,
                            userId,
                            messageId,
                            chat,
                            this.truncateLength(filteredResponse),
                            delay
                    );

            case MSG_BLOCKS ->
                    this.sendBlocks(channel, userId, messageId, chat, delay, filteredResponse);
        }

        if (!this.checkForMuteCommandContext(filteredResponse))
        {
            TwitchBot.LOGGER.warn("Detected Mute context word, Mira will be muted!");
            BotConfigManager.setCommandIsMuted(CommandHandler.Commands.MIRA.name(), true);
        }

        BotGlobalState.setMiraCommandRunning(false);
    }

    private boolean miraPreFilter(final String messageText)
    {
        final ArrayList<Pattern> filters = FiltersManager.getMiraFilters().getPreFilter();

        for (final Pattern pattern : filters)
        {
            if (pattern.matcher(messageText).find())
            {
                TwitchBot.LOGGER.warn("Mira pre-filter failed: {}", messageText);
                return false;
            }
        }

        return true;
    }

    private String miraPostFilter(final String response)
    {
        String filtered = response;
        final ArrayList<Pattern> filters = FiltersManager.getMiraFilters().getPostFilter();

        for (final Pattern pattern : filters)
        {
            final Matcher matcher = pattern.matcher(filtered);
            if (matcher.find())
            {
                filtered = matcher.replaceAll(" * ");
                TwitchBot.LOGGER.warn("Mira post-filter triggered: '{}'", matcher.pattern());
            }
        }

        return filtered;
    }

    private boolean checkForMuteCommandContext(final String postFilteredResponse)
    {
        final ArrayList<Pattern> muteCommandsContext = FiltersManager.getMiraFilters().getMuteCommandsFilter();

        for (final Pattern pattern : muteCommandsContext)
        {
            if (pattern.matcher(postFilteredResponse).find())
            {
                return false;
            }
        }

        return true;
    }

    private String splitWideWords(final String response)
    {
        final ArrayList<String> words =
                this.splitByMaxLen(response, FiltersManager.getMiraFilters().getWordLengthFilter());

        return String.join(" ", words);
    }

    private String truncateLength(final String response)
    {
        final int maxLength = FiltersManager.getMiraFilters().getMessageLengthFilter();
        if (response.length() <= maxLength) { return response; }
        return response.substring(0, maxLength - 4).concat("...");
    }

    private ArrayList<String> splitByMaxLen(final String str, final int maxLen)
    {
        final String[] words = str.split(" ");
        final ArrayList<String> result = new ArrayList<>();

        for (final String word : words)
        {
            for (int i = 0; i < word.length(); i += maxLen)
            {
                final int end = Math.min(i + maxLen, word.length());
                result.add(word.substring(i, end).trim());
            }
        }

        return result;
    }

    private void sendBlocks(final EventChannel channel,
                            final String userId,
                            final String messageId,
                            final TwitchChat chat,
                            final int delay,
                            final String response)
    {
        final int blockLength = FiltersManager.getMiraFilters().getMessageLengthFilter();
        final int responseLength = response.length();

        TwitchBot.LOGGER.info("Sending {} message block(s)...", (responseLength / blockLength) + 1);

        for (int i = 0; i < responseLength; i += blockLength)
        {
            final int index = Math.min(i + blockLength, responseLength);
            this.replyToMessageWithDelay(
                    channel,
                    userId,
                    messageId,
                    chat,
                    response.substring(i, index),
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
