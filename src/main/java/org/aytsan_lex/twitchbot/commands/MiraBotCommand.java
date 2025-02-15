package org.aytsan_lex.twitchbot.commands;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

import org.aytsan_lex.twitchbot.TwitchBot;
import org.aytsan_lex.twitchbot.filters.MiraFilters;
import org.aytsan_lex.twitchbot.ollama.ModelMessage;

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

    private static final Pattern forwardToChatPattern = Pattern.compile(
            "^#(\\w+):",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE
    );

    private LocalDateTime cooldownExpiresIn;

    public MiraBotCommand()
    {
        this.cooldownExpiresIn = null;
    }

    @Override
    public void execute(final IRCMessageEvent event, final ArrayList<String> args)
    {
        if (args.isEmpty())
        {
            throw new BotCommandError("Args are required for this command!");
        }

        final String channelName = event.getChannel().getName();
        final String userName = event.getUser().getName();
        final int permissionLevel = TwitchBot.getConfigManager().getPermissionLevel(userName);

        if (permissionLevel < this.getRequiredPermissionLevel())
        {
            TwitchBot.LOG.warn("{}: permission denied: {}/{}", userName, permissionLevel, super.getRequiredPermissionLevel());
            return;
        }

        if (super.isTimedOutOnChannelOrModify(channelName))
        {
            TwitchBot.LOG.warn("Command will not execute: you are timed out");
            return;
        }

        if (super.isMuted())
        {
            TwitchBot.LOG.warn("Command will not execute: command is muted!");
            return;
        }

        if (!TwitchBot.getOllamaModelsManager().checkConnection())
        {
            TwitchBot.LOG.warn("Ollama connection failed: {}", TwitchBot.getConfigManager().getConfig().getOllamaHost());
            return;
        }

        if (this.cooldownExpiresIn != null && LocalDateTime.now().isBefore(this.cooldownExpiresIn))
        {
            if (!TwitchBot.getConfigManager().isOwner(userName))
            {
                TwitchBot.LOG.warn("Command will not execute: cooldown");
                return;
            }
        }

        final String message = String.join(" ", args);
        final MiraFilters miraFilters = TwitchBot.getFiltersManager().getMiraFilters();

        if (!miraFilters.testPreFilter(message))
        {
            TwitchBot.replyToMessage(
                    channelName,
                    event.getMessageId().get(),
                    "Даже не пытайся, зайка )"
            );
            return;
        }

        final String finalMessage = this.buildModelMessage(userName, message);
        this.cooldownExpiresIn = LocalDateTime.now().plusSeconds(super.getCooldown());

        final String response = TwitchBot.getOllamaModelsManager().getMiraModel()
                .chatWithModel(new ModelMessage(userName, message, finalMessage))
                .trim()
                .replaceAll("\\s{2,}", " ")
                .replaceAll("—+", "-");

        final String filteredResponse = String.join(
                " ",
                miraFilters.splitWideWords(miraFilters.runReplacementFilter(miraFilters.runPostFilter(response)))
        );

        TwitchBot.LOG.info("Raw model response:\n{}", response);
        TwitchBot.LOG.info("Filtered model response:\n{}", filteredResponse);

        if (!super.isMuted())
        {
            String targetChannelName = channelName;
            String finalResponseMessage = filteredResponse;
            final Matcher matcher = forwardToChatPattern.matcher(finalResponseMessage);

            if (matcher.find())
            {
                targetChannelName = matcher.group(1);
                finalResponseMessage = finalResponseMessage.replaceAll(forwardToChatPattern.pattern(), "");
                TwitchBot.LOG.info("Forward to chat pattern triggered: {}", targetChannelName);
            }

            if (TwitchBot.isConnectedToChat(targetChannelName))
            {
                switch (MessageSendingMode.ofIntValue(TwitchBot.getConfigManager().getConfig().getMessageSendingMode()))
                {
                    case MSG_SINGLE -> TwitchBot.sendMessage(targetChannelName, miraFilters.truncateLength(finalResponseMessage));
                    case MSG_BLOCKS -> this.sendBlocks(targetChannelName, finalResponseMessage);
                }
            }
            else
            {
                TwitchBot.LOG.warn("Message will not send: not connected to chat of '{}'", targetChannelName);
            }
        }
        else
        {
            TwitchBot.LOG.warn("Message will not send: command is muted!");
        }
    }

    private void sendBlocks(final String channelName, final String response)
    {
        final ArrayList<String> messageBlocks = TwitchBot.getFiltersManager().getMiraFilters().splitMessageByBlocks(response);

        TwitchBot.LOG.info("Sending {} message block(s)...", messageBlocks.size());
        messageBlocks.forEach(msg -> TwitchBot.sendMessage(channelName, msg));
        TwitchBot.LOG.info("Message block(s) was sent");
    }

    private String buildModelMessage(final String userName, final String message)
    {
        String finalMessage = TwitchBot.getConfigManager().getConfig().getModelMessageTemplate();

        final String dateTimeStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        finalMessage = finalMessage.replaceAll("(<)datetime(>)", dateTimeStr);
        finalMessage = finalMessage.replaceAll("(<)username(>)", userName);
        finalMessage = finalMessage.replaceAll("(<)permlvl(>)", Integer.toString(TwitchBot.getConfigManager().getPermissionLevel(userName)));
        finalMessage = finalMessage.replaceAll("(<)message(>)", message.trim().replaceAll("\\s{2,}", " "));

        return finalMessage.trim();
    }
}
