package grmasa.com.open_light.db;

import java.util.ArrayList;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

public class Db extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "bulb.db";
    private static final String BULBS_TABLE_NAME = "bulbs";
    private static final String BULBS_COLUMN_ID = "id";
    private static final String BULBS_COLUMN_DEVICE_ID = "device_id";
    private static final String BULBS_COLUMN_IP = "ip";
    private static final String BULBS_COLUMN_NAME = "name";
    private static final String BULBS_COLUMN_FW = "fw";
    private static final String BULBS_COLUMN_PORT = "port";
    private static final String BULBS_COLUMN_SUPPORT = "support";

    public Db(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+BULBS_TABLE_NAME + "("+BULBS_COLUMN_ID+" integer primary key, "+BULBS_COLUMN_DEVICE_ID+" text,"+BULBS_COLUMN_IP+" text,"+BULBS_COLUMN_FW+" text, "+BULBS_COLUMN_PORT+" text,"+BULBS_COLUMN_SUPPORT+" text,"+BULBS_COLUMN_NAME+" text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+BULBS_TABLE_NAME);
        onCreate(db);
    }

    public void insertBulb (String device_id, String ip, String fw, String port, String support, String name) {
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

    public String getData(String device_id, String col) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery( "select "+col+" from "+BULBS_TABLE_NAME+" where "+BULBS_COLUMN_DEVICE_ID+"='"+device_id+"'", null );
        res.moveToFirst();
        String temp = res.getString(res.getColumnIndex(BULBS_COLUMN_IP));
        res.close();
        return temp;
    }

    public int updateBulbIP (String device_id, String ip) {
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

    public void deleteBulb (String device_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(BULBS_TABLE_NAME, BULBS_COLUMN_DEVICE_ID + " = ? ", new String[]{device_id});
    }

    public Bulb getBulb(String device_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM "+BULBS_TABLE_NAME+" WHERE "+BULBS_COLUMN_DEVICE_ID+" = '"+device_id+"'", null);
        res.moveToFirst();
        Bulb b_temp = null;
        while(!res.isAfterLast()){
            b_temp = new Bulb(res.getString(res.getColumnIndex(BULBS_COLUMN_IP)),res.getString(res.getColumnIndex(BULBS_COLUMN_NAME)),res.getString(res.getColumnIndex(BULBS_COLUMN_DEVICE_ID)),res.getString(res.getColumnIndex(BULBS_COLUMN_PORT)),res.getString(res.getColumnIndex(BULBS_COLUMN_FW)),res.getString(res.getColumnIndex(BULBS_COLUMN_SUPPORT)));
            res.moveToNext();
        }
        res.close();
        return b_temp;
    }

    public ArrayList<Bulb> getAllBulbs() {
        ArrayList<Bulb> array_list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+BULBS_TABLE_NAME, null );
        res.moveToFirst();

        while(!res.isAfterLast()){
            Bulb b_temp = new Bulb(res.getString(res.getColumnIndex(BULBS_COLUMN_IP)),res.getString(res.getColumnIndex(BULBS_COLUMN_NAME)),res.getString(res.getColumnIndex(BULBS_COLUMN_DEVICE_ID)),res.getString(res.getColumnIndex(BULBS_COLUMN_PORT)),res.getString(res.getColumnIndex(BULBS_COLUMN_FW)),res.getString(res.getColumnIndex(BULBS_COLUMN_SUPPORT)));
            array_list.add(b_temp);
            res.moveToNext();
        }
        res.close();
        return array_list;
    }
}