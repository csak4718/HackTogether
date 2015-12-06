package apt.hacktogether.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

import apt.hacktogether.event.GroupTabEvent;
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
}
