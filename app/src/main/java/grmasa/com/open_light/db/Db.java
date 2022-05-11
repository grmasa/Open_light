package grmasa.com.open_light.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

public class Db extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "bulb.db";
    private static final int DATABASE_VERSION = 2;
    //Bulbs Table
    private static final String BULBS_TABLE_NAME = "bulbs";
    private static final String BULBS_COLUMN_ID = "id";
    private static final String BULBS_COLUMN_DEVICE_ID = "device_id";
    private static final String BULBS_COLUMN_IP = "ip";
    private static final String BULBS_COLUMN_NAME = "name";
    private static final String BULBS_COLUMN_FW = "fw";
    private static final String BULBS_COLUMN_PORT = "port";
    private static final String BULBS_COLUMN_SUPPORT = "support";
    //Rooms Table
    private static final String ROOMS_TABLE_NAME = "rooms";
    private static final String ROOMS_COLUMN_ID = "id";
    private static final String ROOMS_COLUMN_NAME = "name";
    //bulbsObRoom Table
    private static final String bulbsInRoom_TABLE_NAME = "bulbsObRoom";
    private static final String bulbsInRoom_COLUMN_ROOMNAME = "room_name";
    private static final String bulbsInRoom_COLUMN_BULBID = "bulb_id";

    public Db(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + BULBS_TABLE_NAME + "(" + BULBS_COLUMN_ID + " integer primary key, " + BULBS_COLUMN_DEVICE_ID + " text," + BULBS_COLUMN_IP + " text," + BULBS_COLUMN_FW + " text, " + BULBS_COLUMN_PORT + " text," + BULBS_COLUMN_SUPPORT + " text," + BULBS_COLUMN_NAME + " text)");
        db.execSQL("create table " + ROOMS_TABLE_NAME + "(" + ROOMS_COLUMN_ID + " integer primary key AUTOINCREMENT, " + ROOMS_COLUMN_NAME + " text)");
        db.execSQL("create table " + bulbsInRoom_TABLE_NAME + "(" + bulbsInRoom_COLUMN_ROOMNAME + " text, " + bulbsInRoom_COLUMN_BULBID + " text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("create table " + ROOMS_TABLE_NAME + "(" + ROOMS_COLUMN_ID + " integer primary key AUTOINCREMENT, " + ROOMS_COLUMN_NAME + " text)");
            db.execSQL("create table " + bulbsInRoom_TABLE_NAME + "(" + bulbsInRoom_COLUMN_ROOMNAME + " text, " + bulbsInRoom_COLUMN_BULBID + " text)");
        }
    }

    public void insertRoom(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(ROOMS_COLUMN_NAME, name);
        db.insert(ROOMS_TABLE_NAME, null, cv);
    }

    public int changeRoomName(String name, String oldName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ROOMS_COLUMN_NAME, name);
        db.update(ROOMS_TABLE_NAME, values, ROOMS_COLUMN_NAME + " = ? ", new String[]{oldName});
        Cursor res = db.rawQuery("select changes() 'affected_rows'", null);
        res.moveToFirst();
        int rows = res.getInt(res.getColumnIndex("affected_rows"));
        res.close();
        changeBulbsInRoomName(name, oldName);
        return rows;
    }

    public void deleteRoom(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(ROOMS_TABLE_NAME, ROOMS_COLUMN_NAME + " = ? ", new String[]{name});
        db.delete(bulbsInRoom_TABLE_NAME, bulbsInRoom_COLUMN_ROOMNAME + " = ? ", new String[]{name});
    }

    public void deleteBulbFromRoom(String device_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(bulbsInRoom_TABLE_NAME, bulbsInRoom_COLUMN_BULBID + " = ? ", new String[]{device_id});
    }

    public void changeBulbsInRoomName(String name, String oldName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(bulbsInRoom_COLUMN_ROOMNAME, name);
        db.update(bulbsInRoom_TABLE_NAME, values, bulbsInRoom_COLUMN_ROOMNAME + " = ? ", new String[]{oldName});
    }

    public ArrayList<Bulb> getAllBulbsFromRoom(String name) {
        ArrayList<Bulb> array_list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select " + bulbsInRoom_COLUMN_BULBID + " from " + bulbsInRoom_TABLE_NAME + " where " + bulbsInRoom_COLUMN_ROOMNAME + " = '" + name + "';", null);
        res.moveToFirst();
        while (!res.isAfterLast()) {
            Bulb b_temp = getBulb(res.getString(res.getColumnIndex(bulbsInRoom_COLUMN_BULBID)<0?0:res.getColumnIndex(bulbsInRoom_COLUMN_BULBID)));
            array_list.add(b_temp);
            res.moveToNext();
        }
        res.close();
        return array_list;
    }

    public void insertBulbInRoom(String roomName, String bulbID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(bulbsInRoom_COLUMN_ROOMNAME, roomName);
        cv.put(bulbsInRoom_COLUMN_BULBID, bulbID);
        db.insert(bulbsInRoom_TABLE_NAME, null, cv);
    }

    public void insertBulb(String device_id, String ip, String fw, String port, String support, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(BULBS_COLUMN_DEVICE_ID, device_id);
        cv.put(BULBS_COLUMN_IP, ip);
        cv.put(BULBS_COLUMN_FW, fw);
        cv.put(BULBS_COLUMN_NAME, name);
        cv.put(BULBS_COLUMN_PORT, port);
        cv.put(BULBS_COLUMN_SUPPORT, support);
        db.insert(BULBS_TABLE_NAME, null, cv);
    }

    public int updateBulbIP(String device_id, String ip) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(BULBS_COLUMN_IP, ip);
        db.update(BULBS_TABLE_NAME, values, BULBS_COLUMN_DEVICE_ID + " = ? ", new String[]{device_id});
        Cursor res = db.rawQuery("select changes() 'affected_rows'", null);
        res.moveToFirst();
        int rows = res.getInt(res.getColumnIndex("affected_rows"));
        res.close();
        return rows;
    }

    public void deleteBulb(String device_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(BULBS_TABLE_NAME, BULBS_COLUMN_DEVICE_ID + " = ? ", new String[]{device_id});
    }

    public Bulb getBulb(String device_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + BULBS_TABLE_NAME + " WHERE " + BULBS_COLUMN_DEVICE_ID + " LIKE  '%" + device_id + "%'", null);
        res.moveToFirst();
        Bulb b_temp = null;
        while (!res.isAfterLast()) {
            String tempDevID = res.getString(res.getColumnIndex(BULBS_COLUMN_DEVICE_ID));
            if (tempDevID.contains(" ")){
                tempDevID = tempDevID.replaceAll("\\s+","");
            }
            if(device_id.equals(tempDevID)) {
                b_temp = new Bulb(res.getString(res.getColumnIndex(BULBS_COLUMN_IP)), res.getString(res.getColumnIndex(BULBS_COLUMN_NAME)), res.getString(res.getColumnIndex(BULBS_COLUMN_DEVICE_ID)), res.getString(res.getColumnIndex(BULBS_COLUMN_PORT)), res.getString(res.getColumnIndex(BULBS_COLUMN_FW)), res.getString(res.getColumnIndex(BULBS_COLUMN_SUPPORT)));
            }
            res.moveToNext();
        }
        res.close();
        return b_temp;
    }

    public ArrayList<Bulb> getAllBulbs() {
        ArrayList<Bulb> array_list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + BULBS_TABLE_NAME, null);
        res.moveToFirst();

        while (!res.isAfterLast()) {
            Bulb b_temp = new Bulb(res.getString(res.getColumnIndex(BULBS_COLUMN_IP)), res.getString(res.getColumnIndex(BULBS_COLUMN_NAME)), res.getString(res.getColumnIndex(BULBS_COLUMN_DEVICE_ID)), res.getString(res.getColumnIndex(BULBS_COLUMN_PORT)), res.getString(res.getColumnIndex(BULBS_COLUMN_FW)), res.getString(res.getColumnIndex(BULBS_COLUMN_SUPPORT)));
            array_list.add(b_temp);
            res.moveToNext();
        }
        res.close();
        return array_list;
    }

    public ArrayList<Room> getAllRooms() {
        ArrayList<Room> array_list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + ROOMS_TABLE_NAME, null);
        res.moveToFirst();

        while (!res.isAfterLast()) {
            Room r_temp = new Room(res.getString(res.getColumnIndex(ROOMS_COLUMN_NAME)), res.getString(res.getColumnIndex(ROOMS_COLUMN_ID)));
            r_temp.setBulbList(getAllBulbsFromRoom(res.getString(res.getColumnIndex(ROOMS_COLUMN_NAME))));
            array_list.add(r_temp);
            res.moveToNext();
        }
        res.close();
        return array_list;
    }
}