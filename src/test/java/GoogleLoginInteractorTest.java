import use_case.google_login.GoogleLoginInteractor;

import entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import use_case.AuthFailureException;
import use_case.GoogleAuthResult;
import use_case.IAuthGateway;
import use_case.IUserRepo;
import use_case.PersistenceException;
import use_case.google_login.GoogleLoginOutputBoundary;
import use_case.google_login.GoogleLoginOutputData;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for GoogleLoginInteractor using Mockito, which auto-generates fake objects at runtime to test only the
 *  * interactor's logic.
 * Tests both registration (new user) and login (existing user) flows via Google OAuth.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("GoogleLoginInteractor Tests")
class GoogleLoginInteractorTest {

    @Mock
    private IAuthGateway mockAuthGateway;

    @Mock
    private IUserRepo mockUserRepository;

    @Mock
    private GoogleLoginOutputBoundary mockPresenter;

    private GoogleLoginInteractor interactor;

    private static final String TEST_UID = "google-uid-123";
    private static final String TEST_EMAIL = "user@gmail.com";
    private static final String TEST_DISPLAY_NAME = "Google User";

    @BeforeEach
    void setUp() {
        interactor = new GoogleLoginInteractor(
                mockAuthGateway,
                mockUserRepository,
                mockPresenter
        );
    }

    // ==================== SUCCESS: FIRST-TIME REGISTRATION ====================

    @Test
    @DisplayName("Success: First-time Google user - creates new profile")
    void testFirstTimeGoogleUserRegistration() throws IOException {
        // Arrange
        GoogleAuthResult googleAuthResult = new GoogleAuthResult(TEST_UID, TEST_EMAIL, TEST_DISPLAY_NAME);

        when(mockAuthGateway.loginWithGoogle())
                .thenReturn(googleAuthResult);
        when(mockUserRepository.existsByUid(TEST_UID))
                .thenReturn(false);  // User doesn't exist yet

        // Act
        interactor.execute();

        // Assert
        // 1. Verify OAuth flow was initiated
        verify(mockAuthGateway).loginWithGoogle();

        // 2. Verify checked if user exists
        verify(mockUserRepository).existsByUid(TEST_UID);

        // 3. Verify new user was created and saved
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(mockUserRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals(TEST_UID, savedUser.getUid());
        assertEquals(TEST_EMAIL, savedUser.getEmail());
        assertEquals(TEST_DISPLAY_NAME, savedUser.getNickname());

        // 4. Verify success view was prepared
        ArgumentCaptor<GoogleLoginOutputData> outputCaptor =
                ArgumentCaptor.forClass(GoogleLoginOutputData.class);
        verify(mockPresenter).prepareSuccessView(outputCaptor.capture());

        GoogleLoginOutputData output = outputCaptor.getValue();
        assertEquals(TEST_DISPLAY_NAME, output.getNickname());
        assertTrue(output.isSuccess());
        assertEquals(TEST_UID, output.getUid());

        // 5. Verify no failure view was called
        verify(mockPresenter, never()).prepareFailView(anyString());

        // 6. Verify logout was never called
        verify(mockAuthGateway, never()).logout();
    }

    // ==================== SUCCESS: RETURNING USER LOGIN ====================

    @Test
    @DisplayName("Success: Returning Google user - loads existing profile")
    void testReturningGoogleUserLogin() throws IOException {
        // Arrange
        GoogleAuthResult googleAuthResult = new GoogleAuthResult(TEST_UID, TEST_EMAIL, TEST_DISPLAY_NAME);
        User existingUser = new User(TEST_UID, TEST_EMAIL, "Original Nickname");

        when(mockAuthGateway.loginWithGoogle())
                .thenReturn(googleAuthResult);
        when(mockUserRepository.existsByUid(TEST_UID))
                .thenReturn(true);  // User already exists
        when(mockUserRepository.getUserByUid(TEST_UID))
                .thenReturn(Optional.of(existingUser));

        // Act
        interactor.execute();

        // Assert
        // 1. Verify OAuth flow was initiated
        verify(mockAuthGateway).loginWithGoogle();

        // 2. Verify checked if user exists
        verify(mockUserRepository).existsByUid(TEST_UID);

        // 3. Verify existing user was retrieved (NOT saved again)
        verify(mockUserRepository).getUserByUid(TEST_UID);
        verify(mockUserRepository, never()).save(any(User.class));

        // 4. Verify success view was prepared with EXISTING user's data
        ArgumentCaptor<GoogleLoginOutputData> outputCaptor =
                ArgumentCaptor.forClass(GoogleLoginOutputData.class);
        verify(mockPresenter).prepareSuccessView(outputCaptor.capture());

        GoogleLoginOutputData output = outputCaptor.getValue();
        assertEquals("Original Nickname", output.getNickname());  // Uses existing nickname, not Google's
        assertTrue(output.isSuccess());
        assertEquals(TEST_UID, output.getUid());

        // 5. Verify no failure view was called
        verify(mockPresenter, never()).prepareFailView(anyString());
    }

    // ==================== OAUTH FAILURES ====================

    @Test
    @DisplayName("Failure: Network error during OAuth")
    void testNetworkErrorDuringOAuth() throws IOException {
        // Arrange
        when(mockAuthGateway.loginWithGoogle())
                .thenThrow(new IOException("Network timeout"));

        // Act
        interactor.execute();

        // Assert
        verify(mockPresenter).prepareFailView("Google Sign-In failed: Network timeout");
        verify(mockUserRepository, never()).existsByUid(anyString());
    }

    @Test
    @DisplayName("Failure: Invalid OAuth credentials")
    void testInvalidOAuthCredentials() throws IOException {
        // Arrange
        when(mockAuthGateway.loginWithGoogle())
                .thenThrow(new AuthFailureException("Invalid OAuth client credentials"));

        // Act
        interactor.execute();

        // Assert
        verify(mockPresenter).prepareFailView("Google Sign-In failed: Invalid OAuth client credentials");
    }

    // ==================== PERSISTENCE FAILURES ====================

    @Test
    @DisplayName("Failure: Cannot save new Google user profile")
    void testCannotSaveNewGoogleUserProfile() throws IOException {
        // Arrange
        GoogleAuthResult googleAuthResult = new GoogleAuthResult(TEST_UID, TEST_EMAIL, TEST_DISPLAY_NAME);

        when(mockAuthGateway.loginWithGoogle())
                .thenReturn(googleAuthResult);
        when(mockUserRepository.existsByUid(TEST_UID))
                .thenReturn(false);  // New user
        doThrow(new PersistenceException("Firestore connection failed"))
                .when(mockUserRepository).save(any(User.class));

        // Act
        interactor.execute();

        // Assert
        // 1. OAuth succeeded
        verify(mockAuthGateway).loginWithGoogle();

        // 2. Attempted to save
        verify(mockUserRepository).save(any(User.class));

        // 3. Session was cleared (logout)
        verify(mockAuthGateway).logout();

        // 4. Failure view called
        verify(mockPresenter).prepareFailView("Google Sign-In failed: Could not save user profile.");

        // 5. Success view never called
        verify(mockPresenter, never()).prepareSuccessView(any(GoogleLoginOutputData.class));
    }

    @Test
    @DisplayName("Failure: Profile missing for existing Google user")
    void testProfileMissingForExistingUser() throws IOException {
        // Arrange - existsByUid says true, but getUserByUid returns empty
        GoogleAuthResult googleAuthResult = new GoogleAuthResult(TEST_UID, TEST_EMAIL, TEST_DISPLAY_NAME);

        when(mockAuthGateway.loginWithGoogle())
                .thenReturn(googleAuthResult);
        when(mockUserRepository.existsByUid(TEST_UID))
                .thenReturn(true);  // Claims user exists
        when(mockUserRepository.getUserByUid(TEST_UID))
                .thenReturn(Optional.empty());  // But profile is missing!

        // Act
        interactor.execute();

        // Assert
        verify(mockAuthGateway).logout();
        verify(mockPresenter).prepareFailView("Profile data missing after successful Google login.");
        verify(mockPresenter, never()).prepareSuccessView(any(GoogleLoginOutputData.class));
    }

    // ==================== DATA INTEGRITY ====================

    @Test
    @DisplayName("Verify Google data is correctly mapped to User entity")
    void testGoogleDataMappedCorrectly() throws IOException {
        // Arrange
        GoogleAuthResult googleAuthResult = new GoogleAuthResult(
                "google-uid-abc123",
                "john.doe@gmail.com",
                "John Doe"
        );

        when(mockAuthGateway.loginWithGoogle())
                .thenReturn(googleAuthResult);
        when(mockUserRepository.existsByUid("google-uid-abc123"))
                .thenReturn(false);

        // Act
        interactor.execute();

        // Assert - Verify User entity created with correct data
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(mockUserRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals("google-uid-abc123", savedUser.getUid());
        assertEquals("john.doe@gmail.com", savedUser.getEmail());
        assertEquals("John Doe", savedUser.getNickname());
        assertEquals("en", savedUser.getLanguage());  // Default language
        assertTrue(savedUser.getFavoriteRestaurantIds().isEmpty());  // Empty favorites
    }

    // ==================== EDGE CASES ====================

    @Test
    @DisplayName("Edge Case: Google returns minimal display name")
    void testMinimalDisplayName() throws IOException {
        // Arrange
        GoogleAuthResult googleAuthResult = new GoogleAuthResult(TEST_UID, TEST_EMAIL, "A");

        when(mockAuthGateway.loginWithGoogle())
                .thenReturn(googleAuthResult);
        when(mockUserRepository.existsByUid(TEST_UID))
                .thenReturn(false);

        // Act
        interactor.execute();

        // Assert
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(mockUserRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals("A", savedUser.getNickname());
    }

    @Test
    @DisplayName("Edge Case: Google returns email with special characters")
    void testEmailWithSpecialCharacters() throws IOException {
        // Arrange
        GoogleAuthResult googleAuthResult = new GoogleAuthResult(
                TEST_UID,
                "user+test@gmail.com",  // Plus addressing
                TEST_DISPLAY_NAME
        );

        when(mockAuthGateway.loginWithGoogle())
                .thenReturn(googleAuthResult);
        when(mockUserRepository.existsByUid(TEST_UID))
                .thenReturn(false);

        // Act
        interactor.execute();

        // Assert
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(mockUserRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals("user+test@gmail.com", savedUser.getEmail());
    }


    // ==================== INTEGRATION TESTS ====================

    @Test
    @DisplayName("Integration: Verify execution order for new user")
    void testExecutionOrderForNewUser() throws IOException {
        // Arrange
        GoogleAuthResult googleAuthResult = new GoogleAuthResult(TEST_UID, TEST_EMAIL, TEST_DISPLAY_NAME);

        when(mockAuthGateway.loginWithGoogle())
                .thenReturn(googleAuthResult);
        when(mockUserRepository.existsByUid(TEST_UID))
                .thenReturn(false);

        // Act
        interactor.execute();

        // Assert - Verify execution order
        var inOrder = inOrder(mockAuthGateway, mockUserRepository, mockPresenter);

        // 1. OAuth flow
        inOrder.verify(mockAuthGateway).loginWithGoogle();

        // 2. Check existence
        inOrder.verify(mockUserRepository).existsByUid(TEST_UID);

        // 3. Save new user
        inOrder.verify(mockUserRepository).save(any(User.class));

        // 4. Call presenter
        inOrder.verify(mockPresenter).prepareSuccessView(any(GoogleLoginOutputData.class));
    }

    @Test
    @DisplayName("Integration: Verify execution order for existing user")
    void testExecutionOrderForExistingUser() throws IOException {
        // Arrange
        GoogleAuthResult googleAuthResult = new GoogleAuthResult(TEST_UID, TEST_EMAIL, TEST_DISPLAY_NAME);
        User existingUser = new User(TEST_UID, TEST_EMAIL, TEST_DISPLAY_NAME);

        when(mockAuthGateway.loginWithGoogle())
                .thenReturn(googleAuthResult);
        when(mockUserRepository.existsByUid(TEST_UID))
                .thenReturn(true);
        when(mockUserRepository.getUserByUid(TEST_UID))
                .thenReturn(Optional.of(existingUser));

        // Act
        interactor.execute();

        // Assert - Verify execution order
        var inOrder = inOrder(mockAuthGateway, mockUserRepository, mockPresenter);

        // 1. OAuth flow
        inOrder.verify(mockAuthGateway).loginWithGoogle();

        // 2. Check existence
        inOrder.verify(mockUserRepository).existsByUid(TEST_UID);

        // 3. Load existing user (NOT save)
        inOrder.verify(mockUserRepository).getUserByUid(TEST_UID);

        // 4. Call presenter
        inOrder.verify(mockPresenter).prepareSuccessView(any(GoogleLoginOutputData.class));

        // 5. Verify save was never called
        verify(mockUserRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Verify different behavior for new vs existing users")
    void testNewVsExistingUserBehavior() throws IOException {
        // Test 1: New user flow
        GoogleAuthResult googleAuthResult = new GoogleAuthResult(TEST_UID, TEST_EMAIL, "New User");

        when(mockAuthGateway.loginWithGoogle())
                .thenReturn(googleAuthResult);
        when(mockUserRepository.existsByUid(TEST_UID))
                .thenReturn(false);

        interactor.execute();

        verify(mockUserRepository).save(any(User.class));
        verify(mockUserRepository, never()).getUserByUid(anyString());

        // Reset mocks for test 2
        reset(mockAuthGateway, mockUserRepository, mockPresenter);

        // Test 2: Existing user flow
        User existingUser = new User(TEST_UID, TEST_EMAIL, "Existing User");

        when(mockAuthGateway.loginWithGoogle())
                .thenReturn(googleAuthResult);
        when(mockUserRepository.existsByUid(TEST_UID))
                .thenReturn(true);
        when(mockUserRepository.getUserByUid(TEST_UID))
                .thenReturn(Optional.of(existingUser));

        interactor.execute();

        verify(mockUserRepository, never()).save(any(User.class));
        verify(mockUserRepository).getUserByUid(TEST_UID);
    }
}