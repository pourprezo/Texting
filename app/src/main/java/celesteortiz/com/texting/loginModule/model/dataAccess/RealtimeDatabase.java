package celesteortiz.com.texting.loginModule.model.dataAccess;

import android.support.annotation.NonNull;
import android.util.Log;

import celesteortiz.com.texting.R;
import celesteortiz.com.texting.common.model.EventErrorTypeListener;
import celesteortiz.com.texting.common.model.dataAccess.FirebaseRealtimeDatabaseAPI;
import celesteortiz.com.texting.common.pojo.UserPojo;
import celesteortiz.com.texting.loginModule.events.LoginEvent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;


/*
 * Project: Texting
 * Created by Celeste Ortiz on 15/03/2019
 * RealtimeDatabase class para modulo Login
 * */
public class RealtimeDatabase {
    private FirebaseRealtimeDatabaseAPI mDatabaseAPI;

    //COnstructor que inicializa mDatabaseAPI
    public RealtimeDatabase() {
        mDatabaseAPI = FirebaseRealtimeDatabaseAPI.getInstance();
    }

    public void registerUser(UserPojo user){
        Log.d("DEBUG_C", " * * * (LOGIN) RealtimeDatabase:  Registrando usuario....");
        Map<String, Object> values = new HashMap<>();
        values.put(UserPojo.USERNAME, user.getUsername());
        values.put(UserPojo.EMAIL, user.getEmail());
        values.put(UserPojo.PHOTO_URL, user.getPhotoUrl());

        mDatabaseAPI.getUserReferenceByUID(user.getUid()).updateChildren(values);
    }

    public void checkUserExist(String uid, final EventErrorTypeListener listener){
        Log.d("DEBUG_C", " * * * (LOGIN) RealtimeDatabase: Checando si existe usuario " +uid);
        mDatabaseAPI.getUserReferenceByUID(uid).child(UserPojo.EMAIL).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.d("DEBUG_C", " * * * (LOGIN) RealtimeDatabase/onDataChange() dataSnapshot: " +dataSnapshot);
                        if (!dataSnapshot.exists()){
                            Log.d("DEBUG_C", " * * * (LOGIN) RealtimeDatabase/onDataChange() Usuario nuevo, registrando");

                            listener.onError(LoginEvent.USER_DONOT_EXIST, R.string.login_error_user_exist);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("DEBUG_C", " * * * (LOGIN) RealtimeDatabase/onDataChange() databaseError: " +databaseError);
                        listener.onError(LoginEvent.ERROR_SERVER, R.string.login_message_error);
                    }
                });
    }
}
