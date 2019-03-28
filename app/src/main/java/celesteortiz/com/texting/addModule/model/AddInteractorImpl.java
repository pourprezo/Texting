package celesteortiz.com.texting.addModule.model;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

import celesteortiz.com.texting.addModule.events.AddEvent;
import celesteortiz.com.texting.addModule.model.dataAccess.AddEventListener;
import celesteortiz.com.texting.addModule.model.dataAccess.RealtimeDatabase;
import celesteortiz.com.texting.common.model.BasicEventsCallback;
import celesteortiz.com.texting.common.model.dataAccess.FirebaseAuthenticationAPI;

public class AddInteractorImpl implements AddInteractor {
    private RealtimeDatabase mDatabase;
    private FirebaseAuthenticationAPI mAuthenticationAPI;

    public AddInteractorImpl() {
        this.mDatabase= new RealtimeDatabase();
        this.mAuthenticationAPI = FirebaseAuthenticationAPI.getInstance();
    }

    @Override
    public void addFriend(final String email) {
        Log.d("DEBUG", "AddInteractor.addFriend() Revisando prmero si ya existe amistad.. ");
        mDatabase.isFriend(email, mAuthenticationAPI.getAuthUser(), new BasicEventsCallback() {
            @Override
            public void onSuccess() {
                Log.d("DEBUG", "AddInteractor.onSuccess() No son amigos, enviando solicitud... ");
                mDatabase.addFriend(email, mAuthenticationAPI.getAuthUser(), new BasicEventsCallback() {
                    @Override
                    public void onSuccess() {
                        post(AddEvent.SEND_REQUEST_SUCCESS);
                    }

                    @Override
                    public void onError() {
                        post(AddEvent.ERROR_SERVER);
                    }
                });
            }

            @Override
            public void onError() {
                Log.d("DEBUG", "AddInteractor.onError() Ya son amigos.. ");

                post(AddEvent.ALREADY_FRIENDS);
            }
        });

    }

    /*@Override
    public void isFriend(final String emailFriend) {
        Log.d("DEBUG", "AddInteractor:     Revisando si ya existe amigo para evitar enviar solicitud... ");
        mDatabase.isFriend(emailFriend, mAuthenticationAPI.getAuthUser(), new AddEventListener(){

            @Override
            public void onFriendshipExist() {
                post(AddEvent.ALREADY_FRIENDS);
            }

            @Override
            public void onNoFriendshipExist(String email) {
                post(AddEvent.NOT_FRIENDS, email);
            }

        });

    }*/

    private void post(int typeEvent) {
        post(typeEvent, null);
    }


    private void post(int typeEvent, String emailFriend) {
        Log.d("DEBUG", "AddInteractor:     POSTEANDO EVENTO " + typeEvent);
        AddEvent event = new AddEvent();
        event.setTypeEvent(typeEvent);
        event.setEmail(emailFriend);
        EventBus.getDefault().post(event);

    }
}
