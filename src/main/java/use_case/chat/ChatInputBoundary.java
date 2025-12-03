// src/main/java/use_case/chat/ChatInputBoundary.java
package use_case.chat;

/**
 * Input Boundary for the AI chat use case.
 * The controller calls this interface to start the use case.
 */
public interface ChatInputBoundary {

    /**
     * Execute the AI chat use case.
     *
     * @param inputData request model containing the user's query
     */
    void execute(ChatInputData inputData);
}
