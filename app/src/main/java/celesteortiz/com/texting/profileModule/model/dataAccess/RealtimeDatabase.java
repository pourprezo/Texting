package celesteortiz.com.texting.profileModule.model.dataAccess;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import celesteortiz.com.texting.R;
import celesteortiz.com.texting.common.model.StorageUploadImageCallback;
import celesteortiz.com.texting.common.model.dataAccess.FirebaseRealtimeDatabaseAPI;
import celesteortiz.com.texting.common.pojo.UserPojo;

public class RealtimeDatabase {
    private FirebaseRealtimeDatabaseAPI mDatabaseAPI;

    public RealtimeDatabase(){
        mDatabaseAPI = FirebaseRealtimeDatabaseAPI.getInstance();
    }
    
    public void changeUsername(final UserPojo myUser, final UpdateUserListener listener){
        if (mDatabaseAPI.getUserReferenceByUID(myUser.getUid()) != null){
            Map<String, Object> updates = new HashMap<>();
            updates.put(UserPojo.USERNAME, myUser.getUsername());
            
            mDatabaseAPI.getUserReferenceByUID(myUser.getUid()).updateChildren(updates)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            listener.onSucess();
                            notifyUsernameChangeToContacts(myUser, listener);
                        }
                    });
        }
    }

    private void notifyUsernameChangeToContacts(final UserPojo myUser, final UpdateUserListener listener) {
        mDatabaseAPI.getContactsReference(myUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Hasta este punto ya tendremos la lista de nuestros contctos para notificarles
                for (DataSnapshot child : dataSnapshot.getChildren()){
                    String friendUid = child.getKey();
                    DatabaseReference reference = getContactsReference(friendUid, myUser.getUid());
                    Map<String, Object> updates = new HashMap<>();
                    //Actualizar nuestro Username en el contacto (nuestro) de nuestro(s) amigo(s)
                    updates.put(UserPojo.USERNAME, myUser.getUsername());
                    reference.updateChildren(updates);
                }
                listener.onNotifyContacts();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onError(R.string.profile_error_userUpdated);

            }
        });
    }

    private DatabaseReference getContactsReference(String mainUid, String childUid) {
        //Retornamos /users/{uid Amigo}/contacts/{nuestro Uid}
        return mDatabaseAPI.getUserReferenceByUID(mainUid)
                .child(FirebaseRealtimeDatabaseAPI.PATH_CONTACTS).child(childUid);
    }

    public void updatePhotoUrl(final Uri downloadUri, final String myUid, final StorageUploadImageCallback callback){
        if(mDatabaseAPI.getUserReferenceByUID(myUid) != null){
            Map<String, Object> updates = new HashMap<>();
            updates.put(UserPojo.PHOTO_URL, downloadUri.toString());

            mDatabaseAPI.getUserReferenceByUID(myUid).updateChildren(updates)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        //Notificar que la actualizacion fue correcta
                        @Override
                        public void onSuccess(Void aVoid) {
                            //La que utilizara Authentication para actualiar el perfil de Firebase user
                            callback.onSuccess(downloadUri);
                            //Notificar a los Contactos que he actualizado mi foto
                            notifyPhotoUpdateToContacts(downloadUri.toString(), myUid, callback);
                        }
                    });
        }
    }

    private void notifyPhotoUpdateToContacts(final String photoUrl, final String myUid, final StorageUploadImageCallback callback) {
        mDatabaseAPI.getContactsReference(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Hasta este punto ya tendremos la lista de nuestros contctos para notificarles uno por uno
                for (DataSnapshot child : dataSnapshot.getChildren()){
                    String friendUid = child.getKey();
                    DatabaseReference reference = getContactsReference(friendUid, myUid);

                    Map<String, Object> updates = new HashMap<>();
                    //Actualizar nuestra photourl en el contacto (nuestro) de nuestro(s) amigo(s)
                    updates.put(UserPojo.PHOTO_URL, photoUrl);
                    reference.updateChildren(updates);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError(R.string.profile_error_imageUpdated);

            }
        });
    }

}
