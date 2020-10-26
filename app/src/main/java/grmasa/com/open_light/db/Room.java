package grmasa.com.open_light.db;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Room implements Parcelable {
    private String id;
    private String name;
    private ArrayList<Bulb> array_list;

    Room(String name, String id) {
        this.name = name;
        this.id = id;
    }

    protected Room(Parcel in) {
        id = in.readString();
        name = in.readString();
        array_list = in.createTypedArrayList(Bulb.CREATOR);
    }

    public static final Creator<Room> CREATOR = new Creator<Room>() {
        @Override
        public Room createFromParcel(Parcel in) {
            return new Room(in);
        }

        @Override
        public Room[] newArray(int size) {
            return new Room[size];
        }
    };

    public void setBulbList(ArrayList<Bulb> array_list) {
        this.array_list = array_list;
    }

    public ArrayList<Bulb> getBulbList() {
        return array_list;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getID() {
        return id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(array_list);
        dest.writeString(name);
    }
}
