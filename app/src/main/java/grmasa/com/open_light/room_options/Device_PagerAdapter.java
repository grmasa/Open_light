package grmasa.com.open_light.room_options;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import grmasa.com.open_light.db.Room;
import grmasa.com.open_light.room_options.fragments.Color_fragment;
import grmasa.com.open_light.room_options.fragments.Recommended_fragment;

public class Device_PagerAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;
    private Room room;

    Device_PagerAdapter(FragmentManager fm, int NumOfTabs, Room b) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.room = b;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                Fragment recommended_fragment = new Recommended_fragment();
                Bundle recommended_bundle = new Bundle();
                recommended_bundle.putParcelable("bulb_v", room);
                recommended_fragment.setArguments(recommended_bundle);
                return recommended_fragment;
            case 1:
                Fragment color_fragment = new Color_fragment();
                Bundle color_bundle = new Bundle();
                color_bundle.putParcelable("bulb_v", room);
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