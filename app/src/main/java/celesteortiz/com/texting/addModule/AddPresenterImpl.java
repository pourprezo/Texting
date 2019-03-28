package celesteortiz.com.texting.addModule;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import celesteortiz.com.texting.addModule.events.AddEvent;
import celesteortiz.com.texting.addModule.model.AddInteractor;
import celesteortiz.com.texting.addModule.model.AddInteractorImpl;
import celesteortiz.com.texting.addModule.view.AddView;

public class AddPresenterImpl implements AddPresenter {
    private AddView mView;
    private AddInteractor mInteractor;

    public AddPresenterImpl(AddView mView) {
        this.mView = mView;
        this.mInteractor = new AddInteractorImpl();
    }

    @Override
    public void onShow() {
        EventBus.getDefault().register(this);


    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        mView = null;
    }

    @Override
    public void addFriend(String email) {
        Log.d("DEBUG", "AddPresenter:   Agregar amistad....Llamando a Interactor...");
        if(mView != null){
            mView.disableUIElements();
            mView.showProgress();

            mInteractor.addFriend(email);
        }
    }

    @Subscribe
    @Override
    public void onEventListener(AddEvent event) {
        if(mView != null) {
         mView.hideProgress();
         mView.enableUIElements();

            switch (event.getTypeEvent()) {
                case AddEvent.SEND_REQUEST_SUCCESS:
                    Log.d("DEBUG", "AddPresenter:   OnEventListener.SEND_REQUEST_SUCCESS....Llamando a View...");

                    mView.friendAdded();
                    break;
                case AddEvent.ALREADY_FRIENDS:
                    Log.d("DEBUG", "AddPresenter:   OnEventListener.ALREADY_FRIENDS....Llamando a View...");

                    mView.friendshipExists();
                    break;
                case AddEvent.ERROR_SERVER:
                    Log.d("DEBUG", "AddPresenter:   OnEventListener.ERROR_SERVER...Llamando a View...");

                    mView.friendNotAdded();
                    break;
            }
        }

    }
}
