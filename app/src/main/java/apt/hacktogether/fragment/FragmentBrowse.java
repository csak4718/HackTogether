package apt.hacktogether.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import apt.hacktogether.R;
import apt.hacktogether.activity.BrowseActivity;
import apt.hacktogether.adapter.ViewPagerAdapter;
import apt.hacktogether.utils.Common;

/**
 * Created by de-weikung on 12/4/15.
 */
public class FragmentBrowse extends Fragment{
    private final static String TAG = "FragmentBrowse";
    private View mView;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private String hackathonName;

    private void setupAdapter() {
        adapter.addFrag(FragmentBrowseTab.newInstance(Common.PERSON_TAB, hackathonName), Common.TAB_NAME_PERSON);
        adapter.addFrag(FragmentBrowseTab.newInstance(Common.GROUP_TAB, hackathonName), Common.TAB_NAME_GROUP);
    }

    public void refreshAllTab() {
        adapter.refreshAllTabs();
    }

    public void setFilter(String queryText) {
        adapter.refreshTabsquery(queryText);
        Log.d("refresh","refresh");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BrowseActivity browseActivity = (BrowseActivity) getActivity();
        hackathonName = browseActivity.getHackathonName();

        adapter = new ViewPagerAdapter(getChildFragmentManager());
        setupAdapter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_browse, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);
        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_person_black_24dp);
        tabLayout.getTabAt(0).setText("");
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_people_black_24dp);
        tabLayout.getTabAt(1).setText("");

    }

}

