package celesteortiz.com.texting.chatModule.model;

/*
* Auxiliar importante para conseguir el estado de uestro amigo
* Saber si esta en linea, cual fue su ultima conexion
* y saber con quien esta conectado.
* */
public interface LastConnectionEventListener {
    void onSuccess(boolean online, long lastConnection, String uidConnectedFriend);
}
