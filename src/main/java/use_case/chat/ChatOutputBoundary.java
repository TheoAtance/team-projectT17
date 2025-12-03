// src/main/java/use_case/chat/ChatOutputBoundary.java
package use_case.chat;

/**
 * Output Boundary for the AI chat use case.
 * The presenter implements this interface.
 */
public interface ChatOutputBoundary {

    /**
     * Called when the use case has a result to present
     * (either success or failure).
     *
     * @param outputData response model containing the result
     */
    void present(ChatOutputData outputData);
}
