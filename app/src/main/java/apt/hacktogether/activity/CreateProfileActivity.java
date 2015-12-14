package apt.hacktogether.activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import apt.hacktogether.R;
import apt.hacktogether.event.AddInterestToCreateProfileEvent;
import apt.hacktogether.event.AddPrivateHackathonToCreateProfileEvent;
import apt.hacktogether.event.AddPublicHackathonToCreateProfileEvent;
import apt.hacktogether.event.AddSkillToCreateProfileEvent;
import apt.hacktogether.parse.ParseImpl;
import apt.hacktogether.utils.Common;
import apt.hacktogether.utils.Utils;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import de.hdodenhof.circleimageview.CircleImageView;

public class CreateProfileActivity extends BaseActivity {
    public static final String TAG = Common.TAG_CREATE_PROFILE_ACTIVITY;

    private ParseUser currentUser;
    private ArrayList<String> myInterestIds;
    private ArrayList<String> mySkillIds;
    private ArrayList<String> myPublicHackathonIds;
    private ArrayList<String> myPrivateHackathonIds;

    @Bind(R.id.img_profile) CircleImageView imgProfile;
    @Bind(R.id.txt_username) TextView txtUsername;

    @OnClick(R.id.ll_my_interests) void goAddInterest(){
        Utils.gotoAddInterestActivity(CreateProfileActivity.this, myInterestIds, TAG);
    }
    @Bind(R.id.ll_my_interests_content) FlowLayout ll_MyInterestsContent;

    @OnClick(R.id.ll_my_skills) void goAddSkill(){
        Utils.gotoAddSkillActivity(CreateProfileActivity.this, mySkillIds, TAG);
    }
    @Bind(R.id.ll_my_skills_content) FlowLayout ll_MySkillsContent;

    @OnClick(R.id.ll_public_hackathon) void goAddPublicHackathon(){
        Utils.gotoAddPublicHackathonActivity(CreateProfileActivity.this, myPublicHackathonIds, TAG);
    }
    @Bind(R.id.ll_public_hackathon_content) FlowLayout ll_PublicHackathonContent;

    @OnClick(R.id.ll_private_hackathon) void goAddPrivateHackathon(){
        Utils.gotoAddPrivateHackathonActivity(CreateProfileActivity.this, myPrivateHackathonIds, TAG);
    }
    @Bind(R.id.ll_private_hackathon_content) FlowLayout ll_PrivateHackathonContent;



    @OnClick(R.id.btn_confirm) void createProfile(){
        if(myPublicHackathonIds==null) myPublicHackathonIds = new ArrayList<>();
        if(myPrivateHackathonIds==null) myPrivateHackathonIds = new ArrayList<>();

        // Test User Input
        if (myInterestIds == null || myInterestIds.size() == 0) {
            Toast.makeText(this, Common.ERROR_NO_MY_INTERESTS, Toast.LENGTH_SHORT).show();
            return;
        }
        else if(mySkillIds == null || mySkillIds.size() == 0){
            Toast.makeText(this, Common.ERROR_NO_MY_SKILLS, Toast.LENGTH_SHORT).show();
            return;
        }
        else if(myPublicHackathonIds.size() != 0 && myPrivateHackathonIds.size() != 0){
            for (String myPublicHackathonId: myPublicHackathonIds){
                if (myPrivateHackathonIds.contains(myPublicHackathonId)){
                    Toast.makeText(this, Common.ERROR_HACKATHONS_CONFLICT, Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }

        final ArrayList<String> myHackathonIds = new ArrayList<>();
        myHackathonIds.addAll(myPublicHackathonIds);
        myHackathonIds.addAll(myPrivateHackathonIds);

        final ParseUser currentUser = ParseUser.getCurrentUser();

        // myInterests
        HashMap<String, ParseObject> allInterests = ParseImpl.get_allInterests();
        ParseRelation<ParseObject> myInterests = currentUser.getRelation(Common.OBJECT_USER_INTERESTS);
        for (String myInterestId: myInterestIds){
            myInterests.add(allInterests.get(myInterestId));
        }

        // mySkills
        HashMap<String, ParseObject> allSkills = ParseImpl.get_allSkills();
        ParseRelation<ParseObject> mySkills = currentUser.getRelation(Common.OBJECT_USER_SKILLS);
        for (String mySkillId: mySkillIds){
            mySkills.add(allSkills.get(mySkillId));
        }

        // myNeedGuyHackathons
        HashMap<String, ParseObject> allHackathons = ParseImpl.get_allHackathons();
        ParseRelation<ParseObject> myNeedGuyHackathons = currentUser.getRelation(Common.OBJECT_USER_MYNEEDGUYHACKATHONS);
        for (String myPublicHackathonId: myPublicHackathonIds){
            myNeedGuyHackathons.add(allHackathons.get(myPublicHackathonId));
        }

        // myHackathons
        ParseRelation<ParseObject> myHackathons = currentUser.getRelation(Common.OBJECT_USER_MYHACKATHONS);
        for (String myHackathonId: myHackathonIds){
            myHackathons.add(allHackathons.get(myHackathonId));
        }


        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                // add currentUser into interested_hackers of Interest
                for (String myInterestId: myInterestIds){
                    ParseQuery<ParseObject> interestObjectQuery = ParseQuery.getQuery(Common.OBJECT_INTEREST);
                    interestObjectQuery.getInBackground(myInterestId, new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject interest, ParseException e) {
                            if (e == null){
                                ParseRelation<ParseUser> interested_hackers = interest.getRelation(Common.OBJECT_INTEREST_INTERESTED_HACKERS);
                                interested_hackers.add(currentUser); // Must do add(currentUser) in SaveCallback. Otherwise, won't add.
                                interest.saveInBackground();
                            }
                        }
                    });
                }

                // add currentUser into skilled_hackers of Skill
                for (String mySkillId: mySkillIds){
                    ParseQuery<ParseObject> skillObjectQuery = ParseQuery.getQuery(Common.OBJECT_SKILL);
                    skillObjectQuery.getInBackground(mySkillId, new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject skill, ParseException e) {
                            if (e == null){
                                ParseRelation<ParseUser> skilled_hackers = skill.getRelation(Common.OBJECT_SKILL_SKILLED_HACKERS);
                                skilled_hackers.add(currentUser);
                                skill.saveInBackground();
                            }
                        }
                    });
                }

                // add currentUser into hackersNeedGuy of Hackathon
                for (String myPublicHackathonId: myPublicHackathonIds){
                    ParseQuery<ParseObject> hackathonObjectQuery = ParseQuery.getQuery(Common.OBJECT_HACKATHON);
                    hackathonObjectQuery.getInBackground(myPublicHackathonId, new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject hackathon, ParseException e) {
                            if (e == null){
                                ParseRelation<ParseUser> hackersNeedGuy = hackathon.getRelation(Common.OBJECT_HACKATHON_HACKERSNEEDGUY);
                                hackersNeedGuy.add(currentUser);
                                hackathon.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {

                                        // add currentUser into hackers of Hackathon
                                        for (String myHackathonId: myHackathonIds){
                                            ParseQuery<ParseObject> hackathonObjectQuery = ParseQuery.getQuery(Common.OBJECT_HACKATHON);
                                            hackathonObjectQuery.getInBackground(myHackathonId, new GetCallback<ParseObject>() {
                                                @Override
                                                public void done(ParseObject hackathon, ParseException e) {
                                                    if (e == null){
                                                        ParseRelation<ParseUser> hackers = hackathon.getRelation(Common.OBJECT_HACKATHON_HACKERS);
                                                        hackers.add(currentUser);
                                                        hackathon.saveInBackground();
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        }
                    });
                }


            }
        });

        CreateProfileActivity.this.finish();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        getSupportActionBar().setTitle(R.string.create_profile);

        currentUser = ParseUser.getCurrentUser();
        ParseFile imgFile = currentUser.getParseFile(Common.OBJECT_USER_PROFILE_PIC);
        Picasso.with(this)
                .load(imgFile.getUrl())
                .into(imgProfile);
        txtUsername.setText(currentUser.getString(Common.OBJECT_USER_NICK));

    }

    public void onEvent(AddInterestToCreateProfileEvent event){
        myInterestIds = event.mInterestIdList;
        populateToInterestField(myInterestIds);
    }

    public void onEvent(AddSkillToCreateProfileEvent event){
        mySkillIds = event.mSkillIdList;
        populateToSkillField(mySkillIds);

    }

    public void onEvent(AddPublicHackathonToCreateProfileEvent event){
        myPublicHackathonIds = event.mPublicHackathonIdList;
        populateToPublicHackathonField(myPublicHackathonIds);
    }

    public void onEvent(AddPrivateHackathonToCreateProfileEvent event){
        myPrivateHackathonIds = event.mPrivateHackathonIdList;
        populateToPrivateHackathonField(myPrivateHackathonIds);
    }

    private void populateToPrivateHackathonField(List<String> hackathonIds){
        TextView[] hackathonList = new TextView[hackathonIds.size()];
        int idx = 0;
        for(String id: hackathonIds){

            //Create a new stylized text view
            TextView tv = new TextView(this);
            tv.setText(ParseImpl.getHackathonName(id));
            tv.setTextSize(16);
            tv.setTextColor(getResources().getColor(R.color.white));
            tv.setPadding(5, 5, 5, 5);
            tv.setBackgroundColor(getResources().getColor(R.color.hack_together_blue));
            hackathonList[idx] = tv;

            idx++;

        }

        //Uses the helper function to make sure all participant names are appropriately displayed
        // and not cut off due to size constraints
        addViewsToFlowLayout(ll_PrivateHackathonContent, hackathonList, this);
    }

    private void populateToPublicHackathonField(List<String> hackathonIds){
        TextView[] hackathonList = new TextView[hackathonIds.size()];
        int idx = 0;
        for(String id: hackathonIds){

            //Create a new stylized text view
            TextView tv = new TextView(this);
            tv.setText(ParseImpl.getHackathonName(id));
            tv.setTextSize(16);
            tv.setTextColor(getResources().getColor(R.color.white));
            tv.setPadding(5, 5, 5, 5);
            tv.setBackgroundColor(getResources().getColor(R.color.hack_together_blue));
            hackathonList[idx] = tv;

            idx++;

        }

        //Uses the helper function to make sure all participant names are appropriately displayed
        // and not cut off due to size constraints
        addViewsToFlowLayout(ll_PublicHackathonContent, hackathonList, this);
    }

    private void populateToSkillField(List<String> skillIds){
        TextView[] skillList = new TextView[skillIds.size()];
        int idx = 0;
        for(String id : skillIds){

            //Create a new stylized text view
            TextView tv = new TextView(this);
            tv.setText(ParseImpl.getSkillName(id));
            tv.setTextSize(16);
            tv.setTextColor(getResources().getColor(R.color.white));
            tv.setPadding(5, 5, 5, 5);
            tv.setBackgroundColor(getResources().getColor(R.color.hack_together_blue));
            skillList[idx] = tv;

            idx++;

        }

        //Uses the helper function to make sure all participant names are appropriately displayed
        // and not cut off due to size constraints
        addViewsToFlowLayout(ll_MySkillsContent, skillList, this);

    }

    private void populateToInterestField(List<String> interestIds){
        TextView[] interestList = new TextView[interestIds.size()];
        int idx = 0;
        for(String id : interestIds){

            //Create a new stylized text view
            TextView tv = new TextView(this);
            tv.setText(ParseImpl.getInterestName(id));
            tv.setTextSize(16);
            tv.setTextColor(getResources().getColor(R.color.white));
            tv.setPadding(5, 5, 5, 5);
            tv.setBackgroundColor(getResources().getColor(R.color.hack_together_blue));
            interestList[idx] = tv;

            idx++;

        }

        //Uses the helper function to make sure all participant names are appropriately displayed
        // and not cut off due to size constraints
        addViewsToFlowLayout(ll_MyInterestsContent, interestList, this);

    }



    //Called when the Activity starts, or when the App is coming to the foreground.
    public void onResume() {
        super.onResume();

        // Check to see the state of the LayerClient, and if everything is set up, then good; do nothing.
        Utils.checkSetup(this);

        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("inMessageActivity", false);
        installation.put("user", ParseUser.getCurrentUser());
        installation.saveInBackground();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
}
