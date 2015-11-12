package apt.hacktogether.utils;

import android.graphics.Bitmap;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.util.Map;

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


}
