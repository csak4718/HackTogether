package apt.hacktogether.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.layer.sdk.exceptions.LayerException;
import com.parse.LogInCallback;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import apt.hacktogether.R;
import apt.hacktogether.event.FbPictureEvent;
import apt.hacktogether.event.UserProfileEvent;
import apt.hacktogether.layer.LayerImpl;
import apt.hacktogether.parse.ParseImpl;
import apt.hacktogether.utils.Common;
import apt.hacktogether.utils.FbUtils;
import apt.hacktogether.utils.ParseUtils;
import apt.hacktogether.utils.Utils;
import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;


public class LoginActivity extends BaseActivity {
    private Button mBtnLoginFacebook;
//    private TextView mTxvSplashLogo;
    private RelativeLayout mContainer;
    private Handler mHandler = new Handler();
    private String mNickName;
    private String mFbId;

    @Bind(R.id.progressbar_login) ProgressBar mProgressBar;

    //Make sure both the Layer and Parse configurations are set
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!LayerImpl.hasValidAppID()) {

            showAlert("Invalid Layer App ID", "You will need a valid Layer App ID in order to run this example. " +
                    "If you haven't already, create a Layer account at http://layer.com/signup and then follow these instructions:\n\n" +
                    "1. Go to http://developer.layer.com and sign in\n" +
                    "2. Select \"Keys\" on the left panel\n" +
                    "3. Copy the 'Staging App ID'\n" +
                    "4. Paste that value in the LayerAppID String in LayerImpl.java");

        } else if (!ParseImpl.hasValidAppID()) {

            showAlert("Invalid Parse Credentials", "You will need a valid Parse project in order to run this example. " +
                    "If you haven't already, create a Parse account at http://parse.com and follow these instructions:\n\n" +
                    "1. Sign in and mouse over the Settings icon by your App" +
                    "2. Select the \"Keys\" option" +
                    "3. Copy the Application ID and Client Key" +
                    "4. Paste those values in the ParseAppID and ParseClientKey fields in ParseImpl.java");

        } else {
            //The base class will create a Layer object and connect
            getSupportActionBar().hide();
            setContentView(R.layout.activity_login);
            ButterKnife.bind(this);
            mBtnLoginFacebook = (Button) findViewById(R.id.btn_login_facebook);
//            mTxvSplashLogo = (TextView) findViewById(R.id.txv_splash_logo);
            mContainer = (RelativeLayout) findViewById(R.id.container);
        }


    }

    public void onLayerConnected(){
        if(LayerImpl.isAuthenticated()){
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    gotoNextActivity();
                }
            }, 700);
        }
        else {
            setupLoginButton();
            showLoginAnimation();
        }
    }

    private void gotoNextActivity() {
        Intent it = getIntent();
        if(it.getAction().equals("android.intent.action.VIEW")) {
            if(it != null && it.getData() != null){
                Uri uri = it.getData();
                String host = uri.getHost();

                if(host.contains("im")) {
                    // go to MainActivity first. Then, go to ConversationActivity, Later, go to MessageActivity
                    Intent notiIntent = new Intent(this, MainActivity.class);
                    notiIntent.setData(it.getData());
                    startActivity(notiIntent);
                }
                else{
                    // invitation notification
                    Intent notiIntent = new Intent(this, GroupManageActivity.class);
                    notiIntent.setData(it.getData());
                    startActivity(notiIntent);
                }
            }


        }
        else {
            if(ParseUser.getCurrentUser().getBoolean(Common.OBJECT_USER_IS_RETURN_USER)){
                Utils.gotoMainActivity(this);
            }
            else{
                Utils.gotoCreateProfileActivity(this);
            }

        }
        finish();
    }

    private void setupLoginButton() {

        mBtnLoginFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.VISIBLE);
                final List<String> permissions = new ArrayList<>();
                permissions.add("public_profile");
                permissions.add("user_friends");
                ParseFacebookUtils.logInWithReadPermissionsInBackground(LoginActivity.this, permissions, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, com.parse.ParseException e) {
                        if (user == null) {
                            Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                        } else {
                            // TODO: Important!
                            if (user.isNew()) {
                                Log.d("MyApp", "User signed up and logged in through Facebook!");
                                FbUtils.getUserProfile(AccessToken.getCurrentAccessToken());
                            } else {
                                Log.d("MyApp", "User logged in through Facebook!");
                                //Check to see if the user is already authenticated. If so, start the MainActivity
                                if (LayerImpl.isAuthenticated()){
                                    onUserAuthenticated(ParseUser.getCurrentUser().getObjectId());
                                } else {
                                    //User is logged into Parse, so start the Layer Authentication process
                                    LayerImpl.authenticateUser();
                                }
                            }

                        }
                    }
                });
            }
        });
    }

    //User was Authenticated with Layer. This means their lastMsgContent/conversation history is being
    // downloaded and stored locally, and they can now send/receive messages
    public void onUserAuthenticated(String userID){

        //Go to the MainActivity
        Log.d("Activity", "User authenticated");

        Utils.gotoMainActivity(LoginActivity.this);
        finish();
    }

    //Layer was not able to Authenticate the user for some reason
    public void onUserAuthenticatedError(LayerException e){
        showAlert("Authentication Error", "Error: " + e.toString()
                + "\n\n" + "Layer was not able to Authenticate the user for some reason");

        Log.d("Activity", "Layer was not able to Authenticate the user for some reason. Exception: " + e.toString());
    }

    private void showLoginAnimation() {
        TranslateAnimation animation = new TranslateAnimation(0, 0, 0, -100);
        animation.setDuration(1000);
        animation.setFillAfter(true);
        animation.setFillEnabled(true);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mBtnLoginFacebook.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
//        mTxvSplashLogo.setAnimation(animation);
        mContainer.setAnimation(animation);

        Log.d("Animation", "startAnimation");
//        mTxvSplashLogo.startAnimation(animation);
        mContainer.startAnimation(animation);
    }

    public void onEvent(UserProfileEvent event) {
        mNickName = event.mNickName;
        mFbId = event.mFbId;
        FbUtils.getFbProfilePicture(mFbId);
    }

    // Last step of Sign Up
    public void onEvent(final FbPictureEvent event) {
        Log.d("william", "3");
        ParseUtils.createUserProfile(mNickName, mFbId, event.mPic);

        Log.d("Activity", "User is registered with Parse. Starting Layer authentication.");
        LayerImpl.authenticateUser();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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

    /* some codes that are currently not used: */
//    //Disable the back button
//    public void onBackPressed() {
//
//    }
}
