package celesteortiz.com.texting.mainModule.model;

import celesteortiz.com.texting.common.pojo.UserPojo;

public interface MainInteractor {

    void subscribeToUserList();
    void unsubscribeToUserList();

    void signOff();

    UserPojo getCurrentUser();

    void removeFriend(String friendUid);
    void acceptRequest(UserPojo user);
    void denyRequest(UserPojo user);
}
