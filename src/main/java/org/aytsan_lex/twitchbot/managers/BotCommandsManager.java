package org.aytsan_lex.twitchbot.managers;

import java.util.HashMap;

import org.aytsan_lex.twitchbot.TwitchBot;
import org.aytsan_lex.twitchbot.bot_commands.*;

public class BotCommandsManager implements IManager
{
    private static final HashMap<String, IBotCommand> commandAliases = new HashMap<>(){{
        put("iq", new IqBotCommand());
        put("ben", new BenBotCommand());
        put("mira", new MiraBotCommand());
        put("join", new JoinToChatBotCommand());
        put("leave", new LeaveFromChatBotCommand());
        put("addchannel", new AddChannelBotCommand());
        put("rmchannel", new RemoveChannelBotCommand());
        put("iqmute", new IqMuteBotCommand());
        put("benmute", new BenMuteBotCommand());
        put("miramute", new MiraMuteBotCommand());
        put("perm", new PermissionBotCommand());
        put("readcfg", new ReadcfgBotCommand());
        put("restart", new RestartBotCommand());
        put("status", new StatusBotCommand());
        put("updatefilters", new UpdateFiltersBotCommand());
        put("filterinfo", new FiltersInfoBotCommand());
        put("setcd", new SetCooldownBotCommand());
        put("msgdelay", new MsgDelayBotCommand());
    }};

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
        return true;
    }

    @Override
    public void shutdown()
    {
        commandAliases.clear();
    }
}
