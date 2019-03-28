package celesteortiz.com.texting.loginModule;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import celesteortiz.com.texting.R;
import celesteortiz.com.texting.loginModule.events.LoginEvent;
import celesteortiz.com.texting.loginModule.model.LoginInteractor;
import celesteortiz.com.texting.loginModule.model.LoginInteractorImp;
import celesteortiz.com.texting.loginModule.view.LoginActivity;
import celesteortiz.com.texting.loginModule.view.LoginView;

/*
 * Project: Texting
 * Created by Celeste Ortiz on 15/03/2019
 * LoginPresenterImpl class
 * Implementacion de LoginPresenter
 * */
public class LoginPresenterImpl implements LoginPresenter {
    //Inyeccion de nuestra capa Vista e Interactor
    private LoginView mView;
    private LoginInteractor mInteractor;

    public LoginPresenterImpl(LoginView mView) {
        this.mView = mView;
        this.mInteractor = new LoginInteractorImp();
    }

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
    }

    @Override
    public void onResume() {
        if (setProgress()) {
            mInteractor.onResume();
        }
    }

    @Override
    public void onPause() {
        if (setProgress()){
            mInteractor.onPause();
        }
    }

    private boolean setProgress() {
        if (mView != null){
            mView.showProgress();
            return true;
        }
        return false;
    }

    @Override
    public void onDestroy() {
        mView = null;
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void result(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK){
            switch (requestCode){
                case LoginActivity.RC_SIGN_IN:
                    if (data != null){
                        mView.showLoginSuccessfully(data);
                    }
                    break;
            }
        } else {
            mView.showError(R.string.login_message_error);
        }
    }

    @Override
    public void getStatusAuth() {
        Log.d("DEBUG_C", "Presenter: getStatusAuth() setProgress: " + setProgress());

        if (setProgress()){
            mInteractor.getStatusAuth();
        }
    }

    @Subscribe
    @Override
    public void onEventListener(LoginEvent event) {
        if (mView != null){
            mView.hideProgress();

            switch (event.getTypeEvent()){
                case LoginEvent.STATUS_AUTH_SUCCESS:
                    Log.d("DEBUG_C", "Presenter/onEventListener() LoginEvent: 0");
                    if (setProgress()){
                        mView.showMessageStarting();
                        mView.openMainActivity();
                    }
                    break;
                case LoginEvent.STATUS_AUTH_ERROR:
                    Log.d("DEBUG_C", "Presenter/onEventListener() LoginEvent: 101");
                    mView.openUILogin();
                    break;
                case LoginEvent.ERROR_SERVER:
                    Log.d("DEBUG_C", "Presenter/onEventListener() LoginEvent: 100");
                    mView.showError(event.getResMsg());
                    break;
            }
        }
    }
}
