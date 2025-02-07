package org.aytsan_lex.twitchbot;

import java.util.ArrayList;
import java.util.HashMap;

public class BotConfig
{
    private HashMap<String, String> credentials;
    private String runningOnChannelId;
    private ArrayList<String> channels;
    private HashMap<String, String> timedOutOnChannels;
    private ArrayList<String> bannedOnChannels;
    private ArrayList<String> owners;
    private HashMap<String, Integer> permissions;
    private HashMap<String, Integer> commandPermissionLevels;
    private HashMap<String, Integer> commandCooldowns;
    private ArrayList<String> mutedCommands;
    private String ollamaHost;
    private String miraModelName;
    private String modelMessageTemplate;
    private int messageSendingMode;
    private int delayBetweenMessages;

    public static BotConfig empty()
    {
        return new BotConfig();
    }

    public String getClientId()
    {
        return this.credentials.get("clientId");
    }

    public String getAccessToken()
    {
        return this.credentials.get("accessToken");
    }

    public String getRefreshToken()
    {
        return this.credentials.get("refreshToken");
    }

    public String getRunningOnChannelId()
    {
        return this.runningOnChannelId;
    }

    public ArrayList<String> getChannels()
    {
        return this.channels;
    }

    public HashMap<String, String> getTimedOutOnChannels()
    {
        return this.timedOutOnChannels;
    }

    public ArrayList<String> getBannedOnChannels()
    {
        return this.bannedOnChannels;
    }

    public ArrayList<String> getOwners()
    {
        return this.owners;
    }

    public HashMap<String, Integer> getPermissions()
    {
        return this.permissions;
    }

    public HashMap<String, Integer> getCommandPermissionLevels()
    {
        return this.commandPermissionLevels;
    }

    public HashMap<String, Integer> getCommandCooldowns()
    {
        return this.commandCooldowns;
    }

    public ArrayList<String> getMutedCommands()
    {
        return this.mutedCommands;
    }

    public String getOllamaHost()
    {
        return this.ollamaHost;
    }

    public String getMiraModelName()
    {
        return this.miraModelName;
    }

    public String getModelMessageTemplate()
    {
        return this.modelMessageTemplate;
    }

    public int getMessageSendingMode()
    {
        return this.messageSendingMode;
    }

    public int getDelayBetweenMessages()
    {
        return this.delayBetweenMessages;
    }

    public void setDelayBetweenMessages(int value)
    {
        this.delayBetweenMessages = value;
    }
}
