import data_access.JsonRestaurantDataAccessObject;
import entity.Restaurant;
import entity.RestaurantFactory;
import org.junit.Test;

public class ViewRestaurantTest {

    @Test
    public void getRestaurantTest() throws Exception {
        JsonRestaurantDataAccessObject dao = new JsonRestaurantDataAccessObject(
                        "src/test/test_data/single_restaurant.json",
                        new RestaurantFactory()
                );

        Restaurant restaurant = dao.get("places/ChIJSxkLSwA1K4gRM70cODsdYZs/photos/AWn5SU66h4SqNfDQqDe5SxLU0APjC6mhVPXEebxLhWVhZ7OC530hI-uGsicGnlOKqgDSd7Zc1VN3xngPByAjS_hn3hTetY3MJaJBKnTKWzXPGUSK8TfVELfD6v7JBwTVgNGzqV7i6O0-x225xuolsQzbXn7DNpOU3tK8agswesES5O3UtDE5zOxj-O4WlduWZ1DIfgfVpdEdaunFbhnlxYyRYSL9p44R3BFkgaHWbU7s7J-1R-1JwHuriZM15LLVTYk3kXdILUs1JVQUEYZAaqedpDBa22kWzvjUiR06v07ZBzEPaA");


    }
}
