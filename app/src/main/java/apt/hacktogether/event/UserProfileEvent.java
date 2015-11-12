package apt.hacktogether.event;

/**
 * Created by de-weikung on 11/11/15.
 */
public class UserProfileEvent {
    public String mFbId;
    public String mNickName;
    public UserProfileEvent(String fbId, String nickName) {
        mFbId = fbId;
        mNickName = nickName;
    }
}