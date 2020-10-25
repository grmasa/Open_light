package grmasa.com.open_light;

import android.os.Binder;

import grmasa.com.open_light.db.Room;

public class RoomObjectWrapperForBinder extends Binder {

    private final Room mData;

    public RoomObjectWrapperForBinder(Room data) {
        mData = data;
    }

    public Room getData() {
        return mData;
    }
}