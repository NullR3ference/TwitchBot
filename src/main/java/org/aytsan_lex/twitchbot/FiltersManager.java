package org.aytsan_lex.twitchbot;

import org.aytsan_lex.twitchbot.filters.MiraFilters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class FiltersManager
{
    private static final Logger LOGGER = LoggerFactory.getLogger(FiltersManager.class);
    public static Path FILTERS_PATH = Path.of(Utils.getCurrentWorkingPath() + "/filters");
    private static FiltersManager filtersManagerInstance = null;

    private final File miraFiltersFile;
    private MiraFilters miraFilters;

    private FiltersManager()
    {
        this.miraFiltersFile = new File(FILTERS_PATH + "/mira_filters.json");
        this.readFilters();
    }

    public static synchronized FiltersManager instance()
    {
        if (filtersManagerInstance == null) { filtersManagerInstance = new FiltersManager(); }
        return filtersManagerInstance;
    }

    public MiraFilters getMiraFilters()
    {
        return this.miraFilters;
    }

    public void readFilters()
    {
        try
        {
            if (!Files.exists(FILTERS_PATH))
            {
                Files.createDirectories(FILTERS_PATH);
            }

            if (!this.miraFiltersFile.exists())
            {
                this.miraFilters = MiraFilters.empty();
                this.miraFiltersFile.createNewFile();
                LOGGER.warn("Mira filters file does`nt exists, creating new, filters will be EMPTY");
            }
            else
            {
                final ArrayList<String> preFilterValues = new ArrayList<>();
                final ArrayList<String> postFilterValues = new ArrayList<>();
                int lengthFilterValue = 0;

                // TODO: Implement readFilters(): read Mira filters from json to arrays

                if (preFilterValues.isEmpty())
                {
                    LOGGER.warn("Mira pre-filter is empty");
                }

                if (postFilterValues.isEmpty())
                {
                    LOGGER.warn("Mira post-filter is empty");
                }

                this.miraFilters = MiraFilters.of(preFilterValues, postFilterValues, lengthFilterValue);
            }
        }
        catch (IOException ignored)
        { }
    }

    private void writeFiltersTemplate()
    {
        // TODO: Implement writeFiltersTemplate() for FiltersManager
        // Write an template json object to file
    }
}
