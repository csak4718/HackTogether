package apt.hacktogether.utils;

/**
 * Created by de-weikung on 11/11/15.
 */
public class Common {
    public final static String TAG_MESSAGE_ACTIVITY = "MessageActivity";
    public final static String TAG_CREATE_GROUP_ACTIVITY = "CreateGroupActivity";
    public final static String TAG_EDIT_GROUP_ACTIVITY = "EditGroupActivity";
    public final static String TAG_EDIT_PROFILE_ACTIVITY = "EditProfileActivity";

    /*
     Parse object
     */
    public final static String INSTALLATION_USER = "user";
    public final static String OBJECT_USER_NICK = "nickname";
    public final static String OBJECT_USER_PROFILE_PIC = "profile_pic";
    public final static String OBJECT_USER_FB_NAME = "fbName";
    public final static String OBJECT_USER_FB_ID = "fbId";
    public final static String OBJECT_USER_MYHACKATHONS = "myHackathons";
    public final static String OBJECT_USER_MYNEEDGUYHACKATHONS = "myNeedGuyHackathons";
    public final static String OBJECT_USER_INTERESTS = "interests";
    public final static String OBJECT_USER_SKILLS = "skills";
    public final static String OBJECT_USER_MYGROUPS = "myGroups";
    public final static String OBJECT_USER_INVITEGROUPS = "inviteGroups";


    public final static String OBJECT_GROUP = "Group";
    public final static String OBJECT_GROUP_NAME = "groupName";
    public final static String OBJECT_GROUP_HACKATHONATTEND = "hackathonAttend";
    public final static String OBJECT_GROUP_MEMBERS = "members";
    public final static String OBJECT_GROUP_GROUPINTERESTS = "groupInterests";
    public final static String OBJECT_GROUP_LOOKFORSKILLS = "lookForSkills";
    public final static String OBJECT_GROUP_PENDINGMEMBERS = "pendingMembers";
    public final static String OBJECT_GROUP_NEEDGUY = "needGuy";

    public final static String OBJECT_HACKATHON = "Hackathon";
    public final static String OBJECT_HACKATHON_NAME = "hackathonName";
    public final static String OBJECT_HACKATHON_HACKERSNEEDGUY = "hackersNeedGuy";
    public final static String OBJECT_HACKATHON_GROUPSNEEDGUY = "groupsNeedGuy";
    public final static String OBJECT_HACKATHON_HACKERS = "hackers";
    public final static String OBJECT_HACKATHON_GROUPS = "groups";

    public final static String OBJECT_INTEREST = "Interest";
    public final static String OBJECT_INTEREST_NAME = "interestName";
    public final static String OBJECT_INTEREST_INTERESTED_HACKERS = "interested_hackers";
    public final static String OBJECT_INTEREST_INTERESTED_GROUPS = "interested_groups";

    public final static String OBJECT_SKILL = "Skill";
    public final static String OBJECT_SKILL_NAME = "skillName";
    public final static String OBJECT_SKILL_SKILLED_HACKERS = "skilled_hackers";
    public final static String OBJECT_SKILL_LOOKFOR_GROUPS = "lookFor_groups";



    // alphabetical order
    public final static String[] HACKATHONS = {"BigRed//Hacks","BoilerMake","BostonHacks","Cal Hacks","Citrus Hack","CodeRED","Codestellation","DubHacks","HackDartmouth","HackDuke","HackGT","HackHarvard","HackHolyoke","HackISU","HackNC","HackNJIT","hackNY","HackPrinceton","HackRPI","HackRU","HackTX","HackUMass","hackUstate","HopHacks","Huskie Hack","Kent Hack Enough","LocalHackDay","MHacks","MinneHack","PennApps","RamHacks","SDHacks","Technica","UGA Hacks","VandyHacks","WHACK","WUHack","WildHacks"};

    // extra data in Intent (for going from one Activity to another)
    public final static String EXTRA_HACKATHON_NAME = "hackathon_name";
    public final static String EXTRA_GROUP_ID = "group_id";
    public final static String EXTRA_PERSON_ID_LIST = "person_id_list";
    public final static String EXTRA_INTEREST_ID_LIST = "interest_id_list";
    public final static String EXTRA_SKILL_ID_LIST = "skill_id_list";
    public final static String EXTRA_TAG = "tag";

    // Tab
    public final static String TAB_NAME_PERSON = "Individual";
    public final static String TAB_NAME_GROUP = "Group";
    public final static int PERSON_TAB = 0;
    public final static int GROUP_TAB = 1;

    public final static String TAB_NAME_MYGROUPS = "My Groups";
    public final static String TAB_NAME_INVITEGROUPS = "invitations";
    public final static int MYGROUPS_TAB = 0;
    public final static int INVITEGROUPS_TAB = 1;

}
