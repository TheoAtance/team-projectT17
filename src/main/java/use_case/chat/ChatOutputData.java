// src/main/java/use_case/chat/ChatOutputData.java
package use_case.chat;

/**
 * Response Model for the AI chat use case.
 * Holds the AI's response text and possible error information.
 */
public class ChatOutputData {

    private final String responseText;
    private final boolean success;
    private final String errorMessage;

    public ChatOutputData(String responseText, boolean success, String errorMessage) {
        this.responseText = responseText;
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public String getResponseText() {
        return responseText;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
