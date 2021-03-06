package apt.hacktogether.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.util.Log;
import android.widget.ImageView;

import com.parse.ParseUser;

import java.util.ArrayList;

import apt.hacktogether.activity.AddInterestActivity;
import apt.hacktogether.activity.AddOneHackathonActivity;
import apt.hacktogether.activity.AddPersonActivity;
import apt.hacktogether.activity.AddPrivateHackathonActivity;
import apt.hacktogether.activity.AddPublicHackathonActivity;
import apt.hacktogether.activity.AddSkillActivity;
import apt.hacktogether.activity.BrowseActivity;
import apt.hacktogether.activity.ConversationsActivity;
import apt.hacktogether.activity.CreateGroupActivity;
import apt.hacktogether.activity.CreateProfileActivity;
import apt.hacktogether.activity.EditGroupActivity;
import apt.hacktogether.activity.EditProfileActivity;
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

    public static void gotoEditGroupActivity(Context context, String groupId){
        Intent it = new Intent(context, EditGroupActivity.class);
        it.putExtra(Common.EXTRA_GROUP_ID, groupId);
        context.startActivity(it);
    }

    public static void gotoCreateGroupActivity(Activity activity){
        Intent it = new Intent(activity, CreateGroupActivity.class);
        activity.startActivity(it);
    }

    public static void gotoAddPersonActivity(Activity activity, ArrayList<String> mTargetParticipants, String tag){
        Intent it = new Intent(activity, AddPersonActivity.class);
        it.putStringArrayListExtra(Common.EXTRA_PERSON_ID_LIST, mTargetParticipants);
        it.putExtra(Common.EXTRA_TAG, tag);
        activity.startActivity(it);
    }

    public static void fromEditGroupToAddPersonActivity(Activity activity, ArrayList<String> mTargetParticipants, ArrayList<String> inactivePersonIds, String tag){
        Intent it = new Intent(activity, AddPersonActivity.class);
        it.putStringArrayListExtra(Common.EXTRA_PERSON_ID_LIST, mTargetParticipants);
        it.putStringArrayListExtra(Common.EXTRA_INACTIVE_PERSON_ID_LIST, inactivePersonIds);
        it.putExtra(Common.EXTRA_TAG, tag);
        activity.startActivity(it);
    }

    public static void gotoAddInterestActivity(Activity activity, ArrayList<String> interestIds, String tag){
        Intent it = new Intent(activity, AddInterestActivity.class);
        it.putStringArrayListExtra(Common.EXTRA_INTEREST_ID_LIST, interestIds);
        it.putExtra(Common.EXTRA_TAG, tag);
        activity.startActivity(it);
    }

    public static void gotoAddSkillActivity(Activity activity, ArrayList<String> skillIds, String tag){
        Intent it = new Intent(activity, AddSkillActivity.class);
        it.putStringArrayListExtra(Common.EXTRA_SKILL_ID_LIST, skillIds);
        it.putExtra(Common.EXTRA_TAG, tag);
        activity.startActivity(it);
    }

    public static void gotoAddPublicHackathonActivity(Activity activity, ArrayList<String> publicHackathonIds, String tag){
        Intent it = new Intent(activity, AddPublicHackathonActivity.class);
        it.putStringArrayListExtra(Common.EXTRA_PUBLIC_HACKATHON_ID_LIST, publicHackathonIds);
        it.putExtra(Common.EXTRA_TAG, tag);
        activity.startActivity(it);
    }

    public static void gotoAddPrivateHackathonActivity(Activity activity, ArrayList<String> privateHackathonIds, String tag){
        Intent it = new Intent(activity, AddPrivateHackathonActivity.class);
        it.putStringArrayListExtra(Common.EXTRA_PRIVATE_HACKATHON_ID_LIST, privateHackathonIds);
        it.putExtra(Common.EXTRA_TAG, tag);
        activity.startActivity(it);
    }

    public static void gotoAddOneHackathonActivity(Activity activity){
        Intent it = new Intent(activity, AddOneHackathonActivity.class);
        activity.startActivity(it);
    }

    public static void toGrayScale(ImageView imgView){
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);

        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        imgView.setColorFilter(filter);
    }

    public static void gotoEditProfileActivity(Activity activity){
        Intent it = new Intent(activity, EditProfileActivity.class);
        activity.startActivity(it);
    }

    public static void gotoCreateProfileActivity(Activity activity){
        Intent it = new Intent(activity, CreateProfileActivity.class);
        activity.startActivity(it);
    }
}
