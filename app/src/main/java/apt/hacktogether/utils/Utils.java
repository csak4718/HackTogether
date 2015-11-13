package apt.hacktogether.utils;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.parse.ParseUser;

import apt.hacktogether.activity.ConversationsActivity;
import apt.hacktogether.activity.LoginActivity;
import apt.hacktogether.activity.MainActivity;
import apt.hacktogether.layer.LayerImpl;

/**
 * Created by de-weikung on 11/11/15.
 */
public class Utils {
    public static void checkSetup(Activity activity){
        //If the user is not authenticated, make sure they are logged in, and if they are, re-authenticate
        if (!LayerImpl.isAuthenticated()) {

            if (ParseUser.getCurrentUser() == null) {

                Log.d("Activity", "User is not authenticated or logged in - returning to login screen");
                gotoLoginActivity(activity);

            } else {

                Log.d("Activity", "User is not authenticated, but is logged in - re-authenticating user");
                LayerImpl.authenticateUser();

            }
        }
    }

    public static void gotoMainActivity(Activity activity){
        Intent it = new Intent(activity, MainActivity.class);
        activity.startActivity(it);
    }

    public static void gotoConversationsActivity(Activity activity){
        Intent it = new Intent(activity, ConversationsActivity.class);
        activity.startActivity(it);
    }

    public static void gotoLoginActivity(Activity activity){
        Intent it = new Intent(activity, LoginActivity.class);
        activity.startActivity(it);
    }

}
