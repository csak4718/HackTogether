package apt.hacktogether.parse;

import android.content.Context;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseCrashReporting;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import apt.hacktogether.activity.BaseActivity;
import apt.hacktogether.utils.Common;
import de.hdodenhof.circleimageview.CircleImageView;

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

    //Merely checks to see if you have updated the App ID and Client Key. If these are set up
    // incorrectly, Parse will fail to initialize
    public static boolean hasValidAppID(){
        if(ParseAppID.equals("PARSE_APP_ID") || ParseClientKey.equals("PARSE_CLIENT_KEY")){
            return false;
        }

        return true;
    }

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

    private static HashMap<String, ParseObject> allHackathons;

    public static HashMap<String, ParseObject> get_allHackathons(){
        return allHackathons;
    }

    public static void cacheAllHackathons(){
        ParseQuery<ParseObject> hackathonQuery = ParseQuery.getQuery(Common.OBJECT_HACKATHON);
        hackathonQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> results, ParseException e) {
                if(e == null){
                    allHackathons = new HashMap<>();
                    for(int i = 0; i < results.size(); i++){
                        allHackathons.put(results.get(i).getObjectId(), results.get(i));
                    }
                }
            }
        });
    }

    public static ArrayList<String> getAllHackathonIds(){
        Set<String> hackathonIdSet = allHackathons.keySet();

        ArrayList<String> hackathonNameList = new ArrayList<>();
        ArrayList<String> orderedByName_hackathonIdList = new ArrayList<>();
        HashMap<String, String> name_id = new HashMap<>();

        Iterator itr = hackathonIdSet.iterator();
        while(itr.hasNext()) {
            String hackathonId = (String)itr.next();
            name_id.put(getHackathonName(hackathonId).toLowerCase(), hackathonId);
            hackathonNameList.add(getHackathonName(hackathonId).toLowerCase());
        }
        Collections.sort(hackathonNameList);

        for(String name: hackathonNameList){
            orderedByName_hackathonIdList.add(name_id.get(name));
        }

        return orderedByName_hackathonIdList;
    }

    public static String getHackathonName(String id){
        if(id != null && allHackathons != null && allHackathons.containsKey(id) && allHackathons.get(id) != null)
            return allHackathons.get(id).getString(Common.OBJECT_HACKATHON_NAME);

        //If the handle can't be found, return whatever value was passed in
        return id;
    }



    private static HashMap<String, ParseObject> allSkills;
    
    public static HashMap<String, ParseObject> get_allSkills(){
        return allSkills;
    }
      // correct
//    public static void cacheAllSkills(){
//        // cache all skills synchronously
//        ParseQuery<ParseObject> skillQuery = ParseQuery.getQuery(Common.OBJECT_SKILL);
//        try {
//            List<ParseObject> results = skillQuery.find();
//            allSkills = new HashMap<>();
//            for(int i = 0; i < results.size(); i++){
//                allSkills.put(results.get(i).getObjectId(), results.get(i));
//            }
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//    }

    // test
    public static void cacheAllSkills(){
        ParseQuery<ParseObject> skillQuery = ParseQuery.getQuery(Common.OBJECT_SKILL);
        skillQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> results, ParseException e) {
                if(e == null){
                    allSkills = new HashMap<>();
                    for(int i = 0; i < results.size(); i++){
                        allSkills.put(results.get(i).getObjectId(), results.get(i));
                    }
                }
            }
        });
    }

    public static Set<String> getAllSkillIds(){
        Set<String> skillIdSet = allSkills.keySet();
        return skillIdSet;
    }

    public static String getSkillName(String id){
        if(id != null && allSkills != null && allSkills.containsKey(id) && allSkills.get(id) != null)
            return allSkills.get(id).getString(Common.OBJECT_SKILL_NAME);

        //If the handle can't be found, return whatever value was passed in
        return id;
    }



    private static HashMap<String, ParseObject> allInterests;

    public static HashMap<String, ParseObject> get_allInterests(){
        return allInterests;
    }

    // correct
//    public static void cacheAllInterests(){
//        // cache all interests synchronously
//        ParseQuery<ParseObject> interestQuery = ParseQuery.getQuery(Common.OBJECT_INTEREST);
//        try {
//            List<ParseObject> results = interestQuery.find();
//            allInterests = new HashMap<>();
//            for(int i = 0; i < results.size(); i++){
//                allInterests.put(results.get(i).getObjectId(), results.get(i));
//            }
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//    }

//  test
    public static void cacheAllInterests(){
        ParseQuery<ParseObject> interestQuery = ParseQuery.getQuery(Common.OBJECT_INTEREST);
        interestQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> results, ParseException e) {
                if(e == null){
                    allInterests = new HashMap<>();
                    for(int i = 0; i < results.size(); i++){
                        allInterests.put(results.get(i).getObjectId(), results.get(i));
                    }
                }
            }
        });
    }

    public static Set<String> getAllInterestIds(){
        Set<String> interestIdSet = allInterests.keySet();
        return interestIdSet;
    }

    public static String getInterestName(String id){
        if(id != null && allInterests != null && allInterests.containsKey(id) && allInterests.get(id) != null)
            return allInterests.get(id).getString(Common.OBJECT_INTEREST_NAME);

        //If the handle can't be found, return whatever value was passed in
        return id;
    }



    //We keep track of all users associated with this app in Parse. You can override this to implement
    // your own user management system (based on a friends list, for example)
    private static HashMap<String, ParseUser> allUsers;
//    private static HashMap<String, CircleImageView> allUserCircleViews;

    public static HashMap<String, ParseUser> get_allUsers(){
        return allUsers;
    }
//    public static HashMap<String, CircleImageView> get_allUserCircleViews(){
//        return allUserCircleViews;
//    }

    public static void cacheAllUsers(final BaseActivity activity){
        // cache all users asynchronously
        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
        userQuery.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> results, ParseException e) {
                if(e == null){
                    allUsers = new HashMap<>();
                    for(int i = 0; i < results.size(); i++){
                        allUsers.put(results.get(i).getObjectId(), results.get(i));
                    }
                }
            }
        });
    }

//    cache CircleImageView at the same time
//    public static void cacheAllUsers(final BaseActivity activity){
//        // cache all users asynchronously
//        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
//        userQuery.findInBackground(new FindCallback<ParseUser>() {
//            public void done(List<ParseUser> results, ParseException e) {
//                if(e == null){
//                    allUsers = new HashMap<>();
//                    allUserCircleViews = new HashMap<>();
//                    for(int i = 0; i < results.size(); i++){
//                        allUsers.put(results.get(i).getObjectId(), results.get(i));
//
//
//                        ParseFile imgFile = results.get(i).getParseFile(Common.OBJECT_USER_PROFILE_PIC);
//                        CircleImageView ImagePic = new CircleImageView(activity);
//                        Picasso.with(activity)
//                                .load(imgFile.getUrl())
//                                .into(ImagePic);
//                        allUserCircleViews.put(results.get(i).getObjectId(), ImagePic);
//                    }
//                }
//            }
//        });
//    }


//    Official doc says don't use find()
//    public static void cacheAllUsers(){
//        // cache all users synchronously
//        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
//        try {
//            List<ParseUser> results = userQuery.find();
//            allUsers = new HashMap<>();
//            for(int i = 0; i < results.size(); i++){
//                allUsers.put(results.get(i).getObjectId(), results.get(i));
//            }
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//    }



    //Returns all userId NOT including the currently signed in user
    public static Set<String> getAllFriends(){
        Set<String> friends = allUsers.keySet();
        String currentUserId = ParseUser.getCurrentUser().getObjectId();
        if(friends.contains(currentUserId))
            friends.remove(currentUserId);
        return friends;
    }

    //Takes a ParseObject id and returns the associated username (De-Wei: Here we return user's nickname) for display purposes
    public static String getUsername(String id){

        //Does this id appear in the "all users" list?
        if(id != null && allUsers != null && allUsers.containsKey(id) && allUsers.get(id) != null)
            return allUsers.get(id).getString(Common.OBJECT_USER_NICK);

        //Does this id belong to the currently signed in user?
        if(id != null && ParseUser.getCurrentUser() != null && id.equals(ParseUser.getCurrentUser().getObjectId()))
            return ParseUser.getCurrentUser().getString(Common.OBJECT_USER_NICK);

        //If the handle can't be found, return whatever value was passed in
        return id;
    }

    public static ParseFile getUserIcon(String id){

        //Does this id appear in the "all users" list?
        if(id != null && allUsers != null && allUsers.containsKey(id) && allUsers.get(id) != null)
            return allUsers.get(id).getParseFile(Common.OBJECT_USER_PROFILE_PIC);

        //Does this id belong to the currently signed in user?
        if(id != null && ParseUser.getCurrentUser() != null && id.equals(ParseUser.getCurrentUser().getObjectId()))
            return ParseUser.getCurrentUser().getParseFile(Common.OBJECT_USER_PROFILE_PIC);

        //If the handle can't be found, return null
        return null;
    }


}
