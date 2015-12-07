package apt.hacktogether.activity;

import android.content.res.Configuration;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.parse.ParseFile;
import com.parse.ParseUser;

import apt.hacktogether.R;
import apt.hacktogether.fragment.FragmentGroupManage;
import apt.hacktogether.utils.Common;
import apt.hacktogether.utils.ParseUtils;
import apt.hacktogether.utils.Utils;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class GroupManageActivity extends BaseActivity {
    /*
     DrawerLayout
     */
    @Bind(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @Bind(R.id.drawer_img_profile) CircleImageView imgProfile;
    @Bind(R.id.drawer_txt_name) TextView txtName;
    private ActionBarDrawerToggle mDrawerToggle;
    private ActionBar mActionBar;

    /*
     Fragment
     */
    FragmentGroupManage fragmentGroupManage;

    @OnClick(R.id.btn_conversation) void goConversation() {
        Utils.gotoConversationsActivity(GroupManageActivity.this);
        mDrawerLayout.closeDrawers();
    }
    @OnClick(R.id.btn_group_manage) void goGroupManage() {
        // stay here; just close drawer
        mDrawerLayout.closeDrawers();
    }
    @OnClick(R.id.btn_hackathons) void goHackathons() {
        Utils.gotoMainActivity(GroupManageActivity.this);
        mDrawerLayout.closeDrawers();
    }
    @OnClick(R.id.btn_settings) void goSettings() {
//        TODO: uncomment later
//        Utils.gotoSettingsActivity(GroupManageActivity.this);
        mDrawerLayout.closeDrawers();
    }

    private void setupActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setTitle(R.string.group_manage);
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_manage);
        ButterKnife.bind(this);

        setupActionBar();
        setupDrawer();

        fragmentGroupManage = new FragmentGroupManage();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_content, fragmentGroupManage)
                .commit();
    }

    //Called when the Activity starts, or when the App is coming to the foreground.
    public void onResume() {
        super.onResume();
        if(fragmentGroupManage != null) {
            fragmentGroupManage.refreshAllTab();
        }

        // Check to see the state of the LayerClient, and if everything is set up, then good; do nothing.
        Utils.checkSetup(this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_group_manage, menu);
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

        return super.onOptionsItemSelected(item);
    }
}
