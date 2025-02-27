package org.aytsan_lex.twitchbot.ollama;

import java.util.ArrayList;

import com.google.common.collect.ArrayListMultimap;

public interface IOllamaModel
{
    String chatWithModel(final ModelMessage message);
    ArrayList<String> getQuestionsHistory();
    void clearQuestionsHistory();
    ArrayListMultimap<String, String> getParams();
}
