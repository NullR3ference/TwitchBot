package org.aytsan_lex.twitchbot.managers;

import java.lang.reflect.Constructor;
import java.util.HashMap;

import org.aytsan_lex.twitchbot.TwitchBot;
import org.aytsan_lex.twitchbot.bot_commands.*;

public class BotCommandsManager implements IManager
{
    private static class BotCommandAlias
    {
        private final IBotCommand commandObject;
        private final Class<?> classObject;

        private BotCommandAlias(IBotCommand commandObject, Class<?> classObject)
        {
            this.commandObject = commandObject;
            this.classObject = classObject;
        }

        public static BotCommandAlias of(Class<? extends BotCommandBase> classObject)
        {
            IBotCommand commandObject;
            Constructor<? extends BotCommandBase> ctor;

            try
            {
                ctor = classObject.getConstructor();
                commandObject = ctor.newInstance();
            }
            catch (Exception ignored)
            {
                return null;
            }

            return new BotCommandAlias(commandObject, classObject);
        }

        public IBotCommand getCommandObject()
        {
            return this.commandObject;
        }

        public Class<?> getClassObject()
        {
            return this.classObject;
        }
    }

    private static final HashMap<String, BotCommandAlias> commandAliases = new HashMap<>()
    {{
        put("iq", BotCommandAlias.of(IqBotCommand.class));
        put("ben", BotCommandAlias.of(BenBotCommand.class));
        put("mira", BotCommandAlias.of(MiraBotCommand.class));
        put("join", BotCommandAlias.of(JoinToChatBotCommand.class));
        put("leave", BotCommandAlias.of(LeaveFromChatBotCommand.class));
        put("addchannel", BotCommandAlias.of(AddChannelBotCommand.class));
        put("rmchannel", BotCommandAlias.of(RemoveChannelBotCommand.class));
        put("iqmute", BotCommandAlias.of(IqMuteBotCommand.class));
        put("benmute", BotCommandAlias.of(BenMuteBotCommand.class));
        put("miramute", BotCommandAlias.of(MiraMuteBotCommand.class));
        put("perm", BotCommandAlias.of(PermissionBotCommand.class));
        put("readcfg", BotCommandAlias.of(ReadcfgBotCommand.class));
        put("restart", BotCommandAlias.of(RestartBotCommand.class));
        put("status", BotCommandAlias.of(StatusBotCommand.class));
        put("updatefilters", BotCommandAlias.of(UpdateFiltersBotCommand.class));
        put("filterinfo", BotCommandAlias.of(FiltersInfoBotCommand.class));
        put("setcd", BotCommandAlias.of(SetCooldownBotCommand.class));
        put("msgdelay", BotCommandAlias.of(MsgDelayBotCommand.class));
    }};

    public IBotCommand getCommandByName(final String name)
    {
        if (commandAliases.containsKey(name))
        {
            return commandAliases.get(name).getCommandObject();
        }
        return null;
    }

    public Class<?> getCommandClassByName(final String name)
    {
        if (commandAliases.containsKey(name))
        {
            return commandAliases.get(name).getClassObject();
        }
        return null;
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
