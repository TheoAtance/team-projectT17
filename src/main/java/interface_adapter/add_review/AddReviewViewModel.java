package interface_adapter.add_review;

import interface_adapter.ViewModel;

public class AddReviewViewModel extends ViewModel<AddReviewState> {

    public AddReviewViewModel(){
        super("add review");
        setState(new AddReviewState());

    }
}
