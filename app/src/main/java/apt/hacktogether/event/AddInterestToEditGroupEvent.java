package apt.hacktogether.event;

import java.util.ArrayList;

/**
 * Created by de-weikung on 12/11/15.
 */
public class AddInterestToEditGroupEvent {
    public ArrayList<String> mInterestIdList;
    public AddInterestToEditGroupEvent(ArrayList<String> list) {
        mInterestIdList = list;
    }
}
