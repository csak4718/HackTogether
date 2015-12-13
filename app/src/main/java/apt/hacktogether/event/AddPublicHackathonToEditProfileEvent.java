package apt.hacktogether.event;

import java.util.ArrayList;

/**
 * Created by de-weikung on 12/12/15.
 */
public class AddPublicHackathonToEditProfileEvent {
    public ArrayList<String> mPublicHackathonIdList;
    public AddPublicHackathonToEditProfileEvent(ArrayList<String> list) {
        mPublicHackathonIdList = list;
    }
}
