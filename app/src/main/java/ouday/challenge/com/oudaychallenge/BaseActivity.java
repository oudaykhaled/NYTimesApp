package ouday.challenge.com.oudaychallenge;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;


/**
 * All Activities in this project must implement this class
 * This is to make sure that getCurrentActivity return the correct activity instance
 */
public class BaseActivity extends AppCompatActivity {

    private static BaseActivity currentActivity;


    public static BaseActivity getCurrentActivity(){
        return currentActivity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentActivity = this;
    }

    @Override
    protected void onResume() {
        super.onResume();
        currentActivity = this;
    }


}
