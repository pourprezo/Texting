package celesteortiz.com.texting.chatModule.model;

import android.app.Activity;
import android.net.Uri;

public interface ChatInteractor {
    //Suscribirnos al estado de nuestro amigo para saber si esta conectado o no
    void subscribeToFriend(String friendUid, String friendEmail);
    void unSubscribeToFriend(String friendUid);

    void subscribeToMessages();
    void unSubscribeToMessages();

    void sendMessage(String message);
    void sendImage(Activity activity, Uri imageUri);



}
