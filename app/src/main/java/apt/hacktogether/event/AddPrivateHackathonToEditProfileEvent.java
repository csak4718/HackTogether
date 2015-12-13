package apt.hacktogether.event;

import java.util.ArrayList;

/**
 * Created by de-weikung on 12/12/15.
 */
public class AddPrivateHackathonToEditProfileEvent {
    public ArrayList<String> mPrivateHackathonIdList;
    public AddPrivateHackathonToEditProfileEvent(ArrayList<String> list) {
        mPrivateHackathonIdList = list;
    }
}
