package celesteortiz.com.texting.chatModule.model.dataAccess;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bumptech.glide.util.Util;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import celesteortiz.com.texting.R;
import celesteortiz.com.texting.chatModule.model.LastConnectionEventListener;
import celesteortiz.com.texting.chatModule.model.MessageEventListener;
import celesteortiz.com.texting.chatModule.model.SendMessagesListener;
import celesteortiz.com.texting.common.Constants;
import celesteortiz.com.texting.common.model.dataAccess.FirebaseRealtimeDatabaseAPI;
import celesteortiz.com.texting.common.pojo.Message;
import celesteortiz.com.texting.common.pojo.UserPojo;
import celesteortiz.com.texting.common.utils.UtilsCommon;
import celesteortiz.com.texting.profileModule.model.dataAccess.RealtimeDatabase;

public class RealtimeDatabaseChat {
    private static final String PATH_CHATS = "chats";
    private static final String PATH_MESSAGES = "messages";

    private FirebaseRealtimeDatabaseAPI mDatabaseAPI;

    private ChildEventListener mMessagesEventListener;
    private ValueEventListener mFriendProfileListener;

    public RealtimeDatabaseChat(){
        mDatabaseAPI = FirebaseRealtimeDatabaseAPI.getInstance();
    }

    public FirebaseRealtimeDatabaseAPI getmDatabaseAPI(){
        return mDatabaseAPI;
    }

    public void subscribeToMessages(String myEmail, String friendEmail, final MessageEventListener listener){
        Log.d("DEBUG CHAT", "Realtime Database: subscribeToMessages");
        if(mMessagesEventListener == null){
            Log.d("DEBUG CHAT", "Realtime Database: mMessagesEventListener == null");
            mMessagesEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Log.d("DEBUG CHAT", "Realtime Database: onChildAdded()");
                    listener.onMessageReceived(getMessage(dataSnapshot));
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    switch (databaseError.getCode()){
                        case DatabaseError.PERMISSION_DENIED:
                            listener.onError(R.string.chat_error_permission_denied);
                            break;
                        default:
                            listener.onError(R.string.common_error_server);
                            break;
                    }
                }
            };
        }
        Log.d("DEBUG CHAT", "Realtime Database: mMessagesEventListener no es null");
        Log.d("DEBUG CHAT", "Realtime Database: addChildEventListener(mMessagesEventListener)");
        getChatMessagesReference(myEmail, friendEmail).addChildEventListener(mMessagesEventListener);
    }

    private DatabaseReference getChatMessagesReference(String myEmail, String friendEmail) {
        return getChatReference(myEmail, friendEmail).child(PATH_MESSAGES);

    }

    private DatabaseReference getChatReference(String myEmail, String friendEmail) {
        String myEmailEncoded = UtilsCommon.getEmailEncoded(myEmail);
        String friendEmailEncoded = UtilsCommon.getEmailEncoded(friendEmail);

        //Nodo unico para la conversacion de dos personas a partir de su correo
        String keyChat = myEmailEncoded + FirebaseRealtimeDatabaseAPI.SEPARATOR + friendEmailEncoded;

        //Ordenar alfabeticamente los correos, de este modo habra unestandar y todos accederan
        //exactamente al mismo nodo que quieran acceder, sin importar quien inicie la conversacion
        if(myEmailEncoded.compareTo(friendEmailEncoded) > 0){
            keyChat = friendEmailEncoded + FirebaseRealtimeDatabaseAPI.SEPARATOR + myEmailEncoded;

        }
        return mDatabaseAPI.getRootReference().child(PATH_CHATS).child(keyChat);
    }

    private Message getMessage(DataSnapshot dataSnapshot) {
        Log.d("DEBUG CHAT", "Realtime Database: getMessage()");
        Message message = dataSnapshot.getValue(Message.class);

        if(message != null){
            message.setUid(dataSnapshot.getKey());
            Log.d("DEBUG CHAT", "Realtime Database: message = "+ message);

        }
        return message;
    }

    public void unSubscribToMessages(String myEmail, String friendEmail){
        Log.d("DEBUG CHAT", "Realtime Database: unSubscribToMessages()");
        if(mMessagesEventListener != null){
            Log.d("DEBUG CHAT", "Realtime Database: mMessagesEventListener != null, Removiendo....");
            getChatMessagesReference(myEmail, friendEmail).removeEventListener(mMessagesEventListener);
        }

    }

    //Suscribirse al estado de un amigo, se llama cuando se abre una conversacion
    public void subscribeToFriend(String uid, final LastConnectionEventListener listener){
        Log.d("DEBUG CHAT", "Realtime Database: subscribeToFriend()");
        if(mFriendProfileListener == null){
            Log.d("DEBUG CHAT", "Realtime Database: mFriendProfileListener is Null");
            mFriendProfileListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.d("DEBUG CHAT", "Realtime Database: mFriendProfileListener onDataChange");

                    long lastConnectionFriend = 0;
                    String uidConnectedFriend = "";

                    //1er caso: Exraer la ultima conexion
                    try{
                       Long value = dataSnapshot.getValue(Long.class);
                       if(value != null){
                           lastConnectionFriend = value;
                       }
                    } catch(Exception e){
                        e.printStackTrace();
                        //2er caso: Exraer la ultima conexion y con quien se ha conectado el usuario
                        String lastConnectionWith = dataSnapshot.getValue(String.class);
                        if(lastConnectionWith != null && !lastConnectionWith.isEmpty()){
                            String[] values = lastConnectionWith.split(FirebaseRealtimeDatabaseAPI.SEPARATOR);

                            if(values.length > 0){
                                lastConnectionFriend = Long.valueOf(values[0]);

                                if(values.length > 1){
                                    uidConnectedFriend = values[1];
                                }
                            }
                        }
                    }

                    listener.onSuccess(lastConnectionFriend == Constants.ONLINE_VALUE,
                            lastConnectionFriend, uidConnectedFriend);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            };
        }

        Log.d("DEBUG CHAT", "Realtime Database: mFriendProfileListener is not Null");

        ////(offline) Con esto le decimos a Firebase Realtime Database que esa queremos que no se habilite
        // la persistencia de datos, y que queremos que siempre se vaya a consultar al servidor
        mDatabaseAPI.getUserReferenceByUID(uid).child(UserPojo.LAST_CONNECTION_WITH).keepSynced(true);

        mDatabaseAPI.getUserReferenceByUID(uid).child(UserPojo.LAST_CONNECTION_WITH)
                .addValueEventListener(mFriendProfileListener);

    }

    //Dessucribirnos del estado de nuestro amigo
    public void unsubscribeToFriend(String uid){
        Log.d("DEBUG CHAT", "Realtime Database: unsubscribeToFriend()");
        if(mFriendProfileListener != null){
            Log.d("DEBUG CHAT", "Realtime Database: mFriendProfileListener is not Null...Removing listener");
            mDatabaseAPI.getUserReferenceByUID(uid).child(UserPojo.LAST_CONNECTION_WITH)
                    .removeEventListener(mFriendProfileListener);
        }
    }


    /*
    *   READ / UNREAD MESSAGES
    * */

    //Resetear el numero de mensajes leidos por nosotros, al momento de entrar en la aplicacion y
    // notificar que ya los hemos leido
    public void setMessageRead(String myUid, String friendUid){
        final DatabaseReference userReference = getOneContactReference(myUid, friendUid);

        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserPojo user = dataSnapshot.getValue(UserPojo.class);
                if(user != null){
                    Map<String, Object> updates = new HashMap<>();
                    //Setear a 0 los mensajes sin leer en el contacto de nuestro amigo
                    updates.put(UserPojo.MESSAGES_UNREAD, 0);
                    Log.d("DEBUG CHAT", "Realtime Database: Seteando a 0 los mensajes sin leer");
                    userReference.updateChildren(updates);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

    }

    //Acceder a nuestro usuario/contactos/uid de nuestro contacto para seter a 0 el contador de mensajes
    private DatabaseReference getOneContactReference(String uidMain, String uidChild) {
        return mDatabaseAPI.getUserReferenceByUID(uidMain).child(FirebaseRealtimeDatabaseAPI.PATH_CONTACTS)
                .child(uidChild);
    }

    //Sumar mensajes que no se han leido al contacto al cual le estamos enviando los mensajes y no
    //esta conectado o no esta conectado con nosotros
    public void sumUnreadMessages(String myUid, String friendUid){
        Log.d("DEBUG CHAT", "Realtime Database: Destinatario no conectado, agregando mensaje sin leer...");
        //Acceder al perfil de neustro amigo y luego a nuestro contacto para agregar los mensajes no leidos
        final  DatabaseReference userReference = getOneContactReference(friendUid, myUid);

        //Se utiliza Transaccion para que todos los que queran afectar un mismo dato tendran esperar a
        // que uno finalice para poder actuar. Y en caso de tener algun problema como por ejemplo,
        //multiples incidencias en el mismo punto, nuestra libreria de Firebase se ecargara por debajo
        //de intentarlo hasta que lo logre y de esta forma no tendremos que preocuparnos por nada de esto
        userReference.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                UserPojo user = mutableData.getValue(UserPojo.class);
                if(user == null){
                    return Transaction.success(mutableData);
                }

                user.setMessagesUnread(user.getMessagesUnread() + 1);
                mutableData.setValue(user);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) { }
        });

    }

    /*
    * SEND MESSAGE
    * */
    //Enviar un mensaje
    public void sendMessage(String msg, String photoUrl, String friendEmail, UserPojo myUser,
                            final SendMessagesListener listener){
        Log.d("DEBUG CHAT", "Realtime Database: Send Message...");
        Message message = new Message();
        message.setSender(myUser.getEmail());
        message.setMsg(msg);
        message.setPhotoUrl(photoUrl);

        Log.d("DEBUG CHAT", "De: " + myUser.getEmail());
        Log.d("DEBUG CHAT", "Para: " + friendEmail);
        Log.d("DEBUG CHAT", "Mensaje: " + msg);
        Log.d("DEBUG CHAT", "Photo: " + photoUrl);

        DatabaseReference chatReference = getChatMessagesReference(myUser.getEmail(), friendEmail);
        chatReference.push().setValue(message, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if(databaseError == null){
                    Log.d("DEBUG CHAT", "Realtime Database: Se ha guardado/enviado mensaje con exito..");
                    listener.onSuccess();
                }
            }
        });

    }

}
