package org.aytsan_lex.twitchbot.ollama;

import com.google.common.collect.ArrayListMultimap;

public interface IOllamaModel
{
    String chatWithModel(final ModelMessage message);
    ArrayListMultimap<String, String> getParams();
}
