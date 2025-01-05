package org.aytsan_lex.twitchbot.botcommands.filters;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class PoliticsFilter
{
    // TODO: Update politics context filter
    // https://regex101.com/

    public static final ArrayList<Pattern> VALUES = new ArrayList<>(Arrays.asList(
            Pattern.compile("([\\W|\\d|\\s]*)хесус([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)xесус([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)xeсус([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)xecус([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)xecyс([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)xecyc([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)хeсус([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)хесyс([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),

            Pattern.compile("([\\W|\\d|\\s]*)зeтник([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)зeтники([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)зeтников([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)3eтник([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)3eтники([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)3eтников([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),

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

            Pattern.compile("([\\W|\\d|\\s]*)россия([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)россию([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)русский([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)русские([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)рф([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)украин([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)укр([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)404([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)сша([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)палестина([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)израиль([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)политик([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
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
            Pattern.compile("([\\W|\\d|\\s]*)свобода([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)свободу([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)соя([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)соевый([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)наш([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)ненаш([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)не наш([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)агент([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)террорист([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)национальность([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE),
            Pattern.compile("([\\W|\\d|\\s]*)национальности([\\W|\\d|\\s]*)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.UNICODE_CASE)
    ));
}
