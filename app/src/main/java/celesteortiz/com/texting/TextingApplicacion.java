package celesteortiz.com.texting;
import android.app.Application;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.FirebaseDatabase;

public class TextingApplicacion extends Application {
    private RequestQueue mRequestQueue;
    private static TextingApplicacion mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        configFirebase();

        mInstance = this;
    }

    private void configFirebase(){
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

    public static synchronized TextingApplicacion getInstance(){
        return mInstance;
    }

    public RequestQueue getmRequestQueue(){
        if (mRequestQueue == null){
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToReqQueue(Request<T> request){
        request.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        getmRequestQueue().add(request);
    }
}

