package celesteortiz.com.texting.addModule.model.dataAccess;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

import celesteortiz.com.texting.common.model.BasicEventsCallback;
import celesteortiz.com.texting.common.model.dataAccess.FirebaseRealtimeDatabaseAPI;
import celesteortiz.com.texting.common.pojo.UserPojo;
import celesteortiz.com.texting.common.utils.UtilsCommon;

public class RealtimeDatabase {
    private FirebaseRealtimeDatabaseAPI mDatabaseAPI;

    public RealtimeDatabase() {
        mDatabaseAPI = FirebaseRealtimeDatabaseAPI.getInstance();
    }

    public void addFriend(String email, UserPojo myUser, final BasicEventsCallback callback){
        Map<String, Object> myUserMap = new HashMap<>();

        //Extraer mi datos y guardarlos en un HashMap para enviarlos por Solicitud
        myUserMap.put(UserPojo.USERNAME, myUser.getUsername());
        myUserMap.put(UserPojo.EMAIL, myUser.getEmail());
        myUserMap.put(UserPojo.PHOTO_URL, myUser.getPhotoUrl());

        //Codificamos el email para que sea nuestra key
        final String emailEncoded = UtilsCommon.getEmailEncoded(email);

        //Obtener la referencia de las solicitudes de mi amigo /requests/{email}
        DatabaseReference userReference = mDatabaseAPI.getRequestReference(emailEncoded);

        //Buscar mi usurio en la referencia de mi amigo y actualizar datos
        userReference.child(myUser.getUid()).updateChildren(myUserMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callback.onSuccess();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onError();
                    }
                });
    }

    public void isFriend(final String emailToAdd, UserPojo myUser, final BasicEventsCallback callback){

        //Obtener la referencia de las solicitudes de mi amigo /requests/{email}
        DatabaseReference myContactsRef = mDatabaseAPI.getContactsReference(myUser.getUid());

        myContactsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot contact : dataSnapshot.getChildren()){
                    DataSnapshot emailContact = contact.child("email");

                    //Si el correo que quiero agregar es igual al correo del amigo que se esta recorriendo en turno, entonces enviar Listener
                    if(emailContact.getValue().toString().equals(emailToAdd)){
                        callback.onError();
                        Log.d("DEBUG", "RealtimeDatabase:     Ya existe amigo en lista de contactos... ");
                        break;
                    }else{
                        callback.onSuccess();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


}
