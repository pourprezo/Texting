package celesteortiz.com.texting.common.utils;

import android.content.Context;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.FragmentActivity;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.regex.Pattern;

import celesteortiz.com.texting.R;
import celesteortiz.com.texting.mainModule.view.MainActivity;
import de.hdodenhof.circleimageview.CircleImageView;

public class UtilsCommon {
    /*
    * Codificar un correo electronico
    * */
    public static String getEmailEncoded(String email){
        String preKey = email.replace("_", "__");
        return preKey.replace(".", "_");
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
}
