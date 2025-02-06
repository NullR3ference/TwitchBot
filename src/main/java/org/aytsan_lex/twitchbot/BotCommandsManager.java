package org.aytsan_lex.twitchbot;

import java.util.HashMap;
import org.aytsan_lex.twitchbot.commands.*;

public class BotCommandsManager
{
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
    private static final IBotCommand setPermissionBotCommand = new SetPermissionBotCommand();
    private static final IBotCommand readcfgBotCommand = new ReadcfgBotCommand();
    private static final IBotCommand restartBotCommand = new RestartBotCommand();
    private static final IBotCommand statusBotCommand = new StatusBotCommand();
    private static final IBotCommand updateFiltersBotCommand = new UpdateFiltersBotCommand();
    private static final IBotCommand filtersInfoBotCommand = new FiltersInfoBotCommand();
    private static final IBotCommand setCooldownBotCommand = new SetCooldownBotCommand();
    private static final IBotCommand msgDelayBotCommand = new MsgDelayBotCommand();

    private static final HashMap<String, IBotCommand> commandAliases = new HashMap<>()
    {{
        put("iq", iqBotCommand);
        put("ben", benBotCommand);
        put("mira", miraBotCommand);
        put("join", joinToChatBotCommand);
        put("leave", leaveFromChatBotCommand);
        put("addchannel", addChannelBotCommand);
        put("rmchannel", removeChannelBotCommand);
        put("iqmute", iqMuteBotCommand);
        put("benmute", benMuteBotCommand);
        put("miramute", miraMuteBotCommand);
        put("permit", setPermissionBotCommand);
        put("readcfg", readcfgBotCommand);
        put("restart", restartBotCommand);
        put("status", statusBotCommand);
        put("updatefilters", updateFiltersBotCommand);
        put("filterinfo", filtersInfoBotCommand);
        put("setcd", setCooldownBotCommand);
        put("msgdelay", msgDelayBotCommand);
    }};

    public static IBotCommand getCommandByName(final String name)
    {
        return commandAliases.get(name);
    }

    public static void setCommandIsMuted(final String commandName, boolean isMuted)
    {
        BotConfigManager.setCommandIsMuted(commandName, isMuted);
        BotConfigManager.writeConfig();
    }
}
