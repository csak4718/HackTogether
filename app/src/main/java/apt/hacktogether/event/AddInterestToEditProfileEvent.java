package apt.hacktogether.event;

import java.util.ArrayList;

/**
 * Created by de-weikung on 12/12/15.
 */
public class AddInterestToEditProfileEvent {
    public ArrayList<String> mInterestIdList;
    public AddInterestToEditProfileEvent(ArrayList<String> list) {
        mInterestIdList = list;
    }
}
