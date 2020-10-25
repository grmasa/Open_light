package grmasa.com.open_light.db;

import java.util.ArrayList;

public class Room {
    private String id;
    private String name;
    private ArrayList<Bulb> array_list;

    Room(String name, String id) {
        this.name = name;
        this.id = id;
    }

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
}
