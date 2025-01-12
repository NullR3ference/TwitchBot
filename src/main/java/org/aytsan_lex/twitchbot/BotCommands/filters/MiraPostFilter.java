package org.aytsan_lex.twitchbot.BotCommands.filters;

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
            Pattern.compile("([\\W|\\d|\\s]*)cuckold([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)hегр([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)heгр([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)hегp([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)аутист([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)аутизм([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)куколд([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)инцел([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)симп([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)девственник([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)гей([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)пидор([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)нацист([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)нацизм([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)фашист([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)фашизм([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)сексист([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)даун([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)узкоглаз([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),

            Pattern.compile("([\\W|\\d|\\s]*)http:/([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)https:/([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)ftp:/([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)wws:/([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)file://([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*).com([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*).ru([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*).de([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)www.([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),

            Pattern.compile("([\\W|\\d|\\s]*)фирамир([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)фираммир([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)фира([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)римариф([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)бб([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)хуесос([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)все к дк([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)дк([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)кашин([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)хесус([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)!age([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)```∫([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)∫([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)∇([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)ψ([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE)
    ));
}
