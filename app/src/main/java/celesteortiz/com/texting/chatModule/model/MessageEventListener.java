package celesteortiz.com.texting.chatModule.model;

import celesteortiz.com.texting.common.pojo.Message;

/*
* Suscripcion a los mensajes
* */
public interface MessageEventListener {
    void onMessageReceived(Message message);
    void onError(int resMsg);

}
