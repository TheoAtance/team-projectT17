package entity;

/**
 * A entity representing a review on a restaurant. Review has an authorId, restaurantId, content, and number of up votes
 */
public class Review {
    private String authorId;
    private String restaurantId;
    private String content;
    private int upVotes;


    /**
     * Constructor for a review object given upVotes, and non-empty authorId, restaurantId and content.
     * @param authorId id of the user who made the comment
     * @param restaurantId id of the restaurant this review is associated to
     * @param content id of the content of this review
     * @param upVotes the number of upvotes this review has received
     */
    public Review(String authorId, String restaurantId, String content, int upVotes){
        this.authorId = checkNonEmpty(authorId, "authorId");
        this.restaurantId = checkNonEmpty(restaurantId, "restaurantId");
        this.content = checkNonEmpty(content, "review content");
        this.upVotes = upVotes;
    }

    /**
     *
     * @param authorId id of the user who made the comment
     * @param restaurantId id of the restaurant this review is associated to
     * @param content id of the content of this review
     */
    public Review(String authorId, String restaurantId, String content){
        this.authorId = checkNonEmpty(authorId, "authorId");
        this.restaurantId = checkNonEmpty(restaurantId, "restaurantId");
        this.content = checkNonEmpty(content, "review content");
        this.upVotes = 0;
    }

    //============ Getters ==============
    public String getAuthorId(){
        return authorId;
    }

    public String getContent() {
        return content;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public int getUpVotes() {
        return upVotes;
    }

    //============ Setters ==============
    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setUpVotes(int upVotes) {
        this.upVotes = upVotes;
    }

    //============ Helpers ==============

    public String checkNonEmpty(String content, String varName){
        if(content == null || content.isBlank()){
            throw new IllegalArgumentException(varName + " cannot be null or empty.");
        }

        return content;
    }


}
