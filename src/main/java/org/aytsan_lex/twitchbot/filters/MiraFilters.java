package org.aytsan_lex.twitchbot.filters;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class MiraFilters
{
    private static final int DEFAULT_MESSAGE_LEN_FILTER = 250;
    private static final int DEFAULT_WORD_LEN_FILTER = 15;

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

    public ArrayList<Pattern> getMuteCommandsFilter()
    {
        return this.muteCommandsFilter;
    }

    public int getMessageLengthFilter()
    {
        return this.messageLengthFilter;
    }

    public int getWordLengthFilter()
    {
        return this.wordLengthFilter;
    }
}
