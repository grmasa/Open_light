package grmasa.com.open_light;

import android.os.Binder;

import grmasa.com.open_light.db.Bulb;

public class BulbObjectWrapperForBinder extends Binder {

    private final Bulb mData;

    public BulbObjectWrapperForBinder(Bulb data) {
        mData = data;
    }

    public Bulb getData() {
        return mData;
    }
}