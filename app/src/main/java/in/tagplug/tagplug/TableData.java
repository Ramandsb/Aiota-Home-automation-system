package in.tagplug.tagplug;

import android.provider.BaseColumns;

/**
 * Created by Ramandeep on 19-08-2015.
 */
public class TableData {
    public TableData() {

    }

    public static abstract class Tableinfo implements BaseColumns {
        public static final String TITLE = "title";
        public static final String IMAGE_NAME = "image_name";
        public static final String APP_ID = "app_id";
        public static final String DATABASE_NAME = "dbapp";
        public static final String TABLE_NAME = "appliance_table";
        public static final String SCHEDULE_TABLE_NAME = "schedule_table";
        public static final String START_HOUR = "start_hour";
        public static final String END_HOUR = "end_hour";
        public static final String START_MIN = "start_min";
        public static final String END_MIN = "end_min";
        public static final String UNIQUE_STAMP = "unique_stamp";

    }
}