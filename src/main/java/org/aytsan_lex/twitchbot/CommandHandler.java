package org.aytsan_lex.twitchbot;

import java.util.ArrayList;
import java.util.Arrays;
import org.aytsan_lex.twitchbot.BotCommands.*;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandHandler
{
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandHandler.class);

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
    }

    private static final IBotCommand iqBotCommand = new IqBotCommand();
    private static final IBotCommand benBotCommand = new BenBotCommand();
    private static final IBotCommand miraBotCommand = new MiraBotCommand();
    private static final IBotCommand joinToChatBotCommand = new JoinToChatBotCommand();
    private static final IBotCommand leaveFromChatBotCommand = new LeaveFromChatBotCommand();
    private static final IBotCommand addChannelBotCommand = new AddChannelBotCommand();
    private static final IBotCommand removeChannelBotCommand = new RemoveChannelBotCommand();
    private static final IBotCommand iqMuteBotCommand = new IqMuteBotCommand();
    private static final IBotCommand benMuteBotCommand = new BenMuteBotCommand();
    private static final IBotCommand miraMuteBotCommand = new MiraMuteBotCommand();
    private static final IBotCommand permitBotCommand = new SetPermissionBotCommand();
    private static final IBotCommand readcfgBotCommand = new ReadcfgBotCommand();
    private static final IBotCommand restartBotCommand = new RestartBotCommand();
    private static final IBotCommand statusBotCommand = new StatusBotCommand();

    public static void handleCommand(final String message,
                                     final IRCMessageEvent event)
    {
        final ArrayList<String> cmdArgs = new ArrayList<>(
                Arrays.asList(message.replaceFirst("^%", "").split(" "))
        );

        final String cmd = cmdArgs.get(0).trim().toUpperCase();
        cmdArgs.remove(0);

        LOGGER.info("Command: '{}', args: {}", cmd, cmdArgs);

        try
        {
            switch (CommandHandler.Commands.valueOf(cmd))
            {
                case IQ ->
                {
                    if (!BotConfig.instance().commandIsMuted(cmd))
                    {
                        final String messageText = String.join(" ", cmdArgs);
                        iqBotCommand.execute(messageText, event);
                    }
                    else
                    {
                        LOGGER.warn("Iq command is muted");
                    }
                }

                case BEN ->
                {
                    if (!BotConfig.instance().commandIsMuted(cmd))
                    {
                        final String messageText = String.join(" ", cmdArgs);
                        benBotCommand.execute(messageText, event);
                    }
                    else
                    {
                        LOGGER.warn("Ben command is muted");
                    }
                }

                case MIRA ->
                {
                    if (!BotConfig.instance().commandIsMuted(cmd))
                    {
                        if (!cmdArgs.isEmpty())
                        {
                            final String messageText = String.join(" ", cmdArgs);
                            miraBotCommand.execute(messageText, event);
                        }
                    }
                    else
                    {
                        LOGGER.warn("Mira command is muted");
                    }
                }

                case JOIN ->
                {
                    if (!cmdArgs.isEmpty())
                    {
                        final String channelName = cmdArgs.get(0).trim();
                        joinToChatBotCommand.execute(channelName, event);
                    }
                }

                case LEAVE ->
                {
                    String channelName = "";

                    if (!cmdArgs.isEmpty())
                    {
                        channelName = cmdArgs.get(0).trim();
                    }
                    else
                    {
                        channelName = event.getChannel().getName();
                    }

                    leaveFromChatBotCommand.execute(channelName, event);
                }

                case ADD ->
                {
                    if (!cmdArgs.isEmpty())
                    {
                        final String channelName = cmdArgs.get(0).trim();
                        addChannelBotCommand.execute(channelName, event);
                    }
                }

                case REMOVE ->
                {
                    if (!cmdArgs.isEmpty())
                    {
                        final String channelName = cmdArgs.get(0).trim();
                        removeChannelBotCommand.execute(channelName, event);
                    }
                }

                case IQMUTE ->
                {
                    boolean isMuted = true;
                    if (!cmdArgs.isEmpty()) { isMuted = Boolean.parseBoolean(cmdArgs.get(0).trim()); }
                    iqMuteBotCommand.execute(isMuted, event);
                }

                case BENMUTE ->
                {
                    boolean isMuted = true;
                    if (!cmdArgs.isEmpty()) { isMuted = Boolean.parseBoolean(cmdArgs.get(0).trim()); }
                    benMuteBotCommand.execute(isMuted, event);
                }

                case MIRAMUTE ->
                {
                    boolean isMuted = true;
                    if (!cmdArgs.isEmpty()) { isMuted = Boolean.parseBoolean(cmdArgs.get(0).trim()); }
                    miraMuteBotCommand.execute(isMuted, event);
                }

                case PERMIT ->
                {
                    if (cmdArgs.size() >= 2)
                    {
                        final String targetUserName = cmdArgs.get(0).trim();
                        final int targetPermissionLvl = Integer.parseInt(cmdArgs.get(1));
                        permitBotCommand.execute(targetUserName, targetPermissionLvl, event);
                    }
                }

                case READCFG -> readcfgBotCommand.execute(event);
                case RESTART -> restartBotCommand.execute(event);
                case STATUS -> statusBotCommand.execute(event);
            }
        }
        catch (IllegalArgumentException e)
        {
            LOGGER.info("Invalid (or unknown) command: '{}'", cmd);
        }
        catch (BotCommandError e)
        {
            LOGGER.error("Bot command error '{}': {}", cmd, e.getMessage());
        }
    }
}
