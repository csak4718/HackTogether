package apt.hacktogether.event;

import java.util.ArrayList;

/**
 * Created by de-weikung on 12/12/15.
 */
public class AddPrivateHackathonToCreateProfileEvent {
    public ArrayList<String> mPrivateHackathonIdList;
    public AddPrivateHackathonToCreateProfileEvent(ArrayList<String> list) {
        mPrivateHackathonIdList = list;
    }
}
