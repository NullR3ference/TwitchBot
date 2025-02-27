package org.aytsan_lex.twitchbot.ollama;

import java.util.ArrayList;
import java.util.HashMap;

public interface IOllamaModel
{
    String chatWithModel(final ModelMessage message);
    ArrayList<String> getQuestionsHistory();
    void clearQuestionsHistory();
    HashMap<String, String> getParams();
}
