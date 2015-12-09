package apt.hacktogether.activity;

import android.content.res.Configuration;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import apt.hacktogether.R;
import apt.hacktogether.adapter.HackathonsAdapter;
import apt.hacktogether.fragment.FragmentBrowse;
import apt.hacktogether.layer.LayerImpl;
import apt.hacktogether.utils.Common;
import apt.hacktogether.utils.ParseUtils;
import apt.hacktogether.utils.Utils;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.recyclerview.animators.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.adapters.AnimationAdapter;
import jp.wasabeef.recyclerview.animators.adapters.ScaleInAnimationAdapter;


public class MainActivity extends BaseActivity {
    @Bind(R.id.recyclerView_hackathons) RecyclerView mRecyclerView;
    private HackathonsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    /*
  DrawerLayout
  */
    @Bind(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @Bind(R.id.drawer_img_profile) CircleImageView imgProfile;
    @Bind(R.id.drawer_txt_name)TextView txtName;
    private ActionBarDrawerToggle mDrawerToggle;
    private ActionBar mActionBar;

    /*
     Fragment
     */
    FragmentBrowse fragmentBrowse;

    @OnClick(R.id.btn_conversation) void goConversation() {
        Utils.gotoConversationsActivity(MainActivity.this);
        mDrawerLayout.closeDrawers();
    }
    @OnClick(R.id.btn_group_manage) void goGroupManage() {
        Utils.gotoGroupManageActivity(MainActivity.this);
        mDrawerLayout.closeDrawers();
    }
    @OnClick(R.id.btn_hackathons) void goHackathons() {
        Utils.gotoMainActivity(MainActivity.this);
        mDrawerLayout.closeDrawers();
    }
    @OnClick(R.id.btn_settings) void goSettings() {
//        TODO: uncomment later
//        Utils.gotoSettingsActivity(BrowseActivity.this);
        mDrawerLayout.closeDrawers();
    }

    private void setupActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setTitle(R.string.hackathons);
    }
    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.app_name,
                R.string.app_name);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        /*
         setup header layout
         */
        ParseFile imgFile = ParseUser.getCurrentUser().getParseFile(Common.OBJECT_USER_PROFILE_PIC);
        ParseUtils.displayParseImage(imgFile, imgProfile);
        txtName.setText(ParseUser.getCurrentUser().getString(Common.OBJECT_USER_NICK));

    }

    public void testCreateUserProfile(){
        final ParseUser currentUser = ParseUser.getCurrentUser();

        // user input
        ArrayList<String> hackathonName_List = new ArrayList<>();
        hackathonName_List.add("BigRed//Hacks");

        final ArrayList<Boolean> need_teammate_List = new ArrayList<>();
        need_teammate_List.add(true);

        String[] interestNames = {"Web App"};
//        String[] interestNames = {"Android App", "iOS App"};
        String[] skillNames = {"Java", "Machine learning"};
//        String[] skillNames = {"Java", "Swift", "Machine learning"};



        // hackathon
        for (int i=0; i < hackathonName_List.size(); i++){
            final int finalI = i;

            // Hackathon class
            ParseQuery<ParseObject> hackathonObjectQuery = ParseQuery.getQuery(Common.OBJECT_HACKATHON);
            hackathonObjectQuery.whereEqualTo(Common.OBJECT_HACKATHON_NAME, hackathonName_List.get(i));
            hackathonObjectQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                public void done(final ParseObject hackathon, ParseException e) {
                    if(e == null) {
                        ParseRelation<ParseUser> hackers = hackathon.getRelation(Common.OBJECT_HACKATHON_HACKERS);
                        hackers.add(currentUser);

                        if(need_teammate_List.get(finalI)) {
                            ParseRelation<ParseUser> hackersNeedGuy = hackathon.getRelation(Common.OBJECT_HACKATHON_HACKERSNEEDGUY);
                            hackersNeedGuy.add(currentUser);
                        }

                        hackathon.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null){
                                    // User class
                                    ParseRelation<ParseObject> myHackathons = currentUser.getRelation(Common.OBJECT_USER_MYHACKATHONS);
                                    myHackathons.add(hackathon);

                                    if(need_teammate_List.get(finalI)) {
                                        ParseRelation<ParseObject> myNeedGuyHackathons = currentUser.getRelation(Common.OBJECT_USER_MYNEEDGUYHACKATHONS);
                                        myNeedGuyHackathons.add(hackathon);
                                    }

                                    currentUser.saveInBackground();
                                }
                            }
                        });
                    }

                }
            });
        }



        // interest
        for (String interestName: interestNames){
            // Interest class
            ParseQuery<ParseObject> interestObjectQuery = ParseQuery.getQuery(Common.OBJECT_INTEREST);
            interestObjectQuery.whereEqualTo(Common.OBJECT_INTEREST_NAME, interestName);
            interestObjectQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(final ParseObject interest, ParseException e) {
                    if(e == null){
                        ParseRelation<ParseUser> hackers = interest.getRelation(Common.OBJECT_INTEREST_INTERESTED_HACKERS);
                        hackers.add(currentUser);
                        interest.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null){
                                    // User class
                                    ParseRelation<ParseObject> interests = currentUser.getRelation(Common.OBJECT_USER_INTERESTS);
                                    interests.add(interest);
                                    currentUser.saveInBackground();
                                }
                            }
                        });
                    }
                }
            });
        }


        // skill
        for (String skillName: skillNames){
            // Skill class
            ParseQuery<ParseObject> skillObjectQuery = ParseQuery.getQuery(Common.OBJECT_SKILL);
            skillObjectQuery.whereEqualTo(Common.OBJECT_SKILL_NAME, skillName);
            skillObjectQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(final ParseObject skill, ParseException e) {
                    if(e == null){
                        ParseRelation<ParseUser> hackers = skill.getRelation(Common.OBJECT_SKILL_SKILLED_HACKERS);
                        hackers.add(currentUser);
                        skill.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null){
                                    // User class
                                    ParseRelation<ParseObject> skills = currentUser.getRelation(Common.OBJECT_USER_SKILLS);
                                    skills.add(skill);
                                    currentUser.saveInBackground();
                                }
                            }
                        });
                    }
                }
            });
        }


    }

    public void test(){
        ParseQuery<ParseObject> testObjectQuery = ParseQuery.getQuery("TestObject");
        testObjectQuery.whereEqualTo("name", "a");
        testObjectQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    ParseRelation<ParseUser> user = object.getRelation("user");
                    user.add(ParseUser.getCurrentUser());
                    object.saveInBackground();
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        //getSupportActionBar().setTitle(R.string.hackathons);

        // testing function: testCreateUserProfile
//        testCreateUserProfile();
        setupActionBar();
        setupDrawer();
        // test function
//        test();


        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter
        mAdapter = new HackathonsAdapter(this, Common.HACKATHONS);
        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(mAdapter);
        ScaleInAnimationAdapter scaleAdapter = new ScaleInAnimationAdapter(alphaAdapter);
        scaleAdapter.setFirstOnly(false);
        mRecyclerView.setAdapter(scaleAdapter);

    }

    //Called when the Activity starts, or when the App is coming to the foreground.
    public void onResume() {
        super.onResume();

        // Check to see the state of the LayerClient, and if everything is set up, then good; do nothing.
        Utils.checkSetup(this);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_browse, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }
}
