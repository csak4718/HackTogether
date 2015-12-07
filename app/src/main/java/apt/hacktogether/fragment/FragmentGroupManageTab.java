package apt.hacktogether.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.melnykov.fab.FloatingActionButton;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import apt.hacktogether.R;
import apt.hacktogether.adapter.InviteGroupsTabAdapter;
import apt.hacktogether.adapter.MyGroupsTabAdapter;
import apt.hacktogether.event.InviteGroupsTabEvent;
import apt.hacktogether.event.MyGroupsTabEvent;
import apt.hacktogether.utils.Common;
import apt.hacktogether.utils.ParseUtils;
import apt.hacktogether.utils.Utils;
import de.greenrobot.event.EventBus;

/**
 * Created by de-weikung on 12/6/15.
 */
public class FragmentGroupManageTab extends FragmentTab {
    private int mType;

    private SwipeRefreshLayout swipeRefreshLayout;
    private View mView;
    private ListView mListView;
    private List<ParseObject> mList;
    private BaseAdapter mAdapter;

    public static FragmentGroupManageTab newInstance(int type) {
        FragmentGroupManageTab fragment = new FragmentGroupManageTab();
        Bundle args = new Bundle();
        args.putInt("type", type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        mType = args.getInt("type");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_group_manage_tab, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) mView.findViewById(R.id.swipe_refresh);
        mListView = (ListView) mView.findViewById(R.id.listview_group_manage_tab);
        mList = new ArrayList<>();
        setupAdapter();
        mListView.setAdapter(mAdapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNewData();
            }
        });

        getNewData();

        if (mType == Common.MYGROUPS_TAB) {
            FloatingActionButton createGroupButton = (FloatingActionButton) mView.findViewById(R.id.btn_create_group);
            createGroupButton.setVisibility(View.VISIBLE);
        }

        return mView;
    }

    @Override
    public void getNewData() {
        if(mType == Common.MYGROUPS_TAB) {
            ParseUtils.getMyGroups();
        }
        else if(mType == Common.INVITEGROUPS_TAB) {
            ParseUtils.getInviteGroups();
        }
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

    public void onEvent(MyGroupsTabEvent event) {
        if(mType == Common.MYGROUPS_TAB) {
            refresh_mList(event.myGroupsList);
            swipeRefreshLayout.setRefreshing(false);
        }
    }
    public void onEvent(InviteGroupsTabEvent event) {
        if(mType == Common.INVITEGROUPS_TAB) {
            refresh_mList(event.inviteGroupsList);
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void refresh_mList(List<ParseObject> list) {
        mList.clear();
        mList.addAll(list);
        mAdapter.notifyDataSetChanged();
    }

    private void setupAdapter() {
        if(mType == Common.MYGROUPS_TAB) {
            mAdapter = new MyGroupsTabAdapter(getActivity(), mList);
        }
        else if(mType == Common.INVITEGROUPS_TAB) {
            mAdapter = new InviteGroupsTabAdapter(getActivity(), mList);
        }
    }
}
