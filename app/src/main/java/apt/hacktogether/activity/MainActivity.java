package apt.hacktogether.activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.parse.ParseObject;
import com.parse.ParseUser;

import apt.hacktogether.R;
import apt.hacktogether.adapter.HackathonsAdapter;
import apt.hacktogether.layer.LayerImpl;
import apt.hacktogether.utils.Common;
import apt.hacktogether.utils.Utils;
import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {
    @Bind(R.id.recyclerView_hackathons) RecyclerView mRecyclerView;
    private HackathonsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public void testCreateUserProfile(){
        // participate which hackathon
//        ParseObject hackathon = new ParseObject(Common.OBJECT_HACKATHON);

        // need teammate or not


        // interested in:

        // skills:


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        getSupportActionBar().setTitle(R.string.hackathons);

        // testing function: testCreateUserProfile
        testCreateUserProfile();


        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter
        mAdapter = new HackathonsAdapter(this, Common.HACKATHONS);
        mRecyclerView.setAdapter(mAdapter);

    }

    //Called when the Activity starts, or when the App is coming to the foreground.
    public void onResume() {
        super.onResume();

        // Check to see the state of the LayerClient, and if everything is set up, then good; do nothing.
        Utils.checkSetup(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
