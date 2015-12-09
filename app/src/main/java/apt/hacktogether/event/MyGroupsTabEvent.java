package apt.hacktogether.event;

import com.parse.ParseObject;

import java.util.List;

/**
 * Created by de-weikung on 12/6/15.
 */
public class MyGroupsTabEvent {
    public List<ParseObject> myGroupsList;
    public MyGroupsTabEvent(List<ParseObject> list) {
        myGroupsList = list;
    }
}
