package apt.hacktogether.event;

import java.util.ArrayList;

/**
 * Created by de-weikung on 12/10/15.
 */
public class AddSkillToCreateGroupEvent {
    public ArrayList<String> mSkillIdList;
    public AddSkillToCreateGroupEvent(ArrayList<String> list) {
        mSkillIdList = list;
    }
}
