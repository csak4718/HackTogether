package apt.hacktogether.activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.parse.ParseFile;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;
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



    @OnClick(R.id.btn_confirm) void create(){
        
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
