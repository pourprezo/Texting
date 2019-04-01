package celesteortiz.com.texting.chatModule.model.dataAccess;


import android.app.Activity;
import android.net.Uri;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import celesteortiz.com.texting.R;
import celesteortiz.com.texting.common.model.StorageUploadImageCallback;
import celesteortiz.com.texting.common.model.dataAccess.FirebaseStorageAPI;
import celesteortiz.com.texting.profileModule.model.dataAccess.Storage;

public class StorageChat {
    //Definicion de constante que representara a la carpeta de chats en Storage
    private static final String PATH_CHATS="chats";

    private FirebaseStorageAPI mStorageAPI;

    public StorageChat(){
        mStorageAPI = FirebaseStorageAPI.getInstance();
    }

    /* Adjuntar imagen / subir imagen a Storage y recibir imagen de descarga*/
    public void uploadImageChat(Activity activity, final Uri imageUri, String myEmail, final StorageUploadImageCallback callback){
        if(imageUri.getLastPathSegment() != null){
            StorageReference photoRef = mStorageAPI.getPhotosReferenceByEmail(myEmail).child(PATH_CHATS)
                    .child(imageUri.getLastPathSegment());
            photoRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    taskSnapshot.getStorage().getDownloadUrl()
                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    //Obtener la url de descarga para pasarla a nuestro cargador
                                    //al dataAccess solo le interesa definirlo al interactor?
                                    if(uri != null){
                                        callback.onSuccess(uri);
                                    }else {
                                        callback.onError(R.string.chat_error_imageUpload);
                                    }
                                }
                            });
                }
            });
        }

    }
}
