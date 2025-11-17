import data_access.JsonRestaurantDataAccessObject;
import entity.RestaurantFactory;
import org.junit.Test;

public class JsonRestaurantDAOTest {
    @Test
    public void testPrintNames() throws Exception {
        JsonRestaurantDataAccessObject dao =
                new JsonRestaurantDataAccessObject(
                        "src/main/java/data/restaurant.json",
                        new RestaurantFactory()
                );

        dao.printAllNames();
    }
}
