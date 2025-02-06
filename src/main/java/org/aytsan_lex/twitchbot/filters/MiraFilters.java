package org.aytsan_lex.twitchbot.filters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MiraFilters
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MiraFilters.class);

    private static final int DEFAULT_MESSAGE_LEN_FILTER = 250;
    private static final int DEFAULT_WORD_LEN_FILTER = 15;

    private static final HashMap<Pattern, String> ageDetectionFilter = new HashMap<>(){{
        put(Pattern.compile(
                "мне \\d+ лет",
                Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE
        ), "мне ** лет");

        put(Pattern.compile(
                "мне \\d+",
                Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE
        ), "мне **");

        put(Pattern.compile(
                "\\d+ лет мне",
                Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE
        ), "** лет мне");

        put(Pattern.compile(
                "\\d+ мне лет",
                Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE
        ), "** мне лет");

        put(Pattern.compile(
                "im \\d+ years",
                Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE
        ), "im ** years");

        put(Pattern.compile(
                "im \\d+",
                Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE
        ), "im **");

        put(Pattern.compile(
                "im \\d+ y. o.",
                Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE
        ), "im ** y. o.");

        put(Pattern.compile(
                "i \\d+ years",
                Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE
        ), "i ** years");
    }};

    private final ArrayList<Pattern> preFilter;
    private final ArrayList<Pattern> postFilter;
    private final ArrayList<Pattern> muteCommandsFilter;


    private int messageLengthFilter = DEFAULT_MESSAGE_LEN_FILTER;
    private int wordLengthFilter = DEFAULT_WORD_LEN_FILTER;

    private MiraFilters()
    {
        this.preFilter = new ArrayList<>();
        this.postFilter = new ArrayList<>();
        this.muteCommandsFilter = new ArrayList<>();
    }

    private MiraFilters(ArrayList<String> preFilter,
                        ArrayList<String> postFilter,
                        ArrayList<String> muteCommandsFilter,
                        int lenFilter,
                        int wordLengthFilter)
    {
        this.preFilter = new ArrayList<>(preFilter.size());
        this.postFilter = new ArrayList<>(postFilter.size());
        this.muteCommandsFilter = new ArrayList<>(muteCommandsFilter.size());

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

        muteCommandsFilter.forEach(str ->
            this.muteCommandsFilter.add(Pattern.compile(
                    str,
                    Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE
            ))
        );
    }

    public static MiraFilters of(ArrayList<String> preFilter,
                                 ArrayList<String> postFilter,
                                 ArrayList<String> muteCommandsFilter,
                                 int lenFilter,
                                 int wordLengthFilter)
    {
        return new MiraFilters(preFilter, postFilter, muteCommandsFilter, lenFilter, wordLengthFilter);
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

    public boolean testMuteCommandsFilter(final String response)
    {
        for (final Pattern pattern : this.muteCommandsFilter)
        {
            if (pattern.matcher(response).find())
            {
                return false;
            }
        }
        return true;
    }

    public boolean testBotCommandFilter(final String response)
    {
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

        for (final Map.Entry<Pattern, String> entry : ageDetectionFilter.entrySet())
        {
            final Matcher matcher = entry.getKey().matcher(postFiltered);
            final String replacement = entry.getValue();

            if (matcher.find())
            {
                postFiltered = matcher.replaceAll(replacement);
                LOGGER.warn("Mira age detection filter triggered: '{}'", matcher.pattern());
            }
        }

        return postFiltered;
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
