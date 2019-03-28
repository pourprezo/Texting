package celesteortiz.com.texting.loginModule;

import android.content.Intent;
import celesteortiz.com.texting.loginModule.events.LoginEvent;
/*
 * Project: Texting
 * Created by Celeste Ortiz on 15/03/2019
 * LoginPresenter
 * */
public interface LoginPresenter {
    void onCreate();
    void onResume();
    void onPause();
    void onDestroy();

    /* Que hacer cuando reciba una respuesta en ActivityResult */
    void result(int requestCode, int resultCode, Intent data);
    /* Consultar el estado de la autenticacion actual */
    void getStatusAuth();
    void onEventListener(LoginEvent event);

}
