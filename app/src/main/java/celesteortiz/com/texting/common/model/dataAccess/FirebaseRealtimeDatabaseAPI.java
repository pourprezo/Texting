package celesteortiz.com.texting.common.model.dataAccess;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

import celesteortiz.com.texting.common.Constants;
import celesteortiz.com.texting.common.pojo.UserPojo;

/*
 * Project: Texting
 * Created by Celeste Ortiz on 15/03/2019
 * FirebaseRealtimeDatabaseAPI class
 * Recurso Comun
 * */
public class FirebaseRealtimeDatabaseAPI {
    //Ramas
    public static final String SEPARATOR = "___&___";
    public static final String PATH_USERS = "users";
    public static final String PATH_CONTACTS = "contacts";
    public static final String PATH_REQUESTS = "requests";

    private DatabaseReference mDatabaseReferene;

    private static class SingletonHolder{
        //Llama al constructor
        private static final FirebaseRealtimeDatabaseAPI INSTANCE = new FirebaseRealtimeDatabaseAPI();
    }

    //Retorna la referencia en la constante INSTANCE
    public static FirebaseRealtimeDatabaseAPI getInstance() {
        return SingletonHolder.INSTANCE;
    }

    //Constructor que al ser llamado inicializa a mDatabaseReference
    private FirebaseRealtimeDatabaseAPI(){
        this.mDatabaseReferene = FirebaseDatabase.getInstance().getReference();

        Log.d("DEBUG", " * * * FirebaseRealtimeDatabaseAPI:     Inicializando mDatabaseReference...... ");
        Log.d("DEBUG", " * * * FirebaseRealtimeDatabaseAPI:     ... " + mDatabaseReferene);
    }

    /*
    * Referencias
    * */
    public DatabaseReference getRootReference(){
        return mDatabaseReferene.getRoot();
    }

    //Obtener la referencia de un usuario en particular
    public DatabaseReference getUserReferenceByUID(String uid){
        return getRootReference().child(PATH_USERS).child(uid);
    }

    //Obtener la referencia de los ContactosFragment de un usuario
    public DatabaseReference getContactsReference(String uid) {
        return getUserReferenceByUID(uid).child(PATH_CONTACTS);
    }

    //Obtener la referencia de las Solicitudes de un usuario
    public DatabaseReference getRequestReference(String email) {
        return getRootReference().child(PATH_REQUESTS).child(email);
    }

    //Actualizar la hora de ultima conexion del usuario
    public void updateMyLastConnection(boolean online, String uid) {
        Log.d("DEBUG", " * * * FirebaseRealtimeDatabaseAPI:     Actualizar mi ultima conexion.... STATUS: "+ online + " Usuario: "+ uid );
        updateMyLastConnection(online, "", uid);
    }

    public void updateMyLastConnection(boolean online, String uidFriend, String uid){
        Log.d("DEBUG", " * * * FirebaseRealtimeDatabaseAPI:      Actualizando ultima conexion...STATUS: "+ online + " uidFriend: "+ uidFriend + " Usuario: "+ uid );

        String lastConnectionWith = Constants.ONLINE_VALUE + SEPARATOR + uidFriend;

        Map<String, Object> values = new HashMap<>();
        values.put(UserPojo.LAST_CONNECTION_WITH, online ? lastConnectionWith : ServerValue.TIMESTAMP);
        getUserReferenceByUID(uid).updateChildren(values);

        Log.d("DEBUG", " * * * FirebaseRealtimeDatabaseAPI:      Valores actualizados: lastConnectionWith...  " + lastConnectionWith);


        //onDisconect se despachara en cuando Firebase se de cuenta de que el usuario ya no esta
        //conectado
        if(online){
            Log.d("DEBUG", " * * * FirebaseRealtimeDatabaseAPI:      Usuario en linea ");
            getUserReferenceByUID(uid).child(UserPojo.LAST_CONNECTION_WITH).onDisconnect()
                    .setValue(ServerValue.TIMESTAMP);
        }else{
            Log.d("DEBUG", " * * * FirebaseRealtimeDatabaseAPI:      Usuario fuera de  linea ");
            getUserReferenceByUID(uid).child(UserPojo.LAST_CONNECTION_WITH).onDisconnect().cancel();
        }

    }
}
