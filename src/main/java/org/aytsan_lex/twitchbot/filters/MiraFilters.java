package org.aytsan_lex.twitchbot.filters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.FormattingStyle;
import com.google.gson.GsonBuilder;

public class MiraFilters
{
    public static class Adapter
    {
        public ArrayList<String> preFilterValues;
        public ArrayList<String> postFilterValues;
        public HashMap<String, String> replacementFilterValues;
        public int lengthFilterValue;
        public int wordLengthFilterValue;

        public static Adapter fromPatterns(MiraFilters filters)
        {
            Adapter adapter = new Adapter();

            adapter.preFilterValues = filters.preFilter
                    .stream()
                    .map(Pattern::pattern)
                    .collect(Collectors.toCollection(ArrayList::new));

            adapter.postFilterValues = filters.postFilter
                    .stream()
                    .map(Pattern::pattern)
                    .collect(Collectors.toCollection(ArrayList::new));

            adapter.replacementFilterValues = new HashMap<>(filters.replacementFilter.size());

            filters.replacementFilter.forEach(((pattern, replacement) -> {
                adapter.replacementFilterValues.put(pattern.pattern(), replacement);
            }));

            adapter.lengthFilterValue = filters.messageLengthFilter;
            adapter.wordLengthFilterValue = filters.wordLengthFilter;

            return adapter;
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(MiraFilters.class);

    private static final int DEFAULT_MESSAGE_LEN_FILTER = 300;
    private static final int DEFAULT_WORD_LEN_FILTER = 16;

    private final ArrayList<Pattern> preFilter;
    private final ArrayList<Pattern> postFilter;
    private final HashMap<Pattern, String> replacementFilter;

    private int messageLengthFilter = DEFAULT_MESSAGE_LEN_FILTER;
    private int wordLengthFilter = DEFAULT_WORD_LEN_FILTER;

    private MiraFilters()
    {
        this.preFilter = new ArrayList<>();
        this.postFilter = new ArrayList<>();
        this.replacementFilter = new HashMap<>();
    }

    private MiraFilters(final ArrayList<String> preFilter,
                        final ArrayList<String> postFilter,
                        final HashMap<String, String> replacementFilter,
                        final int lenFilter,
                        final int wordLengthFilter)
    {
        this.preFilter = new ArrayList<>(preFilter.size());
        this.postFilter = new ArrayList<>(postFilter.size());
        this.replacementFilter = new HashMap<>(replacementFilter.size());

        if (lenFilter > 0)
        {
            this.messageLengthFilter = lenFilter;
        }

        if (wordLengthFilter > 0)
        {
            this.wordLengthFilter = wordLengthFilter;
        }

        preFilter.forEach(str ->
                this.preFilter.add(Pattern.compile(
                        str,
                        Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE
                ))
        );

        postFilter.forEach(str ->
                this.postFilter.add(Pattern.compile(
                        str,
                        Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE
                ))
        );

        replacementFilter.forEach((pattern, replacement) ->
            this.replacementFilter.put(Pattern.compile(
                    pattern,
                    Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
                    replacement
            )
        );
    }

    public static MiraFilters fromAdapter(final Adapter adapter)
    {
        return new MiraFilters(
                adapter.preFilterValues,
                adapter.postFilterValues,
                adapter.replacementFilterValues,
                adapter.lengthFilterValue,
                adapter.wordLengthFilterValue
        );
    }

    public static MiraFilters empty()
    {
        return new MiraFilters();
    }

    public ArrayList<Pattern> getPreFilter()
    {
        return this.preFilter;
    }

    public ArrayList<Pattern> getPostFilter()
    {
        return this.postFilter;
    }

    public int getMessageLengthFilter()
    {
        return this.messageLengthFilter;
    }

    public int getWordLengthFilter()
    {
        return this.wordLengthFilter;
    }

    public boolean testPreFilter(final String response)
    {
        for (final Pattern pattern : this.preFilter)
        {
            if (pattern.matcher(response).find())
            {
                LOGGER.warn("Mira pre-filter failed: {}", response);
                return false;
            }
        }
        return true;
    }

    public String runPostFilter(final String response)
    {
        String postFiltered = response;

        for (final Pattern pattern : this.postFilter)
        {
            final Matcher matcher = pattern.matcher(postFiltered);
            if (matcher.find())
            {
                postFiltered = matcher.replaceAll(" * ");
                LOGGER.warn("Mira post-filter triggered: '{}'", matcher.pattern());
            }
        }

        return postFiltered;
    }

    public String runReplacementFilter(final String postFiltered)
    {
        String result = postFiltered;

        for (var elem : this.replacementFilter.entrySet())
        {
            final Pattern pattern = elem.getKey();
            final Matcher matcher = pattern.matcher(result);

            if (matcher.find())
            {
                final String replacement = elem.getValue();
                result = matcher.replaceAll(replacement);
                LOGGER.warn("Replacement filter triggered: '{}' -> '{}'", matcher.pattern(), replacement);
            }
        }

        return result;
    }

    public String truncateLength(final String response)
    {
        if (response.length() <= this.messageLengthFilter) { return response; }
        return response.substring(0, this.messageLengthFilter - 4).concat("...");
    }

    public ArrayList<String> splitWideWords(final String response)
    {
        return this.splitByMaxLen(response, this.wordLengthFilter);
    }

    public ArrayList<String> splitMessageByBlocks(final String response)
    {
        final ArrayList<String> result = new ArrayList<>();
        final int responseLength = response.length();

        for (int i = 0; i < responseLength; i += this.messageLengthFilter)
        {
            final int index = Math.min(i + this.messageLengthFilter, responseLength);
            result.add(response.substring(i, index));
        }

        return result;
    }

    private ArrayList<String> splitByMaxLen(final String str, final int maxLen)
    {
        final String[] words = str.split(" ");
        final ArrayList<String> result = new ArrayList<>();

        for (final String word : words)
        {
            for (int i = 0; i < word.length(); i += maxLen)
            {
                final int end = Math.min(i + maxLen, word.length());
                result.add(word.substring(i, end).trim());
            }
        }

        return result;
    }
}
