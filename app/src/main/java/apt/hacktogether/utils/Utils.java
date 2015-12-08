package apt.hacktogether.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.parse.ParseUser;

import java.util.ArrayList;

import apt.hacktogether.activity.AddPersonActivity;
import apt.hacktogether.activity.BrowseActivity;
import apt.hacktogether.activity.ConversationsActivity;
import apt.hacktogether.activity.CreateGroupActivity;
import apt.hacktogether.activity.EditGroupActivity;
import apt.hacktogether.activity.GroupManageActivity;
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

    public static void gotoBrowseActivity(Context context, String hackathonName){
        Intent it = new Intent(context, BrowseActivity.class);
        it.putExtra(Common.EXTRA_HACKATHON_NAME, hackathonName);
        context.startActivity(it);
    }

    public static void gotoGroupManageActivity(Activity activity){
        Intent it = new Intent(activity, GroupManageActivity.class);
        activity.startActivity(it);
    }

    public static void gotoEditGroupActivity(Context context, String groupName){
        Intent it = new Intent(context, EditGroupActivity.class);
        it.putExtra(Common.EXTRA_GROUP_NAME, groupName);
        context.startActivity(it);
    }

    public static void gotoCreateGroupActivity(Activity activity){
        Intent it = new Intent(activity, CreateGroupActivity.class);
        activity.startActivity(it);
    }

    public static void gotoAddPersonActivity(Activity activity, ArrayList<String> mTargetParticipants){
        Intent it = new Intent(activity, AddPersonActivity.class);
        it.putStringArrayListExtra(Common.EXTRA_PERSON_ID_LIST, mTargetParticipants);
        activity.startActivity(it);
    }

//      TODO: uncomment later
//    public static void gotoSettingsActivity(Activity activity){
//        Intent it = new Intent(activity, SettingsActivity.class);
//        activity.startActivity(it);
//    }

}
