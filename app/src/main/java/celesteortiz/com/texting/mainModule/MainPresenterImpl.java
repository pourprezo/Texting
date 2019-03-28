package celesteortiz.com.texting.mainModule;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import celesteortiz.com.texting.common.pojo.UserPojo;
import celesteortiz.com.texting.mainModule.events.MainEvent;
import celesteortiz.com.texting.mainModule.model.MainInteractor;
import celesteortiz.com.texting.mainModule.model.MainInteractorImpl;
import celesteortiz.com.texting.mainModule.view.MainView;

public class MainPresenterImpl implements MainPresenter {
    private MainView mView;
    private MainInteractor mInteractor;

    public MainPresenterImpl(MainView mView) {
        this.mView = mView;
        this.mInteractor = new MainInteractorImpl();
    }

    @Override
    public void onCreate() {
        Log.d("DEBUG", "MainPresenter:   onCreate() Registrar a EventBus...");
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        Log.d("DEBUG", "MainPresenter:   onDestroy() DesRegistrar de EventBus y Destruir vista...");
        EventBus.getDefault().unregister(this);
        mView = null;
    }

    @Override
    public void onPause() {
        Log.d("DEBUG", "MainPresenter:   onPause() DesSuscribir de Lista de contactos....Llamando a Interactor...");
        if(mView != null){
            mInteractor.unsubscribeToUserList();
        }
    }

    @Override
    public void onResume() {
        Log.d("DEBUG", "MainPresenter:   onResume() Suscribir a lista de contactos...Llamando a Interactor...");
        if(mView != null){
            mInteractor.subscribeToUserList();
        }
    }

    @Override
    public void signOff() {
        Log.d("DEBUG", "MainPresenter:   Cerrar sesion, Suscribir a lista de contactos, destroy....Llamando a Interactor...");
        mInteractor.unsubscribeToUserList();
        mInteractor.signOff();
        onDestroy();
    }

    @Override
    public UserPojo getCurrentUser() {
        Log.d("DEBUG", "MainPresenter:  Obtener usuario actual....Llamando a Interactor...");
        return mInteractor.getCurrentUser();
    }

    @Override
    public void removeFriend(String friendUid) {
        Log.d("DEBUG", "MainPresenter:   Remover amistad....Llamando a Interactor...");
        if(mView != null){
            mInteractor.removeFriend(friendUid);
        }
    }

    @Override
    public void acceptRequest(UserPojo user) {
        Log.d("DEBUG", "MainPresenter:   Aceptar Solicitud....Llamando a Interactor...");
        if(mView != null){
            mInteractor.acceptRequest(user);
        }
    }

    @Override
    public void denyRequest(UserPojo user) {
        Log.d("DEBUG", "MainPresenter:   Rechazar Solicitud....Llamando a Interactor...");
        if(mView != null){
            mInteractor.denyRequest(user);
        }
    }

    @Subscribe
    @Override
    public void onEventListener(MainEvent event) {
        Log.d("DEBUG", "MainPresenter:   onEventListener");
        Log.d("DEBUG", "MainPresenter:   Se llamara a la View segun el Evento.....");
        if(mView != null){
            UserPojo user = event.getUser();

            switch (event.getTypeEvent()){
                case MainEvent.USER_ADDED:
                    mView.friendAdded(user);
                    break;
                case MainEvent.USER_UPDATED:
                    mView.friendUpdated(user);
                    break;
                case MainEvent.USER_REMOVED:
                    if (user != null) {
                        mView.friendRemoved(user);
                    } else {
                        mView.showFriendRemoved();
                    }
                    break;
                case MainEvent.REQUEST_ADDED:
                    mView.requestAdded(user);
                    break;
                case MainEvent.REQUEST_UPDATED:
                    mView.requestUpdated(user);
                    break;
                case MainEvent.REQUEST_REMOVED:
                    mView.requestRemoved(user);
                    break;
                case MainEvent.REQUEST_ACCEPTED:
                    mView.showRequestAccepted(user.getUsername());
                    break;
                case MainEvent.REQUEST_DENIED:
                    mView.showRequestDenied();
                    break;
                case MainEvent.ERROR_SERVER:
                    mView.showError(event.getResMsg());
                    break;
            }
        }
    }
}
