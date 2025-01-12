package org.aytsan_lex.twitchbot;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.util.Objects;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.FormattingStyle;

public class BotConfig
{
    private class Config
    {
        private class Credentials
        {
            public String clientId;
            public String accessToken;
            public String refreshToken;
            public ArrayList<String> tokenScopes;
        }

        public Credentials credentials;
        public ArrayList<String> channels;
        public ArrayList<String> owners;
        public HashMap<String, Integer> permissions;
        public ArrayList<String> mutedCommands;
    }

    private static BotConfig botConfigInstance = null;
    private static final String CURRENT_WORKING_DIR = Path.of("").toAbsolutePath().toString();

    public static final Path LOG_BASE_PATH = Path.of(CURRENT_WORKING_DIR + "/chatlogs");
    public static final Path CONFIG_PATH = Path.of(CURRENT_WORKING_DIR + "/config");

    private final File configFile;
    private Config config;

    private BotConfig()
    {
        this.configFile = new File(CONFIG_PATH + "/config.json");
        this.config = new Config();

        try
        {
            if (!Files.exists(LOG_BASE_PATH))
            {
                Files.createDirectories(LOG_BASE_PATH);
            }

            if (!Files.exists(CONFIG_PATH))
            {
                Files.createDirectories(CONFIG_PATH);
            }

            this.readConfig();
        }
        catch (Exception e)
        {
            TwitchBot.LOGGER.error("BotConfig error: {}", e.getMessage());
            System.exit(1);
        }
    }

    public static synchronized BotConfig instance()
    {
        if (botConfigInstance == null) { botConfigInstance = new BotConfig(); }
        return botConfigInstance;
    }

    public ArrayList<String> getChannels()
    {
        return this.config.channels;
    }

    public String getClientId()
    {
        return this.config.credentials.clientId;
    }

    public String getAccessToken()
    {
        return this.config.credentials.accessToken;
    }

    public String getRefreshToken()
    {
        return this.config.credentials.refreshToken;
    }

    public ArrayList<String> getTokenScopes()
    {
        return this.config.credentials.tokenScopes;
    }

    public int getPermissionLevel(String userId)
    {
        if (this.config.owners.contains(userId)) { return 777; }
        if (this.config.permissions.containsKey(userId)) { return this.config.permissions.get(userId); }
        return 0;
    }

    public void setPermissionLevel(String userId, int level)
    {
        if (level == 0)
        {
            this.config.owners.removeIf(id -> Objects.equals(id, userId));
            this.config.permissions.remove(userId);
        }
        else if (level >= 100)
        {
            if (!this.config.owners.contains(userId)) { this.config.owners.add(userId); }
            this.config.permissions.remove(userId);
        }
        else
        {
            this.config.permissions.put(userId, level);
            this.config.owners.removeIf(id -> Objects.equals(id, userId));
        }
    }

    public boolean isOwner(String userId)
    {
        return this.config.owners.contains(userId);
    }

    public boolean addChannel(String channelName)
    {
        if (!this.config.channels.contains(channelName))
        {
            this.config.channels.add(channelName);
            return true;
        }
        return false;
    }

    public boolean removeChannel(String channelName)
    {
        final int index = this.config.channels.indexOf(channelName);
        if (index != -1) { this.config.channels.remove(index); }
        return index != -1;
    }

    public boolean commandIsMuted(String cmd)
    {
        return this.config.mutedCommands.contains(cmd);
    }

    public void setCommandIsMuted(String cmd, boolean isMuted)
    {
        if (isMuted)
        {
            if (!this.config.mutedCommands.contains(cmd))
            {
                this.config.mutedCommands.add(cmd);
            }
        }
        else
        {
            this.config.mutedCommands.removeIf(c -> Objects.equals(c, cmd));
        }
    }

    public void saveChanges()
    {
        final Gson gson = new GsonBuilder().setFormattingStyle(FormattingStyle.PRETTY).create();
        final String jsonData = gson.toJson(this.config);

        try
        {
            final FileWriter fileWriter = new FileWriter(this.configFile);
            fileWriter.write(jsonData);
            fileWriter.close();
        }
        catch (IOException e)
        {
            TwitchBot.LOGGER.error("Failed to save config: {}", e.getMessage());
        }
    }

    public void updateConfig()
    {
        try
        {
            this.readConfig();
        }
        catch (IOException e)
        {
            TwitchBot.LOGGER.error("Failed to update config: {}", e.getMessage());
        }
    }

    private void readConfig() throws IOException
    {
        this.config = new Gson().fromJson(new FileReader(this.configFile), Config.class);
    }
}
