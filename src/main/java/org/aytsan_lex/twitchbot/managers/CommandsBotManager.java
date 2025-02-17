package org.aytsan_lex.twitchbot.managers;

import java.util.HashMap;

import org.aytsan_lex.twitchbot.TwitchBot;
import org.aytsan_lex.twitchbot.bot_commands.*;

public class CommandsBotManager implements IBotManager
{
    private static final HashMap<String, IBotCommand> commandAliases = new HashMap<>();

    public IBotCommand getCommandByName(final String name)
    {
        return commandAliases.get(name);
    }

    public void setCommandIsMuted(Class<?> botCommandClass, boolean isMuted)
    {
        TwitchBot.getConfigManager().setCommandIsMuted(botCommandClass, isMuted);
        TwitchBot.getConfigManager().saveFile();
    }

    @Override
    public boolean initialize()
    {
        commandAliases.put("iq", new IqBotCommand());
        commandAliases.put("ben", new BenBotCommand());
        commandAliases.put("mira", new MiraBotCommand());
        commandAliases.put("join", new JoinToChatBotCommand());
        commandAliases.put("leave", new LeaveFromChatBotCommand());
        commandAliases.put("addchannel", new AddChannelBotCommand());
        commandAliases.put("rmchannel", new RemoveChannelBotCommand());
        commandAliases.put("iqmute", new IqMuteBotCommand());
        commandAliases.put("benmute", new BenMuteBotCommand());
        commandAliases.put("miramute", new MiraMuteBotCommand());
        commandAliases.put("perm", new PermissionBotCommand());
        commandAliases.put("readcfg", new ReadcfgBotCommand());
        commandAliases.put("restart", new RestartBotCommand());
        commandAliases.put("status", new StatusBotCommand());
        commandAliases.put("updatefilters", new UpdateFiltersBotCommand());
        commandAliases.put("filterinfo", new FiltersInfoBotCommand());
        commandAliases.put("setcd", new SetCooldownBotCommand());
        commandAliases.put("msgdelay", new MsgDelayBotCommand());
        return true;
    }

    @Override
    public void shutdown()
    {
        commandAliases.clear();
    }
}
