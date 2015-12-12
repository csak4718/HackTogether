package apt.hacktogether.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import apt.hacktogether.R;
import apt.hacktogether.activity.BrowseActivity;
import apt.hacktogether.adapter.GroupTabAdapter;
import apt.hacktogether.adapter.PersonTabAdapter;
import apt.hacktogether.event.GroupTabEvent;
import apt.hacktogether.event.PersonTabEvent;
import apt.hacktogether.utils.Common;
import apt.hacktogether.utils.ParseUtils;
import de.greenrobot.event.EventBus;

/**
 * Created by de-weikung on 12/5/15.
 */
public class FragmentBrowseTab extends FragmentTab {
    private final static String TAG = "FragmentBrowseTab";
    private int mType;
    private String hackathonName;

    private SwipeRefreshLayout swipeRefreshLayout;
    private View mView;
    private ListView mListView;
    private List<ParseUser> mList_hackersNeedGuy;
    private List<ParseObject> mList_groupsNeedGuy;
    private BaseAdapter mAdapter;
    String query_text;

    public static FragmentBrowseTab newInstance(int type, String hackathon_name) {
        FragmentBrowseTab fragment = new FragmentBrowseTab();
        Bundle args = new Bundle();
        args.putInt("type", type);
        args.putString("hackathon_name", hackathon_name);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        mType = args.getInt("type");
        hackathonName = args.getString("hackathon_name");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_browse_tab, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) mView.findViewById(R.id.swipe_refresh);
        mListView = (ListView) mView.findViewById(R.id.listview_browse_tab);
        mList_hackersNeedGuy = new ArrayList<>();
        mList_groupsNeedGuy = new ArrayList<>();
        setupAdapter();
        mListView.setAdapter(mAdapter);


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNewData();
            }
        });

// mListView don't need to be clickable now
//        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                ParseObject task = (ParseObject) mAdapter.getItem(position);
//                Utils.gotoMyTaskActivity(getActivity(), task.getObjectId(), mType);
//            }
//        });

        getNewData();

        return mView;
    }

    @Override
    public void getNewData() {
        if(mType == Common.PERSON_TAB) {
            ParseUtils.getHackersNeedGuy(hackathonName);
        }
        else if(mType == Common.GROUP_TAB) {
            ParseUtils.getGroupsNeedGuy(hackathonName);
        }
    }
    @Override
    public void query(String text){
        if(mType == Common.PERSON_TAB) {
            ParseUtils.getHackersNeedGuy(hackathonName);
        }
        else if(mType == Common.GROUP_TAB) {
            ParseUtils.getGroupsNeedGuy(hackathonName);
        }
        query_text = text;
    }

    @Override
    public void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    public void onEvent(PersonTabEvent event) {
        if(mType == Common.PERSON_TAB) {
            refresh_mList_hackersNeedGuy(event.hackersNeedGuyList);
            if(query_text == null || query_text.length() < 1){}
            else{
                for(final ParseUser user: mList_hackersNeedGuy){
                    ParseRelation<ParseObject> userInterestsRelation = user.getRelation(Common.OBJECT_USER_INTERESTS);
                    ParseQuery<ParseObject> userInterestsQuery = userInterestsRelation.getQuery();
                    userInterestsQuery.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(final List<ParseObject> userInterests, ParseException e) {
                            ParseRelation<ParseObject> userSkillsRelation = user.getRelation(Common.OBJECT_USER_SKILLS);
                            ParseQuery<ParseObject> userSkillsQuery = userSkillsRelation.getQuery();
                            userSkillsQuery.findInBackground(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> userSkills, ParseException e) {
                                    StringBuilder strBuilder = new StringBuilder();
                                    for (ParseObject interest : userInterests) {
                                        strBuilder.append(interest.getString(Common.OBJECT_INTEREST_NAME));
                                        strBuilder.append(",");
                                    }
                                    for (ParseObject skill : userSkills) {
                                        strBuilder.append(skill.getString(Common.OBJECT_SKILL_NAME));
                                        strBuilder.append(",");
                                    }
                                    String interestsString = strBuilder.toString();
                                    boolean i = user.getString(Common.OBJECT_USER_NICK).toLowerCase().contains(query_text.toLowerCase());
                                    boolean j = interestsString.toLowerCase().contains(query_text.toLowerCase());
                                    if (i || j) {
                                    } else {
                                        Log.d("Remove","remove+"+interestsString.toLowerCase()+"USER"+user.getString(Common.OBJECT_USER_NICK).toLowerCase());
                                        mList_groupsNeedGuy.remove(user);
                                        Log.d("List_removed","Remocer"+mList_groupsNeedGuy);
                                    }
                                }
                            });
                        }
                    });
                }
            }
            swipeRefreshLayout.setRefreshing(false);
        }
    }
    public void onEvent(GroupTabEvent event) {
        if(mType == Common.GROUP_TAB) {
            refresh_mList_groupsNeedGuy(event.groupsNeedGuyList);
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void refresh_mList_hackersNeedGuy(List<ParseUser> list) {
        mList_hackersNeedGuy.clear();
        mList_hackersNeedGuy.addAll(list);
        mAdapter.notifyDataSetChanged();
    }

    private void refresh_mList_groupsNeedGuy(List<ParseObject> list) {
        mList_groupsNeedGuy.clear();
        mList_groupsNeedGuy.addAll(list);
        mAdapter.notifyDataSetChanged();
    }

    private void setupAdapter() {
        if(mType == Common.PERSON_TAB) {
            mAdapter = new PersonTabAdapter(getActivity(), mList_hackersNeedGuy);
        }
        else if(mType == Common.GROUP_TAB) {
            mAdapter = new GroupTabAdapter(getActivity(), mList_groupsNeedGuy);
        }
    }
}
