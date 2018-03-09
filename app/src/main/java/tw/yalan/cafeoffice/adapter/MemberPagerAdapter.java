package tw.yalan.cafeoffice.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * Created by Alan Ding on 2017/4/18.
 */

public class MemberPagerAdapter extends FragmentStatePagerAdapter {
    List<? extends Fragment> list;

    public MemberPagerAdapter(FragmentManager fm, List<? extends Fragment> list) {
        super(fm);
        this.list = list;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return super.getPageTitle(position);
    }

    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }
}
