package celesteortiz.com.texting.loginModule.model.dataAccess;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import celesteortiz.com.texting.common.model.dataAccess.FirebaseAuthenticationAPI;
import celesteortiz.com.texting.common.pojo.UserPojo;

/*
 * Project: Texting
 * Created by Celeste Ortiz on 15/03/2019
 * FirebaseAuthentication class
 * */
public class FirebaseAuthentication {
    private FirebaseAuthenticationAPI mAuthenticationAPI;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    public FirebaseAuthentication() {
        mAuthenticationAPI = FirebaseAuthenticationAPI.getInstance();
    }

    public void onResume(){
        mAuthenticationAPI.getmFirebaseAuth().addAuthStateListener(mAuthStateListener);
    }

    public void onPause(){
        if (mAuthStateListener != null){
            mAuthenticationAPI.getmFirebaseAuth().removeAuthStateListener(mAuthStateListener);
        }
    }

    public void getStatusAuth(final StatusAuthCallback callback){
        Log.d("DEBUG_C", " * * * (LOGIN) FirebaseAuthentication/getStatusAuth() ... ");
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                Log.d("DEBUG_C", " * * * (LOGIN) FirebaseAuthentication/onAuthStateChanged()");
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){
                    Log.d("DEBUG_C", " * * * (LOGIN) FirebaseAuthentication/onAuthStateChanged() User is not null ");
                    callback.onGetUser(user);
                } else {
                    Log.d("DEBUG_C", " * * * (LOGIN) FirebaseAuthentication/onAuthStateChanged() User is null, Launching UI Login ");

                    callback.onLaunchUILogin();
                }
            }
        };
    }

    public UserPojo getCurrentUser(){
        Log.d("DEBUG_C", " * * * (LOGIN) FirebaseAuthentication:          Obteniendo usuario actual... ");

        return mAuthenticationAPI.getAuthUser();
    }

}
