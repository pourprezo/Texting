package celesteortiz.com.texting.chatModule.model;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;

import com.firebase.ui.auth.data.model.User;

import org.greenrobot.eventbus.EventBus;

import celesteortiz.com.texting.chatModule.events.ChatEvent;
import celesteortiz.com.texting.chatModule.model.dataAccess.RealtimeDatabaseChat;
import celesteortiz.com.texting.chatModule.model.dataAccess.StorageChat;
import celesteortiz.com.texting.common.Constants;
import celesteortiz.com.texting.common.model.StorageUploadImageCallback;
import celesteortiz.com.texting.common.model.dataAccess.FirebaseAuthenticationAPI;
import celesteortiz.com.texting.common.model.dataAccess.FirebaseRealtimeDatabaseAPI;
import celesteortiz.com.texting.common.pojo.Message;
import celesteortiz.com.texting.common.pojo.UserPojo;

public class ChatInteractorImpl implements ChatInteractor {
    private RealtimeDatabaseChat mDatabase;
    private FirebaseAuthenticationAPI mAuthenticationAPI;
    private StorageChat mStorage;

    private UserPojo mMyUser;
    private String mFriendUid;
    private String mFriendEmail;

    private long mLastConnectionFriend;
    private String mUidConnectedFriend = "";

    public ChatInteractorImpl(){
        this.mDatabase = new RealtimeDatabaseChat();
        this.mAuthenticationAPI = FirebaseAuthenticationAPI.getInstance();
        this.mStorage = new StorageChat();
    }

    private UserPojo getCurrentUser(){
        if(mMyUser == null){
            mMyUser = mAuthenticationAPI.getAuthUser();

        }
        return mMyUser;
    }

    @Override
    public void subscribeToFriend(String friendUid, String friendEmail) {
        this.mFriendEmail = friendEmail;
        this.mFriendUid = friendUid;

        mDatabase.subscribeToFriend(friendUid, new LastConnectionEventListener() {
            //Nos devuelve respuesta en onSuccess con los valores del estado actual de nuestro amigo
            @Override
            public void onSuccess(boolean online, long lastConnection, String uidConnectedFriend) {
                postStatusFriend(online, lastConnection);
               //
                mUidConnectedFriend = uidConnectedFriend;
                mLastConnectionFriend = lastConnection;
            }
        });

        //Notificar que hemos leido los mensajes del usuario actual con el cual estamos conectados
        mDatabase.setMessageRead(getCurrentUser().getUid(), friendUid);
    }

    @Override
    public void unSubscribeToFriend(String friendUid) {
        mDatabase.unsubscribeToFriend(friendUid);

    }

    @Override
    public void subscribeToMessages() {
        mDatabase.subscribeToMessages(getCurrentUser().getEmail(), mFriendEmail, new MessageEventListener() {
            @Override
            public void onMessageReceived(Message message) {
                String msgSender = message.getSender();
                message.setSentByMe(msgSender.equals(getCurrentUser().getEmail()));
                postMessage(message);
            }

            @Override
            public void onError(int resMsg) {
                post(ChatEvent.ERROR_SERVER, resMsg);

            }
        });
        mDatabase.getmDatabaseAPI().updateMyLastConnection(Constants.ONLINE, mFriendUid,getCurrentUser().getUid());
    }

    @Override
    public void unSubscribeToMessages() {
        mDatabase.unSubscribToMessages(getCurrentUser().getEmail(), mFriendEmail);

        //Actualizar nuestra ultima conexion, ponernos offline ya que este metodo se detonara en el onPause
        mDatabase.getmDatabaseAPI().updateMyLastConnection(Constants.OFFLINE, getCurrentUser().getUid());
    }

    @Override
    public void sendMessage(String message) {
        Log.d("DEBUG CHAT", "Interactor: Send Message...");
        sendMessage(message, null);
    }

    @Override
    public void sendImage(Activity activity, Uri imageUri) {
        mStorage.uploadImageChat(activity, imageUri, getCurrentUser().getEmail(), new StorageUploadImageCallback() {
            @Override
            public void onSuccess(Uri newUri) {
                //Una vez que se haya subido la imagen con exito vamos a recibir la nueva URI que
                // pertenece a la URL de descarga
                sendMessage(null, newUri.toString());
                postUploadSuccess();
            }

            @Override
            public void onError(int resMsg) {
                post(ChatEvent.IMAGE_UPLOAD_FAILED, resMsg);
            }
        });
    }

    private void sendMessage(final String msg, String photoUrl){
        mDatabase.sendMessage(msg, photoUrl, mFriendEmail, getCurrentUser(), new SendMessagesListener() {
            @Override
            public void onSuccess() {
                Log.d("DEBUG CHAT", "Interactor: Mensaje enviado con exito...");
                //Una vez que se haya insertado correcamente el mje en Realtime Database,
                // Validar si procede o no el incrementar el numero de mensajes no leidos por el usuario

                //Si no esta conectado conmigo, entonces incrementa el numero de mensajes no leidos
                // de mi parte
                if(!mUidConnectedFriend.equals(getCurrentUser().getUid())){
                    Log.d("DEBUG CHAT", "Interactor: Destinatario no conectado, agregando mensaje Unread");
                    mDatabase.sumUnreadMessages(getCurrentUser().getUid(), mFriendUid);

                    // TODO: 01/04/2019 Push Notifications
                }
            }
        });
    }

    private void postUploadSuccess() {
        post(ChatEvent.IMAGE_UPLOEAD_SUCCESS, 0, null, false, 0);

    }



    private void postMessage(Message message){
        post(ChatEvent.MESSAGE_ADDED, 0, message, false, 0);
    }

    private void post(int typeEvent, int resMsg) {
        post(typeEvent, resMsg, null, false, 0);
    }

    private void postStatusFriend(boolean online, long lastConnection) {
        post(ChatEvent.GET_STATUS_FRIEND, 0, null, online, lastConnection);
    }

    private void post(int typeEvent, int resMsg, Message message, boolean online, long lastConnection){
        ChatEvent event = new ChatEvent();
        event.setTypeEvent(typeEvent);
        event.setResMsg(resMsg);
        event.setMessage(message);
        event.setConnected(online);
        event.setLastConnection(lastConnection);
        EventBus.getDefault().post(event);
    }
}
