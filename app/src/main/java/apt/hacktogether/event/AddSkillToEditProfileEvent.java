package apt.hacktogether.event;

import java.util.ArrayList;

/**
 * Created by de-weikung on 12/12/15.
 */
public class AddSkillToEditProfileEvent {
    public ArrayList<String> mSkillIdList;
    public AddSkillToEditProfileEvent(ArrayList<String> list) {
        mSkillIdList = list;
    }
}
