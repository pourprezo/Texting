package celesteortiz.com.texting.addModule.view;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import celesteortiz.com.texting.addModule.AddPresenter;
import celesteortiz.com.texting.addModule.AddPresenterImpl;
import celesteortiz.com.texting.R;
import celesteortiz.com.texting.common.utils.UtilsCommon;

/*
 * Project: Texting
 * Created by Celeste Ortiz on 18/03/2019
 *
 * AddFragment class
 * Modulo para agregar amigos / enviar solicitudes
 **/
public class AddFragment extends DialogFragment implements DialogInterface.OnShowListener, AddView {

    @BindView(R.id.etEmail)
    TextInputEditText etEmail;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.contentMainAdd)
    FrameLayout contentMainAdd;
    Unbinder unbinder;

    private Button positiveButton;

    private AddPresenter mPresenter;


    public AddFragment() {
        // Required empty public constructor
        mPresenter = new AddPresenterImpl(this);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Log.d("DEBUG", "(View) AddFragment:   onCreateDialog() Creando AlertDialog....");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
        .setTitle(R.string.addFriend_title)
        .setPositiveButton(R.string.common_label_accept, null)
        .setNeutralButton(R.string.common_label_cancel, null);

        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_add, null);
        builder.setView(view);
        unbinder = ButterKnife.bind(this, view);

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(this);
        return dialog;

    }

    @Override
    public void onShow(DialogInterface dialogInterface) {
        Log.d("DEBUG", "(View) AddFragment:   onShow()");

        final AlertDialog dialog = (AlertDialog)getDialog();

        if(dialog != null){
            positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE);
            //negativeBUtton no se crea global para que no se desabilite
            Button negativeButton = dialog.getButton(Dialog.BUTTON_NEGATIVE);

            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(UtilsCommon.validateEmail(getActivity(), etEmail)){

                        Log.d("DEBUG", "(View) AddFragment:   Positive button...Llamando a Presenter.addFriend");
                        mPresenter.addFriend(etEmail.getText().toString().trim());

                    }
                }
            });

            negativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("DEBUG", "(View) AddFragment:   Negative button...Cerrando dialogo");
                    dismiss();
                }
            });

        }
        //Registrarse en eventBus
        mPresenter.onShow();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("DEBUG", "(View) AddFragment:   onDestroy...Llamando a Presenter...");

        mPresenter.onDestroy();
    }

    /*
    * Metodos implementatod de la interfaz  AddView
    * */
    @Override
    public void enableUIElements() {
        etEmail.setEnabled(true);
        positiveButton.setEnabled(true);
    }

    @Override
    public void disableUIElements() {
        etEmail.setEnabled(false);
        positiveButton.setEnabled(false);
    }

    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void friendAdded() {
        Toast.makeText(getActivity(), R.string.addFriend_message_request_dispatched, Toast.LENGTH_SHORT).show();
        dismiss();
    }

    @Override
    public void friendshipExists() {
        Toast.makeText(getActivity(), R.string.addFriend_message_friendship_exists, Toast.LENGTH_SHORT).show();
        etEmail.requestFocus();
    }
    @Override
    public void friendNotAdded() {
       etEmail.setError(getString(R.string.addFriend_error_message));
       etEmail.requestFocus();


    }
}
