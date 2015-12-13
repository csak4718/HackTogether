package apt.hacktogether.activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
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
import apt.hacktogether.event.AddInterestToEditProfileEvent;
import apt.hacktogether.event.AddPrivateHackathonToEditProfileEvent;
import apt.hacktogether.event.AddPublicHackathonToEditProfileEvent;
import apt.hacktogether.event.AddSkillToEditProfileEvent;
import apt.hacktogether.parse.ParseImpl;
import apt.hacktogether.utils.Common;
import apt.hacktogether.utils.ParseUtils;
import apt.hacktogether.utils.Utils;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends BaseActivity {
    public static final String TAG = Common.TAG_EDIT_PROFILE_ACTIVITY;

    private ParseUser currentUser;
    private ArrayList<String> myInterestIds;
    private ArrayList<String> mySkillIds;
    private ArrayList<String> myPublicHackathonIds;
    private ArrayList<String> myPrivateHackathonIds;

    @Bind(R.id.img_profile) CircleImageView imgProfile;
    @Bind(R.id.txt_username) TextView txtUsername;

    @OnClick(R.id.ll_my_interests) void goAddInterest(){
        Utils.gotoAddInterestActivity(EditProfileActivity.this, myInterestIds, TAG);
    }
    @Bind(R.id.ll_my_interests_content) FlowLayout ll_MyInterestsContent;

    @OnClick(R.id.ll_my_skills) void goAddSkill(){
        Utils.gotoAddSkillActivity(EditProfileActivity.this, mySkillIds, TAG);
    }
    @Bind(R.id.ll_my_skills_content) FlowLayout ll_MySkillsContent;

    @OnClick(R.id.ll_public_hackathon) void goAddPublicHackathon(){
        Utils.gotoAddPublicHackathonActivity(EditProfileActivity.this, myPublicHackathonIds, TAG);
    }
    @Bind(R.id.ll_public_hackathon_content) FlowLayout ll_PublicHackathonContent;

    @OnClick(R.id.ll_private_hackathon) void goAddPrivateHackathon(){
        Utils.gotoAddPrivateHackathonActivity(EditProfileActivity.this, myPrivateHackathonIds, TAG);
    }
    @Bind(R.id.ll_private_hackathon_content) FlowLayout ll_PrivateHackathonContent;



    @OnClick(R.id.btn_confirm) void save(){
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




        final ArrayList<String> myHackathonIds = new ArrayList<>(); // First do: myPublicHackathonIds + myPrivateHackathonIds
        myHackathonIds.addAll(myPublicHackathonIds);
        myHackathonIds.addAll(myPrivateHackathonIds);

        final ArrayList<String> delete_myInterestIds = new ArrayList<>();
        final ArrayList<String> delete_mySkillIds = new ArrayList<>();
        final ArrayList<String> delete_myNeedGuyHackathonIds = new ArrayList<>(); //2nd easy: query old, compare with myPublicHackathonIds, create delete_myNeedGuyHackathonIds
        final ArrayList<String> delete_myHackathonIds = new ArrayList<>(); //3rd: query old myHackathons, compare with myHackathonIds, create delete_myHackathonIds


        final HashMap<String, ParseObject> allHackathons = ParseImpl.get_allHackathons();

        // myNeedGuyHackathons
        final ParseRelation<ParseObject> myNeedGuyHackathons = currentUser.getRelation(Common.OBJECT_USER_MYNEEDGUYHACKATHONS);
        ParseQuery<ParseObject> myNeedGuyHackathonsQuery = myNeedGuyHackathons.getQuery();
        myNeedGuyHackathonsQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> original_myNeedGuyHackathons, ParseException e) {
                if (e == null){
                    for(ParseObject original_myNeedGuyHackathon: original_myNeedGuyHackathons){
                        if(!myPublicHackathonIds.contains(original_myNeedGuyHackathon.getObjectId())) delete_myNeedGuyHackathonIds.add(original_myNeedGuyHackathon.getObjectId());
                    }

                    for(String delete_myNeedGuyHackathonId: delete_myNeedGuyHackathonIds){
                        ParseUtils.removeUnwantedMyNeedGuyHackathon(currentUser.getObjectId(), delete_myNeedGuyHackathonId);
                    }
                }
            }
        });
        for(String myPublicHackathonId: myPublicHackathonIds){
            myNeedGuyHackathons.add(allHackathons.get(myPublicHackathonId));
        }

        // myHackathons
        final ParseRelation<ParseObject> myHackathons = currentUser.getRelation(Common.OBJECT_USER_MYHACKATHONS);
        ParseQuery<ParseObject> myHackathonsQuery = myHackathons.getQuery();
        myHackathonsQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> original_myHackathons, ParseException e) {
                if (e==null){
                    for(ParseObject original_myHackathon: original_myHackathons){
                        if(!myHackathonIds.contains(original_myHackathon.getObjectId())) delete_myHackathonIds.add(original_myHackathon.getObjectId());
                    }

                    for(String delete_myHackathonId: delete_myHackathonIds){
                        ParseUtils.removeUnwantedMyHackathon(currentUser.getObjectId(), delete_myHackathonId);
                    }
                }
            }
        });
        for(String myHackathonId: myHackathonIds){
            myHackathons.add(allHackathons.get(myHackathonId));
        }


        // myInterests
        final HashMap<String, ParseObject> allInterests = ParseImpl.get_allInterests();
        final ParseRelation<ParseObject> myInterests = currentUser.getRelation(Common.OBJECT_USER_INTERESTS);
        ParseQuery<ParseObject> interestsQuery = myInterests.getQuery();
        interestsQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> originalInterests, ParseException e) {
                if (e==null){
                    for (ParseObject originalInterest: originalInterests){
                        if(!myInterestIds.contains(originalInterest.getObjectId())) delete_myInterestIds.add(originalInterest.getObjectId());
                    }

                    for (String delete_myInterestId: delete_myInterestIds){
                        ParseUtils.removeUnwantedMyInterest(currentUser.getObjectId(), delete_myInterestId);
                    }
                }

            }
        });
        for (String myInterestId: myInterestIds){ // Do not put this loop into the above done function. Won't work.
            myInterests.add(allInterests.get(myInterestId));
        }

        // mySkills
        final HashMap<String, ParseObject> allSkills = ParseImpl.get_allSkills();
        final ParseRelation<ParseObject> mySkills = currentUser.getRelation(Common.OBJECT_USER_SKILLS);
        ParseQuery<ParseObject> skillsQuery = mySkills.getQuery();
        skillsQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> originalSkills, ParseException e) {
                if (e==null){
                    for (ParseObject originalSkill: originalSkills){
                        if(!mySkillIds.contains(originalSkill.getObjectId())) delete_mySkillIds.add(originalSkill.getObjectId());
                    }

                    for (String delete_mySkillId: delete_mySkillIds){
                        ParseUtils.removeUnwantedMySkill(currentUser.getObjectId(), delete_mySkillId);
                    }
                }

            }
        });
        for (String mySkillId: mySkillIds){ // Do not put this loop into the above done function. Won't work.
            mySkills.add(allSkills.get(mySkillId));
        }


        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                // remove currentUser from interested_hackers of particular Interest (ex: Android App)
                for (String delete_myInterestId: delete_myInterestIds){
                    ParseQuery<ParseObject> interestObjectQuery = ParseQuery.getQuery(Common.OBJECT_INTEREST);
                    interestObjectQuery.getInBackground(delete_myInterestId, new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject interest, ParseException e) {
                            if (e == null){
//                                ParseRelation<ParseUser> interested_hackers = interest.getRelation(Common.OBJECT_INTEREST_INTERESTED_HACKERS);
//                                interested_hackers.remove(currentUser); // Must do in SaveCallback. Otherwise, won't remove.
//                                interest.saveInBackground();
                                ParseUtils.removeUserFromInterestedHackers(interest.getObjectId(), currentUser.getObjectId());
                            }
                        }
                    });
                }
                // add currentUser into interested_hackers of particular Interest (ex: Web App)
                for (String myInterestId: myInterestIds){
                    ParseQuery<ParseObject> interestObjectQuery = ParseQuery.getQuery(Common.OBJECT_INTEREST);
                    interestObjectQuery.getInBackground(myInterestId, new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject interest, ParseException e) {
                            if (e == null){
                                ParseRelation<ParseUser> interested_hackers = interest.getRelation(Common.OBJECT_INTEREST_INTERESTED_HACKERS);
                                interested_hackers.add(currentUser); // Must do in SaveCallback. Otherwise, won't add.
                                interest.saveInBackground();
                            }
                        }
                    });
                }

                // remove currentUser from skilled_hackers of particular Skill
                for (String delete_mySkillId: delete_mySkillIds){
                    ParseQuery<ParseObject> skillObjectQuery = ParseQuery.getQuery(Common.OBJECT_SKILL);
                    skillObjectQuery.getInBackground(delete_mySkillId, new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject skill, ParseException e) {
                            if (e == null){
//                                ParseRelation<ParseUser> skilled_hackers = skill.getRelation(Common.OBJECT_SKILL_SKILLED_HACKERS);
//                                skilled_hackers.remove(currentUser); // Must do in SaveCallback. Otherwise, won't remove.
//                                skill.saveInBackground();
                                ParseUtils.removeUserFromSkilledHackers(skill.getObjectId(), currentUser.getObjectId());
                            }
                        }
                    });
                }
                // add currentUser into skilled_hackers of particular Skill
                for (String mySkillId: mySkillIds){
                    ParseQuery<ParseObject> skillObjectQuery = ParseQuery.getQuery(Common.OBJECT_SKILL);
                    skillObjectQuery.getInBackground(mySkillId, new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject skill, ParseException e) {
                            if (e == null){
                                ParseRelation<ParseUser> skilled_hackers = skill.getRelation(Common.OBJECT_SKILL_SKILLED_HACKERS);
                                skilled_hackers.add(currentUser); // Must do in SaveCallback. Otherwise, won't add.
                                skill.saveInBackground();
                            }
                        }
                    });
                }

                // remove currentUser from hackersNeedGuy of particular Hackathon
                for (String delete_myNeedGuyHackathonId: delete_myNeedGuyHackathonIds){
                    ParseQuery<ParseObject> hackathonObjectQuery = ParseQuery.getQuery(Common.OBJECT_HACKATHON);
                    hackathonObjectQuery.getInBackground(delete_myNeedGuyHackathonId, new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject hackathon, ParseException e) {
                            if (e == null){
//                                ParseRelation<ParseUser> hackersNeedGuy = hackathon.getRelation(Common.OBJECT_HACKATHON_HACKERSNEEDGUY);
//                                hackersNeedGuy.remove(currentUser); // Must do in SaveCallback. Otherwise, won't remove.
//                                hackathon.saveInBackground();
                                ParseUtils.removeUserFromHackersNeedGuy(hackathon.getObjectId(), currentUser.getObjectId());
                            }
                        }
                    });
                }
                // add currentUser into hackersNeedGuy of particular Hackathon
                for (String myPublicHackathonId: myPublicHackathonIds){
                    ParseQuery<ParseObject> hackathonObjectQuery = ParseQuery.getQuery(Common.OBJECT_HACKATHON);
                    hackathonObjectQuery.getInBackground(myPublicHackathonId, new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject hackathon, ParseException e) {
                            if (e == null){
                                ParseRelation<ParseUser> hackersNeedGuy = hackathon.getRelation(Common.OBJECT_HACKATHON_HACKERSNEEDGUY);
                                hackersNeedGuy.add(currentUser); // Must do in SaveCallback. Otherwise, won't add.
                                hackathon.saveInBackground();
                            }
                        }
                    });
                }

                // remove currentUser from hackers of particular Hackathon
                for (String delete_myHackathonId: delete_myHackathonIds){
                    ParseQuery<ParseObject> hackathonObjectQuery = ParseQuery.getQuery(Common.OBJECT_HACKATHON);
                    hackathonObjectQuery.getInBackground(delete_myHackathonId, new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject hackathon, ParseException e) {
                            if (e == null){
//                                ParseRelation<ParseUser> hackers = hackathon.getRelation(Common.OBJECT_HACKATHON_HACKERS);
//                                hackers.remove(currentUser); // Must do in SaveCallback. Otherwise, won't remove.
//                                hackathon.saveInBackground();
                                ParseUtils.removeUserFromHackers(hackathon.getObjectId(), currentUser.getObjectId());
                            }
                        }
                    });
                }
                // add currentUser into hackers of particular Hackathon
                for (String myHackathonId: myHackathonIds){
                    ParseQuery<ParseObject> hackathonObjectQuery = ParseQuery.getQuery(Common.OBJECT_HACKATHON);
                    hackathonObjectQuery.getInBackground(myHackathonId, new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject hackathon, ParseException e) {
                            if (e == null){
                                ParseRelation<ParseUser> hackers = hackathon.getRelation(Common.OBJECT_HACKATHON_HACKERS);
                                hackers.add(currentUser); // Must do in SaveCallback. Otherwise, won't add.
                                hackathon.saveInBackground();
                            }
                        }
                    });
                }

            }
        });


        EditProfileActivity.this.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.edit_profile);

        currentUser = ParseUser.getCurrentUser();
        ParseFile imgFile = currentUser.getParseFile(Common.OBJECT_USER_PROFILE_PIC);
        Picasso.with(this)
                .load(imgFile.getUrl())
                .into(imgProfile);
        txtUsername.setText(currentUser.getString(Common.OBJECT_USER_NICK));

        initializeMyInterests();
        initializeMySkills();
        initializePublicHackathons_and_PrivateHackathons();
    }

    public void onEvent(AddInterestToEditProfileEvent event){
        myInterestIds = event.mInterestIdList;
        populateToInterestField(myInterestIds);
    }

    public void onEvent(AddSkillToEditProfileEvent event){
        mySkillIds = event.mSkillIdList;
        populateToSkillField(mySkillIds);
    }

    public void onEvent(AddPublicHackathonToEditProfileEvent event){
        myPublicHackathonIds = event.mPublicHackathonIdList;
        populateToPublicHackathonField(myPublicHackathonIds);
    }

    public void onEvent(AddPrivateHackathonToEditProfileEvent event){
        myPrivateHackathonIds = event.mPrivateHackathonIdList;
        populateToPrivateHackathonField(myPrivateHackathonIds);
    }

    private void initializePublicHackathons_and_PrivateHackathons(){
        if (myPublicHackathonIds == null) myPublicHackathonIds = new ArrayList<>();
        if (myPrivateHackathonIds == null) myPrivateHackathonIds = new ArrayList<>();

        ParseRelation<ParseObject> myHackathonsRelation = currentUser.getRelation(Common.OBJECT_USER_MYHACKATHONS);
        ParseQuery<ParseObject> hackathonsQuery = myHackathonsRelation.getQuery();
        hackathonsQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(final List<ParseObject> myHackathons, ParseException e) {

                ParseRelation<ParseObject> myNeedGuyHackathons = currentUser.getRelation(Common.OBJECT_USER_MYNEEDGUYHACKATHONS);
                ParseQuery<ParseObject> hackathonsQuery = myNeedGuyHackathons.getQuery();
                hackathonsQuery.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> hackathons, ParseException e) {
                        // initialize myPublicHackathonIds
                        for (ParseObject hackathon: hackathons){
                            myPublicHackathonIds.add(hackathon.getObjectId());
                        }
                        populateToPublicHackathonField(myPublicHackathonIds);

                        // initialize myPrivateHackathonIds
                        ArrayList<String> myHackathonIds = new ArrayList<>();
                        for (ParseObject myHackathon: myHackathons){
                            myHackathonIds.add(myHackathon.getObjectId());
                        }
                        for (String myHackathonId: myHackathonIds){
                            if(!myPublicHackathonIds.contains(myHackathonId)) myPrivateHackathonIds.add(myHackathonId);
                        }
                        populateToPrivateHackathonField(myPrivateHackathonIds);
                    }
                });

            }
        });

    }

    private void populateToPrivateHackathonField(List<String> hackathonIds){
        TextView[] hackathonList = new TextView[hackathonIds.size()];
        int idx = 0;
        for(String id : hackathonIds){

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
        for(String id : hackathonIds){

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

    private void initializeMySkills(){
        if (mySkillIds == null) mySkillIds = new ArrayList<>();

        ParseRelation<ParseObject> mySkills = currentUser.getRelation(Common.OBJECT_USER_SKILLS);
        ParseQuery<ParseObject> skillsQuery = mySkills.getQuery();
        skillsQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> skills, ParseException e) {
                for (ParseObject skill: skills){
                    mySkillIds.add(skill.getObjectId());
                }
                populateToSkillField(mySkillIds);
            }
        });

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

    private void initializeMyInterests(){
        if (myInterestIds == null) myInterestIds = new ArrayList<>();

        ParseRelation<ParseObject> myInterests = currentUser.getRelation(Common.OBJECT_USER_INTERESTS);
        ParseQuery<ParseObject> interestsQuery = myInterests.getQuery();
        interestsQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> interests, ParseException e) {
                for (ParseObject interest: interests){
                    myInterestIds.add(interest.getObjectId());
                }
                populateToInterestField(myInterestIds);
            }
        });

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

    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
