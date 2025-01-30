package org.aytsan_lex.twitchbot.filters;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class MiraFilters
{
    private static final int DEFAULT_MESSAGE_LEN_FILTER = 300;

    private final ArrayList<Pattern> preFilter;
    private final ArrayList<Pattern> postFilter;
    private int messageLengthFilter = DEFAULT_MESSAGE_LEN_FILTER;

    private MiraFilters()
    {
        this.preFilter = new ArrayList<>();
        this.postFilter = new ArrayList<>();
    }

    private MiraFilters(ArrayList<String> preFilter, ArrayList<String> postFilter, int lenFilter)
    {
        this.preFilter = new ArrayList<>(preFilter.size());
        this.postFilter = new ArrayList<>(postFilter.size());

        if (lenFilter > 0)
        {
            this.messageLengthFilter = lenFilter;
        }

        preFilter.forEach(str ->
                this.preFilter.add(Pattern.compile(
                        str,
                        Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE)
                ));

        postFilter.forEach(str ->
                this.postFilter.add(Pattern.compile(
                        str,
                        Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE)
                ));
    }

    public static MiraFilters of(ArrayList<String> preFilter, ArrayList<String> postFilter, int lenFilter)
    {
        return new MiraFilters(preFilter, postFilter, lenFilter);
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
}
