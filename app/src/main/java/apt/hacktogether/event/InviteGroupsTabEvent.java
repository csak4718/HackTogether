package apt.hacktogether.event;

import com.parse.ParseObject;

import java.util.List;

/**
 * Created by de-weikung on 12/6/15.
 */
public class InviteGroupsTabEvent {
    public List<ParseObject> inviteGroupsList;
    public InviteGroupsTabEvent(List<ParseObject> list) {
        inviteGroupsList = list;
    }
}
