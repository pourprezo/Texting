package celesteortiz.com.texting.loginModule.view;

import android.content.Intent;
/*
 * Project: Texting
 * Created by Celeste Ortiz on 15/03/2019
 * LoginView
 * */
public interface LoginView {
    /* Progress bar methods */
    void showProgress();
    void hideProgress();

    /* Lanzar Main Activity una vez logueados podremos ver nuestro listado de contactos*/
    void openMainActivity();
    /* Abrir ventana de logueo con Firebase UI*/
    void openUILogin();

    /* Login exitoso  */
    void showLoginSuccessfully(Intent data);
    /* Mostrar mensaje una vez que se haya logueado el usuario */
    void showMessageStarting();

    void showError(int resMsg);
}
