package org.aytsan_lex.twitchbot;

import java.io.FileReader;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.util.Objects;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.FormattingStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BotConfigManager
{
    private final Logger LOGGER = LoggerFactory.getLogger(BotConfigManager.class);

    private static BotConfigManager botConfigInstance = null;
    private static final String CURRENT_WORKING_DIR = Path.of("").toAbsolutePath().toString();

    public static final Path LOG_BASE_PATH = Path.of(CURRENT_WORKING_DIR + "/chatlogs");
    public static final Path CONFIG_PATH = Path.of(CURRENT_WORKING_DIR + "/config");

    private final File configFile;
    private BotConfig config;

    private BotConfigManager()
    {
        this.configFile = new File(CONFIG_PATH + "/config.json");
        this.config = new BotConfig();

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

            if (!this.configFile.exists())
            {
                LOGGER.error("Config file is empty or does`nt exists. Creating new config file");
                this.configFile.createNewFile();
                this.writeConfigTemplate();
                System.exit(1);
            }
            else
            {
                this.readConfig();
                this.checkConfig();
            }
        }
        catch (Exception e)
        {
            LOGGER.error("Error: {}", e.getMessage());
            System.exit(1);
        }
    }

    public static synchronized BotConfigManager instance()
    {
        if (botConfigInstance == null) { botConfigInstance = new BotConfigManager(); }
        return botConfigInstance;
    }

    public BotConfig getConfig()
    {
        return this.config;
    }

    public int getPermissionLevel(String name)
    {
        if (isOwner(name)) { return 777; }
        if (this.config.getPermissions().containsKey(name)) { return this.config.getPermissions().get(name); }
        return 0;
    }

    public void setPermissionLevel(String userId, int level)
    {
        if (level == 0)
        {
            this.config.getOwners().removeIf(id -> Objects.equals(id, userId));
            this.config.getPermissions().remove(userId);
        }
        else if (level >= 100)
        {
            if (!this.config.getOwners().contains(userId)) { this.config.getOwners().add(userId); }
            this.config.getPermissions().remove(userId);
        }
        else
        {
            this.config.getPermissions().put(userId, level);
            this.config.getOwners().removeIf(id -> Objects.equals(id, userId));
        }
    }

    public boolean isOwner(String userId)
    {
        return this.config.getOwners().contains(userId);
    }

    public boolean addChannel(String channelName)
    {
        if (!this.config.getChannels().contains(channelName))
        {
            this.config.getChannels().add(channelName);
            return true;
        }
        return false;
    }

    public boolean removeChannel(String channelName)
    {
        final int index = this.config.getChannels().indexOf(channelName);
        if (index != -1) { this.config.getChannels().remove(index); }
        return index != -1;
    }

    public boolean commandIsMuted(String cmd)
    {
        return this.config.getMutedCommands().contains(cmd);
    }

    public boolean isTimedOutChannel(String channelName)
    {
        return this.config.getTimedOutOnChannels().containsKey(channelName);
    }

    public LocalDateTime getTimeoutExpiredIn(String channelName)
    {
        return LocalDateTime.parse(
                this.config.getTimedOutOnChannels().get(channelName),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
        );
    }

    public void removeTimedOutChannel(String channelName)
    {
        this.config.getTimedOutOnChannels().remove(channelName);
    }

    public void setChannelIsTimedOut(String channelName, LocalDateTime expiredIn)
    {
        this.config.getTimedOutOnChannels().put(
                channelName,
                expiredIn.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"))
        );
    }

    public void setCommandIsMuted(String cmd, boolean isMuted)
    {
        if (isMuted)
        {
            if (!this.config.getMutedCommands().contains(cmd))
            {
                this.config.getMutedCommands().add(cmd);
            }
        }
        else
        {
            this.config.getMutedCommands().removeIf(c -> Objects.equals(c, cmd));
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
            LOGGER.error("Failed to save config: {}", e.getMessage());
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
            LOGGER.error("Failed to update config: {}", e.getMessage());
        }
    }

    private void readConfig() throws IOException
    {
        this.config = new Gson().fromJson(new FileReader(this.configFile), BotConfig.class);
    }

    private void checkConfig()
    {
        if (this.config.getCredentials().isEmpty())
        {
            LOGGER.error("Credentials are empty!");
            System.exit(1);
        }

        if (this.config.getRunningOnChannelId().isEmpty())
        {
            LOGGER.error("runningOnChannelId is empty!");
            System.exit(1);
        }
    }

    private void writeConfigTemplate() throws IOException
    {
        final String template = """
                {
                  "credentials": {
                      "clientId": "",
                      "accessToken": "",
                      "refreshToken": ""
                  },
                  "runningOnChannelId": "",
                  "channels": [],
                  "timedOutOnChannels": {},
                  "owners": [],
                  "permissions": {},
                  "mutedCommands": [],
                  "ollamaHost": "http://localhost:11434",
                  "miraModelName": "gemma2-mira",
                  "milaModelName": ""
                }
                """;

        FileWriter fileWriter = new FileWriter(this.configFile);
        fileWriter.write(template);
        fileWriter.close();
    }
}
