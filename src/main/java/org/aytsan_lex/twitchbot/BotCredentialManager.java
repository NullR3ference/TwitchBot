package org.aytsan_lex.twitchbot;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class BotCredentialManager
{
    public static class Credentials
    {
        private String userId;
        private String clientId;
        private String accessToken;
        private String refreshToken;

        public String getUserId()
        {
            return this.userId;
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

    private static final Logger LOG = LoggerFactory.getLogger(BotCredentialManager.class);

    private static final Path CREDENTIALS_BASE_PATH = Path.of(Utils.getCurrentWorkingPath() + "/credentials");
    private static final File credentialsFile = new File(CREDENTIALS_BASE_PATH + "/credentials.json");
    private static Credentials credentials = new Credentials();

    public static void initialize()
    {
        try
        {
            if (!Files.exists(CREDENTIALS_BASE_PATH))
            {
                LOG.info("Creating credentials folder");
                Files.createDirectories(CREDENTIALS_BASE_PATH);
            }

            if (!credentialsFile.exists())
            {
                LOG.warn("Creating new file, credentials will be empty!");
                credentialsFile.createNewFile();
                writeCredentialsTemplate();
            }
        }
        catch (IOException e)
        {
            LOG.error("Initialization failed: {}", e.getMessage());
        }
    }

    public static void readCredentials()
    {
        try
        {
            final FileReader fileReader = new FileReader(credentialsFile);
            credentials = new Gson().fromJson(fileReader, Credentials.class);
        }
        catch (IOException e)
        {
            LOG.error("Failed to read credentials: {}", e.getMessage());
        }
    }

    public static Credentials getCredentials()
    {
        return credentials;
    }

    private static void writeCredentialsTemplate()
    {
        try
        {
            final String template =
                    """
                    {
                      "userId": "",
                      "clientId": "",
                      "accessToken": "",
                      "refreshToken": ""
                    }
                    """;

            final FileWriter fileWriter = new FileWriter(credentialsFile);
            fileWriter.write(template);
            fileWriter.close();
        }
        catch (IOException e)
        {
            LOG.error("Failed to write credentials template!");
        }
    }
}
