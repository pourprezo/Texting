package celesteortiz.com.texting.addModule.events;

public class AddEvent {
    public static final int SEND_REQUEST_SUCCESS = 0;
    public static final int ALREADY_FRIENDS = 1;
    public static final int NOT_FRIENDS = 2;
    public static final int ERROR_SERVER = 100;

    private int typeEvent;
    private String email;

    public AddEvent() {
    }

    public int getTypeEvent() {
        return typeEvent;
    }

    public void setTypeEvent(int typeEvent) {
        this.typeEvent = typeEvent;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
