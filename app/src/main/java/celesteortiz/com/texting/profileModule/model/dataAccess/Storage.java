package celesteortiz.com.texting.profileModule.model.dataAccess;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import celesteortiz.com.texting.R;
import celesteortiz.com.texting.common.model.StorageUploadImageCallback;
import celesteortiz.com.texting.common.model.dataAccess.FirebaseStorageAPI;

public class Storage {
    private static final String PATH_PROFILE = "profile";
    private FirebaseStorageAPI mStorageAPI;

    public Storage() {
        mStorageAPI = FirebaseStorageAPI.getInstance();
    }

    /*Necesitamos cambiar la URL porque estamos usando Glide y Glide con su cache, mientras no vea
    que cambia la URL no volvera a cambiar una nueva imagen desde internet */

    //Subir Imagen y obtener la url de la nueva imagen
    public void uploadImageProfile(Activity activity, Uri imageUri, String email,
                                   final StorageUploadImageCallback callback){

        if(imageUri.getLastPathSegment() != null){

            final StorageReference photoRef = mStorageAPI.getPhotosReferenceByEmail(email)
                    .child(PATH_PROFILE).child(imageUri.getLastPathSegment());

            photoRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //En caso de Success vamos a conseguir la Url de descarga para seguir con el proceso
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            if(uri != null){
                                //Si la uri no es nula, la devolvemos
                                callback.onSuccess(uri);
                            }else {
                                callback.onError(R.string.profile_error_imageUpdated);
                            }
                        }
                    });
                }
            });

        }else{
            callback.onError(R.string.profile_error_invalid_image);
        }

    }

    //Eliminar imagen de perfil antigua
    public void deleteOldImage(String oldPhotoUrl, String downloadUrl){
        if (oldPhotoUrl != null && !oldPhotoUrl.isEmpty()){
            //Referencia para la foto actual de perfil
            StorageReference storageReference = mStorageAPI.getmFirebaseStorage().getReferenceFromUrl(downloadUrl);
            //Referencia para la foto antigua de perfil
            StorageReference oldStorageReference = null;
            try {
                oldStorageReference = mStorageAPI.getmFirebaseStorage().getReferenceFromUrl(oldPhotoUrl);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //Comparar que la nueva y antigua no sean iguales y ahora eliminar
            if (oldStorageReference != null && !oldStorageReference.getPath().equals(storageReference.getPath())){
                oldStorageReference.delete().addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }
        }
    }
}
