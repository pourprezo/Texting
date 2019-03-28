package celesteortiz.com.texting.addModule.model.dataAccess;

public interface AddEventListener {
    void onFriendshipExist();
    void onNoFriendshipExist(String email);
}
