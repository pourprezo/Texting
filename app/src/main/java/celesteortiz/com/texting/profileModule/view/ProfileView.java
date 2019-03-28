package celesteortiz.com.texting.profileModule.view;

import android.content.Intent;

public interface ProfileView {
    void enableUIElements();
    void disableUIElements();

    void showProgress();
    void hideProgress();

    void showProgressImage();
    void hideProgressImage();

    void showUserData(String username, String email, String photoUrl );
    void launchGallery();
    //Previsualizar la imagen seleccionada para confirmar si la queremos enviar
    // o no al servidor
    void openDialogPreview(Intent data);

    //Comportamiento cuando se este en modo edicion o normal
    void menuEditMode();
    void menuNormalMode();

    //Eventos cuando se haya subido bien la imagen
    void updateImageSuccess(String photoUrl);

    void saveUserNameSuccess();

    void setResultOk(String username, String photoUrl);

    void onErrorUpload(int resMsg);
    void onError(int resMsg);

}
