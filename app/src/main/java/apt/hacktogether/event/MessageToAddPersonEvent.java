package apt.hacktogether.event;

import java.util.ArrayList;

/**
 * Created by de-weikung on 12/7/15.
 */
public class MessageToAddPersonEvent {
    public ArrayList<String> mTargetParticipants;
    public MessageToAddPersonEvent(ArrayList<String> list) {
        mTargetParticipants = list;
    }
}
