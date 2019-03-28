package celesteortiz.com.texting.mainModule.model.dataAccess;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import celesteortiz.com.texting.R;
import celesteortiz.com.texting.common.model.BasicEventsCallback;
import celesteortiz.com.texting.common.model.dataAccess.FirebaseRealtimeDatabaseAPI;
import celesteortiz.com.texting.common.pojo.UserPojo;
import celesteortiz.com.texting.common.utils.UtilsCommon;

public class RealtimeDatabase {
    private FirebaseRealtimeDatabaseAPI mDatabaseAPI;

    private ChildEventListener mUserEventListener;
    private ChildEventListener mRequestEventListener;

    public RealtimeDatabase() {
        mDatabaseAPI=FirebaseRealtimeDatabaseAPI.getInstance();
    }

    /*
     * References
     * */
    public FirebaseRealtimeDatabaseAPI getmDatabaseAPI() {
        return mDatabaseAPI;
    }

    private DatabaseReference getUsersReference(){
        return mDatabaseAPI.getRootReference().child(FirebaseRealtimeDatabaseAPI.PATH_USERS);
    }

    /*
    * Public methods
    * */

    //Obtener lista de contactos de un Usuario
    public void subscribeToUserList(String myUid, final UserEventListener listener){
        Log.d("DEBUG", " * * * (MAIN) RealtimeDatabase:     Suscribiendo a Lista de ContactosFragment...");

        if(mUserEventListener == null){
            Log.d("DEBUG", " * * * (MAIN) RealtimeDatabase:     mUserEventListener is NULL ");

            mUserEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Log.d("DEBUG", " * * * (MAIN) RealtimeDatabase:                   .onChildAdded() ");
                    listener.onUserAdded(getUser(dataSnapshot));
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Log.d("DEBUG", " * * * (MAIN) RealtimeDatabase:                   .onChildChanged() ");
                    listener.onUserUpdated(getUser(dataSnapshot));
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    Log.d("DEBUG", " * * * (MAIN) RealtimeDatabase:                   .onChildRemoved() ");
                    listener.onUserRemoved(getUser(dataSnapshot));
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d("DEBUG", " * * * (MAIN) RealtimeDatabase:                   .onCancelled() " + databaseError);
                    switch (databaseError.getCode()){
                        case DatabaseError.PERMISSION_DENIED:
                            listener.onError(R.string.main_error_permission_denied);
                            break;
                        default:
                            listener.onError(R.string.common_error_server);
                            break;
                    }
                }
            };
        }

        Log.d("DEBUG", " * * * (MAIN) RealtimeDatabase:                                            y  Anadiendo EventListener ");
        mDatabaseAPI.getContactsReference(myUid).addChildEventListener(mUserEventListener);

    }

    private UserPojo getUser(DataSnapshot dataSnapshot) {
        Log.d("DEBUG", " * * * (MAIN) RealtimeDatabase:     Obtener usuario....");
        Log.d("DEBUG", " * * * (MAIN) RealtimeDatabase:     dataSnapshot: " + dataSnapshot);

        UserPojo user = dataSnapshot.getValue(UserPojo.class);

        if(user != null){

            user.setUid(dataSnapshot.getKey());
        }

        Log.d("DEBUG", " * * * (MAIN) RealtimeDatabase:                          El usuario es: " +user);
        return user;
    }

    //Obtener lista de Solicitudes
    public void subscribeToRequests(String email, final UserEventListener listener){
        Log.d("DEBUG", " * * * (MAIN) RealtimeDatabase:     Suscribiendo a Solicitudes...");

        if(mRequestEventListener == null){
            mRequestEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Log.d("DEBUG", " * * * (MAIN) RealtimeDatabase:     Suscribiendo a Solicitudes: onChildAdded ");
                    listener.onUserAdded(getUser(dataSnapshot));
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Log.d("DEBUG", " * * * (MAIN) RealtimeDatabase:     Suscribiendo a Solicitudes: onChildChanged ");
                    listener.onUserUpdated(getUser(dataSnapshot));
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    Log.d("DEBUG", " * * * (MAIN) RealtimeDatabase:     Suscribiendo a Solicitudes: onChildRemoved ");
                    listener.onUserRemoved(getUser(dataSnapshot));
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d("DEBUG", " * * * (MAIN) RealtimeDatabase:     Suscribiendo a Solicitudes: onCancelled ");
                    listener.onError(R.string.common_error_server);
                }
            };
        }
        final String emailEncoded= UtilsCommon.getEmailEncoded(email);

        Log.d("DEBUG", " * * * (MAIN) RealtimeDatabase:                             y anadiendo Event Listener");
        mDatabaseAPI.getRequestReference(emailEncoded).addChildEventListener(mRequestEventListener);

    }

    public void unsubscribeToUsers(String uid){
        if(mUserEventListener != null){
            Log.d("DEBUG", " * * * (MAIN) RealtimeDatabase:     Des suscribiendo de ContactosFragment y removiendo Event Listener");
            mDatabaseAPI.getContactsReference(uid).removeEventListener(mUserEventListener);
        }
    }

    public void unsubscribeToRequests(String email){
        Log.d("DEBUG", " * * * (MAIN) RealtimeDatabase:     Dessuscribiendo de Solicitudes.... email : "+ email);
        if(mRequestEventListener != null){
            final String emailEncoded= UtilsCommon.getEmailEncoded(email);
            mDatabaseAPI.getRequestReference(emailEncoded).removeEventListener(mRequestEventListener);
        }

    }

    /*
    * Metodos para remover Usuarios, Aceptar o Rechazar Solicitudes
    * */
    public void removeUser(String friendUid, String myUid, final BasicEventsCallback callback){
        Log.d("DEBUG", " * * * (MAIN) RealtimeDatabase:     Removiendo amigo .....");

        Map<String, Object> removeUserMap = new HashMap<>();
        //Eliminar amigo de lista de contactos de un usuario
        removeUserMap.put(myUid+"/"+FirebaseRealtimeDatabaseAPI.PATH_CONTACTS+"/"+ friendUid, null);
        //Eliminar al usuario de la lista de contactos de su amigo
        removeUserMap.put(friendUid+"/"+FirebaseRealtimeDatabaseAPI.PATH_CONTACTS+"/"+ myUid, null);

        //Realizar la actualizacion y comunicar si fue exitoso o fallido
        getUsersReference().updateChildren(removeUserMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if(databaseError == null){
                    Log.d("DEBUG", " * * * (MAIN) RealtimeDatabase:     Se removio amigo con Exito.....");
                    callback.onSuccess();
                }else{
                    Log.d("DEBUG", " * * * (MAIN) RealtimeDatabase:     Error No se pudo remover amigo....." + databaseError);
                    callback.onError();
                }
            }
        });
    }

    //Aceptar Solicitud
    public void acceptRequest(UserPojo user, UserPojo myUser, final BasicEventsCallback callback){
        Log.d("DEBUG", " * * * (MAIN) RealtimeDatabase:     Aceptando Solicitud.....");

       //Recolectar los datos del amigo
        Map<String, String> userRequestMap = new HashMap<>();
        userRequestMap.put(UserPojo.USERNAME, user.getUsername());
        userRequestMap.put(UserPojo.EMAIL, user.getEmail());
        userRequestMap.put(UserPojo.PHOTO_URL, user.getPhotoUrl());

        //Recolectar los datos de nuestro usuario
        Map<String, String> myUserMap = new HashMap<>();
        myUserMap.put(UserPojo.USERNAME, myUser.getUsername());
        myUserMap.put(UserPojo.EMAIL, myUser.getEmail());
        myUserMap.put(UserPojo.PHOTO_URL, myUser.getPhotoUrl());

        //Codificamos nuesto correo
        final String emailEncoded = UtilsCommon.getEmailEncoded(myUser.getEmail());

        Map<String, Object> acceptRequest = new HashMap<>();


            //Agregar a mis contactos al usuario que solicita amistad
            acceptRequest.put(FirebaseRealtimeDatabaseAPI.PATH_USERS +"/"+ myUser.getUid() +"/"+
                    FirebaseRealtimeDatabaseAPI.PATH_CONTACTS +"/"+ user.getUid(), userRequestMap);

            //Agregarme como contacto de el usuario que solicita  amistad
            acceptRequest.put(FirebaseRealtimeDatabaseAPI.PATH_USERS +"/"+ user.getUid() +"/"+
                    FirebaseRealtimeDatabaseAPI.PATH_CONTACTS +"/"+ myUser.getUid(), myUserMap);


            //Eliminar la solicitud de amistad
            acceptRequest.put(FirebaseRealtimeDatabaseAPI.PATH_REQUESTS +"/"+ emailEncoded +"/"+
                    user.getUid(), null);

            mDatabaseAPI.getRootReference().updateChildren(acceptRequest, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if(databaseError == null){
                        Log.d("DEBUG", " * * * (MAIN) RealtimeDatabase:     Se acepto solicitud con Exito.....");
                        callback.onSuccess();
                    }else{
                        Log.d("DEBUG", " * * * (MAIN) RealtimeDatabase:     Error: No se pudo aceptar la solicitud....." + databaseError);
                        callback.onError();
                    }
                }
            });

    }

    //Rechazar solicitud
    public void denyRequest(UserPojo user, String myEmail, final BasicEventsCallback callback){
        Log.d("DEBUG", " * * * (MAIN) RealtimeDatabase:     Rechazando Solicitud.....");
        Log.d("DEBUG", "                 Usuario: "+ user.getUid() + "My Email: " + myEmail);

        final String emailEncoded = UtilsCommon.getEmailEncoded(myEmail);

        mDatabaseAPI.getRequestReference(emailEncoded).child(user.getUid())
                .removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        if(databaseError == null){
                            Log.d("DEBUG", " * * * (MAIN) RealtimeDatabase:     Se rechazo solicitud con Exito.....");
                            callback.onSuccess();
                        }else{
                            Log.d("DEBUG", " * * * (MAIN) RealtimeDatabase:     Error: No se pudo rechazar la solicitud....." + databaseError);
                            callback.onError();
                        }
                    }
                });

    }

}
