package org.aytsan_lex.twitchbot.managers;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.FormattingStyle;

import org.aytsan_lex.twitchbot.Utils;
import org.aytsan_lex.twitchbot.bot_commands.BotCommandBase;

public class ConfigManager extends ConfigFileBasedManager
{
    private static final Logger LOG = LoggerFactory.getLogger(ConfigManager.class);
    private static final Path CONFIG_BASE_PATH = Path.of(Utils.getCurrentWorkingPath() + "/config");
    private static final Object FILE_ACCESS_SYNC = new Object();

    private static final File configFile = new File(CONFIG_BASE_PATH + "/config.json");
    private BotConfig config = BotConfig.empty();

    public BotConfig getConfig()
    {
        return config;
    }

    public int getPermissionLevel(final String userName)
    {
        if (this.isOwner(userName)) { return 777; }
        if (config.getPermissions().containsKey(userName)) { return config.getPermissions().get(userName); }
        return 0;
    }

    public void setPermissionLevel(final String userName, final int level)
    {
        if (level == 0)
        {
            config.getOwners().removeIf(name -> Objects.equals(name, userName));
            config.getPermissions().remove(userName);
        }
        else if (level >= 777)
        {
            if (!config.getOwners().contains(userName)) { config.getOwners().add(userName); }
            config.getPermissions().remove(userName);
        }
        else
        {
            config.getPermissions().put(userName, level);
            config.getOwners().removeIf(name -> Objects.equals(name, userName));
        }
    }

    public boolean isOwner(final String userName)
    {
        return config.getOwners().contains(userName);
    }

    public boolean addChannel(final String channelName)
    {
        if (!config.getChannels().contains(channelName))
        {
            config.getChannels().add(channelName);
            return true;
        }
        return false;
    }

    public boolean removeChannel(final String channelName)
    {
        final int index = config.getChannels().indexOf(channelName);
        if (index != -1) { config.getChannels().remove(index); }
        return index != -1;
    }

    public boolean commandIsMuted(Class<?> botCommandClass)
    {
        return config.getMutedCommands().contains(botCommandClass.getSimpleName());
    }

    public void setCommandIsMuted(Class<? extends BotCommandBase> botCommandClass, final boolean isMuted)
    {
        final String commandName = botCommandClass.getSimpleName();

        if (isMuted)
        {
            if (!config.getMutedCommands().contains(commandName))
            {
                config.getMutedCommands().add(commandName);
            }
        }
        else
        {
            config.getMutedCommands().removeIf(c -> c.equals(commandName));
        }
    }

    public boolean isTimedOutOnChannel(final String channelName)
    {
        return config.getTimedOutOnChannels().containsKey(channelName);
    }

    public LocalDateTime getTimeoutEndsAt(final String channelName)
    {
        return LocalDateTime.parse(
                config.getTimedOutOnChannels().get(channelName),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
        );
    }

    public void setIsTimedOutOnChannel(final String channelName, final LocalDateTime endsAt)
    {
        config.getTimedOutOnChannels().put(
                channelName,
                endsAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"))
        );
    }

    public void removeTimedOutOnChannel(final String channelName)
    {
        config.getTimedOutOnChannels().remove(channelName);
    }

    public int getRequiredPermissionLevel(Class<?> botCommandClass)
    {
        final String name = botCommandClass.getSimpleName();
        if (config.getCommandPermissionLevels().containsKey(name))
        {
            return config.getCommandPermissionLevels().get(name);
        }
        return 777;
    }

    public int getCommandCooldown(Class<?> botCommandClass)
    {
        final String name = botCommandClass.getSimpleName();
        if (config.getCommandCooldowns().containsKey(name))
        {
            return config.getCommandCooldowns().get(name);
        }
        return 0;
    }

    public int getDelayBetweenMessages()
    {
        return config.getDelayBetweenMessages();
    }

    public void setDelayBetweenMessages(final int delay)
    {
        config.setDelayBetweenMessages(delay);
    }

    public void writeAndUpdate(final BotConfig newConfig)
    {
        this.config = newConfig;
        this.saveFile();
    }

    @Override
    public void readFile()
    {
        try
        {
            this.readFileInternal();
        }
        catch (IOException e)
        {
            LOG.error("Failed to read config: {}", e.getMessage());
        }
    }

    @Override
    public void saveFile()
    {
        try
        {
            this.saveFileInternal();
        }
        catch (IOException e)
        {
            LOG.error("Failed to save config: {}", e.getMessage());
        }
    }

    @Override
    protected boolean onInitialize()
    {
        LOG.info("Initializing...");

        try
        {
            if (!Files.exists(CONFIG_BASE_PATH))
            {
                LOG.warn("Config folder is missing, creating new...");
                Files.createDirectories(CONFIG_BASE_PATH);
            }

            if (!Files.exists(configFile.toPath()))
            {
                LOG.warn("Config file is missing, creating new...");
                Files.createFile(configFile.toPath());
                this.writeConfigTemplate();
            }

            this.readFileInternal();
        }
        catch (IOException e)
        {
            LOG.error("Initialization failed: {}", e.getMessage());
            return false;
        }

        return true;
    }

    @Override
    protected void onShutdown()
    {
        LOG.info("Shutting down...");
    }

    private void readFileInternal() throws IOException
    {
        synchronized (FILE_ACCESS_SYNC)
        {
            final FileReader fileReader = new FileReader(configFile);
            config = new Gson().fromJson(fileReader, BotConfig.class);
        }
    }

    private void saveFileInternal() throws IOException
    {
        final FileWriter fileWriter = new FileWriter(configFile);
        final String jsonData = new GsonBuilder().setFormattingStyle(FormattingStyle.PRETTY).create().toJson(config);

        synchronized (FILE_ACCESS_SYNC)
        {
            fileWriter.write(jsonData);
            fileWriter.close();
        }
    }

    private void writeConfigTemplate() throws IOException
    {
        final String template = """
                {
                  "runningOnChannelId": "",
                  "channels": [],
                  "timedOutOnChannels": {},
                  "bannedOnChannels": [],
                  "owners": [],
                  "permissions": {},
                  "commandPermissionLevels": {
                    "AddChannelBotCommand": 777,
                    "BenBotCommand": 0,
                    "BenMuteBotCommand": 777,
                    "FiltersInfoBotCommand": 777,
                    "IqBotCommand": 0,
                    "IqMuteBotCommand": 777,
                    "JoinToChatBotCommand": 777,
                    "LeaveFromChatBotCommand": 777,
                    "MiraBotCommand": 1,
                    "MiraMuteBotCommand": 777,
                    "ReadcfgBotCommand": 777,
                    "RemoveChannelBotCommand": 777,
                    "RestartBotCommand": 777,
                    "SetPermissionBotCommand": 777,
                    "StatusBotCommand": 777,
                    "UpdateFiltersBotCommand": 777
                  },
                  "commandCooldowns": {},
                  "mutedCommands": [],
                  "ollamaHost": "http://127.0.0.1:11434",
                  "miraModelName": "gemma2-9b-mira1.0",
                  "modelMessageTemplate": "'<username>' говорит: '<message>'",
                  "messageSendingMode": 0,
                  "delayBetweenMessages": 1100
                }
                """;

        final FileWriter fileWriter = new FileWriter(configFile);
        fileWriter.write(template);
        fileWriter.close();
    }
}
