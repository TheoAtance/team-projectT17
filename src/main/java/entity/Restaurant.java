package entity;

/**
 * An entity representing a restaurant. Restaurant has a reference id, name, address, rating, and type.
 */
public class Restaurant {
    private final String id;
    private String name;
    private String address;
    private String type;
    private double rating;
    private StudentDiscount studentDiscount;

    /**
     * Create a new restaurant with given non-empty id, name, address, and valid rating.
     * @param id reference id of restaurant
     * @param name name of restaurant
     * @param address address of restaurant
     * @param type type of cuisine served by restaurant
     * @param rating rating of restaurant
     * @param hasDiscount whether the restaurant has a student discount
     * @param discountValue the discount value if applicable
     */
    public Restaurant(String id, String name, String address, String type, double rating,
                      boolean hasDiscount, double discountValue) {
        this.id = checkNonEmpty(id, "id");
        this.name = checkNonEmpty(name, "name");
        this.address = checkNonEmpty(address, "address");
        this.rating = checkValidRating(rating, "rating");
        this.type = checkNonEmpty(type, "type");
        this.studentDiscount = new StudentDiscount(hasDiscount, discountValue);
    }

    /**
     * Create a new restaurant that is not currently in the system, with given non-empty id, name, and address.
     * @param id reference id of restaurant
     * @param name name of restaurant
     * @param address address of restaurant
     * @param type type of cuisine served by restaurant
     * @param hasDiscount whether the restaurant has a student discount
     * @param discountValue the discount value if applicable
     */
    public Restaurant(String id, String name, String address, String type,
                      boolean hasDiscount, double discountValue) {
        this.id = checkNonEmpty(id, "id");
        this.name = checkNonEmpty(name, "name");
        this.address = checkNonEmpty(address, "address");
        this.type = checkNonEmpty(type, "type");
        this.rating = 0;
        this.studentDiscount = new StudentDiscount(hasDiscount, discountValue);
    }

    //================= Getters ==================

    public String getId() { return id; }

    public String getName() { return name; }

    public String getAddress() { return address; }

    public double getRating() { return rating; }

    public String getType() { return type; }

    public double getDiscountValue() { return studentDiscount.value; }

    public boolean hasStudentDiscount() { return studentDiscount.hasDiscount; }

    //================= Setters ==================

    public void setName(String name) {
        this.name = checkNonEmpty(name,"name");
    }

    public void setAddress(String address) {
        this.address = checkNonEmpty(address, "address");
    }

    public void setRating(double rating) {
        this.rating = checkValidRating(rating, "rating");
    }

    public void setType(String type) {
        this.type = checkNonEmpty(type, "type");
    }

    public void setStudentDiscount(boolean hasDiscount, double value) {
        studentDiscount.setHasDiscount(hasDiscount);
        studentDiscount.setValue(value);
    }

    //=============== Helpers =====================

    private String checkNonEmpty(String content, String varName) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException(varName + " cannot be null or empty.");
        }
        return content;
    }

    private double checkValidRating(double content, String varName) {
        if (content > 5 || content < 0) {
            throw new IllegalArgumentException(varName + " is not within the range 0 - 5");
        }
        return content;
    }

    private String checkValidType(String content, String varName) {
        // To be implemented when a set of valid types is determined
        return content;
    }

    //================= Inner class ==================

    private static class StudentDiscount {
        private boolean hasDiscount;
        private double value;

        public StudentDiscount(boolean hasDiscount, double value) {
            this.hasDiscount = hasDiscount;
            this.value = value;
        }

        public boolean hasDiscount() { return hasDiscount; }
        public double getValue() { return value; }

        public void setHasDiscount(boolean hasDiscount) { this.hasDiscount = hasDiscount; }
        public void setValue(double value) { this.value = value; }
    }
}