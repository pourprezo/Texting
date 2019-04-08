package celesteortiz.com.texting.common.model.dataAccess;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import celesteortiz.com.texting.common.utils.UtilsCommon;

public class FirebaseCloudMessagingAPI {
    private FirebaseMessaging mFirebaseMessaging;

    private static class SingletonHolder{
        private static final FirebaseCloudMessagingAPI INSTANCE = new FirebaseCloudMessagingAPI();
    }

    public static FirebaseCloudMessagingAPI getInstance(){
        return SingletonHolder.INSTANCE;
    }

    public FirebaseCloudMessagingAPI(){
        this.mFirebaseMessaging = FirebaseMessaging.getInstance();
    }

    //Methods
    public void subscribeToMyTopic(String myEmail){
        mFirebaseMessaging.subscribeToTopic(UtilsCommon.getEmailToTopic(myEmail))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(!task.isSuccessful()){
                            //Reintentar y notificar
                        }
                    }
                });
    }

    public void unsubscribeToMyTopic(String myEmail){
        mFirebaseMessaging.unsubscribeFromTopic(UtilsCommon.getEmailToTopic(myEmail))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(!task.isSuccessful()){
                            //Reintentar y notificar
                        }
                    }
                });

    }

}
