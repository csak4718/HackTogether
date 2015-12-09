package apt.hacktogether.activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import apt.hacktogether.R;
import apt.hacktogether.event.AddPersonToCreateGroupEvent;
import apt.hacktogether.layer.LayerImpl;
import apt.hacktogether.parse.ParseImpl;
import apt.hacktogether.utils.Common;
import apt.hacktogether.utils.Utils;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

public class CreateGroupActivity extends BaseActivity {
    public static final String TAG = Common.TAG_CREATE_GROUP_ACTIVITY;

    @Bind(R.id.edt_group_name) EditText edtGroupName;
    @Bind(R.id.txt_hackathon_header) TextView txtHackathonHeader;
    @Bind(R.id.txt_hackathon_content) TextView txtHackathonContent;
    @OnClick(R.id.txt_member_header) void goAddPerson(){
        Utils.gotoAddPersonActivity(CreateGroupActivity.this, selectedPersonIds, TAG);
    }
    @Bind(R.id.ll_member_content) LinearLayout ll_MemberContent;
    @Bind(R.id.switch_need_teammates) Switch switchNeedTeammates;
    @Bind(R.id.spec_container) LinearLayout ll_SpecContainer;
    @Bind(R.id.txt_group_interests_header) TextView txtGroupInterestsHeader;
    @Bind(R.id.ll_group_interests_content) LinearLayout ll_GroupInterestsContent;
    @Bind(R.id.txt_look_for_skills_header) TextView txtLookForSkillsHeader;
    @Bind(R.id.ll_look_for_skills_content) LinearLayout ll_LookForSkillsContent;
    @OnClick(R.id.btn_confirm) void create(){
        // TODO: from selectedPersonIds, use For Loop to add them into pendingMembers Relation
        String groupName = edtGroupName.getText().toString();
        
        HashMap<String, ParseUser> allUsers = ParseImpl.get_allUsers();

    }

    private ArrayList<String> selectedPersonIds;
    private ArrayList<String> groupInterestIds;
    private ArrayList<String> lookForSkillIds;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.create_group);


        ll_MemberContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.gotoAddPersonActivity(CreateGroupActivity.this, selectedPersonIds, TAG);
            }
        });

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
        populateToField(selectedPersonIds);
    }

    private void populateToField(List<String> participantIds){
        //We will not include the Authenticated user in the "To:" field, since they know they are
        // already part of the Conversation
        TextView[] participantList = new TextView[participantIds.size()-1];
        int idx = 0;
        for(String id : participantIds){
            if(!id.equals(LayerImpl.getLayerClient().getAuthenticatedUserId())){

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
        }

        //Uses the helper function to make sure all participant names are appropriately displayed
        // and not cut off due to size constraints
        populateViewWithWrapping(ll_MemberContent, participantList, this);
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
