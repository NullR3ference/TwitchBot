package org.aytsan_lex.twitchbot;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;

public class BotConfig
{
    public static final String OWN_CHANNEL = "aytsan_lex";

    private static final File m_CredentialsFile = new File("credentials.txt");
    private static final File m_ChannelsFile = new File("channels.txt");
    private static final ArrayList<String> m_Credentials = new ArrayList<>();
    private static final ArrayList<String> m_Channels = new ArrayList<>();

    private static BotConfig m_Instance = null;

    public static synchronized BotConfig instance()
    {
        if (m_Instance == null) { m_Instance = new BotConfig(); }
        return m_Instance;
    }

    public ArrayList<String> getChannels()
    {
        return m_Channels;
    }

    public String getClientId()
    {
        return m_Credentials.get(0);
    }

    public String getAccessToken()
    {
        return m_Credentials.get(1);
    }

    public String getRefreshToken()
    {
        return m_Credentials.get(2);
    }

    private BotConfig()
    {
        try
        {
            if (!m_CredentialsFile.exists())
            {
                System.out.println("[-] Credentials file does not exists");
                System.out.println("[-] Create credentials.txt file and write credentials in this file");
                System.exit(1);
            }

            if (!m_ChannelsFile.exists())
            {
                m_ChannelsFile.createNewFile();
            }

            readCredentials();
            readChannels();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void readCredentials() throws IOException
    {
        final Scanner scanner = new Scanner(m_CredentialsFile);
        for (int i = 0; (i < 3) && (scanner.hasNext()); i++)
        {
            final String line = scanner.nextLine();
            if (!line.isEmpty())
            {
                m_Credentials.add(line);
            }
        }
    }

    private void readChannels() throws IOException
    {
        final Scanner scanner = new Scanner(m_ChannelsFile);
        while (scanner.hasNext())
        {
            final String channel = scanner.nextLine();
            if (!channel.isEmpty())
            {
                m_Channels.add(channel);
            }
        }
    }
}
