package celesteortiz.com.texting.common.utils;

import android.content.Context;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.AppCompatEditText;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import celesteortiz.com.texting.R;

public class UtilsCommon {
    /*
    * Codificar un correo electronico
    * */
    public static String getEmailEncoded(String email){
        String preKey = email.replace("_", "__");
        return preKey.replace(".", "_");
    }

    //Este metodo servira para la hora de Suscribirnos o desuscribirnos a un topico para recibir notificaciones.
    // Primero Codificar nuestro correo
    //despues Eliminar el arroba
    public static String getEmailToTopic(String email){
        String topic = getEmailEncoded(email);
        topic = topic.replace("@", "64");
        return topic;

    }

    //Cargar imagenes basicas con Glide
    public static void loadImage(Context context, String photoUrl, ImageView target) {
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .error(R.drawable.ic_emoticon_sad)
                .placeholder(R.drawable.ic_emoticon_tongue);

        Glide.with(context)
                .load(photoUrl)
                .apply(options)
                .into(target);
    }

    public static boolean validateEmail(Context context, EditText etEmail) {
        boolean isValid = true;

        String email = etEmail.getText().toString().trim();

        if(email.isEmpty()){
            etEmail.setError(context.getString(R.string.common_validate_field_required));
            etEmail.requestFocus();
            isValid=false;

        }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            //Patterns : Utilidad del SDK de Android para revisar que el correo sea valido
            etEmail.setError(context.getString(R.string.common_validate_email_invalid));
            etEmail.requestFocus();
            isValid=false;

        }
        return isValid;

    }
    /* Verificacion de versiones */
    public static boolean hasMaterialDesign() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    /* Mostrar mensajes con Snackbar */
    public static void showSnackbar(View contentMain, int resMsg) {
        showSnackbar(contentMain, resMsg, Snackbar.LENGTH_SHORT);
    }

    public static void showSnackbar(View contentMain, int resMsg, int duration) {
        Snackbar.make(contentMain, resMsg, duration).show();
    }

    public static boolean validateMessage(EditText etMessage) {
        return etMessage.getText() != null &&
                !etMessage.getText().toString().trim().isEmpty();
    }
}
