package apt.hacktogether.event;

import com.parse.ParseUser;

import java.util.List;

/**
 * Created by de-weikung on 12/5/15.
 */
public class PersonTabEvent {
    public List<ParseUser> hackersNeedGuyList;
    public PersonTabEvent(List<ParseUser> list) {
        hackersNeedGuyList = list;
    }
}
