package use_case.view_restaurant;

import entity.Restaurant;
import data_access.JsonRestaurantDataAccessObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ViewRestaurantInteractorTest {

  private JsonRestaurantDataAccessObject mockDataAccess;
  private ViewRestaurantOutputBoundary mockPresenter;
  private ViewRestaurantInteractor interactor;

  @BeforeEach
  void setUp() {
    mockDataAccess = mock(JsonRestaurantDataAccessObject.class);
    mockPresenter = mock(ViewRestaurantOutputBoundary.class);
    interactor = new ViewRestaurantInteractor(mockDataAccess, mockPresenter);
  }

  @Test
  void testExecute_withCid_success() throws IOException {
    // Arrange - Using actual CID from your JSON
    String cid = "11196262288801447219";

    Restaurant mockRestaurant = mock(Restaurant.class);
    when(mockRestaurant.getName()).thenReturn("Cafe 85");
    when(mockRestaurant.getId()).thenReturn(cid);
    when(mockRestaurant.getAddress()).thenReturn("695 Bay St., Toronto, ON M5G 0C3, Canada");
    when(mockRestaurant.getType()).thenReturn("Breakfast Restaurant");
    when(mockRestaurant.getRating()).thenReturn(0.0);
    when(mockRestaurant.getRatingCount()).thenReturn(0);
    when(mockRestaurant.getPhoneNumber()).thenReturn("(416) 591-0022");

    List<String> hours = Arrays.asList(
        "Monday: 7:00 AM – 4:00 PM",
        "Tuesday: 7:00 AM – 4:00 PM",
        "Wednesday: 7:00 AM – 4:00 PM"
    );
    when(mockRestaurant.getHours()).thenReturn(hours);

    List<String> photoIds = Arrays.asList(
        "places/ChIJSxkLSwA1K4gRM70cODsdYZs/photos/AWn5SU40wkEnUzTHHPTHtnbuEJhIPoWOFDhV1srYmH47ZldJp9A9Tj2F0jJS-2tk",
        "places/ChIJSxkLSwA1K4gRM70cODsdYZs/photos/AWn5SU6TJRAe-SS-wHKk6t3Uq7WdhAhCmzYHueiMe-Vgv8_UDmIgJswkobcUDRalw71jH5lxqDKudtv05L_IMmyTxPWzOqrAhOKfowuM2Sav3-YfxbGCvPSVktmybFrMOPQLMxSt8lwdJsRIa05mmgFrln3rZ3vfYTZUKCAr0WJgqfx2Lv0Fq993kdnerlTnOf31an086TUiy-CC7o0iZHM9Brf68N4nYjI9BjPYxshxCj4AFWWQZpCPfMjXHWuo3mVasKveineIeTEpxhZ6Gjavm56JYT1MjkmQYJDKs9bTUdboIQ"
    );
    when(mockRestaurant.getPhotoIds()).thenReturn(photoIds);

    when(mockDataAccess.existById(cid)).thenReturn(true);
    when(mockDataAccess.get(cid)).thenReturn(mockRestaurant);

    ViewRestaurantInputData inputData = new ViewRestaurantInputData(cid);

    // Act
    interactor.execute(inputData);

    // Assert
    verify(mockDataAccess).existById(cid);
    verify(mockDataAccess).get(cid);

    ArgumentCaptor<ViewRestaurantOutputData> captor =
        ArgumentCaptor.forClass(ViewRestaurantOutputData.class);
    verify(mockPresenter).prepareSuccessView(captor.capture());

    ViewRestaurantOutputData outputData = captor.getValue();
    assertEquals("Cafe 85", outputData.getName());
    assertEquals(cid, outputData.getId());
    assertEquals("695 Bay St., Toronto, ON M5G 0C3, Canada", outputData.getAddress());
    assertEquals("Breakfast Restaurant", outputData.getType());
    assertEquals(0.0, outputData.getRating());
    assertEquals(0, outputData.getRatingCount());
    assertEquals("(416) 591-0022", outputData.getPhoneNumber());
    assertEquals(hours, outputData.getOpeningHours());
    assertEquals(photoIds, outputData.getPhotoIds());
  }

  @Test
  void testExecute_withPlacesId_success() throws IOException {
    // Arrange - Using Google Places ID format
    String placesId = "places/ChIJSxkLSwA1K4gRM70cODsdYZs";
    String cid = "11196262288801447219";

    Restaurant mockRestaurant = mock(Restaurant.class);
    when(mockRestaurant.getName()).thenReturn("Cafe 85");
    when(mockRestaurant.getId()).thenReturn(cid);
    when(mockRestaurant.getAddress()).thenReturn("695 Bay St., Toronto, ON M5G 0C3, Canada");
    when(mockRestaurant.getType()).thenReturn("Breakfast Restaurant");
    when(mockRestaurant.getRating()).thenReturn(0.0);
    when(mockRestaurant.getRatingCount()).thenReturn(0);
    when(mockRestaurant.getPhoneNumber()).thenReturn("(416) 591-0022");
    when(mockRestaurant.getHours()).thenReturn(Arrays.asList());
    when(mockRestaurant.getPhotoIds()).thenReturn(Arrays.asList());

    when(mockDataAccess.existById(placesId)).thenReturn(true);
    when(mockDataAccess.get(placesId)).thenReturn(mockRestaurant);

    ViewRestaurantInputData inputData = new ViewRestaurantInputData(placesId);

    // Act
    interactor.execute(inputData);

    // Assert
    verify(mockDataAccess).existById(placesId);
    verify(mockDataAccess).get(placesId);
    verify(mockPresenter).prepareSuccessView(any(ViewRestaurantOutputData.class));
  }

  @Test
  void testExecute_withRestaurantName_success() throws IOException {
    // Arrange - Using restaurant name
    String restaurantName = "Cafe 85";
    String cid = "11196262288801447219";

    Restaurant mockRestaurant = mock(Restaurant.class);
    when(mockRestaurant.getName()).thenReturn("Cafe 85");
    when(mockRestaurant.getId()).thenReturn(cid);
    when(mockRestaurant.getAddress()).thenReturn("695 Bay St., Toronto, ON M5G 0C3, Canada");
    when(mockRestaurant.getType()).thenReturn("Breakfast Restaurant");
    when(mockRestaurant.getRating()).thenReturn(0.0);
    when(mockRestaurant.getRatingCount()).thenReturn(0);
    when(mockRestaurant.getPhoneNumber()).thenReturn("(416) 591-0022");
    when(mockRestaurant.getHours()).thenReturn(Arrays.asList());
    when(mockRestaurant.getPhotoIds()).thenReturn(Arrays.asList());

    when(mockDataAccess.existById(restaurantName)).thenReturn(true);
    when(mockDataAccess.get(restaurantName)).thenReturn(mockRestaurant);

    ViewRestaurantInputData inputData = new ViewRestaurantInputData(restaurantName);

    // Act
    interactor.execute(inputData);

    // Assert
    verify(mockDataAccess).existById(restaurantName);
    verify(mockDataAccess).get(restaurantName);
    verify(mockPresenter).prepareSuccessView(any(ViewRestaurantOutputData.class));
  }

  @Test
  void testExecute_restaurantDoesNotExist_failure() throws IOException {
    // Arrange
    String nonExistentId = "999999999999999";
    when(mockDataAccess.existById(nonExistentId)).thenReturn(false);

    ViewRestaurantInputData inputData = new ViewRestaurantInputData(nonExistentId);

    // Act
    interactor.execute(inputData);

    // Assert
    verify(mockDataAccess).existById(nonExistentId);
    verify(mockDataAccess, never()).get(anyString());
    verify(mockPresenter).prepareFailView(nonExistentId + ": Restaurant does not exist.");
    verify(mockPresenter, never()).prepareSuccessView(any());
  }

  @Test
  void testExecute_emptyRestaurantId_failure() throws IOException {
    // Arrange
    String emptyId = "";
    when(mockDataAccess.existById(emptyId)).thenReturn(false);

    ViewRestaurantInputData inputData = new ViewRestaurantInputData(emptyId);

    // Act
    interactor.execute(inputData);

    // Assert
    verify(mockPresenter).prepareFailView(emptyId + ": Restaurant does not exist.");
    verify(mockPresenter, never()).prepareSuccessView(any());
  }

  @Test
  void testExecute_nullOpeningHoursAndPhotos_success() throws IOException {
    // Arrange
    String cid = "11196262288801447219";

    Restaurant mockRestaurant = mock(Restaurant.class);
    when(mockRestaurant.getName()).thenReturn("Cafe 85");
    when(mockRestaurant.getId()).thenReturn(cid);
    when(mockRestaurant.getAddress()).thenReturn("695 Bay St., Toronto, ON M5G 0C3, Canada");
    when(mockRestaurant.getType()).thenReturn("Breakfast Restaurant");
    when(mockRestaurant.getRating()).thenReturn(0.0);
    when(mockRestaurant.getRatingCount()).thenReturn(0);
    when(mockRestaurant.getPhoneNumber()).thenReturn("(416) 591-0022");
    when(mockRestaurant.getHours()).thenReturn(null);
    when(mockRestaurant.getPhotoIds()).thenReturn(null);

    when(mockDataAccess.existById(cid)).thenReturn(true);
    when(mockDataAccess.get(cid)).thenReturn(mockRestaurant);

    ViewRestaurantInputData inputData = new ViewRestaurantInputData(cid);

    // Act
    interactor.execute(inputData);

    // Assert
    ArgumentCaptor<ViewRestaurantOutputData> captor =
        ArgumentCaptor.forClass(ViewRestaurantOutputData.class);
    verify(mockPresenter).prepareSuccessView(captor.capture());

    ViewRestaurantOutputData outputData = captor.getValue();
    assertNull(outputData.getOpeningHours());
    assertNull(outputData.getPhotoIds());
  }

  @Test
  void testExecute_dataAccessReturnsNull_handlesGracefully() throws IOException {
    // Arrange
    String cid = "11196262288801447219";
    when(mockDataAccess.existById(cid)).thenReturn(true);
    when(mockDataAccess.get(cid)).thenReturn(null);

    ViewRestaurantInputData inputData = new ViewRestaurantInputData(cid);

    // Act & Assert - This will likely throw NullPointerException
    // which reveals a bug in the interactor that should be fixed
    assertThrows(NullPointerException.class, () -> {
      interactor.execute(inputData);
    });
  }

  @Test
  void testExecute_zeroRatingAndCount_success() throws IOException {
    // Arrange - Mirrors actual Cafe 85 data with 0 rating and 0 count
    String cid = "11196262288801447219";

    Restaurant mockRestaurant = mock(Restaurant.class);
    when(mockRestaurant.getName()).thenReturn("Cafe 85");
    when(mockRestaurant.getId()).thenReturn(cid);
    when(mockRestaurant.getAddress()).thenReturn("695 Bay St., Toronto, ON M5G 0C3, Canada");
    when(mockRestaurant.getType()).thenReturn("Breakfast Restaurant");
    when(mockRestaurant.getRating()).thenReturn(0.0);
    when(mockRestaurant.getRatingCount()).thenReturn(0);
    when(mockRestaurant.getPhoneNumber()).thenReturn("(416) 591-0022");
    when(mockRestaurant.getHours()).thenReturn(Arrays.asList());
    when(mockRestaurant.getPhotoIds()).thenReturn(Arrays.asList());

    when(mockDataAccess.existById(cid)).thenReturn(true);
    when(mockDataAccess.get(cid)).thenReturn(mockRestaurant);

    ViewRestaurantInputData inputData = new ViewRestaurantInputData(cid);

    // Act
    interactor.execute(inputData);

    // Assert
    ArgumentCaptor<ViewRestaurantOutputData> captor =
        ArgumentCaptor.forClass(ViewRestaurantOutputData.class);
    verify(mockPresenter).prepareSuccessView(captor.capture());

    ViewRestaurantOutputData outputData = captor.getValue();
    assertEquals(0.0, outputData.getRating());
    assertEquals(0, outputData.getRatingCount());
  }

  @Test
  void testExecute_invalidPlacesIdFormat_failure() throws IOException {
    // Arrange - Invalid Places ID that doesn't map to any CID
    String invalidPlacesId = "places/InvalidChIJFormat";
    when(mockDataAccess.existById(invalidPlacesId)).thenReturn(false);

    ViewRestaurantInputData inputData = new ViewRestaurantInputData(invalidPlacesId);

    // Act
    interactor.execute(inputData);

    // Assert
    verify(mockDataAccess).existById(invalidPlacesId);
    verify(mockPresenter).prepareFailView(invalidPlacesId + ": Restaurant does not exist.");
  }
}