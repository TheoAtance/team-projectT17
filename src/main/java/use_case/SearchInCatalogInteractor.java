package use_case;

import entity.QuerySpec;
import entity.Restaurant;
import service.RestaurantCatalog;
import util.TextMatch;

import java.util.ArrayList;
import java.util.List;


public class SearchInCatalogInteractor {

    private final RestaurantCatalog catalog;

    public SearchInCatalogInteractor(RestaurantCatalog catalog) {
        this.catalog = catalog;
    }

    public List<Restaurant> handle(QuerySpec q) {
        List<Restaurant> all = catalog.getAll();
        List<Restaurant> result = new ArrayList<>();

        for (Restaurant r : all) {
            if (TextMatch.passesHardFilters(q, r)) {
                result.add(r);
            }
        }
        return result;
    }
}
