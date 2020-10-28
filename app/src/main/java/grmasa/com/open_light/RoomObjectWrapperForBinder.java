package grmasa.com.open_light;

import android.app.ProgressDialog;
import android.os.Binder;

import grmasa.com.open_light.db.Room;

public class RoomObjectWrapperForBinder extends Binder {

    private Room mData;
    private ProgressDialog dialog;

    public RoomObjectWrapperForBinder(Room data) {
        this.mData = data;
    }

    public RoomObjectWrapperForBinder(ProgressDialog dialog) {
        this.dialog = dialog;
    }

    public Room getData() {
        return mData;
    }
    public ProgressDialog getDataDialog() {
        return dialog;
    }
}