package apt.hacktogether.parse;

import android.content.Context;

import com.parse.Parse;
import com.parse.ParseCrashReporting;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseUser;

import apt.hacktogether.utils.Common;

/**
 * Created by de-weikung on 11/11/15.
 */

/*
 * ParseImpl.java
 * Handles the Parse implementation. You will need to sign up for a Parse account and set the
 *  Application ID and Client Key in order to sign into Parse and manage your users.
 *
 *  This class handles the "Friends List" (all users in your Parse account), as well as some general
 *   helper functions.
 */
public class ParseImpl {

    //If you haven't, make sure you set up a Parse account at http://parse.com, then follow these
    // instructions:
    // 1. Log into your Parse account
    // 2. Mouse over the settings option in your project (gear icon)
    // 3. Select "Keys"
    // 4. Copy the "Application ID" and "Client Key" into the following fields:
    private static String ParseAppID = "7CUhVjsEag7xUb6I7uCyuQ0GDXWMAkKQbvt3zWv3";
    private static String ParseClientKey = "vGxeVZzHN6p4TxQLaLnzRSirMkY3ZPhiC7rzmajI";

    //Called from the Application class to set up Parse
    public static void initialize(Context context){
        // Enable Local Datastore.
        Parse.enableLocalDatastore(context.getApplicationContext());
        ParseCrashReporting.enable(context.getApplicationContext());
        Parse.initialize(context.getApplicationContext(), ParseAppID, ParseClientKey);
        ParseFacebookUtils.initialize(context.getApplicationContext());


        ParsePush.subscribeInBackground("");
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();

        if(ParseUser.getCurrentUser() != null) installation.put(Common.INSTALLATION_USER, ParseUser.getCurrentUser());

        installation.saveInBackground();
    }
}
