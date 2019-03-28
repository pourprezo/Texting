package celesteortiz.com.texting.mainModule;

import celesteortiz.com.texting.common.pojo.UserPojo;
import celesteortiz.com.texting.mainModule.events.MainEvent;

/*
 * Project: Texting
 * Created by Celeste Ortiz on 18/03/2019
 *
 * MainPresenter interface
 * */
public interface MainPresenter {
    void onCreate();
    void onDestroy();
    void onPause();
    void onResume();

    void signOff();
    UserPojo getCurrentUser();
    void removeFriend(String friendUid);

    void acceptRequest(UserPojo user);
    void denyRequest(UserPojo user);

    void onEventListener(MainEvent event);

}
