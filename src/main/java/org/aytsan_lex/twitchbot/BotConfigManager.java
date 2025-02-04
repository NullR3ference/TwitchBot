package org.aytsan_lex.twitchbot;

import java.util.Objects;
import java.io.FileReader;
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
import com.google.gson.GsonBuilder;
import com.google.gson.FormattingStyle;

public class BotConfigManager
{
    private static final Logger LOGGER = LoggerFactory.getLogger(BotConfigManager.class);
    private static final Path CONFIG_PATH = Path.of(Utils.getCurrentWorkingPath() + "/config");
    private static final File configFile = new File(CONFIG_PATH + "/config.json");
    private static BotConfig config = BotConfig.empty();

    public static void initialize()
    {
        LOGGER.info("Initializing...");
        try
        {
            if (!Files.exists(CONFIG_PATH))
            {
                LOGGER.info("Creating filters folder");
                Files.createDirectories(CONFIG_PATH);
            }

            if (!configFile.exists())
            {
                LOGGER.warn("Config file does`nt exists, creating new, config will be EMPTY!");
                configFile.createNewFile();
                writeConfigTemplate();
            }
        }
        catch (Exception e)
        {
            LOGGER.error("Initialization failed: {}", e.getMessage());
        }
    }

    public static void readConfig()
    {
        try
        {
            config = new Gson().fromJson(new FileReader(configFile), BotConfig.class);
        }
        catch (IOException e)
        {
            LOGGER.error("Failed to read config from file: {}", e.getMessage());
        }
    }

    public static void writeConfig()
    {
        try
        {
            final Gson gson = new GsonBuilder().setFormattingStyle(FormattingStyle.PRETTY).create();
            final String jsonData = gson.toJson(config);
            final FileWriter fileWriter = new FileWriter(configFile);

            fileWriter.write(jsonData);
            fileWriter.close();
        }
        catch (IOException e)
        {
            LOGGER.error("Failed to write config: {}", e.getMessage());
        }
    }

    public static boolean credentialsIsEmpty()
    {
        final String clientId = config.getClientId();
        final String accessToken = config.getAccessToken();
        if (clientId == null || accessToken == null) { return false; }
        return clientId.isEmpty() || accessToken.isEmpty();
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

    public static void setPermissionLevel(String userId, int level)
    {
        if (level == 0)
        {
            config.getOwners().removeIf(id -> Objects.equals(id, userId));
            config.getPermissions().remove(userId);
        }
        else if (level >= 777)
        {
            if (!config.getOwners().contains(userId)) { config.getOwners().add(userId); }
            config.getPermissions().remove(userId);
        }
        else
        {
            config.getPermissions().put(userId, level);
            config.getOwners().removeIf(id -> Objects.equals(id, userId));
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

    public static boolean commandIsMuted(String cmd)
    {
        return config.getMutedCommands().contains(cmd);
    }

    public static boolean isTimedOutChannel(String channelName)
    {
        return config.getTimedOutOnChannels().containsKey(channelName);
    }

    public static LocalDateTime getTimeoutExpiredIn(String channelName)
    {
        return LocalDateTime.parse(
                config.getTimedOutOnChannels().get(channelName),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
        );
    }

    public static void removeTimedOutChannel(String channelName)
    {
        config.getTimedOutOnChannels().remove(channelName);
    }

    public static void setChannelIsTimedOut(String channelName, LocalDateTime expiredIn)
    {
        config.getTimedOutOnChannels().put(
                channelName,
                expiredIn.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"))
        );
    }

    public static void setCommandIsMuted(String cmd, boolean isMuted)
    {
        if (isMuted)
        {
            if (!config.getMutedCommands().contains(cmd))
            {
                config.getMutedCommands().add(cmd);
            }
        }
        else
        {
            config.getMutedCommands().removeIf(c -> Objects.equals(c, cmd));
        }
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

    private static void writeConfigTemplate()
    {
        try
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
                  "commandPermissionLevels": {
                    "AddChannelBotCommand": 777,
                    "BenBotCommand": 0,
                    "BenMuteBotCommand": 777,
                    "CancelVoteBotCommand": 777,
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
                    "StartVoteBotCommand": 777,
                    "StatusBotCommand": 777,
                    "UpdateFiltersBotCommand": 777,
                    "VoteBotCommand": 0,
                    "VoteInfoBotCommand": 777
                  },
                  "commandCooldowns": {},
                  "mutedCommands": [],
                  "ollamaHost": "http://localhost:11434",
                  "miraModelName": "gemma2-9b-mira1.0",
                  "modelMessageTemplate": "'<username>' говорит: '<message>'",
                  "messageSendingMode": 0
                }
                """;

            FileWriter fileWriter = new FileWriter(configFile);
            fileWriter.write(template);
            fileWriter.close();
        }
        catch (IOException e)
        {
            LOGGER.error("Failed to write config template: {}", e.getMessage());
        }
    }
}
