package org.aytsan_lex.twitchbot.ollama;

import java.util.ArrayList;

public interface IOllamaModel
{
    String chatWithModel(final ModelMessage message);
    ArrayList<String> getQuestionsHistory();
}
