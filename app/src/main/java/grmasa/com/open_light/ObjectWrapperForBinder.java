package grmasa.com.open_light;

import android.os.Binder;

import grmasa.com.open_light.db.Bulb;

public class ObjectWrapperForBinder extends Binder {

    private final Bulb mData;

    public ObjectWrapperForBinder(Bulb data) {
        mData = data;
    }

    public Bulb getData() {
        return mData;
    }
}