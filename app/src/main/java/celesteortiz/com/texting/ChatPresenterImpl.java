package celesteortiz.com.texting;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import celesteortiz.com.texting.chatModule.ChatPresenter;
import celesteortiz.com.texting.chatModule.events.ChatEvent;
import celesteortiz.com.texting.chatModule.model.ChatInteractor;
import celesteortiz.com.texting.chatModule.model.ChatInteractorImpl;
import celesteortiz.com.texting.chatModule.view.ChatView;
import celesteortiz.com.texting.common.Constants;

public class ChatPresenterImpl implements ChatPresenter {
    private ChatView mView;
    private ChatInteractor mInteractor;

    private String mFriendUid, mFriendEmail;

    public ChatPresenterImpl(ChatView mView){
        this.mView = mView;
        this.mInteractor = new ChatInteractorImpl();
    }

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);

    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        mView = null;

    }

    @Override
    public void onPause() {
        if (mView != null) {
            mInteractor.unSubscribeToFriend(mFriendUid);
            mInteractor.unSubscribeToMessages();
        }
    }

    @Override
    public void onResume() {
        if (mView != null) {
            mInteractor.subscribeToFriend(mFriendUid, mFriendEmail);
            mInteractor.subscribeToMessages();
        }

    }

    @Override
    public void setupFriend(String uid, String email) {
        mFriendEmail = email;
        mFriendUid = uid;

    }

    @Override
    public void sendMessage(String msg) {
        Log.d("DEBUG CHAT", "Presenter: Send Message...");
        if (mView != null) {
            mInteractor.sendMessage(msg);
        }
    }

    @Override
    public void sendImage(Activity activity, Uri imageUri) {
        if(mView != null){
            mView.showProgress();
            mInteractor.sendImage(activity, imageUri);
        }
    }

    //Despues de haber elegido una imagen de la galeria...
    @Override
    public void result(int requestCode, int resultCode, Intent data) {
        if(requestCode == Constants.RC_PHOTO_PICKER && resultCode == Activity.RESULT_OK){
            mView.openDialogPreview(data);

        }
    }
@Subscribe
    @Override
    public void onEventListener(ChatEvent event) {
    if (mView != null) {
        switch (event.getTypeEvent()){
            case ChatEvent.MESSAGE_ADDED:
                Log.d("DEBUG CHAT", "Presenter: MESSAGE_ADDED");
                mView.onMessageReceived(event.getMessage());
                break;
            case ChatEvent.IMAGE_UPLOEAD_SUCCESS:
                Log.d("DEBUG CHAT", "Presenter: IMAGE_UPLOEAD_SUCCESS");
                mView.hideProgress();
                break;
            case ChatEvent.GET_STATUS_FRIEND:
                Log.d("DEBUG CHAT", "Presenter: GET_STATUS_FRIEND");
                mView.onStatusUser(event.isConnected(), event.getLastConnection());
                break;
            case ChatEvent.ERROR_PROCESS_DATA:
                break;
            case ChatEvent.ERROR_SERVER:
                break;
            case ChatEvent.ERROR_VOLLEY:
                Log.d("DEBUG CHAT", "Presenter: ERROR_VOLLEY");
                mView.hideProgress();
                mView.onError(event.getResMsg());
                break;
        }
    }
    }
}
