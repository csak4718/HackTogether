package apt.hacktogether.event;



import com.parse.ParseObject;

import java.util.List;

/**
 * Created by de-weikung on 12/5/15.
 */
public class GroupTabEvent {
    public List<ParseObject> groupsNeedGuyList;
    public GroupTabEvent(List<ParseObject> list) {
        groupsNeedGuyList = list;
    }
}
