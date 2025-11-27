import use_case.custom_login.CustomLoginInputData;
import use_case.custom_login.CustomLoginOutputBoundary;
import use_case.custom_login.CustomLoginUserInteractor;
import use_case.custom_login.CustomLoginOutputData;

import entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import use_case.AuthFailureException;
import use_case.IAuthGateway;
import use_case.IUserRepo;
import use_case.PersistenceException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CustomLoginUserInteractor using Mockito, which auto-generates fake objects at runtime to test only the
 * interactor's logic.
 * Tests all success and failure paths for user login.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CustomLoginUserInteractor Tests")
class CustomLoginUserInteractorTest {

    @Mock
    private IAuthGateway mockAuthGateway;

    @Mock
    private IUserRepo mockUserRepository;

    @Mock
    private CustomLoginOutputBoundary mockPresenter;

    private CustomLoginUserInteractor interactor;

    private static final String TEST_UID = "test-uid-123";
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD = "password123";
    private static final String TEST_NICKNAME = "TestUser";

    @BeforeEach
    void setUp() {
        interactor = new CustomLoginUserInteractor(
                mockAuthGateway,
                mockUserRepository,
                mockPresenter
        );
    }

    // ==================== SUCCESS CASES ====================

    @Test
    @DisplayName("Success: Valid login authenticates and retrieves user profile")
    void testSuccessfulLogin() {
        // Arrange
        CustomLoginInputData inputData = new CustomLoginInputData(TEST_EMAIL, TEST_PASSWORD);

        User mockUser = new User(TEST_UID, TEST_EMAIL, TEST_NICKNAME);

        when(mockAuthGateway.loginWithEmailAndPassword(TEST_EMAIL, TEST_PASSWORD))
                .thenReturn(TEST_UID);
        when(mockAuthGateway.getCurrentUserUid())
                .thenReturn(TEST_UID);
        when(mockUserRepository.getUserByUid(TEST_UID))
                .thenReturn(mockUser);

        // Act
        interactor.execute(inputData);

        // Assert
        // 1. Verify authentication was attempted
        verify(mockAuthGateway).loginWithEmailAndPassword(TEST_EMAIL, TEST_PASSWORD);

        // 2. Verify loadAllUsers was called after successful login
        verify(mockUserRepository).loadAllUsers();

        // 3. Verify getCurrentUserUid was called by CurrentUser instance
        verify(mockAuthGateway, atLeastOnce()).getCurrentUserUid();

        // 4. Verify success view was prepared with correct data
        ArgumentCaptor<CustomLoginOutputData> outputCaptor =
                ArgumentCaptor.forClass(CustomLoginOutputData.class);
        verify(mockPresenter).prepareSuccessView(outputCaptor.capture());

        CustomLoginOutputData output = outputCaptor.getValue();
        assertEquals(TEST_NICKNAME, output.getNickname());
        assertTrue(output.isSuccess());
        assertEquals(TEST_UID, output.getUid());

        // 5. Verify no failure view was called
        verify(mockPresenter, never()).prepareFailView(anyString());

        // 6. Verify logout was never called
        verify(mockAuthGateway, never()).logout();
    }

    // ==================== AUTHENTICATION FAILURES ====================

    @Test
    @DisplayName("Failure: Invalid password")
    void testInvalidPassword() {
        // Arrange
        CustomLoginInputData inputData = new CustomLoginInputData(TEST_EMAIL, "wrongPassword");

        when(mockAuthGateway.loginWithEmailAndPassword(TEST_EMAIL, "wrongPassword"))
                .thenThrow(new AuthFailureException("Invalid email or password."));

        // Act
        interactor.execute(inputData);

        // Assert
        verify(mockAuthGateway).loginWithEmailAndPassword(TEST_EMAIL, "wrongPassword");
        verify(mockPresenter).prepareFailView("Login failed: Invalid email or password.");
        verify(mockUserRepository, never()).getUserByUid(anyString());
        verify(mockUserRepository, never()).loadAllUsers();
        verify(mockPresenter, never()).prepareSuccessView(any(CustomLoginOutputData.class));
    }

    @Test
    @DisplayName("Failure: Email not found")
    void testEmailNotFound() {
        // Arrange
        CustomLoginInputData inputData = new CustomLoginInputData("nonexistent@example.com", TEST_PASSWORD);

        when(mockAuthGateway.loginWithEmailAndPassword("nonexistent@example.com", TEST_PASSWORD))
                .thenThrow(new AuthFailureException("Invalid email or password."));

        // Act
        interactor.execute(inputData);

        // Assert
        verify(mockPresenter).prepareFailView("Login failed: Invalid email or password.");
        verify(mockUserRepository, never()).getUserByUid(anyString());
        verify(mockUserRepository, never()).loadAllUsers();
    }

    @Test
    @DisplayName("Failure: Network error during authentication")
    void testNetworkErrorDuringAuth() {
        // Arrange
        CustomLoginInputData inputData = new CustomLoginInputData(TEST_EMAIL, TEST_PASSWORD);

        when(mockAuthGateway.loginWithEmailAndPassword(TEST_EMAIL, TEST_PASSWORD))
                .thenThrow(new RuntimeException("Network timeout"));

        // Act
        interactor.execute(inputData);

        // Assert
        verify(mockPresenter).prepareFailView("Login failed: Network timeout");
        verify(mockUserRepository, never()).getUserByUid(anyString());
        verify(mockUserRepository, never()).loadAllUsers();
    }

    // ==================== DATA INTEGRITY FAILURES ====================

    @Test
    @DisplayName("Failure: User profile missing from database after successful auth")
    void testProfileMissingAfterSuccessfulAuth() {
        // Arrange - Auth succeeds but profile doesn't exist
        CustomLoginInputData inputData = new CustomLoginInputData(TEST_EMAIL, TEST_PASSWORD);

        when(mockAuthGateway.loginWithEmailAndPassword(TEST_EMAIL, TEST_PASSWORD))
                .thenReturn(TEST_UID);
        when(mockUserRepository.getUserByUid(TEST_UID))
                .thenReturn(null);  // Profile missing!

        // Act
        interactor.execute(inputData);

        // Assert
        // 1. Auth succeeded
        verify(mockAuthGateway).loginWithEmailAndPassword(TEST_EMAIL, TEST_PASSWORD);

        // 2. Attempted to retrieve profile
        verify(mockUserRepository).getUserByUid(TEST_UID);

        // 3. Session was cleared (logout)
        verify(mockAuthGateway).logout();

        // 4. Failure view called with appropriate message
        verify(mockPresenter).prepareFailView("Account found, but profile data is missing.");

        // 5. Success view never called
        verify(mockPresenter, never()).prepareSuccessView(any(CustomLoginOutputData.class));

        // 6. loadAllUsers should never be called if profile is missing
        verify(mockUserRepository, never()).loadAllUsers();
    }

    // ==================== EDGE CASES ====================

    @Test
    @DisplayName("Edge Case: Empty email")
    void testEmptyEmail() {
        // Arrange
        CustomLoginInputData inputData = new CustomLoginInputData("", TEST_PASSWORD);

        when(mockAuthGateway.loginWithEmailAndPassword("", TEST_PASSWORD))
                .thenThrow(new AuthFailureException("Invalid email format"));

        // Act
        interactor.execute(inputData);

        // Assert
        verify(mockPresenter).prepareFailView("Login failed: Invalid email format");
        verify(mockUserRepository, never()).loadAllUsers();
    }

    @Test
    @DisplayName("Edge Case: Empty password")
    void testEmptyPassword() {
        // Arrange
        CustomLoginInputData inputData = new CustomLoginInputData(TEST_EMAIL, "");

        when(mockAuthGateway.loginWithEmailAndPassword(TEST_EMAIL, ""))
                .thenThrow(new AuthFailureException("Password cannot be empty"));

        // Act
        interactor.execute(inputData);

        // Assert
        verify(mockPresenter).prepareFailView("Login failed: Password cannot be empty");
        verify(mockUserRepository, never()).loadAllUsers();
    }

    @Test
    @DisplayName("Edge Case: User with special characters in nickname")
    void testUserWithSpecialCharactersInNickname() {
        // Arrange
        CustomLoginInputData inputData = new CustomLoginInputData(TEST_EMAIL, TEST_PASSWORD);

        User mockUser = new User(TEST_UID, TEST_EMAIL, "Test@User#123");

        when(mockAuthGateway.loginWithEmailAndPassword(TEST_EMAIL, TEST_PASSWORD))
                .thenReturn(TEST_UID);
        when(mockAuthGateway.getCurrentUserUid())
                .thenReturn(TEST_UID);
        when(mockUserRepository.getUserByUid(TEST_UID))
                .thenReturn(mockUser);

        // Act
        interactor.execute(inputData);

        // Assert
        ArgumentCaptor<CustomLoginOutputData> outputCaptor =
                ArgumentCaptor.forClass(CustomLoginOutputData.class);
        verify(mockPresenter).prepareSuccessView(outputCaptor.capture());

        CustomLoginOutputData output = outputCaptor.getValue();
        assertEquals("Test@User#123", output.getNickname());

        // Verify loadAllUsers was called
        verify(mockUserRepository).loadAllUsers();
    }

    @Test
    @DisplayName("Integration: Verify complete execution order")
    void testCompleteExecutionOrder() {
        // Arrange
        CustomLoginInputData inputData = new CustomLoginInputData(TEST_EMAIL, TEST_PASSWORD);
        User mockUser = new User(TEST_UID, TEST_EMAIL, TEST_NICKNAME);

        when(mockAuthGateway.loginWithEmailAndPassword(TEST_EMAIL, TEST_PASSWORD))
                .thenReturn(TEST_UID);
        when(mockAuthGateway.getCurrentUserUid())
                .thenReturn(TEST_UID);
        when(mockUserRepository.getUserByUid(TEST_UID))
                .thenReturn(mockUser);

        // Act
        interactor.execute(inputData);

        // Assert - Verify execution order
        var inOrder = inOrder(mockAuthGateway, mockUserRepository, mockPresenter);

        // 1. First, authenticate
        inOrder.verify(mockAuthGateway).loginWithEmailAndPassword(TEST_EMAIL, TEST_PASSWORD);

        // 2. Then, retrieve profile
        inOrder.verify(mockUserRepository).getUserByUid(TEST_UID);

        // 3. Load all users into cache
        inOrder.verify(mockUserRepository).loadAllUsers();

        // 4. Finally, call presenter
        inOrder.verify(mockPresenter).prepareSuccessView(any(CustomLoginOutputData.class));

        // 5. Logout should never be called in success case
        verify(mockAuthGateway, never()).logout();
    }

    @Test
    @DisplayName("Verify user data is correctly passed to presenter")
    void testUserDataPassedCorrectly() {
        // Arrange
        CustomLoginInputData inputData = new CustomLoginInputData(TEST_EMAIL, TEST_PASSWORD);

        // Create user with all fields populated
        User mockUser = new User(TEST_UID, TEST_EMAIL, TEST_NICKNAME);

        when(mockAuthGateway.loginWithEmailAndPassword(TEST_EMAIL, TEST_PASSWORD))
                .thenReturn(TEST_UID);
        when(mockAuthGateway.getCurrentUserUid())
                .thenReturn(TEST_UID);
        when(mockUserRepository.getUserByUid(TEST_UID))
                .thenReturn(mockUser);

        // Act
        interactor.execute(inputData);

        // Assert - Verify all user data is passed to output
        ArgumentCaptor<CustomLoginOutputData> outputCaptor =
                ArgumentCaptor.forClass(CustomLoginOutputData.class);
        verify(mockPresenter).prepareSuccessView(outputCaptor.capture());

        CustomLoginOutputData output = outputCaptor.getValue();
        assertNotNull(output);
        assertEquals(TEST_NICKNAME, output.getNickname());
        assertEquals(TEST_UID, output.getUid());
        assertTrue(output.isSuccess());

        // Verify loadAllUsers was called
        verify(mockUserRepository).loadAllUsers();
    }

    @Test
    @DisplayName("Verify CurrentUser can retrieve user after successful login")
    void testCurrentUserFunctionalityAfterLogin() {
        // Arrange
        CustomLoginInputData inputData = new CustomLoginInputData(TEST_EMAIL, TEST_PASSWORD);
        User mockUser = new User(TEST_UID, TEST_EMAIL, TEST_NICKNAME);

        when(mockAuthGateway.loginWithEmailAndPassword(TEST_EMAIL, TEST_PASSWORD))
                .thenReturn(TEST_UID);
        when(mockAuthGateway.getCurrentUserUid())
                .thenReturn(TEST_UID);
        when(mockUserRepository.getUserByUid(TEST_UID))
                .thenReturn(mockUser);

        // Act
        interactor.execute(inputData);

        // Assert
        // Verify that getCurrentUserUid was called at least once
        // (by the CurrentUser test instance in the interactor)
        verify(mockAuthGateway, atLeastOnce()).getCurrentUserUid();

        // Verify getUserByUid was called at least twice
        // (once for the main flow, once for CurrentUser test)
        verify(mockUserRepository, atLeast(2)).getUserByUid(TEST_UID);
    }
}