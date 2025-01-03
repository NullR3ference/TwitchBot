package org.aytsan_lex.twitchbot;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class BotConfig
{
    private static final String CWD = Path.of("").toAbsolutePath().toString();
    public static final Path LOG_BASE_PATH = Path.of(CWD + "/chatlogs");
    public static final Path CONFIG_PATH = Path.of(CWD + "/config");

    private static BotConfig botConfigInstance = null;

    private final File credentialsFile;
    private final File channelsFile;
    private final ArrayList<String> credentials;
    private ArrayList<String> channels;

    private BotConfig()
    {
        System.out.println("[=] [BotConfig] LOG_BASE_PATH: " + LOG_BASE_PATH);
        System.out.println("[=] [BotConfig] CONFIG_PATH " + CONFIG_PATH);

        this.credentialsFile = new File(CONFIG_PATH + "/credentials.txt");
        this.channelsFile = new File(CONFIG_PATH + "/channels.txt");
        this.credentials = new ArrayList<>();
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
            this.readChannels();
        }
        catch (IOException e)
        {
            e.printStackTrace();
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
        return this.credentials.get(0);
    }

    public String getAccessToken()
    {
        return this.credentials.get(1);
    }

    public String getRefreshToken()
    {
        return this.credentials.get(2);
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
            e.printStackTrace();
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
