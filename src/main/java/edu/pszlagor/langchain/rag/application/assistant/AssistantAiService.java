package edu.pszlagor.langchain.rag.application.assistant;

import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;

@AiService
interface AssistantAiService {

    @UserMessage("""
            Use the following pieces of context to answer the question at the end.
            Don't try to make up an answer.
            Use three sentences maximum. Keep the answer as concise as possible.
            If the answer cannot be found in the provided context, write: "{{fallbackAnswer}}“
            Context surrounded by triple backticks: ```{{information}}```
            Question surrounded by triple backticks: ```{{question}}```
            """)
    String chat(@V("question") String userMessage, @V("information") String information, @V("fallbackAnswer") String fallbackAnswer);
}
