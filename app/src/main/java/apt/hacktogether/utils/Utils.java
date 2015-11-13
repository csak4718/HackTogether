package apt.hacktogether.utils;

import android.app.Activity;
import android.content.Intent;

import apt.hacktogether.activity.ConversationsActivity;
import apt.hacktogether.activity.LoginActivity;
import apt.hacktogether.activity.MainActivity;

/**
 * Created by de-weikung on 11/11/15.
 */
public class Utils {
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
