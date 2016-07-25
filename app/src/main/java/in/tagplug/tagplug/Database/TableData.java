package in.tagplug.tagplug.Database;

import android.provider.BaseColumns;

/**
 * Created by Ramandeep on 19-08-2015.
 */
public class TableData {
    public TableData() {

    }

    public static abstract class Tableinfo implements BaseColumns {
        public static final String DATABASE_NAME = "dbapp";
        /////////////////////////////////////////////////////////
        public static final String TITLE = "title";
        public static final String IMAGE_NAME = "image_name";
        public static final String APP_ID = "app_id";
        public static final String DEVICE_STATE = "device_state";
        public static final String DEVICE_INTENSITY = "device_intensity";
        public static final String TABLE_APPLIANCE = "table_appliance";
        //////////////////////////////////////////////////////////////////////
        public static final String TABLE_SCHEDULE = "table_schedule";
        public static final String START_HOUR = "start_hour";
        public static final String END_HOUR = "end_hour";
        public static final String START_MIN = "start_min";
        public static final String END_MIN = "end_min";
        public static final String UNIQUE_STAMP = "unique_stamp";
        ////////////////////////////////////////////////////
        public static final String TABLE_DATA_POINTS = "table_data_points";
        public static final String DEVICE_ID = "device_id";
        public static final String USER_ID = "user_id";
        public static final String DEVICE_LOCATION = "device_location";
        public static final String DEVICE_DESCRIPTION = "device_description";
        public static final String DEVICE_STRENGTH = "device_strength";
        public static final String DEVICE_INSTANTANIOUS_WATTAGE = "device_instantanious_wattage";
        public static final String EVENT_TIME_STAMP = "event_time_stamp";
        public static final String EVENT_MODE = "event_mode";
        public static final String EVENT_DESCRIPTION = "event_description";
        public static final String EVENT_NAME = "event_name";
        public static final String EVENT_DEVICE_ID = "event_device_id";
        public static final String EVENT_DEVICE_NAME = "event_device_name";
        public static final String EVENT_PATH = "event_path";
        public static final String OUTSIDE_TEMP = "outside_temp";
        public static final String INSIDE_TEMP = "inside_temp";
        public static final String CLOCK_TIME = "clock_time";
        /////////////////////////////////////////////////////////
        public static final String TABLE_SIGNUP_DETAILS = "table_signup_details";
        public static final String USERNAME = "username";
        public static final String PASSWORD = "password";
        public static final String HOME_LAT = "home_lat";
        public static final String HOME_LONG = "home_long";




    }
}