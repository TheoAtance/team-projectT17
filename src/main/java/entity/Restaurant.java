package entity;

import java.util.List;

/**
 * An entity representing a restaurant. Restaurant has a reference id, name, address, rating, and
 * type.
 */
public class Restaurant {

  private final String id;
  private final String name;
  private final Location location;
  private final String type;
  private final Rating rating;
  private final Contact contact;
  private final List<String> openingHours;
  private final StudentDiscount studentDiscount;
  private final List<String> photoIds;

  // ============= Builder =============

  // Private constructor that Builder uses
  private Restaurant(Builder builder) {
    this.id = builder.id;
    this.name = builder.name;
    this.location = builder.location;
    this.type = builder.type;
    this.rating = builder.rating;
    this.contact = builder.contact;
    this.photoIds = builder.photoIds;
    this.openingHours = builder.openingHours;
    this.studentDiscount = builder.studentDiscount;
  }

  public String getId() {
    return id;
  }

  //================= Getters ==================

  public String getName() {
    return name;
  }

  // --------- Location ---------
  public String getAddress() {
    return location.getAddress();
  }

  public double getLatitude() {
    return location.getLatitude();
  }

  public double getLongitude() {
    return location.getLongitude();
  }

  public String getMapUri() {
    return location.getMapUri();
  }

  // --------- Type ---------
  public String getType() {
    return type;
  }

  // --------- Rating ---------
  public double getRating() {
    return rating.getRating();
  }

  public void setRating(double rating) {
    this.rating.setRating(rating);
  }

  public int getRatingCount() {
    return rating.getCount();
  }

  // --------- Contact ---------
  public String getPhoneNumber() {
    return contact.getPhoneNumber();
  }

  public String getWebsiteUri() {
    return contact.getWebsiteUri();
  }

  // --------- Photos ---------
  public List<String> getPhotoIds() {
    return photoIds;
  }

  // --------- Opening hours ---------
  public List<String> getHours() {
    return openingHours;
  }

  // --------- Discount ---------
  public double getDiscountValue() {
    return studentDiscount.value;
  }

  public boolean hasStudentDiscount() {
    return studentDiscount.hasDiscount;
  }

  //================= Setters ==================

  public void setStudentDiscount(boolean hasDiscount, double value) {
    studentDiscount.setHasDiscount(hasDiscount);
    studentDiscount.setValue(value);
  }

  public static class Builder {

    private String id;
    private String name;
    private Location location;
    private String type;
    private Rating rating;
    private Contact contact;
    private List<String> openingHours;
    private StudentDiscount studentDiscount;
    private List<String> photoIds;

    public Builder id(String id) {
      this.id = id;
      return this;
    }

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public Builder location(String address, String mapUri, double latitude, double longitude) {
      this.location = new Location(address, mapUri, latitude, longitude);
      return this;
    }

    public Builder type(String type) {
      this.type = type;
      return this;
    }

    public Builder rating(double rating, int count) {
      this.rating = new Rating(rating, count);
      return this;
    }

    public Builder contact(String phoneNumber, String websiteUri) {
      this.contact = new Contact(phoneNumber, websiteUri);
      return this;
    }

    public Builder openingHours(List<String> openingHours) {
      this.openingHours = openingHours;
      return this;
    }

    public Builder studentDiscount(boolean hasDiscount, double value) {
      this.studentDiscount = new StudentDiscount(hasDiscount, value);
      return this;
    }

    public Builder photoIds(List<String> photoIds) {
      this.photoIds = photoIds;
      return this;
    }

    public Restaurant build() {
      return new Restaurant(this);
    }
  }

  //=============== Helpers (not needed for now)=====================

//    private String checkNonEmpty(String content, String varName) {
//        if (content == null || content.isBlank()) {
//            throw new IllegalArgumentException(varName + " cannot be null or empty.");
//        }
//        return content;
//    }
//
//    private double checkValidRating(double content, String varName) {
//        if (content > 5 || content < 0) {
//            throw new IllegalArgumentException(varName + " is not within the range 0 - 5");
//        }
//        return content;
//    }
//
//    private String checkValidType(String content, String varName) {
//        // To be implemented when a set of valid types is determined
//        return content;
//    }

  //================= Inner classes ==================

  private static class Location {

    private String address;
    private String mapUri;
    private double latitude;
    private double longitude;

    public Location(String address, String mapUri, double latitude, double longitude) {
      this.address = address;
      this.mapUri = mapUri;
      this.latitude = latitude;
      this.longitude = longitude;
    }

    public String getAddress() {
      return address;
    }

    public void setAddress(String address) {
      this.address = address;
    }

    public String getMapUri() {
      return mapUri;
    }

    public void setMapUri(String mapUri) {
      this.mapUri = mapUri;
    }

    public double getLatitude() {
      return latitude;
    }

    public void setLatitude(double latitude) {
      this.latitude = latitude;
    }

    public double getLongitude() {
      return longitude;
    }

    public void setLongitude(double longitude) {
      this.longitude = longitude;
    }
  }

  private static class Rating {

    private double rating;
    private int count;

    public Rating(double rating, int count) {
      this.rating = rating;
      this.count = count;
    }

    public double getRating() {
      return rating;
    }

    public void setRating(double rating) {
      this.rating = rating;
    }

    public int getCount() {
      return count;
    }

    public void setCount(int count) {
      this.count = count;
    }
  }

  private static class Contact {

    private String phoneNumber;
    private String websiteUri;

    public Contact(String phoneNumber, String websiteUri) {
      this.phoneNumber = phoneNumber;
      this.websiteUri = websiteUri;
    }

    public String getPhoneNumber() {
      return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
      this.phoneNumber = phoneNumber;
    }

    public String getWebsiteUri() {
      return websiteUri;
    }

    public void setWebsiteUri(String websiteUri) {
      this.websiteUri = websiteUri;
    }
  }

  private static class PriceRange {

    private int lower;
    private int upper;

    public PriceRange(int lower, int upper) {
      this.lower = lower;
      this.upper = upper;
    }

    public int getLower() {
      return lower;
    }

    public void setLower(int lower) {
      this.lower = lower;
    }

    public int getUpper() {
      return upper;
    }

    public void setUpper(int upper) {
      this.upper = upper;
    }
  }

  private static class StudentDiscount {

    private boolean hasDiscount;
    private double value;

    public StudentDiscount(boolean hasDiscount, double value) {
      this.hasDiscount = hasDiscount;
      this.value = value;
    }

    public boolean hasDiscount() {
      return hasDiscount;
    }

    public double getValue() {
      return value;
    }

    public void setValue(double value) {
      this.value = value;
    }

    public void setHasDiscount(boolean hasDiscount) {
      this.hasDiscount = hasDiscount;
    }
  }
}