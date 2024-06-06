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
            Context surrounded between triple backticks: ```{{information}}```
            Question surrounded between triple backticks:: ```{{question}}```
            If the answer cannot be found in the provided context, write: "{{fallbackAnswer}}“
            """)
    String chat(@V("question") String userMessage, @V("information") String information, @V("fallbackAnswer") String fallbackAnswer);
}
