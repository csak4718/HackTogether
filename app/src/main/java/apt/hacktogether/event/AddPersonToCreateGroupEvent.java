package apt.hacktogether.event;

import java.util.ArrayList;

/**
 * Created by de-weikung on 12/8/15.
 */
public class AddPersonToCreateGroupEvent {
    public ArrayList<String> mPersonIdList;
    public AddPersonToCreateGroupEvent(ArrayList<String> list) {
        mPersonIdList = list;
    }
}
