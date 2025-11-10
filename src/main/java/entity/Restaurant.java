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

    /**
     * Create a new restaurant with given non-empty id, name, address, and valid rating.
     * @param id reference id of restaurant
     * @param name name of restaurant
     * @param address address of restaurant
     * @param rating rating of restaurant
     * @param type type of cuisine served by restaurant
     */
    public Restaurant(String id, String name, String address, String type, double rating){
        this.id = checkNonEmpty(id, "id");
        this.name = checkNonEmpty(id, "name");
        this.address = checkNonEmpty(address, "address");
        this.rating = rating;
        this.type = type;
    }

    /**
     * Create a new restaurant that is not current in the system with given non-empty id, name, address and no rating.
     * @param id reference id of restaurant
     * @param name name of restaurant
     * @param address address of restaurant
     * @param type type of cuisine served by restaurant
     */
    public Restaurant(String id, String name, String address, String type){
        this.id = checkNonEmpty(id, "id");
        this.name = checkNonEmpty(name, "name");
        this.address = checkNonEmpty(address, "address");
        this.type = type;
        this.rating = 0;
    }

    //================= Getters ==================

    public String getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public String getAddress(){
        return address;
    }

    public double getRating(){
        return rating;
    }

    public String getType(){
        return type;
    }

    //================= Setters ==================
    public void setName(String name){
        this.name = checkNonEmpty(name,"name");
    }

    public void setAddress(String address){
        this.address = checkNonEmpty(address, "address");
    }

    public void setRating(double rating){
        this.rating = rating;
    }

    public void setType(String type){
        this.type = checkNonEmpty(type, "type");
    }

    //=============== Helpers =====================

    public String checkNonEmpty(String content, String varName){
        if(content == null || content.isBlank()){
            throw new IllegalArgumentException(varName + " cannot be null or empty.");
        }

        return content;
    }



    public String checkValidType(String content, String varName){
        return ""; // to be implemented when a set of types is determined
    }
}
