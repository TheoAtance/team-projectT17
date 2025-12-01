package entity;

/**
 * A entity representing a review on a restaurant. Review has an authorId, restaurantId, content,
 * and number of up votes
 */
public class Review {

  private String reviewId;
  private String authorId;
  private String restaurantId;
  private String content;
  private String creationDate;
  private int likes;


  /**
   * Constructor for a review object given upVotes, and non-empty authorId, restaurantId and
   * content.
   *
   * @param authorId     id of the user who made the comment
   * @param restaurantId id of the restaurant this review is associated to
   * @param content      id of the content of this review
   * @param upVotes      the number of upvotes this review has received
   */
  public Review(String reviewId, String authorId, String restaurantId, String content,
      String creationDate, int upVotes) {
    this.reviewId = checkNonEmpty(reviewId, "reviewId");
    this.authorId = checkNonEmpty(authorId, "authorId");
    this.restaurantId = checkNonEmpty(restaurantId, "restaurantId");
    this.content = checkNonEmpty(content, "review content");
    this.creationDate = checkNonEmpty(creationDate, "creationDate");
    this.likes = upVotes;
  }

  /**
   *
   * @param authorId     id of the user who made the comment
   * @param restaurantId id of the restaurant this review is associated to
   * @param content      id of the content of this review
   */
  public Review(String reviewId, String authorId, String restaurantId, String creationDate,
      String content) {
    this.reviewId = checkNonEmpty(reviewId, "reviewId");
    this.authorId = checkNonEmpty(authorId, "authorId");
    this.restaurantId = checkNonEmpty(restaurantId, "restaurantId");
    this.content = checkNonEmpty(content, "review content");
    this.creationDate = checkNonEmpty(creationDate, "creationDate");
    this.likes = 0;
  }

  //============ Getters ==============
  public String getReviewId() {
    return reviewId;
  }

  //============ Setters ==============
  public void setReviewId(String reviewId) {
    this.reviewId = reviewId;
  }

  public String getAuthorId() {
    return authorId;
  }

  public void setAuthorId(String authorId) {
    this.authorId = authorId;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(String creationDate) {
    this.creationDate = creationDate;
  }

  public String getRestaurantId() {
    return restaurantId;
  }

  public void setRestaurantId(String restaurantId) {
    this.restaurantId = restaurantId;
  }

  public int getLikes() {
    return likes;
  }

  public void setLikes(int likes) {
    this.likes = likes;
  }

  //============ Helpers ==============

  public String checkNonEmpty(String content, String varName) {
    if (content == null || content.isBlank()) {
      throw new IllegalArgumentException(varName + " cannot be null or empty.");
    }

    return content;
  }


}
