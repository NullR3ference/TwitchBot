package org.aytsan_lex.twitchbot;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;
import com.github.twitch4j.common.events.domain.EventUser;


public class IrcChatMessageHandler
{
    private final HashMap<String, ChannelMessageLogger> channelLoggers = new HashMap<>();

    public IrcChatMessageHandler()
    {
        BotConfig.instance().getChannels().forEach(channelName -> {
            this.channelLoggers.put(channelName, new ChannelMessageLogger(channelName));
        });
    }

    public void handleIrcMessage(IRCMessageEvent event)
    {
        final String commandType = event.getCommandType();

        if (commandType.equals("PRIVMSG"))
        {
            final EventUser user = event.getUser();
            final Optional<String> optionalMessage = event.getMessage();

            if (user != null && optionalMessage.isPresent())
            {
                final String channelName = event.getChannel().getName();
                final String userId = user.getId();
                final String userName = user.getName();
                final String message = optionalMessage.get();

                if (message.startsWith("%") && userId.equals("654681357"))
                {
                    final ArrayList<String> cmdArgs = new ArrayList<>(
                            Arrays.asList(message.replaceFirst("^%", "").split(" "))
                    );

                    String replyMessage = "";

                    if (cmdArgs.size() > 1)
                    {
                        replyMessage = handleMultiargCommand(cmdArgs);
                    }
                    else
                    {
                        final String cmd = cmdArgs.get(0);

                        if (cmd.equals("status"))
                        {
                            replyMessage = "Active channels: %s".formatted(BotConfig.instance().getChannels().size());
                        }

                        else if (cmd.equals("lines"))
                        {
                            replyMessage = "Current log lines: %d"
                                    .formatted(this.channelLoggers.get(channelName).getCurrentLines());
                        }

                        else if (cmd.equals("stop"))
                        {
                            TwitchBot.instance().stop();
                        }
                    }

                    if (!replyMessage.isEmpty())
                    {
                        if (!event.getChannel().getId().equals(userId))
                        {
                            try { TimeUnit.MILLISECONDS.sleep(1100); }
                            catch (InterruptedException e) { }
                        }

                        event.getTwitchChat().sendMessage(
                                channelName,
                                replyMessage,
                                null,
                                event.getMessageId().get()
                        );
                    }
                }
                else
                {
                    final String messageTimestamp =
                            LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));

                    System.out.printf(
                            "[%s] [%s] (%s)[%s]: %s\n",
                            messageTimestamp,
                            channelName,
                            userId,
                            userName,
                            message
                    );

                    this.channelLoggers.get(event.getChannel().getName()).addMessage(event);
                }
            }
        }
    }

    private String handleMultiargCommand(ArrayList<String> cmdArgs)
    {
        final String cmd = cmdArgs.get(0);

        if (cmd.equals("add"))
        {
            final String newChannelName = cmdArgs.get(1);
            if (TwitchBot.instance().joinToChat(newChannelName))
            {
                if (BotConfig.instance().addChannel(newChannelName))
                {
                    this.channelLoggers.put(newChannelName, new ChannelMessageLogger(newChannelName));
                    BotConfig.instance().saveChanges();
                    return "Channel added [%s]".formatted(newChannelName);
                }
                else
                {
                    return "Channel [%s] already added".formatted(newChannelName);
                }
            }
        }

        else if (cmd.equals("del"))
        {
            final String channelName = cmdArgs.get(1);
            if (TwitchBot.instance().leaveFromChat(channelName))
            {
                if (BotConfig.instance().removeChannel(channelName))
                {
                    this.channelLoggers.remove(channelName);
                    BotConfig.instance().saveChanges();
                    return "Channel removed [%s]".formatted(channelName);
                }
                else
                {
                    return "Channel [%s] already removed".formatted(channelName);
                }
            }
        }

        else if (cmd.equals("clear"))
        {
            // TODO: Implement log dir clear for specific channel
        }

        return "";
    }
}
