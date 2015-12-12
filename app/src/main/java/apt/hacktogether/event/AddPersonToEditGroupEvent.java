package apt.hacktogether.event;

import java.util.ArrayList;

/**
 * Created by de-weikung on 12/11/15.
 */
public class AddPersonToEditGroupEvent {
    public ArrayList<String> mPersonIdList;
    public AddPersonToEditGroupEvent(ArrayList<String> list) {
        mPersonIdList = list;
    }
}
