package use_case;

import entity.QuerySpec;
import service.LLMClient;


public class ParseQuerySpecInteractor {

    private final LLMClient llmClient;

    public ParseQuerySpecInteractor(LLMClient llmClient) {
        this.llmClient = llmClient;
    }

    public QuerySpec handle(String userMessage) throws Exception {
        return llmClient.extractQuerySpec(userMessage);
    }
}
