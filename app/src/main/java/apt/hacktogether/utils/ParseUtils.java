package apt.hacktogether.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import apt.hacktogether.event.GroupTabEvent;
import apt.hacktogether.event.InviteGroupsTabEvent;
import apt.hacktogether.event.MyGroupsTabEvent;
import apt.hacktogether.event.PersonTabEvent;
import de.greenrobot.event.EventBus;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by de-weikung on 11/11/15.
 */
public class ParseUtils {
    static public void createUserProfile(String mNickName, String mFbId, Bitmap profilePic) {
        Log.d("william", "4");
        final ParseUser user = ParseUser.getCurrentUser();
        user.put(Common.OBJECT_USER_FB_NAME, mNickName);
        user.put(Common.OBJECT_USER_NICK, mNickName);
        user.put(Common.OBJECT_USER_FB_ID, mFbId);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        profilePic.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bytearray= stream.toByteArray();
        final ParseFile imgFile = new ParseFile(user.getUsername() + "_profile.jpg", bytearray);
        try {
            imgFile.save();
            user.put(Common.OBJECT_USER_PROFILE_PIC, imgFile);
            user.save();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    static public void displayParseImage(final ParseFile imgFile, final CircleImageView imgView) {
        imgFile.getDataInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] bytes, ParseException e) {
                if (e == null) {
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0,
                            bytes.length);
                    if (bmp != null) {
                        imgView.setImageBitmap(bmp);
                    }
                }
            }
        });
    }

    static public void getHackersNeedGuy(String hackathon_name) {
        // get the Hackathon object first
        ParseQuery<ParseObject> hackathonObjectQuery = ParseQuery.getQuery(Common.OBJECT_HACKATHON);
        hackathonObjectQuery.whereEqualTo(Common.OBJECT_HACKATHON_NAME, hackathon_name);
        hackathonObjectQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if(e == null) {
                    // get hackersNeedGuy
                    ParseRelation<ParseUser> hackersNeedGuy = object.getRelation(Common.OBJECT_HACKATHON_HACKERSNEEDGUY);
                    hackersNeedGuy.getQuery().findInBackground(new FindCallback<ParseUser>() {
                        @Override
                        public void done(List<ParseUser> list, ParseException e) {
                            if (e == null){
                                EventBus.getDefault().post(new PersonTabEvent(list));
                            }
                        }
                    });

                }

            }
        });
    }

    static public void getGroupsNeedGuy(String hackathon_name) {
        // get the Hackathon object first
        ParseQuery<ParseObject> hackathonObjectQuery = ParseQuery.getQuery(Common.OBJECT_HACKATHON);
        hackathonObjectQuery.whereEqualTo(Common.OBJECT_HACKATHON_NAME, hackathon_name);
        hackathonObjectQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if(e == null) {

                    // get groupsNeedGuy
                    ParseRelation<ParseObject> groupsNeedGuy = object.getRelation(Common.OBJECT_HACKATHON_GROUPSNEEDGUY);
                    groupsNeedGuy.getQuery().findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> list, ParseException e) {
                            EventBus.getDefault().post(new GroupTabEvent(list));
                        }
                    });

                }

            }
        });
    }

    static public void getMyGroups(){
        ParseUser currentUser = ParseUser.getCurrentUser();
        ParseRelation<ParseObject> myGroups = currentUser.getRelation(Common.OBJECT_USER_MYGROUPS);
        myGroups.getQuery().findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                EventBus.getDefault().post(new MyGroupsTabEvent(list));
            }
        });
    }

    static public void getInviteGroups(){
        ParseUser currentUser = ParseUser.getCurrentUser();
        ParseRelation<ParseObject> inviteGroups = currentUser.getRelation(Common.OBJECT_USER_INVITEGROUPS);
        inviteGroups.getQuery().findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                EventBus.getDefault().post(new InviteGroupsTabEvent(list));
            }
        });
    }

    static public void acceptInvitation(final ParseObject inviteGroup){
        final ParseUser currentUser = ParseUser.getCurrentUser();

        // move this user from pendingMembers to members in Group class
        ParseRelation<ParseUser> members = inviteGroup.getRelation(Common.OBJECT_GROUP_MEMBERS);
        members.add(currentUser);
        ParseRelation<ParseUser> pendingMembers = inviteGroup.getRelation(Common.OBJECT_GROUP_PENDINGMEMBERS);
        pendingMembers.remove(currentUser);
        inviteGroup.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                String hackathonName = inviteGroup.getString(Common.OBJECT_GROUP_HACKATHONATTEND);
                ParseQuery<ParseObject> hackathonObjectQuery = ParseQuery.getQuery(Common.OBJECT_HACKATHON);
                hackathonObjectQuery.whereEqualTo(Common.OBJECT_HACKATHON_NAME, hackathonName);
                hackathonObjectQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(final ParseObject hackathon, ParseException e) {

                        // after getting hackathon object, add this hackathon object into myHackathons
                        ParseRelation<ParseObject> myHackathons = currentUser.getRelation(Common.OBJECT_USER_MYHACKATHONS);
                        myHackathons.add(hackathon);

                        // add to myGroups
                        ParseRelation<ParseObject> myGroups = currentUser.getRelation(Common.OBJECT_USER_MYGROUPS);
                        myGroups.add(inviteGroup);

                        // remove from inviteGroups
                        ParseRelation<ParseObject> inviteGroups = currentUser.getRelation(Common.OBJECT_USER_INVITEGROUPS);
                        inviteGroups.remove(inviteGroup);
                        currentUser.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {

                                // in Hackathon class, hackers add this user (And do nothing on hackersNeedGuy)
                                ParseRelation<ParseUser> hackers = hackathon.getRelation(Common.OBJECT_HACKATHON_HACKERS);
                                hackers.add(currentUser);
                                hackathon.saveInBackground();
                            }
                        });

                    }
                });
            }
        });

    }

    static public void rejectInvitation(final ParseObject inviteGroup){
        final ParseUser currentUser = ParseUser.getCurrentUser();

        // remove this user from pendingMembers
        ParseRelation<ParseUser> pendingMembers = inviteGroup.getRelation(Common.OBJECT_GROUP_PENDINGMEMBERS);
        pendingMembers.remove(currentUser);
        inviteGroup.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {

                // remove this inviteGroup from inviteGroups of current user.
                ParseRelation<ParseObject> inviteGroups = currentUser.getRelation(Common.OBJECT_USER_INVITEGROUPS);
                inviteGroups.remove(inviteGroup);
                currentUser.saveInBackground();
            }
        });

    }

    static public void addGroupToInviteGroups(ParseObject group, ArrayList<String> selectedPersonIds){
        for (String selectedPersonId: selectedPersonIds){
            Map<String, Object> params = new HashMap<>();
            params.put("groupId", group.getObjectId());
            params.put("pendingMemberId", selectedPersonId);
            ParseCloud.callFunctionInBackground("addGroupToInviteGroup", params);
        }
    }

    static public void removeUnwantedGroupInterest(String groupId, String delete_groupInterestId){
        Map<String, Object> params = new HashMap<>();
        params.put("groupId", groupId);
        params.put("delete_groupInterestId", delete_groupInterestId);
        ParseCloud.callFunctionInBackground("removeUnwantedGroupInterest", params);
    }

    static public void removeUnwantedLookForSkill(String groupId, String delete_lookForSkillId){
        Map<String, Object> params = new HashMap<>();
        params.put("groupId", groupId);
        params.put("delete_lookForSkillId", delete_lookForSkillId);
        ParseCloud.callFunctionInBackground("removeUnwantedLookForSkill", params);
    }

    static public void removeUnwantedMyInterest(String userId, String delete_myInterestId){
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("delete_myInterestId", delete_myInterestId);
        ParseCloud.callFunctionInBackground("removeUnwantedMyInterest", params);
    }

    static public void removeUnwantedMySkill(String userId, String delete_mySkillId){
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("delete_mySkillId", delete_mySkillId);
        ParseCloud.callFunctionInBackground("removeUnwantedMySkill", params);
    }

    static public void removeUnwantedMyNeedGuyHackathon(String userId, String delete_myNeedGuyHackathonId){
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("delete_myNeedGuyHackathonId", delete_myNeedGuyHackathonId);
        ParseCloud.callFunctionInBackground("removeUnwantedMyNeedGuyHackathon", params);
    }

    static public void removeUnwantedMyHackathon(String userId, String delete_myHackathonId){
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("delete_myHackathonId", delete_myHackathonId);
        ParseCloud.callFunctionInBackground("removeUnwantedMyHackathon", params);
    }

    static public void removeUserFromInterestedHackers(String interestId, String delete_userId){
        Map<String, Object> params = new HashMap<>();
        params.put("interestId", interestId);
        params.put("delete_userId", delete_userId);
        ParseCloud.callFunctionInBackground("removeUserFromInterestedHackers", params);
    }

    static public void removeUserFromSkilledHackers(String skillId, String delete_userId){
        Map<String, Object> params = new HashMap<>();
        params.put("skillId", skillId);
        params.put("delete_userId", delete_userId);
        ParseCloud.callFunctionInBackground("removeUserFromSkilledHackers", params);
    }

    static public void removeUserFromHackersNeedGuy(String hackathonId, String delete_userId){
        Map<String, Object> params = new HashMap<>();
        params.put("hackathonId", hackathonId);
        params.put("delete_userId", delete_userId);
        ParseCloud.callFunctionInBackground("removeUserFromHackersNeedGuy", params);
    }

    static public void removeUserFromHackers(String hackathonId, String delete_userId){
        Map<String, Object> params = new HashMap<>();
        params.put("hackathonId", hackathonId);
        params.put("delete_userId", delete_userId);
        ParseCloud.callFunctionInBackground("removeUserFromHackers", params);
    }
}
