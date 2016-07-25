package in.tagplug.tagplug.Activities;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
//import com.synnapps.carouselview.CarouselView;
//import com.synnapps.carouselview.ImageListener;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import in.tagplug.tagplug.AwsIot.VisualActivity;
import in.tagplug.tagplug.Adapters.CustomGridViewAdapter;
import in.tagplug.tagplug.Database.DatabaseOperations;
import in.tagplug.tagplug.Database.TableData;
import in.tagplug.tagplug.LoginSignup.LoginActivity;
import in.tagplug.tagplug.R;
import in.tagplug.tagplug.Services.GPSTracker;
import in.tagplug.tagplug.Services.Service_localMqtt;
import in.tagplug.tagplug.WeatherReport.RemoteFetch;
import in.tagplug.tagplug.pojo.DataItems;
import in.tagplug.tagplug.pojo.Notify;

////////////created by Ramandeep Singh on 15/05/2016
///////////Main landing activity Which shows added devices//////////
public class MainActivity extends AppCompatActivity {
//    CarouselView carouselView;
    RelativeLayout rl;

    GridView grid;//////grid View object//////////


    DataItems dataItems;  ////////pojo class object
    ArrayList<DataItems> arrayList= new ArrayList<>();  //array list to be passed to adapter
    ArrayList<DataItems> database_list= new ArrayList<>();  ///array list to get data from database
    DatabaseOperations dop;  ///database operation object
    CustomGridViewAdapter ad;        //adapter to show grid view
    String device_name="",device_intensity="";
    String device_id="",device_image_name="";
    Handler handler;
    String temperature="";
    Intent broadcastreciever;
    public static String BROADCAST_MAIN="MAIN_broadcast";
    private final String WIFI_STATE_CHANGE = "android.net.wifi.WIFI_STATE_CHANGED";  //broadcast which shows connected and disconnected behaviour of the wifi
    SharedPreferences login_details; //shared preff to store login details from data base
    SeekBar seekBar ;TextView seekProg;
    Intent publishData;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
// shared preff for login details
        login_details=getSharedPreferences(LoginActivity.LOGINDETAILS,MODE_PRIVATE);
        publishData= new Intent(Service_localMqtt.PUBLISH_LOCAL_DATA);
        // configure the SlidingMenu
        SlidingMenu menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.LEFT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        menu.setShadowWidthRes(R.dimen.shadow_width);
        menu.setShadowDrawable(R.drawable.shadow);
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        menu.setMenu(R.layout.slided_menu);

        //registering recievers
        registerReceiver(receiveMessages, new IntentFilter(BROADCAST_MAIN));
        registerReceiver(wifiStatechange, new IntentFilter(WIFI_STATE_CHANGE));

        //slided menu buttons
        Button addDevice= (Button) menu.getMenu().findViewById(R.id.add_device);
        handler = new Handler();
       addDevice.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(MainActivity.this, InhouseMqttconnection.class);
               startActivity(intent);
           }
       });
        Button awsiot= (Button) menu.getMenu().findViewById(R.id.awsiotconnect);
        awsiot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, VisualActivity.class);
                startActivity(intent);
            }
        });

        //////////////////////////////////////////////
//////////////////////////////////////////
//        carouselView = (CarouselView) findViewById(R.id.carouselView);
//        carouselView.setPageCount(sampleImages.length);
//        carouselView.setImageListener(imageListener);
        dataItems= new DataItems();
        rl= (RelativeLayout) findViewById(R.id.rl);
        grid = (GridView) findViewById(R.id.gridView1);

        dop= new DatabaseOperations(this);
        database_list=dop.readData(dop);  //reading data from database

        Log.d("check", "");
//        dop.eraseData(dop, TableData.Tableinfo.TABLE_NAME);
//        database_list.clear();
        if (database_list.isEmpty()){
            dop.putInformation(dop,"Ac","ac","0");      //dummy data
            dop.putInformation(dop,"Fan","fan","1");
            dop.putInformation(dop,"Light","light","2");
           arrayList= dop.readData(dop);
        }else {

            arrayList=database_list;
        }




        ad= new CustomGridViewAdapter(this,R.layout.row_grid,arrayList); //gridview adapter


        grid.setAdapter(ad);

        grid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {  //grid item listener
                dataItems = arrayList.get(position);
                device_name = dataItems.getTitle();
                device_id = dataItems.getAppid();
                device_image_name = dataItems.getImage();


                Toast.makeText(getBaseContext(), dataItems.getTitle() + "LOng pressed" + device_id,
                        Toast.LENGTH_SHORT).show();

                open(device_image_name); //opening dialog box

                return false;
            }
        });

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
                                    long arg3) {
                // TODO Auto-generated method stub

                dataItems = arrayList.get(pos);

                Toast.makeText(getBaseContext(), dataItems.getTitle() + "////" + dataItems.getAppid(),
                        Toast.LENGTH_SHORT).show();
//publish data to topic
                publishData.putExtra("status", "");
                publishData.putExtra("topic", dataItems.getAppid() + "/to");
                publishData.putExtra("message", "{  \n" +
                        "   \"device_id\":"+dataItems.getAppid()+",\n" +
                        "   \"state\":\"on\"\n" +
                        "}");
                sendBroadcast(publishData);
                //subscribe to topic
                Intent subscribe_response = new Intent(Service_localMqtt.SUBSCRIBE_LOCAL_DATA);
                subscribe_response.putExtra("topic", dataItems.getAppid() + "/from");
                sendBroadcast(subscribe_response);


            }
        });
        //startService
        try{
            Intent intent = new Intent(MainActivity.this, Service_localMqtt.class);
            MainActivity.this.startService(intent);
            Notify.log("startService", "success");

        }catch (Exception e){
            Notify.log("startService","fail");
        }
    }

    public void open(final String device){
        String val="";
        int fetchedProgress=0;
//        if (dop.getSeekbar_val(dop,device_id)==null) {

          val=  dop.getSeekbar_val(dop, device_id);
        if (val.equals("")){
            fetchedProgress=0;

        }else fetchedProgress=Integer.valueOf(val);
            Notify.log("value got", val);
//        }


        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
// ...Irrelevant code for customizing the buttons and title
        dialogBuilder.setCancelable(true);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_label_editor, null);
        dialogBuilder.setView(dialogView);


        final View edit = (View) dialogView.findViewById(R.id.edit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditAppliances.class);
                intent.putExtra("app_id", device_id);
                intent.putExtra("class_name", "main");
                startActivity(intent);
                finish();
            }
        });
        final View schedule = (View) dialogView.findViewById(R.id.schedule);
        schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ScheduleActivity.class);
                intent.putExtra("device_name", device_name);
                startActivity(intent);
                finish();
            }
        });
        final View controller = (View) dialogView.findViewById(R.id.controller);
        controller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ControllerActivity.class);
                intent.putExtra("device_name", device_name);
                startActivity(intent);
                finish();
            }
        });
        seekBar= (SeekBar) dialogView.findViewById(R.id.speed);
        seekProg= (TextView) dialogView.findViewById(R.id.seekprogess);

        switch (device){
            case "ac":
                seekBar.setMax(1400);
                seekBar.setProgress(fetchedProgress);
               setProgressvalues(fetchedProgress,device);
                break;
            case "light":
                seekBar.setMax(100);
                setProgressvalues(fetchedProgress, device);
                seekBar.setProgress(fetchedProgress);
                break;
            case "fan":
                seekBar.setMax(100);
                setProgressvalues(fetchedProgress, device);
                seekBar.setProgress(fetchedProgress);
                break;
            case "lamp":
                seekBar.setMax(100);
                setProgressvalues(fetchedProgress, device);
                seekBar.setProgress(fetchedProgress);
                break;

        }
//

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Notify.log("onProgressChanged", "" + progress);
                setProgressvalues(progress,device);


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                publishData.putExtra("status", "");
                publishData.putExtra("topic", device_id + "/to");
                publishData.putExtra("message", "{  \n" +
                        "   \"device_id\":"+device_id+",\n" +
                        "   \"device_intensity\":"+device_intensity+"\n" +
                        "}");
                sendBroadcast(publishData);

            }
        });
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();



    }

    public void setProgressvalues(int progress,String device){

        if (device.equals("ac")){
            if (progress>0 && progress<100){
                seekProg.setText("16");
                device_intensity=String.valueOf(progress);

                       device_intensity="16";
            }else if (progress>101 && progress<200){
                seekProg.setText("17");
                device_intensity=String.valueOf(progress);
                       device_intensity="17";
            }else if (progress>201 && progress<300){
                seekProg.setText("18");
                device_intensity=String.valueOf(progress);
                       device_intensity="18";
            }else if (progress>301 && progress<400){
                seekProg.setText("19");
                       device_intensity="19";
                device_intensity=String.valueOf(progress);
            }else if (progress>401 && progress<500){
                seekProg.setText("20");
                       device_intensity="20";
                device_intensity=String.valueOf(progress);
            }else if (progress>501 && progress<600){
                seekProg.setText("21");
                       device_intensity="21";
                device_intensity=String.valueOf(progress);
            }else if (progress>601 && progress<700){
                seekProg.setText("22");
                device_intensity=String.valueOf(progress);
                       device_intensity="22";
            }else if (progress>701 && progress<800){
                seekProg.setText("23");
                device_intensity=String.valueOf(progress);
                       device_intensity="23";
            }else if (progress>801 && progress<900){
                seekProg.setText("24");
                device_intensity=String.valueOf(progress);
                       device_intensity="24";
            }else if (progress>901 && progress<1000){
                seekProg.setText("25");
                device_intensity=String.valueOf(progress);
                       device_intensity="25";
            }else if (progress>1001 && progress<1100){
                seekProg.setText("26");
                device_intensity=String.valueOf(progress);
                       device_intensity="26";
            }else if (progress>1101 && progress<1200){
                seekProg.setText("27");
                device_intensity=String.valueOf(progress);
                       device_intensity="27";
            }else if (progress>1201 && progress<1300){
                seekProg.setText("28");
                device_intensity=String.valueOf(progress);
                       device_intensity="28";
            }else if (progress>1301 && progress<1399){
                seekProg.setText("29");
                device_intensity=String.valueOf(progress);
                       device_intensity="29";
            }else if (progress==1400){
                seekProg.setText("30");
                device_intensity=String.valueOf(progress);
                       device_intensity="30";
            }
            ContentValues cv= new ContentValues();
            cv.put(TableData.Tableinfo.DEVICE_INTENSITY,progress);
            dop.updateRow(dop,cv, TableData.Tableinfo.TABLE_APPLIANCE, TableData.Tableinfo.APP_ID,device_id);

        }else if (device.equals("light")){
            if (progress==0){
                seekProg.setText("0");
                device_intensity=String.valueOf(progress);
                        device_intensity="0";
            }else if (progress>1 && progress<24){
                seekProg.setText("1");
                device_intensity=String.valueOf(progress);
                        device_intensity="1";
            }else if (progress>24 && progress<50){
                seekProg.setText("2");
                device_intensity=String.valueOf(progress);
                        device_intensity="2";
            }else if (progress>50 && progress<74){
                seekProg.setText("3");
                device_intensity=String.valueOf(progress);
                        device_intensity="3";
            }else if (progress>75 && progress<99){
                seekProg.setText("4");
                device_intensity=String.valueOf(progress);
                        device_intensity="4";
            }else if (progress==100){
                seekProg.setText("5");
                device_intensity=String.valueOf(progress);
                        device_intensity="5";
            }

            ContentValues cv= new ContentValues();
            cv.put(TableData.Tableinfo.DEVICE_INTENSITY,device_intensity);
            dop.updateRow(dop,cv, TableData.Tableinfo.TABLE_APPLIANCE, TableData.Tableinfo.APP_ID,device_id);


        }else if (device.equals("fan")){
            if (progress==0){
                seekProg.setText("0");
                device_intensity=String.valueOf(progress);
                        device_intensity="0";
            }else if (progress>1 && progress<24){
                seekProg.setText("1");
                device_intensity=String.valueOf(progress);
                        device_intensity="1";
            }else if (progress>24 && progress<50){
                seekProg.setText("2");
                device_intensity=String.valueOf(progress);
                        device_intensity="2";
            }else if (progress>50 && progress<74){
                seekProg.setText("3");
                device_intensity=String.valueOf(progress);
                        device_intensity="3";
            }else if (progress>75 && progress<99){
                seekProg.setText("4");
                device_intensity=String.valueOf(progress);
                        device_intensity="4";
            }else if (progress==100){
                seekProg.setText("5");
                device_intensity=String.valueOf(progress);
                        device_intensity="5";
            }

            ContentValues cv= new ContentValues();
            cv.put(TableData.Tableinfo.DEVICE_INTENSITY,device_intensity);
            dop.updateRow(dop,cv, TableData.Tableinfo.TABLE_APPLIANCE, TableData.Tableinfo.APP_ID,device_id);


        }else if (device.equals("lamp")){
            if (progress==0){
                seekProg.setText("0");
                device_intensity=String.valueOf(progress);
                        device_intensity="0";
            }else if (progress>1 && progress<24){
                seekProg.setText("1");
                device_intensity=String.valueOf(progress);
                        device_intensity="1";
            }else if (progress>24 && progress<50){
                seekProg.setText("2");
                device_intensity=String.valueOf(progress);
                        device_intensity="2";
            }else if (progress>50 && progress<74){
                seekProg.setText("3");
                device_intensity=String.valueOf(progress);
                        device_intensity="3";
            }else if (progress>75 && progress<99){
                seekProg.setText("4");
                device_intensity=String.valueOf(progress);
                        device_intensity="4";
            }else if (progress==100){
                seekProg.setText("5");
                device_intensity=String.valueOf(progress);
                        device_intensity="5";
            }

            ContentValues cv= new ContentValues();
            cv.put(TableData.Tableinfo.DEVICE_INTENSITY,device_intensity);
            dop.updateRow(dop,cv, TableData.Tableinfo.TABLE_APPLIANCE, TableData.Tableinfo.APP_ID,device_id);


        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(wifiStatechange);
        unregisterReceiver(receiveMessages);
    }

    // To set simple images
//    ImageListener imageListener = new ImageListener() {
//        @Override
//        public void setImageForPosition(int position, ImageView imageView) {
//
////            Picasso.with(getApplicationContext()).load(sampleNetworkImageURLs[position]).placeholder(sampleImages[0]).error(sampleImages[3]).fit().centerCrop().into(imageView);
//
//            imageView.setImageResource(sampleImages[position]);
//        }
//    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
//            try{
//
//                Intent intent = new Intent(MainActivity.this, Service_localMqtt.class);
//                MainActivity.this.startService(intent);
//                Notify.log("startService", "success");
//
//            }catch (Exception e){
//                Notify.log("startService","fail");
//            }
//            dop.putDatapoints(dop, device_id, "", device_location, device_description, device_strength, device_instantaneous_current, event_timestamp, event_mode, event_description, event_name, event_device_id, event_device_name, "", event_path, outside_temp, inside_temp, clock_time);
           dop.putDatapoints(dop, "123456", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1");
            String fetchedtemp=   updateWeatherData("delhi","123456");
            Notify.log("fetchedtemp", fetchedtemp);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private String updateWeatherData(final String city, final String device_id){
        new Thread(){
            public void run(){
                final JSONObject json = RemoteFetch.getJSON(MainActivity.this, city);
                Log.d("city",city);
                if(json == null){
                    handler.post(new Runnable(){
                        public void run(){
                            Toast.makeText(MainActivity.this,
                                    "place_not_found",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable(){
                        public void run(){
                            try {
                                JSONObject main = json.getJSONObject("main");
                                temperature=String.valueOf(main.getDouble("temp"));
                                Log.v("Temperature",main.getDouble("temp")+"");
                                Log.v("fullJson",json.toString());
                                ContentValues cv= new ContentValues();
                                cv.put(TableData.Tableinfo.OUTSIDE_TEMP, temperature);
                                dop.updateOutsideTemp(dop,device_id , cv);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        }.start();
   return temperature;
    }

    BroadcastReceiver receiveMessages = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            Bundle message= intent.getExtras();
            String device_id = message.getString("device_id");
            String device_location = message.getString("device_location");
            String device_description = message.getString("device_description");
            String device_strength = message.getString("device_strength");
            String device_instantaneous_current = message.getString("device_instantaneous_current");
            String event_timestamp = message.getString("event_timestamp");
            String event_description = message.getString("event_description");
            String event_mode = message.getString("event_mode");
            String event_name = message.getString("event_name");
            String event_device_id = message.getString("event_device_id");
            String event_device_name = message.getString("event_device_name");
            String event_path = message.getString("event_path");
            String outside_temp = message.getString("outside_temp");
            String inside_temp = message.getString("inside_temp");
            String clock_time = message.getString("clock_time");
            dop.putDatapoints(dop, device_id, "", device_location, device_description, device_strength, device_instantaneous_current, event_timestamp, event_mode, event_description, event_name, event_device_id, event_device_name, "", event_path, outside_temp, inside_temp, clock_time);
         String fetchedtemp=   updateWeatherData("delhi",device_id);
            Notify.log("fetchedtemp", fetchedtemp);
            ContentValues cv= new ContentValues();
            cv.put(TableData.Tableinfo.OUTSIDE_TEMP,fetchedtemp);
            dop.updateOutsideTemp(dop, device_id, cv);
        }
    };

//    private void renderWeather(JSONObject json){
//        try {
//            if (json==null){
//                Log.v("json","null");
//
//            }else {
//                JSONObject main = json.getJSONObject("main");
//                Log.v("Temperature",main.getDouble("temp")+"");
//                Log.v("fullJson",json.toString());
//            }
//        }catch(Exception e){
//            Log.e("SimpleWeather", e.toString());
//        }
//    }


    BroadcastReceiver wifiStatechange = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            double stringLatitude,stringLongitude;
          String home_lat=  login_details.getString("home_lat", "");
            String home_long= login_details.getString("home_long", "");
            if(intent.getAction().equals(WIFI_STATE_CHANGE)) {
//                dop.getSignupinformation(dop);
//                DataItems items= new DataItems();
//             double homelat=  Double.valueOf(items.getHome_lat()) ;
//                double homelong=  Double.valueOf(items.getHome_long()) ;
                GPSTracker gpsTracker = new GPSTracker(context);
                Notify.log("wifistate","changed");
                ConnectivityManager conMngr = (ConnectivityManager)context.getSystemService(context.CONNECTIVITY_SERVICE);
                android.net.NetworkInfo wifi = conMngr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                android.net.NetworkInfo mobile = conMngr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                if (wifi.getState().toString().equals("DISCONNECTED")){
                    stringLatitude = gpsTracker.latitude;
                    stringLongitude = gpsTracker.longitude;
                    Notify.log("currentstate", "disconnected");
                    double dist=    distance(stringLatitude,stringLongitude,Double.valueOf(home_lat),Double.valueOf(home_long));
                    Notify.log("distance", "is"+dist);
                    if (dist>0.00621371){
                        Notify.log("Inhome","false");
                    }else {
                        Notify.log("Inhome","true");
                    }
                }else if (wifi.getState().toString().equals("CONNECTED")){
                    stringLatitude = gpsTracker.latitude;
                    stringLongitude = gpsTracker.longitude;
                double dist=    distance(stringLatitude,stringLongitude,Double.valueOf(home_lat),Double.valueOf(home_long));
                    Notify.log("currentstate", "connected");
                    Notify.log("distance", "is"+dist);
                    if (dist>0.00621371){
                        Notify.log("Inhome","false");
                    }else {
                        Notify.log("Inhome","true");
                    }
                }
//                Notify.log("currentstate", wifi.getState().toString());

            }

        }
    };

    private double distance(double lat1, double lng1, double lat2, double lng2) {

        double earthRadius = 3958.75; // in miles, change to 6371 for kilometer output

        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);

        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);

        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        double dist = earthRadius * c;

        return dist; // output distance, in MILES
    }





}
