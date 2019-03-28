package celesteortiz.com.texting.loginModule.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import celesteortiz.com.texting.R;
import celesteortiz.com.texting.loginModule.LoginPresenter;
import celesteortiz.com.texting.loginModule.LoginPresenterImpl;
import celesteortiz.com.texting.mainModule.view.MainActivity;

/*
 * Project: Texting
 * Created by Celeste Ortiz on 15/03/2019
 * LoginActivity
 * */
public class LoginActivity extends AppCompatActivity implements LoginView{

    public static final int RC_SIGN_IN = 21;
    @BindView(R.id.tvMessage)
    TextView tvMessage;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private LoginPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mPresenter = new LoginPresenterImpl(this);
        mPresenter.onCreate();
        //Inmediatamente verificar si el usuario esta o no autenticado
        mPresenter.getStatusAuth();
    }

    /*
    * Metodos sobre escritos de la clase Activity
     * */
    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPresenter.result(requestCode, resultCode, data);
    }

    /*
     * Metodos implementados de LoginView
     * */

    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void openMainActivity() {
        Log.d("DEBUG_C", "View/openMainActivity()");
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void openUILogin() {
        AuthUI.IdpConfig googleIdp = new AuthUI.IdpConfig.GoogleBuilder().build();

        startActivityForResult(AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setIsSmartLockEnabled(false)
                .setTosAndPrivacyPolicyUrls("www.policy.cusros-android-ant.com",
                        "www.privacity.cusros-android-ant.com")
                .setAvailableProviders(Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build(),
                        googleIdp))
                .setTheme(R.style.BaseTheme)
                .setLogo(R.drawable.web_hi_res_512)
                .build(), RC_SIGN_IN);

    }

    @Override
    public void showLoginSuccessfully(Intent data) {
        Log.d("DEBUG_C", "View/showLoginSuccessfully() ");

        IdpResponse response = IdpResponse.fromResultIntent(data);
        String email = "";

        if(response != null){
            email = response.getEmail();
        }
        Toast.makeText(this, getString(R.string.login_message_success, email),
                Toast.LENGTH_SHORT).show();

        Log.d("DEBUG_C", "View/showLoginSuccessfully() Bienvenido" + email);
    }

    @Override
    public void showMessageStarting() {
        Log.d("DEBUG_C", "View/showMessageStarting() Iniciando sesion...");
        tvMessage.setText(R.string.login_message_loading);

    }

    @Override
    public void showError(int resMsg) {
        Toast.makeText(this, resMsg, Toast.LENGTH_LONG).show();


    }



}
