package org.aytsan_lex.twitchbot;

import java.util.ArrayList;
import java.util.Arrays;
import org.aytsan_lex.twitchbot.BotCommands.*;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

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

        BENMUTE,
        MIRAMUTE,
        IQMUTE,

        PERMIT,

        READCFG,
        RESTART,
    }

    public static void handleCommand(final String message,
                                     final IRCMessageEvent event)
    {
        final ArrayList<String> cmdArgs = new ArrayList<>(
                Arrays.asList(message.replaceFirst("^%", "").split(" "))
        );

        final String cmd = cmdArgs.get(0).trim().toUpperCase();
        cmdArgs.remove(0);

        System.out.println("Command: " + Arrays.asList(cmd));
        System.out.println("Args: " + cmdArgs);

        try
        {
            switch (CommandHandler.Commands.valueOf(cmd))
            {
                case IQ ->
                {
                    if (!BotCommandSharedState.iqCommandIsMuted())
                    {
                        final String messageText = String.join(" ", cmdArgs);
                        new IqBotCommand().execute(messageText, event);
                    }
                }

                case BEN ->
                {
                    if (!BotCommandSharedState.benCommandIsMuted())
                    {
                        final String messageText = String.join(" ", cmdArgs);
                        new BenBotCommand().execute(messageText, event);
                    }
                }

                case MIRA ->
                {
                    if (!BotCommandSharedState.miraCommandIsMuted())
                    {
                        if (!cmdArgs.isEmpty())
                        {
                            final String messageText = String.join(" ", cmdArgs);
                            new MiraBotCommand().execute(messageText, event);
                        }
                    }
                }

                case JOIN ->
                {
                    if (!cmdArgs.isEmpty())
                    {
                        final String channelName = cmdArgs.get(0);
                        new JoinToChatCommand().execute(channelName, event);
                    }
                }

                case LEAVE ->
                {
                    String channelName = "";

                    if (!cmdArgs.isEmpty())
                    {
                        channelName = cmdArgs.get(0);
                    }
                    else
                    {
                        channelName = event.getChannel().getName();
                    }

                    new LeaveFromChatCommand().execute(channelName, event);
                }

                case ADD ->
                {
                    if (!cmdArgs.isEmpty())
                    {
                        final String channelName = cmdArgs.get(0);
                        new AddChannelBotCommand().execute(channelName, event);
                    }
                }

                case REMOVE ->
                {
                    if (!cmdArgs.isEmpty())
                    {
                        final String channelName = cmdArgs.get(0);
                        new RemoveChannelBotCommand().execute(channelName, event);
                    }
                }

                case BENMUTE ->
                {
                    boolean isMuted = true;
                    if (!cmdArgs.isEmpty()) { isMuted = Boolean.parseBoolean(cmdArgs.get(0)); }
                    new BenMuteBotCommand().execute(isMuted, event);
                }

                case MIRAMUTE ->
                {
                    boolean isMuted = true;
                    if (!cmdArgs.isEmpty()) { isMuted = Boolean.parseBoolean(cmdArgs.get(0)); }
                    new MiraMuteBotCommand().execute(isMuted, event);
                }

                case IQMUTE ->
                {
                    if (!cmdArgs.isEmpty())
                    {
                        final boolean isMuted = Boolean.parseBoolean(cmdArgs.get(0));
                        BotCommandSharedState.setIqCommandIsMuted(isMuted);
                    }
                    else { BotCommandSharedState.setIqCommandIsMuted(true); }
                }

                case PERMIT ->
                {
                    if (!cmdArgs.isEmpty() && cmdArgs.size() >= 2)
                    {
                        final String targetUserName = cmdArgs.get(0);
                        final int permissionLevel = Integer.parseInt(cmdArgs.get(1));
                        new SetPermissionBotCommand().execute(targetUserName, permissionLevel, event);
                    }
                }

                case READCFG ->
                {
                    // TODO: Implement readcfg command
                }

                case RESTART ->
                {
                    // TODO: Implement restart command
                    // Something like watchdog which handle the return code from process
                    // https://stackoverflow.com/questions/4159802/how-can-i-restart-a-java-application
                    // https://stackoverflow.com/questions/27306764/capturing-exit-status-code-of-child-process
                    // https://www.man7.org/linux/man-pages/man2/fork.2.html
                    // https://www.man7.org/linux/man-pages/man2/execve.2.html
                    // https://www.man7.org/linux/man-pages/man2/waitpid.2.html
                }
            }
        }
        catch (IllegalArgumentException e)
        {
            System.err.println("Unknown command '%s'".formatted(cmd));
        }
        catch (BotCommandError e)
        {
            System.err.println("Bot command error '%s': %s".formatted(cmd, e.getMessage()));
        }
    }
}
