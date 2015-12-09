package apt.hacktogether.event;

import java.util.ArrayList;

/**
 * Created by de-weikung on 12/8/15.
 */
public class AddPersonToMessageEvent {
    public ArrayList<String> mPersonIdList;
    public AddPersonToMessageEvent(ArrayList<String> list) {
        mPersonIdList = list;
    }
}
