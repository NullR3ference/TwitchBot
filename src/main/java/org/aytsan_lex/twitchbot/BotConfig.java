package org.aytsan_lex.twitchbot;

import java.util.ArrayList;
import java.util.HashMap;

public class BotConfig
{
    public static class Credentials
    {
        private String clientId;
        private String accessToken;
        private String refreshToken;

        public boolean isEmpty()
        {
            return clientId.isEmpty() || accessToken.isEmpty() || refreshToken.isEmpty();
        }

        public String getClientId()
        {
            return this.clientId;
        }

        public String getAccessToken()
        {
            return this.accessToken;
        }

        public String getRefreshToken()
        {
            return this.refreshToken;
        }
    }

    private Credentials credentials;
    private String runningOnChannelId;
    private ArrayList<String> channels;
    private HashMap<String, String> timedOutOnChannels;
    private ArrayList<String> owners;
    private HashMap<String, Integer> permissions;
    private ArrayList<String> mutedCommands;
    private String ollamaHost;
    private String miraModelName;
    private String milaModelName;

    public Credentials getCredentials()
    {
        return this.credentials;
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

    public ArrayList<String> getOwners()
    {
        return this.owners;
    }

    public HashMap<String, Integer> getPermissions()
    {
        return this.permissions;
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

    public String getMilaModelName()
    {
        return this.milaModelName;
    }
}

