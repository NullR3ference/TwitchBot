package org.aytsan_lex.twitchbot.managers;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.FormattingStyle;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.aytsan_lex.twitchbot.Utils;
import org.aytsan_lex.twitchbot.filters.MiraFilters;

public class FiltersManager extends ConfigFileBasedManager
{
    private static final Logger LOG = LoggerFactory.getLogger(FiltersManager.class);
    private static final Path FILTERS_BASE_PATH = Path.of(Utils.getCurrentWorkingPath() + "/filters");
    private static final Object FILE_ACCESS_SYNC = new Object();

    private static final File miraFiltersFile = new File(FILTERS_BASE_PATH + "/mira_filters.json");
    private MiraFilters miraFilters = MiraFilters.empty();

    public MiraFilters getMiraFilters()
    {
        return miraFilters;
    }

    public void writeAndUpdate(final MiraFilters newMiraFilters)
    {
        this.miraFilters = newMiraFilters;
        this.saveFile();
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
            LOG.error("Failed to read filters: {}", e.getMessage());
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
            LOG.error("Failed to save filters: {}", e.getMessage());
        }
    }

    @Override
    protected boolean onInitialize()
    {
        LOG.info("Initializing...");

        try
        {
            if (!Files.exists(FILTERS_BASE_PATH))
            {
                LOG.warn("Filters folder is missing, creating new...");
                Files.createDirectories(FILTERS_BASE_PATH);
            }

            if (!Files.exists(miraFiltersFile.toPath()))
            {
                LOG.warn("Filters file is missing, creating new...");
                LOG.warn("Filters will be EMPTY!");

                Files.createFile(miraFiltersFile.toPath());
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
        synchronized (FILE_ACCESS_SYNC)
        {
            final FileReader fileReader = new FileReader(miraFiltersFile);
            final MiraFilters.Adapter adapter = new Gson().fromJson(fileReader, MiraFilters.Adapter.class);
            miraFilters = MiraFilters.fromAdapter(adapter);
        }
    }

    private void saveFileInternal() throws IOException
    {
        synchronized (FILE_ACCESS_SYNC)
        {
            final FileWriter fileWriter = new FileWriter(miraFiltersFile);
            final String jsonData = new GsonBuilder().setFormattingStyle(FormattingStyle.PRETTY).create().toJson(miraFilters);
            fileWriter.write(jsonData);
            fileWriter.close();
        }
    }

    private void writeTemplate() throws IOException
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

        final FileWriter fileWriter = new FileWriter(miraFiltersFile);
        fileWriter.write(template);
        fileWriter.close();
    }
}
