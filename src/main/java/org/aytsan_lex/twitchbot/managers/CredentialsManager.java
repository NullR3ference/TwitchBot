package org.aytsan_lex.twitchbot.managers;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.Gson;

import org.aytsan_lex.twitchbot.Utils;

public class CredentialsManager extends ConfigFileBasedManager
{
    public record Credentials(String userId, String clientId, String accessToken, String refreshToken)
    {
        private static Credentials empty()
        {
            return new Credentials("", "", "", "");
        }
    }

    private static final Logger LOG = LoggerFactory.getLogger(CredentialsManager.class);
    private static final Path CREDENTIALS_BASE_PATH = Path.of(Utils.getCurrentWorkingPath() + "/credentials");
    private static final File credentialsFile = new File(CREDENTIALS_BASE_PATH + "/credentials.json");

    private Credentials credentials = Credentials.empty();

    public Credentials getCredentials()
    {
        return credentials;
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
            LOG.error("Failed to read credentials: {}", e.getMessage());
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
            LOG.error("Failed to save credentials: {}", e.getMessage());
        }
    }

    @Override
    protected boolean onInitialize()
    {
        LOG.info("Initializing...");

        try
        {
            if (!Files.exists(CREDENTIALS_BASE_PATH))
            {
                LOG.warn("Credentials folder is missing, creating new...");
                Files.createDirectories(CREDENTIALS_BASE_PATH);
            }

            if (!credentialsFile.exists())
            {
                LOG.warn("Credentials file is missing, creating new...");
                credentialsFile.createNewFile();
                this.writeTemplate();
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
        final FileReader fileReader = new FileReader(credentialsFile);
        credentials = new Gson().fromJson(fileReader, Credentials.class);
    }

    private void saveFileInternal() throws IOException
    {
    }

    private void writeTemplate() throws IOException
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
}
