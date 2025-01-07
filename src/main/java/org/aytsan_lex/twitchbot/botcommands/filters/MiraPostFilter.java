package org.aytsan_lex.twitchbot.botcommands.filters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

public class MiraPostFilter
{
    public static ArrayList<Pattern> VALUES = new ArrayList<>(Arrays.asList(
            Pattern.compile("([\\W|\\d|\\s]*)нигер([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)ниггер([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)негр([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)nigga([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)nigger([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)аутист([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)аутизм([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)куколд([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)инцел([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)симп([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)аутизм([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)девственник([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)гей([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)пидор([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)нацист([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)нацизм([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)фашист([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)фашизм([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)сексист([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)даун([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)узкоглаз([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE)
    ));
}
