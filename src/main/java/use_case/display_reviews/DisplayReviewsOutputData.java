package use_case.display_reviews;

public class DisplayReviewsOutputData {

  String authorDisplayName;
  String content;
  String creationDate;

  public DisplayReviewsOutputData(String authorDisplayName, String content, String creationDate) {
    this.authorDisplayName = authorDisplayName;
    this.content = content;
    this.creationDate = creationDate;
  }

  public String getAuthorDisplayName() {
    return authorDisplayName;
  }

  public String getContent() {
    return content;
  }

  public String getCreationDate() {
    return creationDate;
  }


}
