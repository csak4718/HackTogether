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

import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

import apt.hacktogether.R;
import apt.hacktogether.activity.BrowseActivity;
import apt.hacktogether.utils.Common;
import apt.hacktogether.utils.ParseUtils;

/**
 * Created by de-weikung on 12/5/15.
 */
public class FragmentBrowseTab extends FragmentTab {
    private int mType;

    private SwipeRefreshLayout swipeRefreshLayout;
    private View mView;
    private ListView mListView;
    private List<ParseObject> mList;
    private BaseAdapter mAdapter;
    private BrowseActivity browseActivity;

    public static FragmentBrowseTab newInstance(int type) {
        FragmentBrowseTab fragment = new FragmentBrowseTab();
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

        browseActivity = (BrowseActivity) getActivity();

        mView = inflater.inflate(R.layout.fragment_browse_tab, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) mView.findViewById(R.id.swipe_refresh);
        mListView = (ListView) mView.findViewById(R.id.listview_browse_tab);
        mList = new ArrayList<>();
        setupAdapter();
        mListView.setAdapter(mAdapter);


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNewData();
            }
        });

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
            ParseUtils.getHackersNeedGuy();
        }
        else if(mType == Common.GROUP_TAB) {
            ParseUtils.getGroupNeedGuy();
        }
    }

    private void setupAdapter() {
        if(mType == Common.PERSON_TAB) {
            mAdapter = new PersonTabAdapter(getActivity(), mList);
        }
        else if(mType == Common.GROUP_TAB) {
            mAdapter = new GroupTabAdapter(getActivity(), mList);
        }
    }

}
