package celesteortiz.com.texting.profileModule.model.dataAccess;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import celesteortiz.com.texting.R;
import celesteortiz.com.texting.common.model.EventErrorTypeListener;
import celesteortiz.com.texting.common.model.StorageUploadImageCallback;
import celesteortiz.com.texting.common.model.dataAccess.FirebaseAuthenticationAPI;
import celesteortiz.com.texting.common.pojo.UserPojo;
import celesteortiz.com.texting.profileModule.events.ProfileEvent;

/*
* Authentication class
* Autenticacion que unicamente actualiza el perfil de Firebase
* Probando GitHub 28/03/2019
* */
public class Authentication {
    private FirebaseAuthenticationAPI mAuthenticationAPI;

    public Authentication() {
        mAuthenticationAPI = FirebaseAuthenticationAPI.getInstance();
    }

    public FirebaseAuthenticationAPI getmAuthenticationAPI(){
        return mAuthenticationAPI;
    }

    //Actualizar por separado la imagen de perfil y el username. Esto afectara directamente al
    //usuario que se registra en Firebase y no a Realtime Database

    public void updateUsernameFirebaseProfile(UserPojo myUser, final EventErrorTypeListener listener){
        FirebaseUser user = mAuthenticationAPI.getCurrentUser();

        if(user != null){
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(myUser.getUsername())
                    .build();
            user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(!task.isSuccessful()){
                        listener.onError(ProfileEvent.ERROR_PROFILE, R.string.profile_error_userUpdated);
                    }
                }
            });
        }
    }

    public void updateImageFirebaseProfile(final Uri downloadUri, final StorageUploadImageCallback callback){
        FirebaseUser user = mAuthenticationAPI.getCurrentUser();

        if(user != null){
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(downloadUri)
                    .build();
            user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                       callback.onSuccess(downloadUri);
                    }else {
                        callback.onError(R.string.profile_error_imageUpdated);
                    }
                }
            });
        }
    }
}
