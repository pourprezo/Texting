package celesteortiz.com.texting.profileModule;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import org.greenrobot.eventbus.EventBus;

import celesteortiz.com.texting.common.pojo.UserPojo;
import celesteortiz.com.texting.profileModule.events.ProfileEvent;
import celesteortiz.com.texting.profileModule.model.ProfileInteractor;
import celesteortiz.com.texting.profileModule.model.ProfileInteractorImpl;
import celesteortiz.com.texting.profileModule.view.ProfileView;

public class ProfilePresenterImpl implements ProfilePresenter {
    private ProfileView mView;
    private ProfileInteractor mInteractor;
    private boolean isEdit = false;
    private UserPojo mUser;

    public ProfilePresenterImpl(ProfileView mView) {
        this.mView = mView;
        this.mInteractor = new ProfileInteractorImpl();
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
    public void setupUser(String username, String email, String photoUrl) {
        mUser = new UserPojo();
        mUser.setUsername(username);
        mUser.setEmail(email);
        mUser.setPhotoUrl(photoUrl);

        mView.showUserData(username, email, photoUrl);
    }


    //Este metodo se ejecutara cuando hagan click sobre la imagen de perfil y con
    //esta validacion, entrara a la galeria para elegir la nueva imagen
    @Override
    public void checkEditionMode() {
        if(isEdit){
            mView.launchGallery();
        }
    }

    @Override
    public void updateUsername(String username) {
        if(isEdit){
            if(setProgress()){
                mView.showProgress();
                mInteractor.updateUsername(username);
                mUser.setUsername(username);
            }
        }else{
            isEdit = true;
            mView.menuEditMode();
            mView.enableUIElements();
        }
    }

    @Override
    public void updateImage(Activity activity, Uri uri) {
        if(setProgress()){
            mView.showProgressImage();
            mInteractor.updateImage(activity, uri, mUser.getPhotoUrl());
        }
    }

    @Override
    public void result(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK){
            switch (requestCode){
                case ProfileActivity.RC_PHOTO_PICKER:
                   mView.openDialogPreview(data);
                 break;
            }
        }

    }

    @Override
    public void onEventListener(ProfileEvent event) {

    }

    private boolean setProgress() {
        if(mView != null){
            mView.disableUIElements();
            return true;
        }
        return false;
    }
}
