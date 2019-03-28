package celesteortiz.com.texting.addModule.view;

/*
 * Project: Texting
 * Created by Celeste Ortiz on 21/03/2019
 *
 * AddView interface
 *
 **/
public interface AddView {
    void enableUIElements();
    void disableUIElements();
    void showProgress();
    void hideProgress();

    void friendAdded();
    void friendshipExists();
    void friendNotAdded();

}
