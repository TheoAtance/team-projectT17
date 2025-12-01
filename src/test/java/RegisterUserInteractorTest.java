import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import use_case.AuthFailureException;
import use_case.IAuthGateway;
import use_case.IUserRepo;
import use_case.PersistenceException;
import use_case.custom_register.RegisterInputData;
import use_case.custom_register.RegisterOutputBoundary;
import use_case.custom_register.RegisterOutputData;
import use_case.custom_register.RegisterUserInteractor;

/**
 * Unit tests for RegisterUserInteractor using Mockito, which auto-generates fake objects at runtime
 * to test only the interactor's logic. Tests all success and failure paths for user registration.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("RegisterUserInteractor Tests")
class RegisterUserInteractorTest {

  private static final String TEST_UID = "test-uid-123";
  private static final String TEST_EMAIL = "test@example.com";
  private static final String TEST_PASSWORD = "password123";
  private static final String TEST_NICKNAME = "TestUser";
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
        TEST_EMAIL,
        TEST_PASSWORD,
        TEST_PASSWORD,
        TEST_NICKNAME
    );

    User expectedUser = new User(TEST_UID, TEST_EMAIL, TEST_NICKNAME);

    // Mock the auth gateway to return a UID
    when(mockAuthGateway.registerWithEmailAndPassword(TEST_EMAIL, TEST_PASSWORD))
        .thenReturn(TEST_UID);

    // Mock getCurrentUserUid for CurrentUser instance
    when(mockAuthGateway.getCurrentUserUid())
        .thenReturn(TEST_UID);

    // Mock getUserByUid for CurrentUser.getCurrentUser() call
    when(mockUserRepository.getUserByUid(TEST_UID))
        .thenReturn(expectedUser);

    // Act
    interactor.execute(inputData);

    // Assert
    // 1. Verify auth gateway was called with correct credentials
    verify(mockAuthGateway).registerWithEmailAndPassword(TEST_EMAIL, TEST_PASSWORD);

    // 2. Verify user was saved to repository
    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    verify(mockUserRepository).save(userCaptor.capture());

    User savedUser = userCaptor.getValue();
    assertEquals(TEST_UID, savedUser.getUid());
    assertEquals(TEST_EMAIL, savedUser.getEmail());
    assertEquals(TEST_NICKNAME, savedUser.getNickname());

    // 3. Verify loadAllUsers was called after successful registration
    verify(mockUserRepository).loadAllUsers();

    // 4. Verify getCurrentUserUid was called by CurrentUser instance
    verify(mockAuthGateway, atLeastOnce()).getCurrentUserUid();

    // 5. Verify getUserByUid was called by CurrentUser instance
    verify(mockUserRepository, atLeastOnce()).getUserByUid(TEST_UID);

    // 6. Verify success view was prepared
    ArgumentCaptor<RegisterOutputData> outputCaptor = ArgumentCaptor.forClass(
        RegisterOutputData.class);
    verify(mockPresenter).prepareSuccessView(outputCaptor.capture());

    RegisterOutputData output = outputCaptor.getValue();
    assertEquals(TEST_NICKNAME, output.getNickname());
    assertTrue(output.isSuccess());
    assertEquals(TEST_UID, output.getUid());

    // 7. Verify no failure view was called
    verify(mockPresenter, never()).prepareFailView(anyString());
  }

  // ==================== VALIDATION FAILURES ====================

  @Test
  @DisplayName("Failure: Passwords don't match - validation fails before API call")
  void testPasswordMismatch() {
    // Arrange
    RegisterInputData inputData = new RegisterInputData(
        TEST_EMAIL,
        TEST_PASSWORD,
        "differentPassword",  // Doesn't match!
        TEST_NICKNAME
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
    verify(mockUserRepository, never()).loadAllUsers();
    verify(mockUserRepository, never()).getUserByUid(anyString());

    // 4. Verify success view was never called
    verify(mockPresenter, never()).prepareSuccessView(any(RegisterOutputData.class));
  }

  @Test
  @DisplayName("Failure: Empty nickname should fail validation")
  void testEmptyNickname() {
    // Arrange
    RegisterInputData inputData = new RegisterInputData(
        TEST_EMAIL,
        TEST_PASSWORD,
        TEST_PASSWORD,
        ""  // Empty nickname
    );

    // Act
    interactor.execute(inputData);

    // Assert
    verify(mockPresenter).prepareFailView("Nickname cannot be empty.");
    verify(mockAuthGateway, never()).registerWithEmailAndPassword(anyString(), anyString());
    verify(mockUserRepository, never()).save(any(User.class));
    verify(mockUserRepository, never()).loadAllUsers();
    verify(mockPresenter, never()).prepareSuccessView(any());
  }

  @Test
  @DisplayName("Failure: Null nickname should fail validation")
  void testNullNickname() {
    // Arrange
    RegisterInputData inputData = new RegisterInputData(
        TEST_EMAIL,
        TEST_PASSWORD,
        TEST_PASSWORD,
        null  // Null nickname
    );

    // Act
    interactor.execute(inputData);

    // Assert
    verify(mockPresenter).prepareFailView("Nickname cannot be empty.");
    verify(mockAuthGateway, never()).registerWithEmailAndPassword(anyString(), anyString());
    verify(mockUserRepository, never()).loadAllUsers();
  }

  @Test
  @DisplayName("Failure: Whitespace-only nickname should fail validation")
  void testWhitespaceOnlyNickname() {
    // Arrange
    RegisterInputData inputData = new RegisterInputData(
        TEST_EMAIL,
        TEST_PASSWORD,
        TEST_PASSWORD,
        "   "  // Only whitespace
    );

    // Act
    interactor.execute(inputData);

    // Assert
    verify(mockPresenter).prepareFailView("Nickname cannot be empty.");
    verify(mockAuthGateway, never()).registerWithEmailAndPassword(anyString(), anyString());
    verify(mockUserRepository, never()).loadAllUsers();
  }

  // ==================== AUTHENTICATION FAILURES ====================

  @Test
  @DisplayName("Failure: Email already exists in Firebase Auth")
  void testEmailAlreadyExists() {
    // Arrange
    RegisterInputData inputData = new RegisterInputData(
        "existing@example.com",
        TEST_PASSWORD,
        TEST_PASSWORD,
        TEST_NICKNAME
    );

    when(mockAuthGateway.registerWithEmailAndPassword("existing@example.com", TEST_PASSWORD))
        .thenThrow(new AuthFailureException("Email already in use."));

    // Act
    interactor.execute(inputData);

    // Assert
    // 1. Verify auth gateway was called
    verify(mockAuthGateway).registerWithEmailAndPassword("existing@example.com", TEST_PASSWORD);

    // 2. Verify failure view was called with error message
    verify(mockPresenter).prepareFailView("Registration failed: Email already in use.");

    // 3. Verify repository was never called (auth failed)
    verify(mockUserRepository, never()).save(any(User.class));
    verify(mockUserRepository, never()).loadAllUsers();

    // 4. Verify success view was never called
    verify(mockPresenter, never()).prepareSuccessView(any(RegisterOutputData.class));
  }

  @Test
  @DisplayName("Failure: Weak password rejected by Firebase")
  void testWeakPassword() {
    // Arrange
    RegisterInputData inputData = new RegisterInputData(
        TEST_EMAIL,
        "123",  // Too short
        "123",
        TEST_NICKNAME
    );

    when(mockAuthGateway.registerWithEmailAndPassword(TEST_EMAIL, "123"))
        .thenThrow(new AuthFailureException("Password must be at least 6 characters."));

    // Act
    interactor.execute(inputData);

    // Assert
    verify(mockPresenter).prepareFailView(
        "Registration failed: Password must be at least 6 characters.");
    verify(mockUserRepository, never()).save(any(User.class));
    verify(mockUserRepository, never()).loadAllUsers();
  }

  @Test
  @DisplayName("Failure: Network error during authentication")
  void testNetworkErrorDuringAuth() {
    // Arrange
    RegisterInputData inputData = new RegisterInputData(
        TEST_EMAIL,
        TEST_PASSWORD,
        TEST_PASSWORD,
        TEST_NICKNAME
    );

    when(mockAuthGateway.registerWithEmailAndPassword(TEST_EMAIL, TEST_PASSWORD))
        .thenThrow(new RuntimeException("Network timeout"));

    // Act
    interactor.execute(inputData);

    // Assert
    verify(mockPresenter).prepareFailView("Registration failed: Network timeout");
    verify(mockUserRepository, never()).save(any(User.class));
    verify(mockUserRepository, never()).loadAllUsers();
  }

  // ==================== PERSISTENCE FAILURES ====================

  @Test
  @DisplayName("Failure: User profile fails to save to Firestore")
  void testProfileSaveFailure() {
    // Arrange
    RegisterInputData inputData = new RegisterInputData(
        TEST_EMAIL,
        TEST_PASSWORD,
        TEST_PASSWORD,
        TEST_NICKNAME
    );

    when(mockAuthGateway.registerWithEmailAndPassword(TEST_EMAIL, TEST_PASSWORD))
        .thenReturn(TEST_UID);

    // Simulate Firestore failure
    doThrow(new PersistenceException("Could not connect to database"))
        .when(mockUserRepository).save(any(User.class));

    // Act
    interactor.execute(inputData);

    // Assert
    // 1. Auth succeeded
    verify(mockAuthGateway).registerWithEmailAndPassword(TEST_EMAIL, TEST_PASSWORD);

    // 2. Attempted to save user
    verify(mockUserRepository).save(any(User.class));

    // 3. Failure view called with appropriate message
    verify(mockPresenter).prepareFailView("Registration failed: Could not save user profile.");

    // 4. Success view never called
    verify(mockPresenter, never()).prepareSuccessView(any(RegisterOutputData.class));

    // 5. loadAllUsers should not be called if save fails
    verify(mockUserRepository, never()).loadAllUsers();
  }

  @Test
  @DisplayName("Failure: loadAllUsers throws exception after successful save")
  void testLoadAllUsersFailure() {
    // Arrange
    RegisterInputData inputData = new RegisterInputData(
        TEST_EMAIL,
        TEST_PASSWORD,
        TEST_PASSWORD,
        TEST_NICKNAME
    );

    when(mockAuthGateway.registerWithEmailAndPassword(TEST_EMAIL, TEST_PASSWORD))
        .thenReturn(TEST_UID);

    // loadAllUsers throws exception
    doThrow(new PersistenceException("Failed to load users"))
        .when(mockUserRepository).loadAllUsers();

    // Act
    interactor.execute(inputData);

    // Assert
    // 1. Auth succeeded
    verify(mockAuthGateway).registerWithEmailAndPassword(TEST_EMAIL, TEST_PASSWORD);

    // 2. User was saved successfully
    verify(mockUserRepository).save(any(User.class));

    // 3. loadAllUsers was attempted
    verify(mockUserRepository).loadAllUsers();

    // 4. Failure view called because of loadAllUsers exception
    verify(mockPresenter).prepareFailView(contains("Failed to load users"));

    // 5. Success view never called
    verify(mockPresenter, never()).prepareSuccessView(any(RegisterOutputData.class));
  }

  @Test
  @DisplayName("Failure: CurrentUser.getCurrentUser() returns null")
  void testCurrentUserReturnsNull() {
    // Arrange
    RegisterInputData inputData = new RegisterInputData(
        TEST_EMAIL,
        TEST_PASSWORD,
        TEST_PASSWORD,
        TEST_NICKNAME
    );

    when(mockAuthGateway.registerWithEmailAndPassword(TEST_EMAIL, TEST_PASSWORD))
        .thenReturn(TEST_UID);
    when(mockAuthGateway.getCurrentUserUid())
        .thenReturn(TEST_UID);
    when(mockUserRepository.getUserByUid(TEST_UID))
        .thenReturn(null);  // CurrentUser will return null

    // Act
    interactor.execute(inputData);

    // Assert
    // 1. Auth succeeded
    verify(mockAuthGateway).registerWithEmailAndPassword(TEST_EMAIL, TEST_PASSWORD);

    // 2. User was saved
    verify(mockUserRepository).save(any(User.class));

    // 3. loadAllUsers was called
    verify(mockUserRepository).loadAllUsers();

    // 4. Failure view called because getCurrentUser() returned null
    verify(mockPresenter).prepareFailView(contains("Cannot invoke \"entity.User.getNickname()\""));

    // 5. Success view never called
    verify(mockPresenter, never()).prepareSuccessView(any(RegisterOutputData.class));
  }

  // ==================== EDGE CASES ====================

  @Test
  @DisplayName("Edge Case: Empty email string")
  void testEmptyEmail() {
    // Arrange
    RegisterInputData inputData = new RegisterInputData(
        "",  // Empty email
        TEST_PASSWORD,
        TEST_PASSWORD,
        TEST_NICKNAME
    );

    when(mockAuthGateway.registerWithEmailAndPassword("", TEST_PASSWORD))
        .thenThrow(new AuthFailureException("Invalid email format"));

    // Act
    interactor.execute(inputData);

    // Assert
    verify(mockPresenter).prepareFailView("Registration failed: Invalid email format");
    verify(mockUserRepository, never()).loadAllUsers();
  }

  @Test
  @DisplayName("Edge Case: Special characters in nickname")
  void testSpecialCharactersInNickname() {
    // Arrange
    RegisterInputData inputData = new RegisterInputData(
        TEST_EMAIL,
        TEST_PASSWORD,
        TEST_PASSWORD,
        "Test@User#123"  // Special characters
    );

    String specialUid = "special-uid-456";
    User specialUser = new User(specialUid, TEST_EMAIL, "Test@User#123");

    when(mockAuthGateway.registerWithEmailAndPassword(TEST_EMAIL, TEST_PASSWORD))
        .thenReturn(specialUid);
    when(mockAuthGateway.getCurrentUserUid())
        .thenReturn(specialUid);
    when(mockUserRepository.getUserByUid(specialUid))
        .thenReturn(specialUser);

    // Act
    interactor.execute(inputData);

    // Assert - Should succeed, nickname can have special chars
    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    verify(mockUserRepository).save(userCaptor.capture());

    User savedUser = userCaptor.getValue();
    assertEquals("Test@User#123", savedUser.getNickname());

    // Verify loadAllUsers was called
    verify(mockUserRepository).loadAllUsers();

    // Verify success view was called
    verify(mockPresenter).prepareSuccessView(any(RegisterOutputData.class));
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
    User completeUser = new User(firebaseUid, "complete@example.com", "CompleteUser");

    when(mockAuthGateway.registerWithEmailAndPassword(
        "complete@example.com",
        "securePassword123"
    )).thenReturn(firebaseUid);
    when(mockAuthGateway.getCurrentUserUid())
        .thenReturn(firebaseUid);
    when(mockUserRepository.getUserByUid(firebaseUid))
        .thenReturn(completeUser);

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

    // 3. Load all users into cache
    inOrder.verify(mockUserRepository).loadAllUsers();

    // 4. CurrentUser checks getCurrentUserUid and getUserByUid
    inOrder.verify(mockAuthGateway).getCurrentUserUid();
    inOrder.verify(mockUserRepository).getUserByUid(firebaseUid);

    // 5. Finally, call presenter
    inOrder.verify(mockPresenter).prepareSuccessView(any(RegisterOutputData.class));
  }

  @Test
  @DisplayName("Verify CurrentUser can retrieve user after successful registration")
  void testCurrentUserFunctionalityAfterRegistration() {
    // Arrange
    RegisterInputData inputData = new RegisterInputData(
        TEST_EMAIL,
        TEST_PASSWORD,
        TEST_PASSWORD,
        TEST_NICKNAME
    );

    User mockUser = new User(TEST_UID, TEST_EMAIL, TEST_NICKNAME);

    when(mockAuthGateway.registerWithEmailAndPassword(TEST_EMAIL, TEST_PASSWORD))
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

    // Verify getUserByUid was called at least once
    // (for the CurrentUser test)
    verify(mockUserRepository, atLeastOnce()).getUserByUid(TEST_UID);

    // Verify loadAllUsers was called
    verify(mockUserRepository).loadAllUsers();

    // Verify success view was called
    verify(mockPresenter).prepareSuccessView(any(RegisterOutputData.class));
  }

  @Test
  @DisplayName("Verify user data is correctly passed to presenter")
  void testUserDataPassedCorrectly() {
    // Arrange
    RegisterInputData inputData = new RegisterInputData(
        TEST_EMAIL,
        TEST_PASSWORD,
        TEST_PASSWORD,
        TEST_NICKNAME
    );

    User mockUser = new User(TEST_UID, TEST_EMAIL, TEST_NICKNAME);

    when(mockAuthGateway.registerWithEmailAndPassword(TEST_EMAIL, TEST_PASSWORD))
        .thenReturn(TEST_UID);
    when(mockAuthGateway.getCurrentUserUid())
        .thenReturn(TEST_UID);
    when(mockUserRepository.getUserByUid(TEST_UID))
        .thenReturn(mockUser);

    // Act
    interactor.execute(inputData);

    // Assert - Verify all user data is passed to output
    ArgumentCaptor<RegisterOutputData> outputCaptor =
        ArgumentCaptor.forClass(RegisterOutputData.class);
    verify(mockPresenter).prepareSuccessView(outputCaptor.capture());

    RegisterOutputData output = outputCaptor.getValue();
    assertNotNull(output);
    assertEquals(TEST_NICKNAME, output.getNickname());
    assertEquals(TEST_UID, output.getUid());
    assertTrue(output.isSuccess());

    // Verify loadAllUsers was called
    verify(mockUserRepository).loadAllUsers();
  }
}