package apt.hacktogether.activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.ParseFile;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import apt.hacktogether.R;
import apt.hacktogether.event.AddPersonToMessageEvent;
import apt.hacktogether.event.MessageToAddPersonEvent;
import apt.hacktogether.parse.ParseImpl;
import apt.hacktogether.utils.Common;
import apt.hacktogether.utils.Utils;
import de.greenrobot.event.EventBus;
import de.hdodenhof.circleimageview.CircleImageView;

public class AddPersonActivity extends BaseActivity {
    private ArrayList<String> mPersonIdList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_person);

        mPersonIdList = getIntent().getStringArrayListExtra(Common.EXTRA_PERSON_ID_LIST);


        //Update user list from Parse
        ParseImpl.cacheAllUsers();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.select_hackers);

        LinearLayout ll_vertical = (LinearLayout) findViewById(R.id.vertical_ll);

        //Grab a list of all friendID (For now, all friendID are equivalent to all userID except current userID)
        Set friendIds = ParseImpl.getAllFriends();

        //A Map of the horizontal linear layout with the Parse Object ID
        final HashMap<CheckBox, String> allUsers = new HashMap<>();

        if(mPersonIdList == null) mPersonIdList = new ArrayList<>();

        //Go through each friendID and create a horizontal linear layout with a human readable name mapped to the
        // Object ID
        Iterator itr = friendIds.iterator();
        while(itr.hasNext()) {
            String friendId = (String)itr.next();


            LinearLayout ll_horizontal = new LinearLayout(this);
            ll_horizontal.setOrientation(LinearLayout.HORIZONTAL);
            ll_horizontal.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            CheckBox checkBox = new CheckBox(this);
            checkBox.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            checkBox.setText(ParseImpl.getUsername(friendId));
            checkBox.setTextSize(1);
            checkBox.setTextColor(getResources().getColor(R.color.white));
            //If this user is already selected, mark the checkbox
            if(mPersonIdList.contains(friendId)) checkBox.setChecked(true);

            CircleImageView imgProfile = new CircleImageView(this);
            imgProfile.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            imgProfile.getLayoutParams().height = 60;
            imgProfile.getLayoutParams().width = 60;
            ParseFile imgFile = ParseImpl.getUserIcon(friendId);
            Picasso.with(this)
                    .load(imgFile.getUrl())
                    .into(imgProfile);

            TextView textView = new TextView(this);
            textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            textView.setText(ParseImpl.getUsername(friendId));
            textView.setTextSize(20);



            ll_horizontal.addView(checkBox);
            ll_horizontal.addView(imgProfile);
            ll_horizontal.addView(textView);

            ll_vertical.addView(ll_horizontal);
            allUsers.put(checkBox, friendId);
        }

        ImageButton confirmButton = new ImageButton(this);
        confirmButton.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        confirmButton.setImageResource(R.drawable.ic_check_black_24dp);
        confirmButton.setBackgroundColor(getResources().getColor(R.color.green));
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPersonIdList.clear();
                mPersonIdList.add(ParseUser.getCurrentUser().getObjectId());

                Set checkboxes = allUsers.keySet();
                Iterator checkItr = checkboxes.iterator();
                while(checkItr.hasNext()){
                    CheckBox currCheck = (CheckBox)checkItr.next();
                    if(currCheck != null && currCheck.isChecked()){
                        String friendID = allUsers.get(currCheck);
                        mPersonIdList.add(friendID);
                    }
                }

                EventBus.getDefault().post(new AddPersonToMessageEvent(mPersonIdList));
                AddPersonActivity.this.finish();
            }
        });

        ll_vertical.addView(confirmButton);
    }

//    @Override
//    public void onStart() {
//        EventBus.getDefault().register(this);
//        super.onStart();
//    }
//
//    @Override
//    public void onStop() {
//        EventBus.getDefault().unregister(this);
//        super.onStop();
//    }

//    public void onEvent(MessageToAddPersonEvent event) {
//        mPersonIdList.clear();
//        mPersonIdList.addAll(event.mTargetParticipants);
//
//        //Update user list from Parse
//        ParseImpl.cacheAllUsers();
//
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setTitle(R.string.select_hackers);
//
//        LinearLayout ll_vertical = (LinearLayout) findViewById(R.id.vertical_ll);
//
//        //Grab a list of all friendID (For now, all friendID are equivalent to all userID except current userID)
//        Set friendIds = ParseImpl.getAllFriends();
//
//        //A Map of the horizontal linear layout with the Parse Object ID
//        final HashMap<CheckBox, String> allUsers = new HashMap<>();
//
//        if(mPersonIdList == null) mPersonIdList = new ArrayList<>();
//
//        //Go through each friendID and create a horizontal linear layout with a human readable name mapped to the
//        // Object ID
//        Iterator itr = friendIds.iterator();
//        while(itr.hasNext()) {
//            String friendId = (String)itr.next();
//
//
//            LinearLayout ll_horizontal = new LinearLayout(this);
//            ll_horizontal.setOrientation(LinearLayout.HORIZONTAL);
//            ll_horizontal.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//
//            CheckBox checkBox = new CheckBox(this);
//            checkBox.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//            checkBox.setText(ParseImpl.getUsername(friendId));
//            checkBox.setTextSize(1);
//            checkBox.setTextColor(getResources().getColor(R.color.white));
//            //If this user is already selected, mark the checkbox
//            if(mPersonIdList.contains(friendId)) checkBox.setChecked(true);
//
//            CircleImageView imgProfile = new CircleImageView(this);
//            imgProfile.getLayoutParams().height = 60;
//            imgProfile.getLayoutParams().width = 60;
//            ParseFile imgFile = ParseImpl.getUserIcon(friendId);
//            Picasso.with(this)
//                    .load(imgFile.getUrl())
//                    .into(imgProfile);
//
//            TextView textView = new TextView(this);
//            textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//            textView.setText(ParseImpl.getUsername(friendId));
//            textView.setTextSize(20);
//
//
//
//            ll_horizontal.addView(checkBox);
//            ll_horizontal.addView(imgProfile);
//            ll_horizontal.addView(textView);
//
//            ll_vertical.addView(ll_horizontal);
//            allUsers.put(checkBox, friendId);
//        }
//
//        ImageButton confirmButton = new ImageButton(this);
//        confirmButton.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//        confirmButton.setImageResource(R.drawable.ic_check_black_24dp);
//        confirmButton.setBackgroundColor(getResources().getColor(R.color.green));
//        confirmButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mPersonIdList.clear();
//                mPersonIdList.add(ParseUser.getCurrentUser().getObjectId());
//
//                Set checkboxes = allUsers.keySet();
//                Iterator checkItr = checkboxes.iterator();
//                while(checkItr.hasNext()){
//                    CheckBox currCheck = (CheckBox)checkItr.next();
//                    if(currCheck != null && currCheck.isChecked()){
//                        String friendID = allUsers.get(currCheck);
//                        mPersonIdList.add(friendID);
//                    }
//                }
//
//                EventBus.getDefault().post(new AddPersonToMessageEvent(mPersonIdList));
//                AddPersonActivity.this.finish();
//            }
//        });
//
//        ll_vertical.addView(confirmButton);
//    }



    //Called when the Activity starts, or when the App is coming to the foreground.
    public void onResume() {
        super.onResume();

        // Check to see the state of the LayerClient, and if everything is set up, then good; do nothing.
        Utils.checkSetup(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_person, menu);
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
