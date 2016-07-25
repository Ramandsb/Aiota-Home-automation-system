package in.tagplug.tagplug.Database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.lang.ref.SoftReference;
import java.util.ArrayList;

import in.tagplug.tagplug.pojo.DataItems;


public class DatabaseOperations extends SQLiteOpenHelper {


    public DatabaseOperations(Context context) {
        super(context, TableData.Tableinfo.DATABASE_NAME, null, database_version);
    }


    public static final int database_version = 2;
    public String CREATE_APPLIANCE = "CREATE TABLE " + TableData.Tableinfo.TABLE_APPLIANCE + "(" + TableData.Tableinfo.TITLE + " TEXT," +TableData.Tableinfo.IMAGE_NAME + " TEXT," + TableData.Tableinfo.APP_ID + " TEXT," + TableData.Tableinfo.DEVICE_STATE + " TEXT," + TableData.Tableinfo.DEVICE_INTENSITY + " TEXT)";
    public String CREATE_SCHEDULE = "CREATE TABLE " + TableData.Tableinfo.TABLE_SCHEDULE + "("+TableData.Tableinfo.APP_ID + " TEXT,"+ TableData.Tableinfo.START_HOUR + " TEXT,"+TableData.Tableinfo.START_MIN+ " TEXT," + TableData.Tableinfo.END_HOUR + " TEXT," + TableData.Tableinfo.END_MIN + " TEXT," + TableData.Tableinfo.UNIQUE_STAMP + " TEXT);";
    public String CREATE_DATA_POINTS = "CREATE TABLE " + TableData.Tableinfo.TABLE_DATA_POINTS + "("+TableData.Tableinfo.DEVICE_ID + " TEXT,"+ TableData.Tableinfo.USER_ID + " TEXT,"+TableData.Tableinfo.DEVICE_LOCATION+ " TEXT," +TableData.Tableinfo.DEVICE_DESCRIPTION+ " TEXT," + TableData.Tableinfo.DEVICE_STRENGTH + " TEXT," + TableData.Tableinfo.DEVICE_INSTANTANIOUS_WATTAGE + " TEXT," + TableData.Tableinfo.EVENT_MODE + " TEXT,"+TableData.Tableinfo.EVENT_DESCRIPTION+ " TEXT," + TableData.Tableinfo.EVENT_NAME + " TEXT," + TableData.Tableinfo.EVENT_DEVICE_ID + " TEXT," + TableData.Tableinfo.EVENT_TIME_STAMP + " TEXT,"+ TableData.Tableinfo.EVENT_DEVICE_NAME + " TEXT,"+TableData.Tableinfo.EVENT_PATH+ " TEXT," + TableData.Tableinfo.OUTSIDE_TEMP + " TEXT," + TableData.Tableinfo.INSIDE_TEMP + " TEXT," + TableData.Tableinfo.CLOCK_TIME + " TEXT);";
    public String CREATE_SIGNUP_DETAILS = "CREATE TABLE " + TableData.Tableinfo.TABLE_SIGNUP_DETAILS + "(" + TableData.Tableinfo.USERNAME + " TEXT," +TableData.Tableinfo.HOME_LAT + " TEXT," + TableData.Tableinfo.HOME_LONG + " TEXT)";
    @Override
    public void onCreate(SQLiteDatabase sdb) {
        sdb.execSQL(CREATE_APPLIANCE);
        sdb.execSQL(CREATE_SCHEDULE);
        sdb.execSQL(CREATE_DATA_POINTS);
        sdb.execSQL(CREATE_SIGNUP_DETAILS);
        Log.d("Database operations", "Table created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TableData.Tableinfo.TABLE_APPLIANCE);
        onCreate(db);
    }

    public void putInformation(DatabaseOperations dop, String title, String image_name,String app_id){
        SQLiteDatabase SQ = dop.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TableData.Tableinfo.TITLE, title);
        cv.put(TableData.Tableinfo.IMAGE_NAME, image_name);
        cv.put(TableData.Tableinfo.APP_ID, app_id);
        cv.put(TableData.Tableinfo.DEVICE_STATE, "");
        cv.put(TableData.Tableinfo.DEVICE_INTENSITY, "");
        long k = SQ.insert(TableData.Tableinfo.TABLE_APPLIANCE, null, cv);
        Log.d("Database Created", "true");

    }
    public void putSignupdetails(DatabaseOperations dop, String username, String password,String home_lat,String home_long){
        SQLiteDatabase SQ = dop.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TableData.Tableinfo.USERNAME, username);
        cv.put(TableData.Tableinfo.HOME_LAT, home_lat);
        cv.put(TableData.Tableinfo.HOME_LONG, home_long);
        long k = SQ.insert(TableData.Tableinfo.TABLE_SIGNUP_DETAILS, null, cv);
        Log.d("Database Created", "true");

    }
    public void putDatapoints(DatabaseOperations dop,String Device_ID,String User_id,String Device_Location,String Device_Description,String Device_Strength,String Device_Instantaneous_wattage, String Event_Timestamp, String Event_mode, String EVENT_Description, String Event_Name, String Event_Device_id, String Event_device_name, String Event_temperature, String Event_path, String Outside_temp ,String inside_temp,String Clock_time) {
        SQLiteDatabase SQ = dop.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TableData.Tableinfo.DEVICE_ID, Device_ID);
        cv.put(TableData.Tableinfo.USER_ID, User_id);
        cv.put(TableData.Tableinfo.DEVICE_LOCATION, Device_Location);
        cv.put(TableData.Tableinfo.DEVICE_DESCRIPTION, Device_Description);
        cv.put(TableData.Tableinfo.DEVICE_STRENGTH, Device_Strength);
        cv.put(TableData.Tableinfo.DEVICE_INSTANTANIOUS_WATTAGE, Device_Instantaneous_wattage);
        cv.put(TableData.Tableinfo.EVENT_TIME_STAMP, Event_Timestamp);
        cv.put(TableData.Tableinfo.EVENT_MODE, Event_mode);
        cv.put(TableData.Tableinfo.EVENT_DESCRIPTION, EVENT_Description);
        cv.put(TableData.Tableinfo.EVENT_NAME, Event_Name);
        cv.put(TableData.Tableinfo.EVENT_DEVICE_ID, Event_Device_id);
        cv.put(TableData.Tableinfo.EVENT_DEVICE_NAME, Event_device_name);
        cv.put(TableData.Tableinfo.EVENT_PATH, Event_path);
        cv.put(TableData.Tableinfo.OUTSIDE_TEMP, Outside_temp);
        cv.put(TableData.Tableinfo.INSIDE_TEMP, inside_temp);
        cv.put(TableData.Tableinfo.CLOCK_TIME, Clock_time);
        long k = SQ.insert(TableData.Tableinfo.TABLE_DATA_POINTS, null, cv);
        Log.d("Database Created", "true");

    }
    public void putScheduleInformation(DatabaseOperations dop,String app_id, String start_hour, String start_min,String end_hour,String end_min,String unique_stamp) {
        SQLiteDatabase SQ = dop.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TableData.Tableinfo.APP_ID, app_id);
        cv.put(TableData.Tableinfo.START_HOUR, start_hour);
        cv.put(TableData.Tableinfo.START_MIN, start_min);
        cv.put(TableData.Tableinfo.END_HOUR, end_hour);
        cv.put(TableData.Tableinfo.END_MIN, end_min);
        cv.put(TableData.Tableinfo.UNIQUE_STAMP, unique_stamp);
        long k = SQ.insert(TableData.Tableinfo.TABLE_SCHEDULE, null, cv);
        Log.d("Database Created", "true");

    }





    public Cursor getSignupinformation(DatabaseOperations dop){

        SQLiteDatabase SQ = dop.getReadableDatabase();

        Log.d("DatabasRead", "");
        String[] coloumns = {TableData.Tableinfo.USERNAME, TableData.Tableinfo.HOME_LAT,TableData.Tableinfo.HOME_LONG,};
        Cursor cursor = SQ.query(TableData.Tableinfo.TABLE_SIGNUP_DETAILS, coloumns, null, null, null, null, null);


return cursor;
    }
   public String getSeekbar_val(DatabaseOperations dop,String id){
       String value="";
       String q="SELECT * FROM "+ TableData.Tableinfo.TABLE_APPLIANCE+" WHERE "+ TableData.Tableinfo.APP_ID+"=" + id;
       SQLiteDatabase SQ = dop.getReadableDatabase();

       Log.d("DatabasRead", "");
       String[] coloumns = {TableData.Tableinfo.DEVICE_INTENSITY,};
//       Cursor cursor = SQ.query(TableData.Tableinfo.TABLE_APPLIANCE, coloumns, null, null, null, null, null);
       Cursor  cursor = SQ.rawQuery(q,null);
       if (cursor != null && cursor.moveToFirst()) {
           do {
               //create a new movie object and retrieve the data from the cursor to be stored in this movie object
             value=  cursor.getString(cursor.getColumnIndex(TableData.Tableinfo.DEVICE_INTENSITY));
               Log.d("Database read", "true");
           }
           while (cursor.moveToNext());

           cursor.close();

       }

return value;
   }


    public ArrayList<DataItems> readData(DatabaseOperations dop) {
        ArrayList<DataItems> listData = new ArrayList<>();
        SQLiteDatabase SQ = dop.getReadableDatabase();

        Log.d("DatabasRead", "");
        String[] coloumns = {TableData.Tableinfo.TITLE, TableData.Tableinfo.IMAGE_NAME,TableData.Tableinfo.APP_ID,};
        Cursor cursor = SQ.query(TableData.Tableinfo.TABLE_APPLIANCE, coloumns, null, null, null, null, null);
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
        Cursor cursor = SQ.rawQuery("SELECT * from " + TableData.Tableinfo.TABLE_SCHEDULE + " where " + TableData.Tableinfo.APP_ID + "='" + device_name + "'", null, null);
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


    public void deleteRow(DatabaseOperations dop,String id) {

        SQLiteDatabase SQ = dop.getWritableDatabase();
            SQ.delete(TableData.Tableinfo.TABLE_APPLIANCE, TableData.Tableinfo.TITLE+"="+id, null);

    }
    public void deleteScheduler(DatabaseOperations dop,String timestamp) {

        SQLiteDatabase SQ = dop.getWritableDatabase();
        SQ.delete(TableData.Tableinfo.TABLE_SCHEDULE, TableData.Tableinfo.UNIQUE_STAMP+"="+timestamp, null);

    }

    public void updateRow(DatabaseOperations dop,ContentValues dataToInsert,String table_name,String id_to_compare,String id) {
        String where = "id=?";
        String[] whereArgs = new String[] {String.valueOf(id)};

        SQLiteDatabase SQ = dop.getWritableDatabase();
        SQ.update(table_name, dataToInsert, id_to_compare + "=" + id, null);

    }
    public void updateOutsideTemp(DatabaseOperations dop,String id,ContentValues dataToInsert) {
        String where = "id=?";
        String[] whereArgs = new String[] {String.valueOf(id)};

        SQLiteDatabase SQ = dop.getWritableDatabase();
        SQ.update(TableData.Tableinfo.TABLE_DATA_POINTS, dataToInsert, TableData.Tableinfo.DEVICE_ID + "=" + id, null);

    }

    public  void eraseData(DatabaseOperations dop,String tableName){
        SQLiteDatabase db = dop.getWritableDatabase(); // helper is object extends SQLiteOpenHelper
        db.delete(tableName, null, null);
        Log.d("Database Erased", "true");
    }

    public String  getProfilesCount() {
        String countQuery = "SELECT  * FROM " + TableData.Tableinfo.TABLE_APPLIANCE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        String count = Integer.toString(cnt);
        return count;
    }



}