package celesteortiz.com.texting.chatModule.events;

import celesteortiz.com.texting.common.pojo.Message;

/*
* Clase POJO (Plain Old Java Object) para el manejo de eventos
* del modulo de Chat
* */
public class ChatEvent {
    public static final int MESSAGE_ADDED       = 0;
    public static final int IMAGE_UPLOEAD_SUCCESS = 1;
    public static final int GET_STATUS_FRIEND   = 2;
    public static final int ERROR_SERVER        = 100;
    public static final int IMAGE_UPLOAD_FAILED = 101;
    public static final int ERROR_VOLLEY        = 102;
    public static final int ERROR_PROCESS_DATA  = 103;

    private int typeEvent;
    private int resMsg;
    private Message message;
    private boolean connected;
    private long lastConnection;

    public ChatEvent() {
    }

    public int getTypeEvent() {
        return typeEvent;
    }

    public void setTypeEvent(int typeEvent) {
        this.typeEvent = typeEvent;
    }

    public int getResMsg() {
        return resMsg;
    }

    public void setResMsg(int resMsg) {
        this.resMsg = resMsg;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public long getLastConnection() {
        return lastConnection;
    }

    public void setLastConnection(long lastConnection) {
        this.lastConnection = lastConnection;
    }
}