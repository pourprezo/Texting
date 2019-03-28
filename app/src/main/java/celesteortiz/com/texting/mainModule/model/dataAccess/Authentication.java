package celesteortiz.com.texting.mainModule.model.dataAccess;

import android.util.Log;

import celesteortiz.com.texting.common.model.dataAccess.FirebaseAuthenticationAPI;
import celesteortiz.com.texting.common.model.dataAccess.FirebaseRealtimeDatabaseAPI;
import celesteortiz.com.texting.loginModule.model.dataAccess.FirebaseAuthentication;

/*
 * Project: Texting
 * Created by Celeste Ortiz on 18/03/2019
 *
 * Authentication class para la vista Principal (ContactosFragment)
 * */
public class Authentication {
    private FirebaseAuthenticationAPI mAuthenticationAPI;

    public Authentication() {
        mAuthenticationAPI = FirebaseAuthenticationAPI.getInstance();
    }

    public FirebaseAuthenticationAPI getmAuthenticationAPI() {
        return mAuthenticationAPI;
    }

    //Cerrar sesion
    public void signOff(){
        Log.d("DEBUG", " * * *  (MAIN) Authentication:   Cerrando sesion....");
        mAuthenticationAPI.getmFirebaseAuth().signOut();
    }
}
