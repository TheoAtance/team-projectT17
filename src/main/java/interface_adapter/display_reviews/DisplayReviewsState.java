package interface_adapter.display_reviews;

public class DisplayReviewsState {
    private String authorDisplayName ;
    private String content = "";
    private String creationDate = "";
    private String errorMessage;

    public DisplayReviewsState() {
    }

    public String getAuthorDisplayName() {
        return authorDisplayName;
    }

    public void setAuthorDisplayName(String authorDisplayName) {
        this.authorDisplayName = authorDisplayName;
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


}
