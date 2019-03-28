package celesteortiz.com.texting.common.model.dataAccess;

import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import celesteortiz.com.texting.common.pojo.UserPojo;

/*
 * Project: Texting
 * Created by Celeste Ortiz on 15/03/2019
 * FirebaseAuthenticationAPI class
 * */
public class FirebaseAuthenticationAPI {
    private FirebaseAuth mFirebaseAuth;

    private static class SingletonHolder{
        private static final FirebaseAuthenticationAPI INSTANCE = new FirebaseAuthenticationAPI();
    }

    public static FirebaseAuthenticationAPI getInstance(){
        return SingletonHolder.INSTANCE;
    }

    //Constructor que inicializa variable de FirebaseAuth
    private FirebaseAuthenticationAPI() {
       this.mFirebaseAuth = FirebaseAuth.getInstance();
    }

    public FirebaseAuth getmFirebaseAuth() {
        return this.mFirebaseAuth;
    }

    public UserPojo getAuthUser(){
        Log.d("DEBUG", " * * * FirebaseAuthenticationAPI:   Obteniendo usuario autenticado....");
        UserPojo user = new UserPojo();
        if (mFirebaseAuth != null && mFirebaseAuth.getCurrentUser() != null){
            user.setUid(mFirebaseAuth.getCurrentUser().getUid());
            user.setUsername(mFirebaseAuth.getCurrentUser().getDisplayName());
            user.setEmail(mFirebaseAuth.getCurrentUser().getEmail());
            user.setUri(mFirebaseAuth.getCurrentUser().getPhotoUrl());

        }

        Log.d("DEBUG", " * * * FirebaseAuthenticationAPI:   Retornando usuario autenticado...  "+ user.getUsername());
        return user;
    }

    public FirebaseUser getCurrentUser() {
        return mFirebaseAuth.getCurrentUser();
    }
}
