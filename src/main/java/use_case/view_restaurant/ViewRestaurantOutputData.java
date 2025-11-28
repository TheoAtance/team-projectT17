package use_case.view_restaurant;
import java.util.List;
import java.awt.image.BufferedImage;
/**
 * Output data for the view restaurant use case
 */
public class ViewRestaurantOutputData {
    private final String name;
    private final String id;
    private final String address;
    private final String type;
    private final double rating;
    private final int ratingCount;
    private final String phoneNumber;
    private final List<String> openingHours;
    private final List<String> photoIds;

    public static class Builder{
        private String name;
        private String id;
        private String address;
        private String type;
        private double rating;
        private int ratingCount;
        private String phoneNumber;
        private List<String> openingHours;
        private List<String> photoIds;

        public Builder name(String name){
            this.name = name;
            return this;
        }

        public Builder id(String id){
            this.id = id;
            return this;
        }

        public Builder address(String address){
            this.address = address;
            return this;
        }

        public Builder type(String type){
            this.type = type;
            return this;
        }

        public Builder rating(double rating){
            this.rating = rating;
            return this;
        }

        public Builder ratingCount(int ratingCount){
            this.ratingCount = ratingCount;
            return this;
        }

        public Builder phoneNumber(String phoneNumber){
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder openingHours(List<String> openingHours){
            this.openingHours = openingHours;
            return this;
        }

        public Builder photoIds(List<String> photoIds){
            this.photoIds = photoIds;
            return this;
        }

        public ViewRestaurantOutputData build(){
            return new ViewRestaurantOutputData(this);
        }
    }

    public ViewRestaurantOutputData(Builder builder) {
        this.name = builder.name;
        this.id = builder.id;
        this.address = builder.address;
        this.type = builder.type;
        this.rating = builder.rating;
        this.ratingCount = builder.ratingCount;
        this.phoneNumber = builder.phoneNumber;
        this.openingHours = builder.openingHours;
        this.photoIds = builder.photoIds;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public String getType() {
        return type;
    }

    public double getRating() {
        return rating;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public List<String> getOpeningHours() {
        return openingHours;
    }

    public List<String> getPhotoIds() {
        return photoIds;
    }
}
