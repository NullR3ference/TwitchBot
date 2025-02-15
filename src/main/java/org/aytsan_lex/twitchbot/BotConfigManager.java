package org.aytsan_lex.twitchbot;

import java.util.Objects;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.Gson;

// TODO: Implement auto-update config when file has been changed

public class BotConfigManager
{
    private static final Logger LOG = LoggerFactory.getLogger(BotConfigManager.class);
    private static final Path CONFIG_PATH = Path.of(Utils.getCurrentWorkingPath() + "/config");

    private static final File configFile = new File(CONFIG_PATH + "/config.json");
    private static BotConfig config = BotConfig.empty();

    public static void initialize()
    {
        LOG.info("Initializing...");

        try
        {
            if (!Files.exists(CONFIG_PATH))
            {
                LOG.info("Creating config folder");
                Files.createDirectories(CONFIG_PATH);
            }

            if (!configFile.exists())
            {
                LOG.warn("Config file does`nt exists, creating new, config will be EMPTY!");
                configFile.createNewFile();
                writeConfigTemplate();
            }

            readConfig();
        }
        catch (Exception e)
        {
            LOG.error("Initialization failed: {}", e.getMessage());
        }
    }

    public static synchronized String readConfigAdString() throws IOException
    {
        return Files.readString(configFile.toPath());
    }

    public static synchronized void writeConfig(final String configString) throws IOException
    {
        final FileWriter fileWriter = new FileWriter(configFile);
        fileWriter.write(configString);
        fileWriter.close();
    }

    public static void readConfig()
    {
        try
        {
            final String data = readConfigAdString();
            config = new Gson().fromJson(data, BotConfig.class);
        }
        catch (IOException e)
        {
            LOG.error("Failed to read config from file: {}", e.getMessage());
        }
    }

    public static void saveConfig()
    {
        try
        {
            writeConfig(config.asJson());
        }
        catch (IOException e)
        {
            LOG.error("Failed to write config: {}", e.getMessage());
        }
    }

    public static BotConfig getConfig()
    {
        return config;
    }

    public static int getPermissionLevel(String name)
    {
        if (isOwner(name)) { return 777; }
        if (config.getPermissions().containsKey(name)) { return config.getPermissions().get(name); }
        return 0;
    }

    public static void setPermissionLevel(String userName, int level)
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

    public static boolean isOwner(String userId)
    {
        return config.getOwners().contains(userId);
    }

    public static boolean addChannel(String channelName)
    {
        if (!config.getChannels().contains(channelName))
        {
            config.getChannels().add(channelName);
            return true;
        }
        return false;
    }

    public static boolean removeChannel(String channelName)
    {
        final int index = config.getChannels().indexOf(channelName);
        if (index != -1) { config.getChannels().remove(index); }
        return index != -1;
    }

    public static boolean commandIsMuted(Class<?> botCommandClass)
    {
        return config.getMutedCommands().contains(botCommandClass.getSimpleName());
    }

    public static void setCommandIsMuted(Class<?> botCommandClass, boolean isMuted)
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

    public static boolean isTimedOutOnChannel(String channelName)
    {
        return config.getTimedOutOnChannels().containsKey(channelName);
    }

    public static LocalDateTime getTimeoutEndsAt(String channelName)
    {
        return LocalDateTime.parse(
                config.getTimedOutOnChannels().get(channelName),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
        );
    }

    public static void setTimedOutOnChannel(String channelName, LocalDateTime expiredIn)
    {
        config.getTimedOutOnChannels().put(
                channelName,
                expiredIn.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"))
        );
    }

    public static void removeTimedOutOnChannel(String channelName)
    {
        config.getTimedOutOnChannels().remove(channelName);
    }

    public static int getCommandRequiredPermissionLevel(String command)
    {
        if (config.getCommandPermissionLevels().containsKey(command))
        {
            return config.getCommandPermissionLevels().get(command);
        }
        return 777;
    }

    public static int getCommandCooldown(String command)
    {
        if (config.getCommandCooldowns().containsKey(command))
        {
            return config.getCommandCooldowns().get(command);
        }
        return 0;
    }

    public static void setDelayBetweenMessages(int value)
    {
        config.setDelayBetweenMessages(value);
    }

    private static void writeConfigTemplate()
    {
        try
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
                    "BenMuteBotCommand": 777
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

            FileWriter fileWriter = new FileWriter(configFile);
            fileWriter.write(template);
            fileWriter.close();
        }
        catch (IOException e)
        {
            LOG.error("Failed to write config template: {}", e.getMessage());
        }
    }
}
