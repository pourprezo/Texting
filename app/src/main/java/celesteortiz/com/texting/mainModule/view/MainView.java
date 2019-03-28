package celesteortiz.com.texting.mainModule.view;

import celesteortiz.com.texting.common.pojo.UserPojo;

/*
 * Project: Texting
 * Created by Celeste Ortiz on 18/03/2019
 *
 * MainView interface
 * */
public interface MainView {
    void friendAdded(UserPojo user);
    void friendUpdated(UserPojo user);
    void friendRemoved(UserPojo user);

    void requestAdded(UserPojo user);
    void requestUpdated(UserPojo user);
    void requestRemoved(UserPojo user);

    void showRequestAccepted(String username);
    void showRequestDenied();

    void showFriendRemoved();
    void showError(int resMsg);


}
