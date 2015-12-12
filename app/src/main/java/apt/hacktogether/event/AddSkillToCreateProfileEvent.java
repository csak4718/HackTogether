package apt.hacktogether.event;

import java.util.ArrayList;

/**
 * Created by de-weikung on 12/12/15.
 */
public class AddSkillToCreateProfileEvent {
    public ArrayList<String> mSkillIdList;
    public AddSkillToCreateProfileEvent(ArrayList<String> list) {
        mSkillIdList = list;
    }
}
