package app;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.PushService;

import app.Control.LocalStorageAccess;

/**
 * Created by Amir Lahav on 5/31/2015.
 */
public class CustomGlobalApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        LocalStorageAccess.init(this);
        FacebookSdk.sdkInitialize(getApplicationContext());

        Parse.initialize(this, "zxRRUkfVj9v9XNeS1K6GS8vzYuPiOpHHDGBfB6IZ",
                "wlxygUU42jLaOc27tHbvnU0aqZftux6iT1ytH4a5");
        PushService.setDefaultPushCallback(this, SplashScreenActivity.class);
    }
}
