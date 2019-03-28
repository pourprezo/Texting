package celesteortiz.com.texting.profileModule;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import celesteortiz.com.texting.R;

public class ProfileActivity extends AppCompatActivity {

    public static final int RC_PHOTO_PICKER = 22;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
    }
}
