package celesteortiz.com.texting.loginModule.model;

/*
* Project: Texting
* Created by Celeste Ortiz on 15/03/2019
* LoginInteractor interface
* */
public interface LoginInteractor {
    /* Metodos para anadir y remover el listener para la autenticacion */
    void onResume();
    void onPause();

    void getStatusAuth();
}
