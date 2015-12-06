package apt.hacktogether.utils;

/**
 * Created by de-weikung on 11/11/15.
 */
public class Common {
    /*
     Parse object
     */
    public final static String INSTALLATION_USER = "user";
    public final static String OBJECT_USER_NICK = "nickname";
    public final static String OBJECT_USER_PROFILE_PIC = "profile_pic";
    public final static String OBJECT_USER_FB_NAME = "fbName";
    public final static String OBJECT_USER_FB_ID = "fbId";
    public final static String OBJECT_USER_INTERESTS = "interests";
    public final static String OBJECT_USER_SKILLS = "skills";

    public final static String OBJECT_GROUP_NAME = "groupName";
    public final static String OBJECT_GROUP_MEMBERS = "members";
    public final static String OBJECT_GROUP_GROUPINTERESTS = "groupInterests";
    public final static String OBJECT_GROUP_LOOKFORSKILLS = "lookForSkills";

    public final static String OBJECT_HACKATHON = "Hackathon";
    public final static String OBJECT_HACKATHON_NAME = "hackathonName";
    public final static String OBJECT_HACKATHON_HACKERSNEEDGUY = "hackersNeedGuy";
    public final static String OBJECT_HACKATHON_GROUPSNEEDGUY = "groupsNeedGuy";

    public final static String OBJECT_INTEREST_NAME = "interestName";

    public final static String OBJECT_SKILL_NAME = "skillName";



    // alphabetical order
    public final static String[] HACKATHONS = {"BigRed//Hacks","BoilerMake","BostonHacks","Cal Hacks","Citrus Hack","CodeRED","Codestellation","DubHacks","HackDartmouth","HackDuke","HackGT","HackHarvard","HackHolyoke","HackISU","HackNC","HackNJIT","HackPrinceton","HackRPI","HackRU","HackTX","HackUMass","HopHacks","Huskie Hack","Kent Hack Enough","LocalHackDay","MHacks","MinneHack","PennApps","RamHacks","SDHacks","Technica","UGA Hacks","VandyHacks","WHACK","WUHack","WildHacks","hackNY","hackUstate"};

    // extra data in Intent (for going from one Activity to another)
    public final static String EXTRA_HACKATHON_NAME = "hackathon_name";

    // Tab
    public final static String TAB_NAME_PERSON = "Individual";
    public final static String TAB_NAME_GROUP = "Group";
    public final static int PERSON_TAB = 0;
    public final static int GROUP_TAB = 1;
}
