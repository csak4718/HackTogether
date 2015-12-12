package apt.hacktogether.adapter;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import apt.hacktogether.fragment.FragmentBrowse;
import apt.hacktogether.fragment.FragmentTab;

/**
 * Created by de-weikung on 12/5/15.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {
    private List<FragmentTab> mFragmentList;
    private List<String> mFragmentTitleList;

    public void refreshAllTabs() {
        for(FragmentTab fragmentTab : mFragmentList) {
            fragmentTab.getNewData();
            Log.d("Fraglist", "The raglist" + mFragmentList);
        }
    }

    public void refreshTabsquery(String queryText){
        for(FragmentTab fragmentTab : mFragmentList) {
            fragmentTab.query(queryText);
            Log.d("query_Fraglist", "The raglist" + mFragmentList);
        }
    }

    public ViewPagerAdapter(FragmentManager manager) {
        super(manager);
        mFragmentList = new ArrayList<>();
        mFragmentTitleList = new ArrayList<>();
    }

    @Override
    public FragmentTab getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public long getItemId(int position) {
        return mFragmentList.get(position).hashCode();
    }

    public void addFrag(FragmentTab fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    public void clear() {
        mFragmentList.clear();
        mFragmentTitleList.clear();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }
}
