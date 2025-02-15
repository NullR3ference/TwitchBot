package org.aytsan_lex.twitchbot;

import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.Gson;

import org.aytsan_lex.twitchbot.filters.MiraFilters;

// TODO: Implement auto-update filters when file has been changed

public class FiltersManager
{
    private static final Logger LOG = LoggerFactory.getLogger(FiltersManager.class);
    private static final Path FILTERS_PATH = Path.of(Utils.getCurrentWorkingPath() + "/filters");

    private static final File miraFiltersFile = new File(FILTERS_PATH + "/mira_filters.json");
    private static MiraFilters miraFilters = MiraFilters.empty();

    public static void initialize()
    {
        LOG.info("Initializing...");
        try
        {
            if (!Files.exists(FILTERS_PATH))
            {
                LOG.info("Creating filters folder");
                Files.createDirectories(FILTERS_PATH);
            }

            if (!miraFiltersFile.exists())
            {
                miraFilters = MiraFilters.empty();
                miraFiltersFile.createNewFile();
                writeFiltersTemplate();
                LOG.warn("Mira filters file does`nt exists, creating new, filters will be EMPTY");
            }
        }
        catch (IOException e)
        {
            LOG.error("Initialization failed: {}", e.getMessage());
        }
    }

    public static synchronized String readFiltersAsString() throws IOException
    {
        return Files.readString(miraFiltersFile.toPath());
    }

    public static synchronized void saveFilters(final String data) throws IOException
    {
        final FileWriter fileWriter = new FileWriter(miraFiltersFile);
        fileWriter.write(data);
        fileWriter.close();
    }

    public static void readFilters()
    {
        try
        {
            final String data = readFiltersAsString();
            final MiraFilters.Adapter miraFiltersAdapter = new Gson().fromJson(data, MiraFilters.Adapter.class);
            miraFilters = MiraFilters.fromAdapter(miraFiltersAdapter);
        }
        catch (IOException e)
        {
            LOG.error("Failed to read filters from file: {}", e.getMessage());
        }
    }

    public static MiraFilters getMiraFilters()
    {
        return miraFilters;
    }

    private static void writeFiltersTemplate()
    {
        try
        {
            final String template = """
                {
                  "preFilterValues": [],
                  "postFilterValues": [],
                  "replacementFilterValues": {},
                  "lengthFilterValue": 0,
                  "wordLengthFilterValue": 0
                }
                """;

            saveFilters(template);
        }
        catch (IOException e)
        {
            LOG.error("Failed to write filters template: {}", e.getMessage());
        }
    }
}
