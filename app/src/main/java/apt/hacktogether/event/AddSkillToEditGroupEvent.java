package apt.hacktogether.event;

import java.util.ArrayList;

/**
 * Created by de-weikung on 12/11/15.
 */
public class AddSkillToEditGroupEvent {
    public ArrayList<String> mSkillIdList;
    public AddSkillToEditGroupEvent(ArrayList<String> list) {
        mSkillIdList = list;
    }
}
