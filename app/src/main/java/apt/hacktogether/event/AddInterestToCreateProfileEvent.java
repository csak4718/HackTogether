package apt.hacktogether.event;

import java.util.ArrayList;

/**
 * Created by de-weikung on 12/12/15.
 */
public class AddInterestToCreateProfileEvent {
    public ArrayList<String> mInterestIdList;
    public AddInterestToCreateProfileEvent(ArrayList<String> list) {
        mInterestIdList = list;
    }
}
