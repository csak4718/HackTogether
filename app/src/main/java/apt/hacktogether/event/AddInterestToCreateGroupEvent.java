package apt.hacktogether.event;

import java.util.ArrayList;

/**
 * Created by de-weikung on 12/9/15.
 */
public class AddInterestToCreateGroupEvent {
    public ArrayList<String> mInterestIdList;
    public AddInterestToCreateGroupEvent(ArrayList<String> list) {
        mInterestIdList = list;
    }
}
