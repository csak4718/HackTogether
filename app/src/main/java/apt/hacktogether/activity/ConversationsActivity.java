package apt.hacktogether.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.layer.sdk.messaging.Conversation;
import com.melnykov.fab.FloatingActionButton;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

import apt.hacktogether.R;
import apt.hacktogether.adapter.ConversationQueryAdapter;
import apt.hacktogether.adapter.QueryAdapter;
import apt.hacktogether.layer.LayerImpl;
import apt.hacktogether.parse.ParseImpl;
import apt.hacktogether.utils.Utils;

/*
 * ConversationActivity.java
 * Handles displaying all Conversations that the Authenticated user is a part of. Uses the
 *  ConversationQueryAdapter to drive the view, and starts a MessagesActivity if one of those
 *  Conversations is tapped. The user can also Logout, or create a new Conversation.
 */
public class ConversationsActivity extends BaseActivity implements ConversationQueryAdapter.ConversationClickHandler {

    //The Query Adapter that grabs all Conversations and displays them based on the last lastMsgContent
    private ConversationQueryAdapter mConversationsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Creates the LayerClient object if it does not exist
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversations);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.recent_chats);

        Intent it = getIntent();
        if(it != null && it.getData() != null) {
            Uri uri = it.getData();
            String host = uri.getHost();
            String path = uri.getPath();
            Log.d("WILL path", path);

            if(host.contains("im")) {
                // Get conversation-id from notif Intent
                String conversationIdStr = path.substring(1);
                Log.d("WILL conversationIdStr", conversationIdStr);
                Uri conversationIdUri = Uri.parse(conversationIdStr);
                Log.d("WILL conversationIdUri", conversationIdUri.toString());

                Intent intent = new Intent(this, MessageActivity.class);
                intent.putExtra("conversation-id", conversationIdUri);
                startActivity(intent);
            }
        }



//        Button logoutBtn = (Button) findViewById(R.id.logout);
//        if (logoutBtn != null)
//            logoutBtn.setOnClickListener(this);
    }


    //Called when the Activity starts, or when the App is coming to the foreground. Check to see
    // the state of the LayerClient, and if everything is set up, display all the Conversations
    public void onResume() {

        Log.d("Activity", "Conversation Activity onResume");

        super.onResume();

        //If the user is not authenticated, make sure they are logged in, and if they are, re-authenticate
        if (!LayerImpl.isAuthenticated()) {

            if (ParseUser.getCurrentUser() == null) {

                Log.d("Activity", "User is not authenticated or logged in - returning to login screen");
                Utils.gotoLoginActivity(this);

            } else {

                Log.d("Activity", "User is not authenticated, but is logged in - re-authenticating user");
                LayerImpl.authenticateUser();

            }

            //Everything is set up, so start populating the Conversation list
        } else {

            Log.d("Activity", "Starting conversation view");
            setupConversationView();
        }

        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("inMessageActivity", false);
        installation.put("user", ParseUser.getCurrentUser());
        installation.saveInBackground();
    }

    //If the user authenticated, setup the Conversation list
    public void onUserAuthenticated(String id) {
        Log.d("Activity", "User authenticated: " + id);
        setupConversationView();
    }

    //Set up the Query Adapter that will drive the RecyclerView on the conversations_screen
    private void setupConversationView() {

        Log.d("Activity", "Setting conversation view");

        //Grab the Recycler View and list all conversation objects in a vertical list
        RecyclerView conversationsView = (RecyclerView) findViewById(R.id.recyclerView);

        //Register the button click listeners
        FloatingActionButton newConversationBtn = (FloatingActionButton) findViewById(R.id.newConversation);
        if (newConversationBtn != null)
            newConversationBtn.setOnClickListener(this);
        newConversationBtn.attachToRecyclerView(conversationsView);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        conversationsView.setLayoutManager(layoutManager);

        //The Query Adapter drives the recycler view, and calls back to this activity when the user
        // taps on a Conversation
        mConversationsAdapter = new ConversationQueryAdapter(getApplicationContext(), LayerImpl.getLayerClient(), this, new QueryAdapter.Callback() {
            @Override
            public void onItemInserted() {
                Log.d("Activity", "Conversation Adapter, new conversation inserted");
            }
        });

        //Attach the Query Adapter to the Recycler View
        conversationsView.setAdapter(mConversationsAdapter);

        //Execute the Query
        mConversationsAdapter.refresh();
    }

    //Callback from the Query Adapter. When the user taps a Conversation, grab its ID and start
    // a MessageActivity to display all the messages
    public void onConversationClick(Conversation conversation) {
        Log.d("Activity", "Conversation clicked: " + conversation.getId());

        //If the Conversation is valid, start the MessageActivity and pass in the Conversation ID
        if (conversation != null && conversation.getId() != null && !conversation.isDeleted()) {
            Intent intent = new Intent(ConversationsActivity.this, MessageActivity.class);
            intent.putExtra("conversation-id", conversation.getId());
            startActivity(intent);
        }
    }

    //You can handle long clicks as well (such as displaying metadata or deleting a conversation)
    public boolean onConversationLongClick(Conversation conversation) {
        return false;
    }

    //Handle the buttons on the conversation_screen
    public void onClick(View v) {

        switch (v.getId()) {

//            case R.id.logout:
//                Log.d("Activity", "Logout button pressed");
//                logoutUser();
//                break;

            case R.id.newConversation:
                Log.d("Activity", "New conversation button pressed");
                createConversation();
                break;
        }
    }

    //When the user creates a new Conversation, we start the MessageActivity (but we don't bother
    // passing in a Conversation ID)
    private void createConversation() {
        Intent intent = new Intent(ConversationsActivity.this, MessageActivity.class);
        startActivity(intent);
    }

    //When the user logs out, we log out of Parse and Deauthenticate Layer
//    private void logoutUser() {
//        ParseUser.logOut();
//        LayerImpl.getLayerClient().deauthenticate();
//    }

    //Once the user is fully deauthetnicated (all Messaging activity is synced and deleted), we
    // allow another user to login
    public void onUserDeauthenticated() {
        Utils.gotoLoginActivity(this);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_conversations, menu);
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
