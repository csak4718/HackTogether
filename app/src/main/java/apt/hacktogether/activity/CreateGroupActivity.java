package apt.hacktogether.activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import apt.hacktogether.R;
import apt.hacktogether.event.AddInterestToCreateGroupEvent;
import apt.hacktogether.event.AddOneHackathonToCreateGroupEvent;
import apt.hacktogether.event.AddPersonToCreateGroupEvent;
import apt.hacktogether.event.AddSkillToCreateGroupEvent;
import apt.hacktogether.layer.LayerImpl;
import apt.hacktogether.layout.PredicateLayout;
import apt.hacktogether.parse.ParseImpl;
import apt.hacktogether.utils.Common;
import apt.hacktogether.utils.ParseUtils;
import apt.hacktogether.utils.Utils;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

public class CreateGroupActivity extends BaseActivity {
    public static final String TAG = Common.TAG_CREATE_GROUP_ACTIVITY;

    private String hackathonName;
    private ArrayList<String> selectedPersonIds;
    private ArrayList<String> groupInterestIds;
    private ArrayList<String> lookForSkillIds;

    @Bind(R.id.edt_group_name) EditText edtGroupName;

    @OnClick(R.id.ll_hackathon) void goAddOneHackathon(){
        Utils.gotoAddOneHackathonActivity(CreateGroupActivity.this);
    }
    @Bind(R.id.ll_hackathon_content) FlowLayout ll_HackathonContent;

    @OnClick(R.id.ll_members) void goAddPerson(){
        Utils.gotoAddPersonActivity(CreateGroupActivity.this, selectedPersonIds, TAG);
    }
    @Bind(R.id.ll_member_content) FlowLayout ll_MemberContent;

    @Bind(R.id.switch_need_teammates) Switch switchNeedTeammates;
    @Bind(R.id.spec_container) LinearLayout ll_SpecContainer;

    @OnClick(R.id.ll_group_interests) void goAddInterest(){
        Utils.gotoAddInterestActivity(CreateGroupActivity.this, groupInterestIds, TAG);
    }
    @Bind(R.id.ll_group_interests_content) FlowLayout ll_GroupInterestsContent;

    @OnClick(R.id.ll_look_for_skills) void goAddSkill(){
        Utils.gotoAddSkillActivity(CreateGroupActivity.this, lookForSkillIds, TAG);
    }
    @Bind(R.id.ll_look_for_skills_content) FlowLayout ll_LookForSkillsContent;



    @OnClick(R.id.btn_confirm) void create(){
        final ParseUser currentUser = ParseUser.getCurrentUser();
        final ParseObject group = new ParseObject(Common.OBJECT_GROUP);

        // groupName
        group.put(Common.OBJECT_GROUP_NAME, edtGroupName.getText().toString());

        // hackathonAttend
        group.put(Common.OBJECT_GROUP_HACKATHONATTEND, hackathonName);

        // members
        ParseRelation<ParseUser> members = group.getRelation(Common.OBJECT_GROUP_MEMBERS);
        members.add(currentUser);

        if (switchNeedTeammates.isChecked()){
            // needGuy
            // TODO, after finishing, Group Tab will show something if needGuy is true
            group.put(Common.OBJECT_GROUP_NEEDGUY, true);

            // groupInterests
            HashMap<String, ParseObject> allInterests = ParseImpl.get_allInterests();
            ParseRelation<ParseObject> groupInterests = group.getRelation(Common.OBJECT_GROUP_GROUPINTERESTS);
            for (String groupInterestId: groupInterestIds){
                groupInterests.add(allInterests.get(groupInterestId));
            }

            // lookForSkills
            HashMap<String, ParseObject> allSkills = ParseImpl.get_allSkills();
            ParseRelation<ParseObject> lookForSkills = group.getRelation(Common.OBJECT_GROUP_LOOKFORSKILLS);
            for (String lookForSkillId: lookForSkillIds){
                lookForSkills.add(allSkills.get(lookForSkillId));
            }
        }
        else{
            // needGuy
            group.put(Common.OBJECT_GROUP_NEEDGUY, false);
        }

        // pendingMembers
        HashMap<String, ParseUser> allUsers = ParseImpl.get_allUsers();
        ParseRelation<ParseUser> pendingMembers = group.getRelation(Common.OBJECT_GROUP_PENDINGMEMBERS);
        for (String selectedPersonId: selectedPersonIds){
            pendingMembers.add(allUsers.get(selectedPersonId));
        }

        group.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                // add group into myGroups of User
                ParseRelation<ParseObject> myGroups = currentUser.getRelation(Common.OBJECT_USER_MYGROUPS);
                myGroups.add(group);
                currentUser.saveInBackground();

                // add group into inviteGroups of pending members
                ParseUtils.addGroupToInviteGroups(group, selectedPersonIds);

                if (switchNeedTeammates.isChecked()){
                    // add group into interested_groups of Interest
                    for (String groupInterestId: groupInterestIds){
                        ParseQuery<ParseObject> interestObjectQuery = ParseQuery.getQuery(Common.OBJECT_INTEREST);
                        interestObjectQuery.getInBackground(groupInterestId, new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject interest, ParseException e) {
                                if (e == null){
                                    ParseRelation<ParseObject> interested_groups = interest.getRelation(Common.OBJECT_INTEREST_INTERESTED_GROUPS);
                                    interested_groups.add(group);
                                    interest.saveInBackground();
                                }
                            }
                        });
                    }

                    // add group into lookFor_groups of Skill
                    for (String lookForSkillId: lookForSkillIds){
                        ParseQuery<ParseObject> skillObjectQuery = ParseQuery.getQuery(Common.OBJECT_SKILL);
                        skillObjectQuery.getInBackground(lookForSkillId, new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject skill, ParseException e) {
                                if (e == null){
                                    ParseRelation<ParseObject> lookFor_groups = skill.getRelation(Common.OBJECT_SKILL_LOOKFOR_GROUPS);
                                    lookFor_groups.add(group);
                                    skill.saveInBackground();
                                }
                            }
                        });
                    }
                }



                ParseQuery<ParseObject> hackathonObjectQuery = ParseQuery.getQuery(Common.OBJECT_HACKATHON);
                hackathonObjectQuery.whereEqualTo(Common.OBJECT_HACKATHON_NAME, hackathonName);
                hackathonObjectQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(final ParseObject hackathon, ParseException e) {
                        if (e == null){
                            // hackers of Hackathon (simply add current user. Later, in invitation group tab, if user hit accept, he will be added into hackers of Hackathon AND not into hackersNeedGuy of Hackathon)
                            ParseRelation<ParseUser> hackers = hackathon.getRelation(Common.OBJECT_HACKATHON_HACKERS);
                            hackers.add(currentUser);

                            // groupsNeedGuy of Hackathon (if switch is on)
                            if (switchNeedTeammates.isChecked()){
                                ParseRelation<ParseObject> groupsNeedGuy = hackathon.getRelation(Common.OBJECT_HACKATHON_GROUPSNEEDGUY);
                                groupsNeedGuy.add(group);
                            }

                            // groups of Hackathon
                            ParseRelation<ParseObject> groups = hackathon.getRelation(Common.OBJECT_HACKATHON_GROUPS);
                            groups.add(group);

                            hackathon.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    // myHackathons of User
                                    ParseRelation<ParseObject> myHackathons = currentUser.getRelation(Common.OBJECT_USER_MYHACKATHONS);
                                    myHackathons.add(hackathon); // if hackathon is already there, Parse will handle this automatically
                                    currentUser.saveInBackground();
                                }
                            });


                            // Do not put the hackathon into myNeedGuyHackathons of current user
                        }
                    }
                });

            }
        });





        CreateGroupActivity.this.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.create_group);

        switchNeedTeammates.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) ll_SpecContainer.setVisibility(View.VISIBLE);
                else ll_SpecContainer.setVisibility(View.GONE);
            }
        });

        
    }

    public void onEvent(AddPersonToCreateGroupEvent event) {
        selectedPersonIds = event.mPersonIdList;
        populateToMemberField(selectedPersonIds);
    }

    public void onEvent(AddInterestToCreateGroupEvent event){
        groupInterestIds = event.mInterestIdList;
        populateToInterestField(groupInterestIds);
    }

    public void onEvent(AddSkillToCreateGroupEvent event){
        lookForSkillIds = event.mSkillIdList;
        populateToSkillField(lookForSkillIds);
    }

    public void onEvent(AddOneHackathonToCreateGroupEvent event){
        hackathonName = event.hackathon_name;
        populateToHackathonField(hackathonName);
    }

    private void populateToHackathonField(String hackathon_name){
        TextView[] nameList = new TextView[1];

        TextView tv = new TextView(this);
        tv.setText(hackathon_name);
        tv.setTextSize(16);
        tv.setTextColor(getResources().getColor(R.color.white));
        tv.setPadding(5, 5, 5, 5);
        tv.setBackgroundColor(getResources().getColor(R.color.hack_together_blue));
        nameList[0] = tv;

        addViewsToFlowLayout(ll_HackathonContent, nameList, this);

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
        addViewsToFlowLayout(ll_LookForSkillsContent, skillList, this);

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
        addViewsToFlowLayout(ll_GroupInterestsContent, interestList, this);

    }

    private void populateToMemberField(List<String> participantIds){
        TextView[] participantList = new TextView[participantIds.size()];
        int idx = 0;
        for(String id : participantIds){

            //Create a new stylized text view
            TextView tv = new TextView(this);
            tv.setText(ParseImpl.getUsername(id));
            tv.setTextSize(16);
            tv.setTextColor(getResources().getColor(R.color.white));
            tv.setPadding(5, 5, 5, 5);
            tv.setBackgroundColor(getResources().getColor(R.color.hack_together_blue));
            participantList[idx] = tv;

            idx++;

        }

        //Uses the helper function to make sure all participant names are appropriately displayed
        // and not cut off due to size constraints
        addViewsToFlowLayout(ll_MemberContent, participantList, this);
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
        getMenuInflater().inflate(R.menu.menu_create_group, menu);
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
