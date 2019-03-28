package celesteortiz.com.texting.loginModule.model.dataAccess;

import com.google.firebase.auth.FirebaseUser;

/*
 * Project: Texting
 * Created by Celeste Ortiz on 15/03/2019
 * StatusAuthCallback interface
 * */
public interface StatusAuthCallback {
    /* Respuesta para el caso exitoso */
    void onGetUser(FirebaseUser user);

    /* Respuesta para el caso en que no se pueda consultar el usuario se lanza la interfaz
    * de Firebase UI para que se autentique automaticamente */
    void onLaunchUILogin();
}
