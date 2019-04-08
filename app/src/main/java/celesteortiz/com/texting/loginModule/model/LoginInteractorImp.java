package celesteortiz.com.texting.loginModule.model;

import android.util.Log;

import com.google.firebase.auth.FirebaseUser;

import org.greenrobot.eventbus.EventBus;

import celesteortiz.com.texting.common.model.EventErrorTypeListener;
import celesteortiz.com.texting.common.model.dataAccess.FirebaseCloudMessagingAPI;
import celesteortiz.com.texting.common.pojo.UserPojo;
import celesteortiz.com.texting.loginModule.events.LoginEvent;
import celesteortiz.com.texting.loginModule.model.dataAccess.FirebaseAuthentication;
import celesteortiz.com.texting.loginModule.model.dataAccess.RealtimeDatabase;
import celesteortiz.com.texting.loginModule.model.dataAccess.StatusAuthCallback;

public class LoginInteractorImp implements LoginInteractor {
   private FirebaseAuthentication mAuthentication;
   private RealtimeDatabase mDatabase;
   //Notificaciones Push
    private FirebaseCloudMessagingAPI mCloudMessagingAPI;

    public LoginInteractorImp() {
        mAuthentication = new FirebaseAuthentication();
        mDatabase = new RealtimeDatabase();
        //Notificaciones Push
        mCloudMessagingAPI = FirebaseCloudMessagingAPI.getInstance();
    }

    @Override
    public void onResume() {
        mAuthentication.onResume();

    }

    @Override
    public void onPause() {
        mAuthentication.onPause();

    }

    @Override
    public void getStatusAuth() {
        Log.d("DEBUG_C", "Interactor:    Obteniendo Estatus Auth...");
        mAuthentication.getStatusAuth(new StatusAuthCallback() {
            @Override
            public void onGetUser(FirebaseUser user) {
                //Una vez consultado el usuario correctamente posteamos el evento
                post(LoginEvent.STATUS_AUTH_SUCCESS, user);

                //Cuando el usuario si este autenticado, revisar si existe de BD
                mDatabase.checkUserExist(mAuthentication.getCurrentUser().getUid(), new EventErrorTypeListener() {
                    @Override
                    public void onError(int typeEvent, int resMsg) {
                        Log.d("DEBUG_C", "Interactor: checkUserExist().onError()... " + "type event:" +typeEvent);

                        if(typeEvent == LoginEvent.USER_DONOT_EXIST){
                            Log.d("DEBUG_C", "    User do not exist 102, Registrar...");
                            registerUser();
                        }else {
                            post(typeEvent);
                        }
                    }
                });

                //Notificaciones Push , suscribirnos a nuestro propio correo
                //y de esta forma todos los demas usuarios que nos tengan agreados como contacto
                //utilizaran ese topic.
                mCloudMessagingAPI.subscribeToMyTopic(user.getEmail());

            }

            //Para cuando haya un error, y necesitemos lanzar la interfaz de FIrebase UI
            @Override
            public void onLaunchUILogin() {
                //posteamos solamente el typeEvent
                post(LoginEvent.STATUS_AUTH_ERROR);
            }
        });

    }

    private void registerUser() {
        Log.d("DEBUG_C", "Interactor: Registrar Usuario...");
        UserPojo currentUser = mAuthentication.getCurrentUser();
        mDatabase.registerUser(currentUser);
    }

    private void post(int typeEvent) {
        post(typeEvent, null);
    }

    private void post(int typeEvent, FirebaseUser user) {
        Log.d("DEBUG_C", "Interactor: POSTEANDO EVENTO ... ");
        LoginEvent event = new LoginEvent();

        event.setTypeEvent(typeEvent);
        event.setUser(user);
        EventBus.getDefault().post(event);
    }
}
