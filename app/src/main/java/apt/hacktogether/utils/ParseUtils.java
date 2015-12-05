package apt.hacktogether.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.util.Map;

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

}
