import use_case.custom_register.RegisterInputData;
import use_case.custom_register.RegisterOutputBoundary;
import use_case.custom_register.RegisterOutputData;
import use_case.custom_register.RegisterUserInteractor;


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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for RegisterUserInteractor using Mockito, which auto-generates fake objects at runtime to test only the
 * interactor's logic.
 * Tests all success and failure paths for user registration.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("RegisterUserInteractor Tests")
class RegisterUserInteractorTest {

    @Mock
    private IAuthGateway mockAuthGateway;

    @Mock
    private IUserRepo mockUserRepository;

    @Mock
    private RegisterOutputBoundary mockPresenter;

    private RegisterUserInteractor interactor;

    @BeforeEach
    void setUp() {
        interactor = new RegisterUserInteractor(
                mockAuthGateway,
                mockUserRepository,
                mockPresenter
        );
    }

    // ==================== SUCCESS CASES ====================

    @Test
    @DisplayName("Success: Valid registration creates user and calls presenter")
    void testSuccessfulRegistration() {
        // Arrange
        RegisterInputData inputData = new RegisterInputData(
                "test@example.com",
                "password123",
                "password123",
                "TestUser"
        );

        String expectedUid = "firebase-uid-123";
        when(mockAuthGateway.registerWithEmailAndPassword("test@example.com", "password123"))
                .thenReturn(expectedUid);

        // Act
        interactor.execute(inputData);

        // Assert
        // 1. Verify auth gateway was called with correct credentials
        verify(mockAuthGateway).registerWithEmailAndPassword("test@example.com", "password123");

        // 2. Verify user was saved to repository
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(mockUserRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals(expectedUid, savedUser.getUid());
        assertEquals("test@example.com", savedUser.getEmail());
        assertEquals("TestUser", savedUser.getNickname());

        // 3. Verify success view was prepared
        ArgumentCaptor<RegisterOutputData> outputCaptor = ArgumentCaptor.forClass(RegisterOutputData.class);
        verify(mockPresenter).prepareSuccessView(outputCaptor.capture());

        RegisterOutputData output = outputCaptor.getValue();
        assertEquals("TestUser", output.getNickname());
        assertTrue(output.isSuccess());
        assertEquals(expectedUid, output.getUid());

        // 4. Verify no failure view was called
        verify(mockPresenter, never()).prepareFailView(anyString());
    }

    // ==================== VALIDATION FAILURES ====================

    @Test
    @DisplayName("Failure: Passwords don't match - validation fails before API call")
    void testPasswordMismatch() {
        // Arrange
        RegisterInputData inputData = new RegisterInputData(
                "test@example.com",
                "password123",
                "differentPassword",  // Doesn't match!
                "TestUser"
        );

        // Act
        interactor.execute(inputData);

        // Assert
        // 1. Verify failure view was called with correct message
        verify(mockPresenter).prepareFailView("Passwords do not match.");

        // 2. Verify auth gateway was NEVER called (early validation)
        verify(mockAuthGateway, never()).registerWithEmailAndPassword(anyString(), anyString());

        // 3. Verify repository was NEVER called
        verify(mockUserRepository, never()).save(any(User.class));

        // 4. Verify success view was never called
        verify(mockPresenter, never()).prepareSuccessView(any(RegisterOutputData.class));
    }

    // ==================== AUTHENTICATION FAILURES ====================

    @Test
    @DisplayName("Failure: Email already exists in Firebase Auth")
    void testEmailAlreadyExists() {
        // Arrange
        RegisterInputData inputData = new RegisterInputData(
                "existing@example.com",
                "password123",
                "password123",
                "TestUser"
        );

        when(mockAuthGateway.registerWithEmailAndPassword("existing@example.com", "password123"))
                .thenThrow(new AuthFailureException("Email already in use."));

        // Act
        interactor.execute(inputData);

        // Assert
        // 1. Verify auth gateway was called
        verify(mockAuthGateway).registerWithEmailAndPassword("existing@example.com", "password123");

        // 2. Verify failure view was called with error message
        verify(mockPresenter).prepareFailView("Registration failed: Email already in use.");

        // 3. Verify repository was never called (auth failed)
        verify(mockUserRepository, never()).save(any(User.class));

        // 4. Verify success view was never called
        verify(mockPresenter, never()).prepareSuccessView(any(RegisterOutputData.class));
    }

    @Test
    @DisplayName("Failure: Weak password rejected by Firebase")
    void testWeakPassword() {
        // Arrange
        RegisterInputData inputData = new RegisterInputData(
                "test@example.com",
                "123",  // Too short
                "123",
                "TestUser"
        );

        when(mockAuthGateway.registerWithEmailAndPassword("test@example.com", "123"))
                .thenThrow(new AuthFailureException("Password must be at least 6 characters."));

        // Act
        interactor.execute(inputData);

        // Assert
        verify(mockPresenter).prepareFailView("Registration failed: Password must be at least 6 characters.");
        verify(mockUserRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Failure: Network error during authentication")
    void testNetworkErrorDuringAuth() {
        // Arrange
        RegisterInputData inputData = new RegisterInputData(
                "test@example.com",
                "password123",
                "password123",
                "TestUser"
        );

        when(mockAuthGateway.registerWithEmailAndPassword("test@example.com", "password123"))
                .thenThrow(new RuntimeException("Network timeout"));

        // Act
        interactor.execute(inputData);

        // Assert
        verify(mockPresenter).prepareFailView("Registration failed: Network timeout");
        verify(mockUserRepository, never()).save(any(User.class));
    }

    // ==================== PERSISTENCE FAILURES ====================

    @Test
    @DisplayName("Failure: User profile fails to save to Firestore")
    void testProfileSaveFailure() {
        // Arrange
        RegisterInputData inputData = new RegisterInputData(
                "test@example.com",
                "password123",
                "password123",
                "TestUser"
        );

        String expectedUid = "firebase-uid-123";
        when(mockAuthGateway.registerWithEmailAndPassword("test@example.com", "password123"))
                .thenReturn(expectedUid);

        // Simulate Firestore failure
        doThrow(new PersistenceException("Could not connect to database"))
                .when(mockUserRepository).save(any(User.class));

        // Act
        interactor.execute(inputData);

        // Assert
        // 1. Auth succeeded
        verify(mockAuthGateway).registerWithEmailAndPassword("test@example.com", "password123");

        // 2. Attempted to save user
        verify(mockUserRepository).save(any(User.class));

        // 3. Failure view called with appropriate message
        verify(mockPresenter).prepareFailView("Registration failed: Could not save user profile.");

        // 4. Success view never called
        verify(mockPresenter, never()).prepareSuccessView(any(RegisterOutputData.class));
    }

    // ==================== EDGE CASES ====================

    @Test
    @DisplayName("Edge Case: Empty email string")
    void testEmptyEmail() {
        // Arrange
        RegisterInputData inputData = new RegisterInputData(
                "",  // Empty email
                "password123",
                "password123",
                "TestUser"
        );

        when(mockAuthGateway.registerWithEmailAndPassword("", "password123"))
                .thenThrow(new AuthFailureException("Invalid email format"));

        // Act
        interactor.execute(inputData);

        // Assert
        verify(mockPresenter).prepareFailView("Registration failed: Invalid email format");
    }

    @Test
    @DisplayName("Failure: Empty nickname should fail validation before saving")
    void testEmptyNickname() {
        // Arrange
        RegisterInputData inputData = new RegisterInputData(
                "test@example.com",
                "password123",
                "password123",
                ""  // Empty nickname
        );

        // Act
        interactor.execute(inputData);

        // Assert: presenter should show failure message
        verify(mockPresenter).prepareFailView("Nickname cannot be empty.");

        // Auth should never be called
        verify(mockAuthGateway, never()).registerWithEmailAndPassword(anyString(), anyString());

        // Repository should never be called
        verify(mockUserRepository, never()).save(any(User.class));

        // Success view should never be called
        verify(mockPresenter, never()).prepareSuccessView(any());
    }


    @Test
    @DisplayName("Edge Case: Special characters in nickname")
    void testSpecialCharactersInNickname() {
        // Arrange
        RegisterInputData inputData = new RegisterInputData(
                "test@example.com",
                "password123",
                "password123",
                "Test@User#123"  // Special characters
        );

        String expectedUid = "firebase-uid-123";
        when(mockAuthGateway.registerWithEmailAndPassword("test@example.com", "password123"))
                .thenReturn(expectedUid);

        // Act
        interactor.execute(inputData);

        // Assert - Should succeed, nickname can have special chars
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(mockUserRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals("Test@User#123", savedUser.getNickname());
    }

    @Test
    @DisplayName("Integration: Verify complete data flow")
    void testCompleteDataFlowVerification() {
        // Arrange
        RegisterInputData inputData = new RegisterInputData(
                "complete@example.com",
                "securePassword123",
                "securePassword123",
                "CompleteUser"
        );

        String firebaseUid = "firebase-generated-uid-abc123";
        when(mockAuthGateway.registerWithEmailAndPassword(
                "complete@example.com",
                "securePassword123"
        )).thenReturn(firebaseUid);

        // Act
        interactor.execute(inputData);

        // Assert - Verify complete execution order
        var inOrder = inOrder(mockAuthGateway, mockUserRepository, mockPresenter);

        // 1. First, register with auth
        inOrder.verify(mockAuthGateway).registerWithEmailAndPassword(
                "complete@example.com",
                "securePassword123"
        );

        // 2. Then, save to repository
        inOrder.verify(mockUserRepository).save(any(User.class));

        // 3. Finally, call presenter
        inOrder.verify(mockPresenter).prepareSuccessView(any(RegisterOutputData.class));
    }
}