package celesteortiz.com.texting.mainModule.model.dataAccess;

import celesteortiz.com.texting.common.pojo.UserPojo;

/*
 * Project: Texting
 * Created by Celeste Ortiz on 18/03/2019
 * UserEventListener interface
 * Listener que estara a la escucha en caso de que ocurra
 * cualquiera de estos eventos
 * */
public interface UserEventListener {
    void onUserAdded(UserPojo user);
    void onUserUpdated(UserPojo user);
    void onUserRemoved(UserPojo user);

    void onError(int resMsg);
}
