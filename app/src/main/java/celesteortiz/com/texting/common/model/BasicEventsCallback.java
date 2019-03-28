package celesteortiz.com.texting.common.model;

/*
 * Project: Texting
 * Created by Celeste Ortiz on 18/03/2019
 *
 * BasicEventsCallback interfaz
 * Servira para comunicar un evento Exitoso o Error
 * a la clase que llame a RealtimeDatabase class
 * */
public interface BasicEventsCallback {
    void onSuccess();
    void onError();
}
