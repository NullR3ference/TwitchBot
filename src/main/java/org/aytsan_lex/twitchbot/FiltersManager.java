package org.aytsan_lex.twitchbot;

import java.io.File;
import java.io.IOException;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import org.aytsan_lex.twitchbot.filters.MiraFilters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.Gson;

public class FiltersManager
{
    private static class MiraFiltersAdapter
    {
        public ArrayList<String> preFilterValues;
        public ArrayList<String> postFilterValues;
        public int lengthFilterValue;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(FiltersManager.class);
    private static final Path FILTERS_PATH = Path.of(Utils.getCurrentWorkingPath() + "/filters");
    private static final File miraFiltersFile = new File(FILTERS_PATH + "/mira_filters.json");
    private static MiraFilters miraFilters = MiraFilters.empty();

    public static void initialize()
    {
        LOGGER.info("Initializing...");
        try
        {
            if (!Files.exists(FILTERS_PATH))
            {
                LOGGER.info("Creating filters folder");
                Files.createDirectories(FILTERS_PATH);
            }

            if (!miraFiltersFile.exists())
            {
                miraFilters = MiraFilters.empty();
                miraFiltersFile.createNewFile();
                writeFiltersTemplate();
                LOGGER.warn("Mira filters file does`nt exists, creating new, filters will be EMPTY");
            }
        }
        catch (IOException e)
        {
            LOGGER.error("Initialization failed: {}", e.getMessage());
        }
    }

    public static void readFilters()
    {
        try
        {
            final Gson gson = new Gson();
            final MiraFiltersAdapter miraFiltersAdapter =
                    gson.fromJson(new FileReader(miraFiltersFile), MiraFiltersAdapter.class);

            if (miraFiltersAdapter.preFilterValues.isEmpty())
            {
                LOGGER.warn("Mira pre-filter is empty");
            }

            if (miraFiltersAdapter.postFilterValues.isEmpty())
            {
                LOGGER.warn("Mira post-filter is empty");
            }

            miraFilters = MiraFilters.of(
                    miraFiltersAdapter.preFilterValues,
                    miraFiltersAdapter.postFilterValues,
                    miraFiltersAdapter.lengthFilterValue
            );
        }
        catch (IOException e)
        {
            LOGGER.error("Failed to read filters from file: {}", e.getMessage());
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
                  "lengthFilterValue": 0
                }
                """;

            FileWriter fileWriter = new FileWriter(miraFiltersFile);
            fileWriter.write(template);
            fileWriter.close();
        }
        catch (IOException e)
        {
            LOGGER.error("Failed to write filters template: {}", e.getMessage());
        }
    }
}
