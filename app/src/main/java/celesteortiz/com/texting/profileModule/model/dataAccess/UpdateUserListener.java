package celesteortiz.com.texting.profileModule.model.dataAccess;

public interface UpdateUserListener {
    void onSucess();
    void onNotifyContacts();
    void onError(int resMsg);
}
