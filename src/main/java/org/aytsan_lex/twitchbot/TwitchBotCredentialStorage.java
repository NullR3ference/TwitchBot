package org.aytsan_lex.twitchbot;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.philippheuer.credentialmanager.api.IStorageBackend;
import com.github.philippheuer.credentialmanager.domain.Credential;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.google.gson.FormattingStyle;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

class TwitchBotCredentialStorage implements IStorageBackend
{
    private static final Logger LOG = LoggerFactory.getLogger(TwitchBotCredentialStorage.class);
    private static final Path credentialsBasePath = Path.of(Utils.getCurrentWorkingPath() + "/credentials");
    private static final File credentialsFile = new File(credentialsBasePath + "/credentials.json");

    private final ArrayList<Credential> credentials = new ArrayList<>();

    @Override
    public List<Credential> loadCredentials()
    {
        try
        {
            LOG.info("Loading credentials");
            final FileReader fileReader = new FileReader(credentialsFile);
            final Gson gson = new GsonBuilder().create();
            gson.fromJson(fileReader, new TypeToken<ArrayList<OAuth2Credential>>(){}.getType());
        }
        catch (IOException e)
        {
            LOG.error("Failed to load credentials: {}", e.getMessage());
        }
        return this.credentials;
    }

    @Override
    public void saveCredentials(List<Credential> credentials)
    {
        try
        {
            LOG.info("Saving credentials");

            final PrintWriter printWriter = new PrintWriter(new FileWriter(credentialsFile));
            final Gson gson = new GsonBuilder().setFormattingStyle(FormattingStyle.PRETTY).create();

            credentials.forEach(credential ->
                printWriter.println(gson.toJson((OAuth2Credential) credential))
            );

            printWriter.flush();
        }
        catch (IOException e)
        {
            LOG.error("Failed to save credentials: {}", e.getMessage());
        }
    }

    @Override
    public Optional<Credential> getCredentialByUserId(String userId)
    {
        return credentials.stream().filter(c -> c.getUserId().equals(userId)).findFirst();
    }
}
