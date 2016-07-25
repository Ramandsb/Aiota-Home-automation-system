package in.tagplug.tagplug;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;


public class DatabaseOperations extends SQLiteOpenHelper {


    public DatabaseOperations(Context context) {
        super(context, TableData.Tableinfo.DATABASE_NAME, null, database_version);
    }

    public static final int database_version = 2;
    public String CREATE_QUERY = "CREATE TABLE " + TableData.Tableinfo.TABLE_NAME + "(" + TableData.Tableinfo.TITLE + " TEXT," +TableData.Tableinfo.IMAGE_NAME + " TEXT," + TableData.Tableinfo.APP_ID + " TEXT)";
    public String CREATE_SCHEDULE = "CREATE TABLE " + TableData.Tableinfo.SCHEDULE_TABLE_NAME + "("+TableData.Tableinfo.APP_ID + " TEXT,"+ TableData.Tableinfo.START_HOUR + " TEXT,"+TableData.Tableinfo.START_MIN+ " TEXT," + TableData.Tableinfo.END_HOUR + " TEXT," + TableData.Tableinfo.END_MIN + " TEXT," + TableData.Tableinfo.UNIQUE_STAMP + " TEXT);";

    @Override
    public void onCreate(SQLiteDatabase sdb) {
        sdb.execSQL(CREATE_QUERY);
        sdb.execSQL(CREATE_SCHEDULE);
        Log.d("Database operations", "Table created");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TableData.Tableinfo.TABLE_NAME);
        onCreate(db);
    }

    public void putInformation(DatabaseOperations dop, String title, String image_name,String app_id)

    {
        SQLiteDatabase SQ = dop.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TableData.Tableinfo.TITLE, title);
        cv.put(TableData.Tableinfo.IMAGE_NAME, image_name);
        cv.put(TableData.Tableinfo.APP_ID, app_id);
        long k = SQ.insert(TableData.Tableinfo.TABLE_NAME, null, cv);
        Log.d("Database Created", "true");

    }
    public void putScheduleInformation(DatabaseOperations dop,String app_id, String start_hour, String start_min,String end_hour,String end_min,String unique_stamp)

    {
        SQLiteDatabase SQ = dop.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TableData.Tableinfo.APP_ID, app_id);
        cv.put(TableData.Tableinfo.START_HOUR, start_hour);
        cv.put(TableData.Tableinfo.START_MIN, start_min);
        cv.put(TableData.Tableinfo.END_HOUR, end_hour);
        cv.put(TableData.Tableinfo.END_MIN, end_min);
        cv.put(TableData.Tableinfo.UNIQUE_STAMP, unique_stamp);
        long k = SQ.insert(TableData.Tableinfo.SCHEDULE_TABLE_NAME, null, cv);
        Log.d("Database Created", "true");

    }



    public ArrayList<DataItems> readData(DatabaseOperations dop) {
        ArrayList<DataItems> listData = new ArrayList<>();
        SQLiteDatabase SQ = dop.getReadableDatabase();

        Log.d("DatabasRead", "");
        String[] coloumns = {TableData.Tableinfo.TITLE, TableData.Tableinfo.IMAGE_NAME,TableData.Tableinfo.APP_ID,};
        Cursor cursor = SQ.query(TableData.Tableinfo.TABLE_NAME, coloumns, null, null, null, null, null);
//        Cursor cursor = SQ.rawQuery("SELECT * from " + TableData.Tableinfo.TABLE_NAME + " ORDER BY " + TableData.Tableinfo.TIMETOSTART + " ASC", null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                //create a new movie object and retrieve the data from the cursor to be stored in this movie object
                DataItems item = new DataItems();
                item.setTitle(cursor.getString(cursor.getColumnIndex(TableData.Tableinfo.TITLE)));
                item.setImage(cursor.getString(cursor.getColumnIndex(TableData.Tableinfo.IMAGE_NAME)));
                item.setAppid(cursor.getString(cursor.getColumnIndex(TableData.Tableinfo.APP_ID)));
                listData.add(item);
                Log.d("Database read", "true");
            }
            while (cursor.moveToNext());

                cursor.close();

        }
        return listData;
    }

    public ArrayList<DataItems> readScheduleData(DatabaseOperations dop,String device_name) {
        ArrayList<DataItems> listData = new ArrayList<>();
        SQLiteDatabase SQ = dop.getReadableDatabase();

        Log.d("DatabasRead", "");
        String[] coloumns = {TableData.Tableinfo.APP_ID, TableData.Tableinfo.START_HOUR,TableData.Tableinfo.START_MIN,TableData.Tableinfo.END_HOUR,TableData.Tableinfo.END_MIN,};
//        Cursor cursor = SQ.query(TableData.Tableinfo.SCHEDULE_TABLE_NAME, coloumns, null, null, null, null, null);
        Cursor cursor = SQ.rawQuery("SELECT * from " + TableData.Tableinfo.SCHEDULE_TABLE_NAME + " where " + TableData.Tableinfo.APP_ID + "='" + device_name + "'", null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                //create a new movie object and retrieve the data from the cursor to be stored in this movie object
                DataItems item = new DataItems();
                item.setAppid(cursor.getString(cursor.getColumnIndex(TableData.Tableinfo.APP_ID)));
                item.setStart_hour(cursor.getString(cursor.getColumnIndex(TableData.Tableinfo.START_HOUR)));
                item.setStart_min(cursor.getString(cursor.getColumnIndex(TableData.Tableinfo.START_MIN)));
                item.setEnd_hour(cursor.getString(cursor.getColumnIndex(TableData.Tableinfo.END_HOUR)));
                item.setEnd_min(cursor.getString(cursor.getColumnIndex(TableData.Tableinfo.END_MIN)));
                item.setUnique_stamp(cursor.getString(cursor.getColumnIndex(TableData.Tableinfo.UNIQUE_STAMP)));
                listData.add(item);
                Log.d("Database read", "true");
            }
            while (cursor.moveToNext());

            cursor.close();

        }
        return listData;
    }

    public void deleteRow(DatabaseOperations dop,String id)
    {

        SQLiteDatabase SQ = dop.getWritableDatabase();
            SQ.delete(TableData.Tableinfo.TABLE_NAME, TableData.Tableinfo.TITLE+"="+id, null);

    }
    public void deleteScheduler(DatabaseOperations dop,String timestamp)
    {

        SQLiteDatabase SQ = dop.getWritableDatabase();
        SQ.delete(TableData.Tableinfo.SCHEDULE_TABLE_NAME, TableData.Tableinfo.UNIQUE_STAMP+"="+timestamp, null);

    }
    public void updateRow(DatabaseOperations dop,String id,ContentValues dataToInsert)
    {
        String where = "id=?";
        String[] whereArgs = new String[] {String.valueOf(id)};

        SQLiteDatabase SQ = dop.getWritableDatabase();
        SQ.update(TableData.Tableinfo.TABLE_NAME, dataToInsert, TableData.Tableinfo.APP_ID + "=" + id, null);

    }

    public  void eraseData(DatabaseOperations dop,String tableName){
        SQLiteDatabase db = dop.getWritableDatabase(); // helper is object extends SQLiteOpenHelper
        db.delete(tableName, null, null);
        Log.d("Database Erased", "true");
    }

    public String  getProfilesCount() {
        String countQuery = "SELECT  * FROM " + TableData.Tableinfo.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        String count = Integer.toString(cnt);
        return count;
    }



}