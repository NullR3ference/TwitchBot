package org.aytsan_lex.twitchbot;

import java.util.Arrays;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

import org.aytsan_lex.twitchbot.commands.*;

public class CommandHandler
{
    public enum Commands
    {
        IQ,
        BEN,
        MIRA,

        JOIN,
        LEAVE,
        ADD,
        REMOVE,

        IQMUTE,
        BENMUTE,
        MIRAMUTE,

        PERMIT,
        READCFG,
        RESTART,
        STATUS,
        UPDATEFILTERS,
        FILTERSINFO,
        SETCD,
        MSGDELAY
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandHandler.class);

    public static void handleCommand(final String message, final IRCMessageEvent event)
    {
        final ArrayList<String> rawMessage = new ArrayList<>(
                Arrays.asList(message.replaceFirst("^%", "").split(" "))
        );

        final String cmd = rawMessage.get(0).trim().replaceAll("\\s+", "").toLowerCase();

        handleCommandsInNewThread(
                event,
                cmd,
                (rawMessage.size() >= 2)
                        ? new ArrayList<>(rawMessage.subList(1, rawMessage.size()))
                        : new ArrayList<>()
        );
    }

    private static void handleCommandsInNewThread(final IRCMessageEvent event,
                                                  final String cmd,
                                                  final ArrayList<String> cmdArgs)
    {
        final IBotCommand command = BotCommandsManager.getCommandByName(cmd);
        if (command != null)
        {
            LOGGER.info("[{}] Command: '{}', args: {}", event.getUser().getName(), cmd, cmdArgs);
            new Thread(() -> {
                try
                {
                    command.execute(event, cmdArgs);
                }
                catch (BotCommandError e)
                {
                    LOGGER.error("{} error: {}", command.getClass().getSimpleName(), e.getMessage());
                    Thread.currentThread().interrupt();
                }
                catch (Exception e)
                {
                    LOGGER.error("Error: {}", e.getMessage());
                    Thread.currentThread().interrupt();
                }
            }).start();
        }
        else
        {
            LOGGER.info("Invalid (or unknown) command: '{}'", cmd);
        }

//        new Thread(() -> {
//            try
//            {
//                switch (CommandHandler.Commands.valueOf(cmd))
//                {
//                    case IQ ->
//                    {
//                        if (!BotConfigManager.commandIsMuted(cmd))
//                        {
//                            final String messageText = String.join(" ", cmdArgs);
//                            iqBotCommand.execute(messageText, event);
//                        }
//                        else
//                        {
//                            LOGGER.warn("Iq command is muted");
//                        }
//                    }
//
//                    case BEN ->
//                    {
//                        if (!BotConfigManager.commandIsMuted(cmd))
//                        {
//                            final String messageText = String.join(" ", cmdArgs);
//                            benBotCommand.execute(messageText, event);
//                        }
//                        else
//                        {
//                            LOGGER.warn("Ben command is muted");
//                        }
//                    }
//
//                    case MIRA ->
//                    {
//                        if (!cmdArgs.isEmpty())
//                        {
//                            final String messageText = String.join(" ", cmdArgs);
//                            miraBotCommand.execute(messageText, event);
//                        }
//                    }
//
//                    case JOIN ->
//                    {
//                        if (!cmdArgs.isEmpty())
//                        {
//                            final String channelName = cmdArgs.get(0).trim();
//                            joinToChatBotCommand.execute(channelName, event);
//                        }
//                    }
//
//                    case LEAVE ->
//                    {
//                        String channelName = "";
//
//                        if (!cmdArgs.isEmpty())
//                        {
//                            channelName = cmdArgs.get(0).trim();
//                        }
//                        else
//                        {
//                            channelName = event.getChannel().getName();
//                        }
//
//                        leaveFromChatBotCommand.execute(channelName, event);
//                    }
//
//                    case ADD ->
//                    {
//                        if (!cmdArgs.isEmpty())
//                        {
//                            final String channelName = cmdArgs.get(0).trim();
//                            addChannelBotCommand.execute(channelName, event);
//                        }
//                    }
//
//                    case REMOVE ->
//                    {
//                        if (!cmdArgs.isEmpty())
//                        {
//                            final String channelName = cmdArgs.get(0).trim();
//                            removeChannelBotCommand.execute(channelName, event);
//                        }
//                    }
//
//                    case IQMUTE ->
//                    {
//                        boolean isMuted = true;
//                        if (!cmdArgs.isEmpty()) { isMuted = Boolean.parseBoolean(cmdArgs.get(0).trim()); }
//                        iqMuteBotCommand.execute(isMuted, event);
//                    }
//
//                    case BENMUTE ->
//                    {
//                        boolean isMuted = true;
//                        if (!cmdArgs.isEmpty()) { isMuted = Boolean.parseBoolean(cmdArgs.get(0).trim()); }
//                        benMuteBotCommand.execute(isMuted, event);
//                    }
//
//                    case MIRAMUTE ->
//                    {
//                        boolean isMuted = true;
//                        if (!cmdArgs.isEmpty()) { isMuted = Boolean.parseBoolean(cmdArgs.get(0).trim()); }
//                        miraMuteBotCommand.execute(isMuted, event);
//                    }
//
//                    case PERMIT ->
//                    {
//                        if (cmdArgs.size() >= 2)
//                        {
//                            final String targetUserName = cmdArgs.get(0).trim();
//                            final int targetPermissionLvl = Integer.parseInt(cmdArgs.get(1).trim());
//                            permitBotCommand.execute(targetUserName, targetPermissionLvl, event);
//                        }
//                    }
//
//                    case READCFG -> readcfgBotCommand.execute(event);
//                    case RESTART -> restartBotCommand.execute(event);
//                    case STATUS -> statusBotCommand.execute(event);
//                    case UPDATEFILTERS -> updateFiltersBotCommand.execute(event);
//                    case FILTERSINFO -> filterInfoBotCommand.execute(event);
//
//                    case SETCD ->
//                    {
//                        if (cmdArgs.size() >= 2)
//                        {
//                            final String commandName = cmdArgs.get(0).trim();
//                            final int cooldownValue = Integer.parseInt(cmdArgs.get(1).trim());
//                            setCooldownBotCommand.execute(event, commandName, cooldownValue);
//                        }
//                    }
//
//                    case MSGDELAY ->
//                    {
//                        if (cmdArgs.size() >= 2)
//                        {
//                            final String subCommand = cmdArgs.get(0).trim();
//                            final int delayValue = Integer.parseInt(cmdArgs.get(1).trim());
//                            msgDelayBotCommand.execute(event, subCommand, delayValue);
//                        }
//                        else
//                        {
//                            msgDelayBotCommand.execute(event);
//                        }
//                    }
//                }
//            }
//            catch (IllegalArgumentException e)
//            {
//                LOGGER.info("Invalid (or unknown) command: '{}'", cmd);
//            }
//            catch (BotCommandError e)
//            {
//                LOGGER.error("Bot command error '{}': {}", cmd, e.getMessage());
//            }
//        }).start();
    }
}
