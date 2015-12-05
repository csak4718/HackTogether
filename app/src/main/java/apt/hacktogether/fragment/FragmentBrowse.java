package apt.hacktogether.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import apt.hacktogether.R;
import apt.hacktogether.adapter.ViewPagerAdapter;
import apt.hacktogether.utils.Common;

/**
 * Created by de-weikung on 12/4/15.
 */
public class FragmentBrowse extends Fragment{
    private View mView;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;

    private void setupAdapter() {
        adapter.addFrag(FragmentBrowseTab.newInstance(Common.PERSON_TAB), Common.TAB_NAME_PERSON);
        adapter.addFrag(FragmentBrowseTab.newInstance(Common.GROUP_TAB), Common.TAB_NAME_GROUP);
    }

    public void refreshAllTab() {
        adapter.refreshAllTabs();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
    }

}

