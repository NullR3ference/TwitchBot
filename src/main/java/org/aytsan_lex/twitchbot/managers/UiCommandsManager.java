package org.aytsan_lex.twitchbot.managers;

import java.util.HashMap;

import org.aytsan_lex.twitchbot.ui_commands.*;

public class UiCommandsManager implements IManager
{
    private static final HashMap<String, IUiCommand> commandAliases = new HashMap<>()
    {{
        put("requestconfig",    new UiRequestConfigCommand());
        put("requestfilters",   new UiRequestFiltersCommand());
        put("requestmutestate", new UiRequestMuteStateCommand());
        put("requeststatus",    new UiRequestStatusCommand());
        put("updateconfig",     new UiUpdateConfigCommand());
        put("updatefilters",    new UiUpdateFiltersCommand());
        put("mute",             new UiMuteCommand());
        put("addchannel",       new UiAddChannelCommand());
        put("rmchannel",        new UiRemoveChannelCommand());
        put("join",             new UiJoinChannelCommand());
        put("leave",            new UiLeaveChannelCommand());
        put("sendmessage",      new UiSendMessageCommand());
        put("setpermission",    new UiSetPermissionCommand());
        put("restart",          new UiRestartCommand());
    }};

    public IUiCommand getCommandByName(final String command)
    {
        return commandAliases.get(command);
    }

    @Override
    public boolean initialize() { return true; }

    @Override
    public void shutdown() { }
}
