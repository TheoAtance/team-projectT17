// src/main/java/interface_adapter/chat/ChatState.java
package interface_adapter.chat;

public class ChatState {
    private String userInput = "";
    private String response = "";

    public String getUserInput() {
        return userInput;
    }

    public void setUserInput(String userInput) {
        this.userInput = userInput;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}