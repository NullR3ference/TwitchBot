package org.aytsan_lex.twitchbot.BotCommands.filters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

public class MiraPostFilter
{
    public static final Pattern URL_PATTERN = Pattern.compile(
            "\\b(?:http://|https://|ftp://|sftp://|wws://|www.)[a-zA-Z0-9-]+.[a-zA-Z]+\\b",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE
    );

    public static ArrayList<Pattern> VALUES = new ArrayList<>(Arrays.asList(
            Pattern.compile("([\\W|\\d|\\s]*)нигер([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)ниггер([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)негр([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)hегр([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)heгр([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)hегp([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)nigga([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)nigger([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)cuckold([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)аутист([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)аутизм([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)куколд([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)инцел([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)симп(\\W*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)девственник([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)пидор([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)нацист([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)нацизм([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)фашист([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)фашизм([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)даун([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)узкоглаз([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)чинг-чонг([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)чингчонг([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)чин-чонг([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)чинг-чон([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),

            Pattern.compile("([\\W|\\d|\\s]*)фирамир([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)фираммир([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)бб([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)все к дк([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)кашин([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)!age([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)хесус([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)хесус([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)xесус([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)xeсус([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)xecус([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)xecyс([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)xecyc([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)хeсус([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)хесyс([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),

            Pattern.compile("([\\W|\\d|\\s]*)зeтник([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)3eтник([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),

            Pattern.compile("([\\W|\\d|\\s]*)cbo([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)CBO([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)СBO([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)СВ0([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)СBO([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)война([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)войну([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)войны([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)сво([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)специальная военная операция([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)специальную военная операцию([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),

            Pattern.compile("([\\W|\\d|\\s]*)бандер([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)бaндер([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),

            Pattern.compile("([\\W|\\d|\\s]*)политический([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)политические([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)ватник([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)вата([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)ватники([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)ватников([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)зетник([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)зетники([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)зетников([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)либерал([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)либералов([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)либераха([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)центрист([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)центристы([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)центристов([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)анархист([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)анархисты([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)анархистов([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)анархия([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)соя([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)соевый([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)террорист([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE)
    ));
}
