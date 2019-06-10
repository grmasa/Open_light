package grmasa.com.open_light.device_options;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import grmasa.com.open_light.db.Bulb;
import grmasa.com.open_light.device_options.fragments.Color_fragment;
import grmasa.com.open_light.device_options.fragments.Recommended_fragment;

public class Device_PagerAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;
    private Bulb bulb;

    Device_PagerAdapter(FragmentManager fm, int NumOfTabs, Bulb b) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.bulb = b;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                Fragment recommended_fragment = new Recommended_fragment();
                Bundle recommended_bundle = new Bundle();
                recommended_bundle.putParcelable("bulb_v", bulb);
                recommended_fragment.setArguments(recommended_bundle);
                return recommended_fragment;
            case 1:
                Fragment color_fragment = new Color_fragment();
                Bundle color_bundle = new Bundle();
                color_bundle.putParcelable("bulb_v", bulb);
                color_fragment.setArguments(color_bundle);
                return color_fragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}