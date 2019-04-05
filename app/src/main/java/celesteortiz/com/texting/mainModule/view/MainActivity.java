package celesteortiz.com.texting.mainModule.view;

import android.app.ActivityOptions;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import celesteortiz.com.texting.addModule.view.AddFragment;
import celesteortiz.com.texting.R;
import celesteortiz.com.texting.chatModule.view.ChatActivity;
import celesteortiz.com.texting.common.pojo.UserPojo;
import celesteortiz.com.texting.common.utils.UtilsCommon;
import celesteortiz.com.texting.loginModule.view.LoginActivity;
import celesteortiz.com.texting.mainModule.MainPresenter;
import celesteortiz.com.texting.mainModule.MainPresenterImpl;
import celesteortiz.com.texting.mainModule.view.adapters.OnItemClickListener;
import celesteortiz.com.texting.mainModule.view.adapters.RequestAdapter;
import celesteortiz.com.texting.mainModule.view.adapters.UserAdapter;
import celesteortiz.com.texting.profileModule.view.ProfileActivity;
import de.hdodenhof.circleimageview.CircleImageView;

/*
 * Project: Texting
 * Created by Celeste Ortiz on 18/03/2019
 *
 * MainActivity class
 * */
public class MainActivity extends AppCompatActivity implements OnItemClickListener, MainView {

    private static final int RC_PROFILE = 23;
    @BindView(R.id.imgProfile)
    CircleImageView imgProfile;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rvRequests)
    RecyclerView rvRequests;
    @BindView(R.id.rvContacts)
    RecyclerView rvContacts;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.contentMain)
    CoordinatorLayout contentMain;

    private UserAdapter mUserAdapter;
    private RequestAdapter mRequestAdapter;
    private MainPresenter mPresenter;
    private UserPojo mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Log.d("DEBUG", "MainActivity: onCreate()...");

        mPresenter = new MainPresenterImpl(this);
        mPresenter.onCreate();
        mUser = mPresenter.getCurrentUser();
        configToolbar();

        configAdapter();
        configRecyclerView();
    }

    private void configToolbar() {
        Log.d("DEBUG", "MainActivity: Configurando toolbar");
        toolbar.setTitle(mUser.getUsernameValid());
        UtilsCommon.loadImage(this, mUser.getPhotoUrl(), imgProfile);
        setSupportActionBar(toolbar);
    }

    private void configAdapter() {
        Log.d("DEBUG", "(VIEW) MainActivity: Configurando Adapters");
        mUserAdapter = new UserAdapter(new ArrayList<UserPojo>(), this);
        mRequestAdapter = new RequestAdapter(new ArrayList<UserPojo>(), this);

    }

    private void configRecyclerView() {
        Log.d("DEBUG", "(VIEW) MainActivity: Configurando Recycler View..ContactosFragment");
        rvRequests.setLayoutManager(new LinearLayoutManager(this));
        rvRequests.setAdapter(mRequestAdapter);

        rvContacts.setLayoutManager(new LinearLayoutManager(this));
        rvContacts.setAdapter(mUserAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                mPresenter.signOff();
                Intent intent = new Intent(this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
            case R.id.action_profile:
                Intent intentProfile = new Intent(this, ProfileActivity.class);
                intentProfile.putExtra(UserPojo.USERNAME, mUser.getUsername());
                intentProfile.putExtra(UserPojo.EMAIL, mUser.getEmail());
                intentProfile.putExtra(UserPojo.PHOTO_URL, mUser.getPhotoUrl());

                if(UtilsCommon.hasMaterialDesign()){
                    startActivityForResult(intentProfile, RC_PROFILE,
                            ActivityOptions.makeSceneTransitionAnimation(this).toBundle());

                }else{
                    //En caso de que la version sea menor a Lolipop...
                    startActivityForResult(intentProfile, RC_PROFILE);

                }
                break;
            case R.id.action_about:
                openAbout();

                break;

        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Actualizar nombre e imagen una vez que se haya actualizado con exito desde Perfil
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case RC_PROFILE:
                    mUser.setUsername(data.getStringExtra(UserPojo.USERNAME));
                    mUser.setPhotoUrl(data.getStringExtra(UserPojo.PHOTO_URL));
                    configToolbar();
                    break;
            }
        }


    }

    private void openAbout() {
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_about, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.DialogFragmentTheme)
                .setTitle(R.string.main_menu_about)
                .setView(view)
                .setPositiveButton(R.string.common_label_ok, null)
                .setNeutralButton(R.string.about_privacy, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("https://policity.android-cursos-ant.com"));
                        startActivity(intent);
                    }
                });
        builder.show();
    }

    /* Sobrecarga de Metodos del ciclo de vida de Activity */

    @Override
    protected void onResume() {
        Log.d("DEBUG", "(VIEW) MainActivity: onResume()...Llamando a presenter");
        super.onResume();
        mPresenter.onResume();

        clearNotification();
    }

    @Override
    protected void onPause() {
        Log.d("DEBUG", "(VIEW) MainActivity: onPause()...Llamando a presenter");
        super.onPause();
        mPresenter.onPause();
    }

    @Override
    protected void onDestroy() {
        Log.d("DEBUG", "(VIEW) MainActivity: onDestroy()...Llamando a presenter");
        super.onDestroy();
        mPresenter.onDestroy();
    }


    /*
     *      Main View
     * */
    @Override
    public void friendAdded(UserPojo user) {
        Log.d("DEBUG", "(VIEW) MainActivity: friendAdded...Llamando a User Adapter");
        mUserAdapter.add(user);
    }

    @Override
    public void friendUpdated(UserPojo user) {
        Log.d("DEBUG", "(VIEW) MainActivity: friendUpdated...Llamando a User Adapter");
        mUserAdapter.update(user);

    }

    @Override
    public void friendRemoved(UserPojo user) {
        Log.d("DEBUG", "(VIEW) MainActivity: friendRemoved...Llamando a User Adapter");
        mUserAdapter.remove(user);
    }

    @Override
    public void requestAdded(UserPojo username) {
        Log.d("DEBUG", "(VIEW) MainActivity: requestAdded...Llamando a Request Adapter");
        mRequestAdapter.add(username);
    }

    @Override
    public void requestUpdated(UserPojo user) {
        Log.d("DEBUG", "(VIEW) MainActivity: requestUpdated...Llamando a Request Adapter");
        mRequestAdapter.update(user);
    }

    @Override
    public void requestRemoved(UserPojo user) {
        Log.d("DEBUG", "(VIEW) MainActivity: requestRemoved...Llamando a Request Adapter");
        mRequestAdapter.remove(user);
    }

    @Override
    public void showRequestAccepted(String username) {
        Log.d("DEBUG", "(VIEW) MainActivity: showRequestAccepted...");
        Snackbar.make(contentMain, getString(R.string.main_message_request_accepted, username),
                Snackbar.LENGTH_SHORT).show();

    }

    @Override
    public void showRequestDenied() {
        Log.d("DEBUG", "(VIEW) MainActivity: showRequestDenied..");
        Snackbar.make(contentMain, getString(R.string.main_message_request_denied),
                Snackbar.LENGTH_SHORT).show();

    }

    @Override
    public void showFriendRemoved() {
        Log.d("DEBUG", "(VIEW) MainActivity: showFriendRemoved ...");
        Snackbar.make(contentMain, getString(R.string.main_message_user_removed),
                Snackbar.LENGTH_SHORT).show();

    }

    @Override
    public void showError(int resMsg) {
        Log.d("DEBUG", "(VIEW) MainActivity: showError ");
        Snackbar.make(contentMain, resMsg, Snackbar.LENGTH_SHORT).show();

    }


    /*
     *      Metodos implementados de OnItemClickListener
     * */
    @Override
    public void onItemClick(UserPojo user) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(UserPojo.UID, user.getUid());
        intent.putExtra(UserPojo.USERNAME, user.getUsername());
        intent.putExtra(UserPojo.EMAIL, user.getEmail());
        intent.putExtra(UserPojo.PHOTO_URL, user.getPhotoUrl());

        if (UtilsCommon.hasMaterialDesign()) {
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        } else {
            startActivity(intent);
        }


    }

    @Override
    public void onItemLongClick(final UserPojo user) {
        new AlertDialog.Builder(this, R.style.DialogFragmentTheme)
                .setTitle(getString(R.string.main_dialog_title_confirmDelete))
                .setMessage(String.format(Locale.ROOT, getString(R.string.main_dialog_message_confirmDelete),
                        user.getUsernameValid()))
                .setPositiveButton(R.string.main_dialog_accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPresenter.removeFriend(user.getUid());
                    }
                })
                .setNegativeButton(R.string.common_label_cancel, null)
                .show();

    }

    @Override
    public void onAcceptRequest(UserPojo user) {
        Log.d("DEBUG", "(VIEW) MainActivity: onAcceptRequest ");
        mPresenter.acceptRequest(user);
    }

    @Override
    public void onDenyRequest(UserPojo user) {
        Log.d("DEBUG", "(VIEW) MainActivity: onDenyRequest");
        mPresenter.denyRequest(user);
    }

    //Cada vez que el Usuario entre a la bandeja de contactos se borren las notificaciones push
    //que hayan llegado
    private void clearNotification() {
        Log.d("DEBUG", "(VIEW) MainActivity: clearNotification() ");
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancelAll();
        }

    }

    //Boton de Agregar nuevo amigo
    @OnClick(R.id.fab)
    public void onAddClicked() {
        new AddFragment().show(getSupportFragmentManager(), getString(R.string.addFriend_title));

    }
}
