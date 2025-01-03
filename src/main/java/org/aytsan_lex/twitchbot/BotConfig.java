package org.aytsan_lex.twitchbot;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

// TODO: Store config in JSON format

public class BotConfig
{
    public enum CredentialType
    {
        CLIEND_ID,
        USER_ACCESS_TOKEN,
        USER_REFRESH_TOKEN;

        public int asInt() { return this.ordinal(); }
    };

    private static BotConfig botConfigInstance = null;
    private static final String CURRENT_WORKING_DIR = Path.of("").toAbsolutePath().toString();

    public static final Path LOG_BASE_PATH = Path.of(CURRENT_WORKING_DIR + "/chatlogs");
    public static final Path CONFIG_PATH = Path.of(CURRENT_WORKING_DIR + "/config");

    private final File credentialsFile;
    private final File channelsFile;
    private final File permissionsFile;

    private final ArrayList<String> credentials;
    private final HashMap<String, Integer> permissionLevels;
    private final ArrayList<String> channels;

    private BotConfig()
    {
        this.credentialsFile = new File(CONFIG_PATH + "/credentials.txt");
        this.channelsFile = new File(CONFIG_PATH + "/channels.txt");
        this.permissionsFile = new File(CONFIG_PATH + "/permissions.txt");

        this.credentials = new ArrayList<>();
        this.permissionLevels = new HashMap<>();
        this.channels = new ArrayList<>();

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

            if (!this.credentialsFile.exists())
            {
                System.out.println("[-] Credentials file does not exists");
                System.out.println("[-] Create credentials.txt file and write credentials in this file");
                System.exit(1);
            }

            if (!this.channelsFile.exists())
            {
                this.channelsFile.createNewFile();
            }

            this.readCredentials();
            this.readPermissions();
            this.readChannels();
        }
        catch (IOException e)
        {
            System.err.println("[-] BotConfig initialize error: " + e.getMessage());
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
        return this.channels;
    }

    public String getClientId()
    {
        return this.credentials.get(CredentialType.CLIEND_ID.asInt());
    }

    public String getAccessToken()
    {
        return this.credentials.get(CredentialType.USER_ACCESS_TOKEN.asInt());
    }

    public String getRefreshToken()
    {
        return this.credentials.get(CredentialType.USER_REFRESH_TOKEN.asInt());
    }

    public int getPermissionLevel(String userId)
    {
        if (!this.permissionLevels.containsKey(userId)) { return 0; }
        return this.permissionLevels.get(userId);
    }

    public boolean addChannel(String channelName)
    {
        if (!channels.contains(channelName))
        {
            channels.add(channelName);
            return true;
        }
        return false;
    }

    public boolean removeChannel(String channelName)
    {
        final int index = channels.indexOf(channelName);
        if (index != -1) { channels.remove(index); }
        return index != -1;
    }

    public void saveChanges()
    {
        try
        {
            final FileWriter fileWriter = new FileWriter(this.channelsFile);
            for (String channel : this.channels) { fileWriter.write(channel + "\n"); }
            fileWriter.close();
        }
        catch (IOException e)
        {
            System.err.println("[-] Failed to save BotConfig changes: " + e.getMessage());
        }
    }

    private void readCredentials() throws IOException
    {
        final Scanner scanner = new Scanner(this.credentialsFile);
        for (int i = 0; (i < 3) && (scanner.hasNext()); i++)
        {
            final String line = scanner.nextLine();
            if (!line.isEmpty())
            {
                this.credentials.add(line);
            }
        }
    }

    private void readPermissions() throws IOException
    {
        final Scanner scanner = new Scanner(this.permissionsFile);
        while (scanner.hasNext())
        {
            final String line = scanner.nextLine();
            if (!line.isEmpty())
            {
                final String[] permissionSet = line.split(":");
                this.permissionLevels.put(permissionSet[0], Integer.parseInt(permissionSet[1]));
            }
        }
    }

    private void readChannels() throws IOException
    {
        final Scanner scanner = new Scanner(this.channelsFile);
        while (scanner.hasNext())
        {
            final String channel = scanner.nextLine();
            if (!channel.isEmpty())
            {
                channels.add(channel);
            }
        }
    }
}
