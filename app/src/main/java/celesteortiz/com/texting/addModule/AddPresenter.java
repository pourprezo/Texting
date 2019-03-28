package celesteortiz.com.texting.addModule;

import celesteortiz.com.texting.addModule.events.AddEvent;

/*
 * Project: Texting
 * Created by Celeste Ortiz on 18/03/2019
 *
 * AddPresenter interfaz
 *
 **/
public interface AddPresenter {
    void onShow();
    void onDestroy();

    void addFriend(String email);
    /*
    * Escuchar la respuesta despues de Agregar amigo
    * */
    void onEventListener(AddEvent event);

}
