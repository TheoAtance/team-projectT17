import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import use_case.chat.ChatGPTClient;
import use_case.chat.ChatInputData;
import use_case.chat.ChatInteractor;
import use_case.chat.ChatOutputBoundary;
import use_case.chat.ChatOutputData;

class ChatInteractorTest {

    /**
     * Fake implementation of ChatGPTClient that returns a predictable string.
     */
    private static class SuccessClient implements ChatGPTClient {
        @Override
        public String getRestaurantRecommendation(String userQuery) {
            return "RESPONSE_FOR_" + userQuery;
        }
    }

    /**
     * Fake implementation of ChatGPTClient that throws an exception
     * to trigger the catch block in the interactor.
     */
    private static class FailingClient implements ChatGPTClient {
        @Override
        public String getRestaurantRecommendation(String userQuery) {
            throw new RuntimeException("boom!");
        }
    }

    /**
     * Fake presenter that records the last ChatOutputData it received.
     */
    private static class RecordingPresenter implements ChatOutputBoundary {
        ChatOutputData lastData;

        @Override
        public void present(ChatOutputData data) {
            this.lastData = data;
        }
    }

    @Test
    void execute_success_callsClientAndPresenter() {
        // Arrange
        ChatGPTClient client = new SuccessClient();
        RecordingPresenter presenter = new RecordingPresenter();
        ChatInteractor interactor = new ChatInteractor(client, presenter);

        ChatInputData inputData = new ChatInputData("spicy food");

        // Act
        interactor.execute(inputData);

        // Assert
        Assertions.assertNotNull(presenter.lastData);
        Assertions.assertTrue(presenter.lastData.isSuccess());
        Assertions.assertEquals(
                "RESPONSE_FOR_spicy food",
                presenter.lastData.getResponseText()
        );
        Assertions.assertNull(presenter.lastData.getErrorMessage());
    }

    @Test
    void execute_failure_handlesExceptionFromClient() {
        // Arrange
        ChatGPTClient client = new FailingClient();
        RecordingPresenter presenter = new RecordingPresenter();
        ChatInteractor interactor = new ChatInteractor(client, presenter);

        ChatInputData inputData = new ChatInputData("whatever");

        // Act
        interactor.execute(inputData);

        // Assert
        Assertions.assertNotNull(presenter.lastData);
        Assertions.assertFalse(presenter.lastData.isSuccess());
        Assertions.assertEquals("", presenter.lastData.getResponseText());
        Assertions.assertEquals("boom!", presenter.lastData.getErrorMessage());
    }
}

