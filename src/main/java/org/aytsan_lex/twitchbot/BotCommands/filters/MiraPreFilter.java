package org.aytsan_lex.twitchbot.BotCommands.filters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

public class MiraPreFilter
{
    public static ArrayList<Pattern> VALUES = new ArrayList<>(Arrays.asList(
            Pattern.compile("([\\W|\\d|\\s]*)/me([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)гитлер([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)гитлеp([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)гитлeр([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)гойда([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE)
    ));
}