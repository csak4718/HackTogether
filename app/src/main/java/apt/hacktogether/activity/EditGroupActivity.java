package apt.hacktogether.activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
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
import apt.hacktogether.event.AddInterestToEditGroupEvent;
import apt.hacktogether.event.AddPersonToEditGroupEvent;
import apt.hacktogether.event.AddSkillToEditGroupEvent;
import apt.hacktogether.parse.ParseImpl;
import apt.hacktogether.utils.Common;
import apt.hacktogether.utils.ParseUtils;
import apt.hacktogether.utils.Utils;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import de.hdodenhof.circleimageview.CircleImageView;

public class EditGroupActivity extends BaseActivity {
    public static final String TAG = Common.TAG_EDIT_GROUP_ACTIVITY;

    private String groupId;
    private ParseObject myGroup;
    private ArrayList<String> moreMemberIds;
    private ArrayList<String> groupInterestIds;
    private ArrayList<String> lookForSkillIds;
    private ArrayList<String> inactivePersonIds;


    @Bind(R.id.txt_group_name) TextView txtGroupName;
    @Bind(R.id.txt_hackathon_attend) TextView txtHackathonAttend;
    @Bind(R.id.ll_members) FlowLayout ll_Members;
    @Bind(R.id.ll_pending_members) FlowLayout ll_PendingMembers;

    @OnClick(R.id.ll_more_members) void goAddPerson(){
        Utils.fromEditGroupToAddPersonActivity(EditGroupActivity.this, moreMemberIds, inactivePersonIds, TAG);
    }
    @Bind(R.id.ll_more_member_content) FlowLayout ll_MoreMemberContent;

    @Bind(R.id.switch_need_teammates) Switch switchNeedTeammates;
    @Bind(R.id.spec_container) LinearLayout ll_SpecContainer;

    @OnClick(R.id.ll_group_interests) void goAddInterest(){
        Utils.gotoAddInterestActivity(EditGroupActivity.this, groupInterestIds, TAG);
    }
    @Bind(R.id.ll_group_interests_content) FlowLayout ll_GroupInterestsContent;

    @OnClick(R.id.ll_look_for_skills) void goAddSkill(){
        Utils.gotoAddSkillActivity(EditGroupActivity.this, lookForSkillIds, TAG);
    }
    @Bind(R.id.ll_look_for_skills_content) FlowLayout ll_LookForSkillsContent;



    @OnClick(R.id.btn_confirm) void save(){
        if(switchNeedTeammates.isChecked()){
            if(groupInterestIds == null || groupInterestIds.size() == 0){
                Toast.makeText(this, Common.ERROR_NO_GROUP_INTERESTS, Toast.LENGTH_SHORT).show();
                return;
            }
            else if(lookForSkillIds == null || lookForSkillIds.size() == 0){
                Toast.makeText(this, Common.ERROR_NO_LOOK_FOR_SKILLS, Toast.LENGTH_SHORT).show();
                return;
            }
        }



        final ArrayList<String> delete_groupInterestIds = new ArrayList<>();
        final ArrayList<String> delete_lookForSkillIds = new ArrayList<>();

        if (moreMemberIds == null) moreMemberIds = new ArrayList<>();

        // pendingMembers
        HashMap<String, ParseUser> allUsers = ParseImpl.get_allUsers();
        ParseRelation<ParseUser> pendingMembers = myGroup.getRelation(Common.OBJECT_GROUP_PENDINGMEMBERS);
        for (String selectedPersonId: moreMemberIds){
            pendingMembers.add(allUsers.get(selectedPersonId));
        }

        if (switchNeedTeammates.isChecked()){
            // needGuy
            myGroup.put(Common.OBJECT_GROUP_NEEDGUY, true);


            // groupInterests
            final HashMap<String, ParseObject> allInterests = ParseImpl.get_allInterests();
            final ParseRelation<ParseObject> groupInterests = myGroup.getRelation(Common.OBJECT_GROUP_GROUPINTERESTS);
            ParseQuery<ParseObject> interestsQuery = groupInterests.getQuery();
            interestsQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> originalInterests, ParseException e) {
                    if (e==null){
                        for (ParseObject originalInterest: originalInterests){
                            if(!groupInterestIds.contains(originalInterest.getObjectId())) delete_groupInterestIds.add(originalInterest.getObjectId());
                        }

                        for (String delete_groupInterestId: delete_groupInterestIds){
                            ParseUtils.removeUnwantedGroupInterest(myGroup.getObjectId(), delete_groupInterestId);
                        }
                    }

                }
            });
            for (String groupInterestId: groupInterestIds){ // Do not put this loop into the above done function. Won't work.
                groupInterests.add(allInterests.get(groupInterestId));
            }


            // lookForSkills
            final HashMap<String, ParseObject> allSkills = ParseImpl.get_allSkills();
            final ParseRelation<ParseObject> lookForSkills = myGroup.getRelation(Common.OBJECT_GROUP_LOOKFORSKILLS);
            ParseQuery<ParseObject> skillsQuery = lookForSkills.getQuery();
            skillsQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> originalSkills, ParseException e) {
                    for (ParseObject originalSkill: originalSkills){
                        if(!lookForSkillIds.contains(originalSkill.getObjectId())) delete_lookForSkillIds.add(originalSkill.getObjectId());
                    }

                    for (String delete_lookForSkillId: delete_lookForSkillIds){
                        ParseUtils.removeUnwantedLookForSkill(myGroup.getObjectId(), delete_lookForSkillId);
                    }
                }
            });
            for (String lookForSkillId: lookForSkillIds){ // // Do not put this loop into the above done function. Won't work.
                lookForSkills.add(allSkills.get(lookForSkillId));
            }
        }
        else{
            // needGuy
            myGroup.put(Common.OBJECT_GROUP_NEEDGUY, false);

            // clear groupInterests and lookForSkills of myGroup
            final ParseRelation<ParseObject> groupInterests = myGroup.getRelation(Common.OBJECT_GROUP_GROUPINTERESTS);
            ParseQuery<ParseObject> interestsQuery = groupInterests.getQuery();
            interestsQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> interests, ParseException e) {
                    for (ParseObject interest: interests) ParseUtils.removeUnwantedGroupInterest(myGroup.getObjectId(), interest.getObjectId());
                }
            });
            final ParseRelation<ParseObject> lookForSkills = myGroup.getRelation(Common.OBJECT_GROUP_LOOKFORSKILLS);
            ParseQuery<ParseObject> skillsQuery = lookForSkills.getQuery();
            skillsQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> skills, ParseException e) {
                    for (ParseObject skill: skills) ParseUtils.removeUnwantedLookForSkill(myGroup.getObjectId(), skill.getObjectId());
                }
            });
        }

        // myGroup.saveInBackground
        myGroup.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                // add mGroup into inviteGroups of New pending members
                ParseUtils.addGroupToInviteGroups(myGroup, moreMemberIds);


                ParseQuery<ParseObject> hackathonObjectQuery = ParseQuery.getQuery(Common.OBJECT_HACKATHON);
                hackathonObjectQuery.whereEqualTo(Common.OBJECT_HACKATHON_NAME, myGroup.getString(Common.OBJECT_GROUP_HACKATHONATTEND));
                hackathonObjectQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject hackathon, ParseException e) {
                        // groupsNeedGuy of Hackathon (if switch is on)
                        if (switchNeedTeammates.isChecked()){
                            ParseRelation<ParseObject> groupsNeedGuy = hackathon.getRelation(Common.OBJECT_HACKATHON_GROUPSNEEDGUY);
                            groupsNeedGuy.add(myGroup);
                        }
                        else{
                            // in Hackathon class, if switchNeedTeammates.isChecked() is false, remove myGroup from "groupsNeedGuy of the hackathon that they participate in"
                            ParseRelation<ParseObject> groupsNeedGuy = hackathon.getRelation(Common.OBJECT_HACKATHON_GROUPSNEEDGUY);
                            groupsNeedGuy.remove(myGroup);
                        }

                        hackathon.saveInBackground();
                    }
                });


                if (switchNeedTeammates.isChecked()){
                    // TODO: change all removes into cloud code
                    // Add and Remove can do at the same time

                    // remove myGroup from interested_groups of particular Interest (ex: Android App)
                    for (String delete_groupInterestId: delete_groupInterestIds){
                        ParseQuery<ParseObject> interestObjectQuery = ParseQuery.getQuery(Common.OBJECT_INTEREST);
                        interestObjectQuery.getInBackground(delete_groupInterestId, new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject interest, ParseException e) {
                                if (e == null){
                                    ParseRelation<ParseObject> interested_groups = interest.getRelation(Common.OBJECT_INTEREST_INTERESTED_GROUPS);
                                    interested_groups.remove(myGroup); // Must do in SaveCallback. Otherwise, won't remove.
                                    interest.saveInBackground();
                                }
                            }
                        });
                    }
                    // add myGroup into interested_groups of particular Interest (ex: Web App)
                    for (String groupInterestId: groupInterestIds){
                        ParseQuery<ParseObject> interestObjectQuery = ParseQuery.getQuery(Common.OBJECT_INTEREST);
                        interestObjectQuery.getInBackground(groupInterestId, new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject interest, ParseException e) {
                                if (e == null){
                                    ParseRelation<ParseObject> interested_groups = interest.getRelation(Common.OBJECT_INTEREST_INTERESTED_GROUPS);
                                    interested_groups.add(myGroup); // Must do in SaveCallback. Otherwise, won't add.
                                    interest.saveInBackground();
                                }
                            }
                        });
                    }


                    // remove myGroup from lookFor_groups of particular Skill
                    for (String delete_lookForSkillId: delete_lookForSkillIds){
                        ParseQuery<ParseObject> skillObjectQuery = ParseQuery.getQuery(Common.OBJECT_SKILL);
                        skillObjectQuery.getInBackground(delete_lookForSkillId, new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject skill, ParseException e) {
                                if (e == null){
                                    ParseRelation<ParseObject> lookFor_groups = skill.getRelation(Common.OBJECT_SKILL_LOOKFOR_GROUPS);
                                    lookFor_groups.remove(myGroup); // Must do in SaveCallback. Otherwise, won't remove.
                                    skill.saveInBackground();
                                }
                            }
                        });
                    }
                    // add myGroup into lookFor_groups of particular Skill
                    for (String lookForSkillId: lookForSkillIds){
                        ParseQuery<ParseObject> skillObjectQuery = ParseQuery.getQuery(Common.OBJECT_SKILL);
                        skillObjectQuery.getInBackground(lookForSkillId, new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject skill, ParseException e) {
                                if (e == null){
                                    ParseRelation<ParseObject> lookFor_groups = skill.getRelation(Common.OBJECT_SKILL_LOOKFOR_GROUPS);
                                    lookFor_groups.add(myGroup);
                                    skill.saveInBackground();
                                }
                            }
                        });
                    }

                }
                else{
                    // in Interest class, remove myGroup from interested_groups of multiple Interest objects
                    ParseRelation<ParseObject> groupInterests = myGroup.getRelation(Common.OBJECT_GROUP_GROUPINTERESTS);
                    ParseQuery<ParseObject> interestsQuery = groupInterests.getQuery();
                    interestsQuery.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> interests, ParseException e) {
                            for (ParseObject interest: interests){
                                ParseRelation<ParseObject> interestedGroups = interest.getRelation(Common.OBJECT_INTEREST_INTERESTED_GROUPS);
                                interestedGroups.remove(myGroup);
                                interest.saveInBackground();
                            }
                        }
                    });

                    // in Skill class, remove myGroup from lookFor_groups of multiple Skill objects
                    ParseRelation<ParseObject> lookForSkills = myGroup.getRelation(Common.OBJECT_GROUP_LOOKFORSKILLS);
                    ParseQuery<ParseObject> skillsQuery = lookForSkills.getQuery();
                    skillsQuery.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> skills, ParseException e) {
                            for (ParseObject skill: skills){
                                ParseRelation<ParseObject> lookForGroups = skill.getRelation(Common.OBJECT_SKILL_LOOKFOR_GROUPS);
                                lookForGroups.remove(myGroup);
                                skill.saveInBackground();
                            }
                        }
                    });

                }

            }
        });

        EditGroupActivity.this.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.edit_group);

        switchNeedTeammates.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) ll_SpecContainer.setVisibility(View.VISIBLE);
                else ll_SpecContainer.setVisibility(View.GONE);
            }
        });


        groupId = getIntent().getStringExtra(Common.EXTRA_GROUP_ID);
        ParseQuery<ParseObject> groupObjectQuery = ParseQuery.getQuery(Common.OBJECT_GROUP);
        groupObjectQuery.getInBackground(groupId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject group, ParseException e) {
                myGroup = group;
                txtGroupName.setText(myGroup.getString(Common.OBJECT_GROUP_NAME));
                txtHackathonAttend.setText(myGroup.getString(Common.OBJECT_GROUP_HACKATHONATTEND));
                getMembersAndPendingMembers(myGroup);
                if (myGroup.getBoolean(Common.OBJECT_GROUP_NEEDGUY)) {
                    switchNeedTeammates.setChecked(true);
                    ll_SpecContainer.setVisibility(View.VISIBLE);
                    initializeGroupInterests(myGroup);
                    initializeLookForSkills(myGroup);
                }
                else {
                    switchNeedTeammates.setChecked(false);
                    ll_SpecContainer.setVisibility(View.GONE);
                }
            }
        });
    }

    public void onEvent(AddPersonToEditGroupEvent event) {
        moreMemberIds = event.mPersonIdList;
        populateToMoreMemberField(moreMemberIds);
    }

    public void onEvent(AddInterestToEditGroupEvent event){
        groupInterestIds = event.mInterestIdList;
        populateToInterestField(groupInterestIds);
    }

    public void onEvent(AddSkillToEditGroupEvent event){
        lookForSkillIds = event.mSkillIdList;
        populateToSkillField(lookForSkillIds);
    }



    private void populateToMoreMemberField(List<String> moreMemberIds){
        LinearLayout[] wrapList = new LinearLayout[moreMemberIds.size()];

        int idx = 0;
        for(String id: moreMemberIds){
            //Create a new stylized text view
            TextView tv = new TextView(this);
            tv.setText(ParseImpl.getUsername(id));
            tv.setTextSize(16);
            tv.setTextColor(getResources().getColor(R.color.white));
            tv.setPadding(5, 5, 5, 5);

            CircleImageView imgProfile = new CircleImageView(this);
            LinearLayout.LayoutParams imgProfile_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            imgProfile.setLayoutParams(imgProfile_params);
            imgProfile.getLayoutParams().height = 50;
            imgProfile.getLayoutParams().width = 50;
            ParseFile imgFile = ParseImpl.getUserIcon(id);
            Picasso.with(this)
                    .load(imgFile.getUrl())
                    .into(imgProfile);

            LinearLayout wrap = new LinearLayout(this);
            wrap.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            wrap.setBackgroundColor(getResources().getColor(R.color.hack_together_blue));
            wrap.addView(imgProfile);
            wrap.addView(tv);
            wrapList[idx] = wrap;

            idx++;

        }

        //Uses the helper function to make sure all participant names are appropriately displayed
        // and not cut off due to size constraints
        addViewsAndIconsToFlowLayout(ll_MoreMemberContent, wrapList, this);
    }

    private void initializeLookForSkills(ParseObject myGroup){
        if (lookForSkillIds == null) lookForSkillIds = new ArrayList<>();

        ParseRelation<ParseObject> lookForSkills = myGroup.getRelation(Common.OBJECT_GROUP_LOOKFORSKILLS);
        ParseQuery<ParseObject> skillsQuery = lookForSkills.getQuery();
        skillsQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> skills, ParseException e) {
                for (ParseObject skill: skills){
                    lookForSkillIds.add(skill.getObjectId());
                }
                populateToSkillField(lookForSkillIds);
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
        addViewsToFlowLayout(ll_LookForSkillsContent, skillList, this);

    }

    private void initializeGroupInterests(ParseObject myGroup){
        if (groupInterestIds == null) groupInterestIds = new ArrayList<>();

        ParseRelation<ParseObject> groupInterests = myGroup.getRelation(Common.OBJECT_GROUP_GROUPINTERESTS);
        ParseQuery<ParseObject> interestsQuery = groupInterests.getQuery();
        interestsQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> interests, ParseException e) {
                for (ParseObject interest: interests){
                    groupInterestIds.add(interest.getObjectId());
                }
                populateToInterestField(groupInterestIds);
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
        addViewsToFlowLayout(ll_GroupInterestsContent, interestList, this);

    }

    private void getMembersAndPendingMembers(final ParseObject myGroup){
        // During get, initialize inactivePersonIds
        inactivePersonIds = new ArrayList<>();

        // getMembers
        ParseRelation<ParseUser> membersRelation = myGroup.getRelation(Common.OBJECT_GROUP_MEMBERS);
        membersRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> members, ParseException e) {
                // remove first. Otherwise, will have repeated icons.
                ll_Members.removeAllViews();

                for (ParseUser member: members){
                    inactivePersonIds.add(member.getObjectId());

                    ParseFile imgFile = member.getParseFile(Common.OBJECT_USER_PROFILE_PIC);

                    CircleImageView imgProfile = new CircleImageView(EditGroupActivity.this);
                    LinearLayout.LayoutParams imgProfile_params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    imgProfile_params.setMargins(0, 0, 10, 10);
                    imgProfile.setLayoutParams(imgProfile_params);
                    imgProfile.getLayoutParams().height = 80;
                    imgProfile.getLayoutParams().width = 80;
                    imgProfile.setImageResource(R.drawable.ic_account_circle_black_48dp);
                    Picasso.with(EditGroupActivity.this)
                            .load(imgFile.getUrl())
                            .into(imgProfile);

                    ll_Members.addView(imgProfile);
                }

                // getPendingMembers
                ParseRelation<ParseUser> pendingMembersRelation = myGroup.getRelation(Common.OBJECT_GROUP_PENDINGMEMBERS);
                pendingMembersRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {
                    @Override
                    public void done(List<ParseUser> pendingMembers, ParseException e) {
                        // remove first. Otherwise, will have repeated icons.
                        ll_PendingMembers.removeAllViews();

                        for (ParseUser pendingMember: pendingMembers){
                            inactivePersonIds.add(pendingMember.getObjectId());

                            ParseFile imgFile = pendingMember.getParseFile(Common.OBJECT_USER_PROFILE_PIC);

                            CircleImageView imgProfile = new CircleImageView(EditGroupActivity.this);
                            LinearLayout.LayoutParams imgProfile_params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            imgProfile_params.setMargins(0, 0, 10, 0);
                            imgProfile.setLayoutParams(imgProfile_params);
                            imgProfile.getLayoutParams().height = 80;
                            imgProfile.getLayoutParams().width = 80;
                            imgProfile.setImageResource(R.drawable.ic_account_circle_black_48dp);
                            Picasso.with(EditGroupActivity.this)
                                    .load(imgFile.getUrl())
                                    .into(imgProfile);
                            Utils.toGrayScale(imgProfile);

                            ll_PendingMembers.addView(imgProfile);
                        }

                    }
                });

            }
        });
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
        getMenuInflater().inflate(R.menu.menu_edit_group, menu);
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
