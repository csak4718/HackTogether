package apt.hacktogether.event;

import java.util.ArrayList;

/**
 * Created by de-weikung on 12/12/15.
 */
public class AddPublicHackathonToCreateProfileEvent {
    public ArrayList<String> mPublicHackathonIdList;
    public AddPublicHackathonToCreateProfileEvent(ArrayList<String> list) {
        mPublicHackathonIdList = list;
    }
}
