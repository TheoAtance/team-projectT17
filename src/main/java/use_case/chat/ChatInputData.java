// src/main/java/use_case/chat/ChatInputData.java
package use_case.chat;

/**
 * Request Model for the AI chat use case.
 * Currently it just stores the user's query string.
 */
public class ChatInputData {

    private final String userQuery;

    public ChatInputData(String userQuery) {
        this.userQuery = userQuery;
    }

    public String getUserQuery() {
        return userQuery;
    }
}
