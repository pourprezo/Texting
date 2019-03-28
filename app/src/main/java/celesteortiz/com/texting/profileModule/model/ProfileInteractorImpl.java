package celesteortiz.com.texting.profileModule.model;

import android.app.Activity;
import android.net.Uri;

import org.greenrobot.eventbus.EventBus;

import celesteortiz.com.texting.common.model.EventErrorTypeListener;
import celesteortiz.com.texting.common.model.StorageUploadImageCallback;
import celesteortiz.com.texting.common.pojo.UserPojo;
import celesteortiz.com.texting.profileModule.events.ProfileEvent;
import celesteortiz.com.texting.profileModule.model.dataAccess.Authentication;
import celesteortiz.com.texting.profileModule.model.dataAccess.RealtimeDatabase;
import celesteortiz.com.texting.profileModule.model.dataAccess.Storage;
import celesteortiz.com.texting.profileModule.model.dataAccess.UpdateUserListener;

public class ProfileInteractorImpl implements ProfileInteractor {
    private Authentication mAuthentication;
    private RealtimeDatabase mDatabase;
    private Storage mStorage;

    private UserPojo mMyUser;

    public ProfileInteractorImpl() {
        mAuthentication = new Authentication();
        mDatabase = new RealtimeDatabase();
        mStorage = new Storage();
    }

    private UserPojo getCurrentUser(){
        if(mMyUser == null){
            mMyUser = mAuthentication.getmAuthenticationAPI().getAuthUser();
        }
        return mMyUser;
    }

    /*
    * Cambiar el nombre de perfil tanto en Realtime database con en Firebase Authentication
    * */
    @Override
    public void updateUsername(String username) {
        final UserPojo myUser = getCurrentUser();
        myUser.setUsername(username);
        mDatabase.changeUsername(myUser, new UpdateUserListener() {
            @Override
            public void onSucess() {
                mAuthentication.updateUsernameFirebaseProfile(myUser, new EventErrorTypeListener() {
                    @Override
                    public void onError(int typeEvent, int resMsg) {
                        post(typeEvent, null, resMsg);
                    }
                });
            }

            @Override
            public void onNotifyContacts() {
                postUsernameSuccess();
            }

            @Override
            public void onError(int resMsg) {
                post(ProfileEvent.ERROR_USER_NAME, null, resMsg);
            }
        });

    }

    @Override
    public void updateImage(Activity activity, Uri uri, final String oldPhotoUrl) {
        //Subir imagen a storage
        mStorage.uploadImageProfile(activity, uri, getCurrentUser().getEmail(), new StorageUploadImageCallback() {
            @Override
            public void onSuccess(Uri uri) {
                mDatabase.updatePhotoUrl(uri, getCurrentUser().getUid(), new StorageUploadImageCallback() {
                    @Override
                    public void onSuccess(Uri newUri) {
                        post(ProfileEvent.UPLOAD_IMAGE, newUri.toString(), 0);
                    }

                    @Override
                    public void onError(int resMsg) {
                        post(ProfileEvent.ERROR_SERVER, resMsg);
                    }
                });

                //Actualizar en Authentication
                mAuthentication.updateImageFirebaseProfile(uri, new StorageUploadImageCallback() {
                    @Override
                    public void onSuccess(Uri newUri) {
                        mStorage.deleteOldImage(oldPhotoUrl, newUri.toString());
                    }

                    @Override
                    public void onError(int resMsg) {
                        post(ProfileEvent.ERROR_PROFILE, resMsg);
                    }
                });
            }

            @Override
            public void onError(int resMsg) {
                post(ProfileEvent.ERROR_IMAGE, resMsg);
            }
        });
    }

    //Eventbus
    private void post(int typeEvent, String photoUrl, int resMsg) {
        ProfileEvent event = new ProfileEvent();
        event.setPhotoUrl(photoUrl);
        event.setResMsg(resMsg);
        event.setTypeEvent(typeEvent);
        EventBus.getDefault().post(event);
    }

    private void post(int typeEvent, int resMsg) {
        post(typeEvent, null, resMsg);
    }

    private void postUsernameSuccess() {
        post(ProfileEvent.SAVE_USERNAME, null, 0);
    }
}
