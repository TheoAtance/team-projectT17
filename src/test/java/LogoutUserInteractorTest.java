import use_case.logout.LogoutOutputBoundary;
import use_case.logout.LogoutOutputData;
import use_case.logout.LogoutUserInteractor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import use_case.IAuthGateway;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for LogoutUserInteractor using Mockito, which auto-generates fake objects at runtime to test only the
 * interactor's logic.
 * Tests all success and failure paths for user logout.
 */
class LogoutUserInteractorTest {

    private IAuthGateway authGateway;
    private LogoutOutputBoundary presenter;
    private LogoutUserInteractor interactor;

    @BeforeEach
    void setUp() {
        authGateway = mock(IAuthGateway.class);
        presenter = mock(LogoutOutputBoundary.class);

        interactor = new LogoutUserInteractor(authGateway, presenter);
    }

    @Test
    void testLogoutCallsGateway() {
        // WHEN
        interactor.execute();

        // THEN
        verify(authGateway, times(1)).logout();
    }

    @Test
    void testPresenterReceivesSuccess() {
        // WHEN
        interactor.execute();

        // THEN
        ArgumentCaptor<LogoutOutputData> captor =
                ArgumentCaptor.forClass(LogoutOutputData.class);

        verify(presenter).prepareSuccessView(captor.capture());

        LogoutOutputData data = captor.getValue();
        assertEquals("Logged out successfully", data.getMessage());
    }
}
