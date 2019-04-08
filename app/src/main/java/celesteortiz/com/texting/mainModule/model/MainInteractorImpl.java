package celesteortiz.com.texting.mainModule.model;

import android.util.Log;

import com.firebase.ui.auth.data.model.User;

import org.greenrobot.eventbus.EventBus;

import celesteortiz.com.texting.common.Constants;
import celesteortiz.com.texting.common.model.BasicEventsCallback;
import celesteortiz.com.texting.common.model.dataAccess.FirebaseCloudMessagingAPI;
import celesteortiz.com.texting.common.pojo.UserPojo;
import celesteortiz.com.texting.mainModule.events.MainEvent;
import celesteortiz.com.texting.mainModule.model.dataAccess.Authentication;
import celesteortiz.com.texting.mainModule.model.dataAccess.RealtimeDatabase;
import celesteortiz.com.texting.mainModule.model.dataAccess.UserEventListener;

public class MainInteractorImpl implements MainInteractor {
    private RealtimeDatabase mDatabase;
    private Authentication mAuthentication;
    //Notificaciones Push
    private FirebaseCloudMessagingAPI mCloudMessagingAPI;

    private UserPojo mMyUser = null;

    //Constructor que inicializa a variables para obtencion de datos y autenticacion
    public MainInteractorImpl() {
        mDatabase = new RealtimeDatabase();
        mAuthentication = new Authentication();

        //Notificaciones Push
        mCloudMessagingAPI = FirebaseCloudMessagingAPI.getInstance();
    }

    @Override
    public void subscribeToUserList() {
        Log.d("DEBUG", "MainInteractor:   Suscribir a Lista de ContactosFragment...");
        mDatabase.subscribeToUserList(getCurrentUser().getUid(), new UserEventListener() {
            @Override
            public void onUserAdded(UserPojo user) {
                Log.d("DEBUG", "MainInteractor:   Suscribir a Lista de ContactosFragment...onUserAdded()...posteando...");
                post(MainEvent.USER_ADDED, user);
            }

            @Override
            public void onUserUpdated(UserPojo user) {
                Log.d("DEBUG", "MainInteractor:   Suscribir a Lista de ContactosFragment...onUserUpdated()...posteando...");
                post(MainEvent.USER_UPDATED, user);
            }

            @Override
            public void onUserRemoved(UserPojo user) {
                Log.d("DEBUG", "MainInteractor:   Suscribir a Lista de ContactosFragment...onUserRemoved()...posteando...");
                post(MainEvent.USER_REMOVED, user);

            }

            @Override
            public void onError(int resMsg) {
                Log.d("DEBUG", "MainInteractor:   Suscribir a Lista de ContactosFragment...onError()...posteando error..." + resMsg);
                postError(resMsg);

            }
        });

        Log.d("DEBUG", "MainInteractor:   Suscribir a Solicitudes...");

        mDatabase.subscribeToRequests(getCurrentUser().getEmail(), new UserEventListener() {

            @Override
            public void onUserAdded(UserPojo user) {
                Log.d("DEBUG", "MainInteractor:   Suscribir a Solicitudes: onUserAdded()..posteando...");
                post(MainEvent.REQUEST_ADDED, user);
            }

            @Override
            public void onUserUpdated(UserPojo user) {
                Log.d("DEBUG", "MainInteractor:   Suscribir a Solicitudes: onUserUpdated()..posteando...");
                post(MainEvent.REQUEST_UPDATED, user);
            }

            @Override
            public void onUserRemoved(UserPojo user) {
                Log.d("DEBUG", "MainInteractor:   Suscribir a Solicitudes: onUserRemoved()..posteando...");
                post(MainEvent.REQUEST_REMOVED, user);
            }

            @Override
            public void onError(int resMsg) {
                Log.d("DEBUG", "MainInteractor:   Suscribir a Solicitudes: onError()..posteando error...");
                post(MainEvent.ERROR_SERVER);
            }
        });

        //Cambiar la configuracion del usuario con respecto a su conexion
        changeConnectionStatus(Constants.ONLINE);
    }

    @Override
    public void unsubscribeToUserList() {
        Log.d("DEBUG", "MainInteractor:   unsubscribeToUserList()");
        mDatabase.unsubscribeToUsers(getCurrentUser().getUid());
        mDatabase.unsubscribeToRequests(getCurrentUser().getEmail());

        changeConnectionStatus(Constants.OFFLINE);
    }

    @Override
    public void signOff() {
        Log.d("DEBUG", "MainInteractor:   Cerrar sesion....");

        //Notificaciones Push ,
        //Desuscribirnos de nuestro propio correo (topic)
        //antes de llamar a signOff, de otro modo no podriamos obtener el correo del usuario logueado
        mCloudMessagingAPI.unsubscribeToMyTopic(getCurrentUser().getEmail());

        mAuthentication.signOff();

    }

    @Override
    public UserPojo getCurrentUser() {
        Log.d("DEBUG", "MainInteractor:             Obteneniendo usuario actual...");
        return mMyUser == null ? mAuthentication.getmAuthenticationAPI().getAuthUser() : mMyUser;
    }

    @Override
    public void removeFriend(String friendUid) {
        Log.d("DEBUG", "MainInteractor:   Remover amigo....");
        mDatabase.removeUser(friendUid, getCurrentUser().getUid(), new BasicEventsCallback() {
            @Override
            public void onSuccess() {
                Log.d("DEBUG", "MainInteractor:   Remover amigo: onSuccess....posteando");
                post(MainEvent.USER_REMOVED);
            }

            @Override
            public void onError() {
                Log.d("DEBUG", "MainInteractor:   Remover amigo: onError....posteando");
                post(MainEvent.ERROR_SERVER);
            }
        });
    }

    @Override
    public void acceptRequest(final UserPojo user) {
        Log.d("DEBUG", "MainInteractor:   Aceptar solicitud....");

        mDatabase.acceptRequest(user, getCurrentUser(), new BasicEventsCallback() {
            @Override
            public void onSuccess() {
                Log.d("DEBUG", "MainInteractor:   Aceptar solicitud: onSuccess....posteando");
                post(MainEvent.REQUEST_ACCEPTED, user);
            }

            @Override
            public void onError() {
                Log.d("DEBUG", "MainInteractor:   Aceptar solicitud: onError....posteando");
                post(MainEvent.ERROR_SERVER);
            }
        });

    }

    @Override
    public void denyRequest(final UserPojo user) {
        Log.d("DEBUG", "MainInteractor:     Rechazar Solicitud.....");
        mDatabase.denyRequest(user, getCurrentUser().getEmail(), new BasicEventsCallback() {
            @Override
            public void onSuccess() {
                Log.d("DEBUG", "MainInteractor:     Success al Rechazar Solicitud, posteando.....");
                post(MainEvent.REQUEST_DENIED, user);
            }

            @Override
            public void onError() {
                Log.d("DEBUG", "MainInteractor:     Error al Rechazar Solicitud, posteando.....");
                post(MainEvent.ERROR_SERVER);
            }
        });
    }

    /*        Metodos internos      */

    /*
    * Metodos para postear a EventBus
    * */
    private void post(int typeEvent) {
        post(typeEvent, null, 0);
    }

    private void postError(int resMsg) {
        post(MainEvent.ERROR_SERVER, null, resMsg);
    }

    private void post(int typeEvent, UserPojo user) {
       post(typeEvent, user, 0);
    }

    private void post(int typeEvent, UserPojo user, int resMsg) {
        Log.d("DEBUG", "MainInteractor:     POSTEANDO EVENTO " + typeEvent);
        MainEvent event = new MainEvent();
        event.setTypeEvent(typeEvent);
        event.setUser(user);
        event.setResMsg(resMsg);
        EventBus.getDefault().post(event);
    }


    private void changeConnectionStatus(boolean online) {
        Log.d("DEBUG", "MainInteractor:   Cambiar estatus de conexion   Status: " + online);

        mDatabase.getmDatabaseAPI().updateMyLastConnection(online, getCurrentUser().getUid());
    }
}
