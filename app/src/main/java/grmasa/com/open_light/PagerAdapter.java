package grmasa.com.open_light;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import grmasa.com.open_light.fragments.Device_Fragment;
import grmasa.com.open_light.fragments.Room_Fragment;

public class PagerAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;

    PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new Device_Fragment();
            case 1:
                return new Room_Fragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}