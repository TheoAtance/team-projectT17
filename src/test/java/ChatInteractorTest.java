

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import use_case.chat.ChatGPTClient;
import use_case.chat.ChatInputBoundary;
import use_case.chat.ChatInputData;
import use_case.chat.ChatInteractor;
import use_case.chat.ChatOutputBoundary;
import use_case.chat.ChatOutputData;

/**
 * Simple unit test for ChatInteractor.
 * Uses fake ChatGPTClient and fake Presenter so we don't call real API.
 */
public class ChatInteractorTest {

    /**
     * A fake implementation of ChatGPTClient that just records the query
     * and returns a fixed fake response.
     */
    private static class FakeChatGPTClient implements ChatGPTClient {
        String lastQuery;

        @Override
        public String getRestaurantRecommendation(String userQuery) {
            this.lastQuery = userQuery;
            // return a predictable string for assertions
            return "FAKE_RESPONSE for: " + userQuery;
        }
    }

    /**
     * A fake presenter that just remembers the last output data it received.
     */
    private static class FakeChatPresenter implements ChatOutputBoundary {
        ChatOutputData lastData;
        boolean called = false;

        @Override
        public void present(ChatOutputData data) {
            this.called = true;
            this.lastData = data;
        }
    }

    @Test
    public void testExecute_callsClientAndPresenter() {
        // Arrange
        FakeChatGPTClient fakeClient = new FakeChatGPTClient();
        FakeChatPresenter fakePresenter = new FakeChatPresenter();
        ChatInputBoundary interactor = new ChatInteractor(fakeClient, fakePresenter);

        ChatInputData inputData = new ChatInputData("cheap spicy food near UofT");

        interactor.execute(inputData);

        assertEquals("cheap spicy food near UofT", fakeClient.lastQuery);

        assertTrue(fakePresenter.called);
        assertNotNull(fakePresenter.lastData);
        assertEquals(
                "FAKE_RESPONSE for: cheap spicy food near UofT",
                fakePresenter.lastData.getResponseText()
        );
    }
}
