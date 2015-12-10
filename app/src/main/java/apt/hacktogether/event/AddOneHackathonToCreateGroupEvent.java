package apt.hacktogether.event;

/**
 * Created by de-weikung on 12/10/15.
 */
public class AddOneHackathonToCreateGroupEvent {
    public String hackathon_name;
    public AddOneHackathonToCreateGroupEvent(String name) {
        hackathon_name = name;
    }
}
