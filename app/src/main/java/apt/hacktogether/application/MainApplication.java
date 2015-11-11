package apt.hacktogether.application;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.parse.ParseObject;

import apt.hacktogether.parse.ParseImpl;

/**
 * Created by de-weikung on 11/11/15.
 */

/*
 * MainApplication.java
 * Required to initialize Parse. Layer can be initialized in either the Application class or any
 *  Activity class. There are 3 things you need to take care of before you can run the app:
 *
 *  1. Create a Layer account and set your App ID in LayerImpl.java
 *  2. Create a Parse account and set your App ID and Client Key in ParseImpl.java
 *  3. Create a Parse function to Authenticate your user. See MyAuthenticationListener.java for more detail
 */
public class MainApplication extends Application {

    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());
        ParseImpl.initialize(getApplicationContext());

    }
}
