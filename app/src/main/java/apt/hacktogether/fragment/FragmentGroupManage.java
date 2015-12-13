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
 * Created by de-weikung on 12/6/15.
 */
public class FragmentGroupManage extends Fragment {
    private View mView;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    int mTab;

    static public FragmentGroupManage newInstance(int tab) {
        FragmentGroupManage fragment = new FragmentGroupManage();
        Bundle args = new Bundle();
        args.putInt("tab", tab);
        fragment.setArguments(args);
        return fragment;
    }

    private void setupAdapter() {
        adapter.addFrag(FragmentGroupManageTab.newInstance(Common.MYGROUPS_TAB), Common.TAB_NAME_MYGROUPS);
        adapter.addFrag(FragmentGroupManageTab.newInstance(Common.INVITEGROUPS_TAB), Common.TAB_NAME_INVITEGROUPS);
    }

    public void refreshAllTab() {
        adapter.refreshAllTabs();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mTab = args.getInt("tab", 0);

        adapter = new ViewPagerAdapter(getChildFragmentManager());
        setupAdapter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_group_manage, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);
        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        setCurrentPage(mTab);
    }

    public void setCurrentPage(int i ){
        viewPager.setCurrentItem(i);
    }
}
