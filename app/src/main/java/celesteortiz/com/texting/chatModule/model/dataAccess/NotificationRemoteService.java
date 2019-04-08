package celesteortiz.com.texting.chatModule.model.dataAccess;

import android.net.Uri;
import android.util.Log;

import celesteortiz.com.texting.R;
import celesteortiz.com.texting.TextingApplicacion;
import celesteortiz.com.texting.chatModule.events.ChatEvent;
import celesteortiz.com.texting.common.Constants;
import celesteortiz.com.texting.common.model.EventErrorTypeListener;
import celesteortiz.com.texting.common.pojo.UserPojo;
import celesteortiz.com.texting.common.utils.UtilsCommon;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/*
* Data Access para Servidor Remoto con Volley
* */
public class NotificationRemoteService {
    //URL de nuestor servicio remoto
    private static final String TEXTING_RS ="https://androidcursosceleste.000webhostapp.com/Texting/dataAccess/TextingRS.php";
    private static final String SEND_NOTIFICATION ="sendNotification";

    //Hacer peticion correspondiente al servicio remoto por medio de la libreria Volley
    public void sendNotification(String title, String message, String email, String uid,
                                 String myEmail, Uri photoUrl, final EventErrorTypeListener listener){
        JSONObject params = new JSONObject();
        try {
            params.put(Constants.METHOD, SEND_NOTIFICATION);
            params.put(Constants.TITLE, title);
            params.put(Constants.MESSAGE, message);
            params.put(Constants.TOPIC, UtilsCommon.getEmailToTopic(email));
            params.put(UserPojo.UID, uid);
            params.put(UserPojo.EMAIL, myEmail);
            params.put(UserPojo.PHOTO_URL, photoUrl);
            params.put(UserPojo.USERNAME, title);
        } catch (JSONException e) {
            e.printStackTrace();
            listener.onError(ChatEvent.ERROR_PROCESS_DATA, R.string.common_error_process_data);
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, TEXTING_RS,
                params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
               // JSONObject jsonResponse = new JSONObject(response);

                try {
                  Log.d("Debug JSON: ", "onRespose:  " + response.toString());

                    int success = response.getInt(Constants.SUCCESS);

                    switch (success){
                        case ChatEvent.SEND_NOTIFICATION_SUCCESS:

                            break;
                        case ChatEvent.ERROR_METHOD_NOT_EXIST:
                            listener.onError(ChatEvent.ERROR_METHOD_NOT_EXIST, R.string.chat_error_method_not_exist);
                            break;
                        default:
                            listener.onError(ChatEvent.ERROR_SERVER, R.string.common_error_server);
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    listener.onError(ChatEvent.ERROR_PROCESS_DATA, R.string.common_error_process_data);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Debug JSON: ", "onErrorResponse:  " + error.toString());
                Log.i("Volley error", "Message: "+error.getMessage());
                Log.i("Volley error", "Message: "+error.getCause().getMessage());
                Log.i("Volley error", "Message: "+error.getCause());
                Log.i("Volley error", "Message: "+error.getStackTrace());
                Log.i("Volley error", "Message: "+error.getSuppressed());
                listener.onError(ChatEvent.ERROR_VOLLEY, R.string.common_error_volley);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json; charset=utf-8");
                return params;
            }
        };

        TextingApplicacion.getInstance().addToReqQueue(jsonObjectRequest);
    }
}
