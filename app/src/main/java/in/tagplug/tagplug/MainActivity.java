package in.tagplug.tagplug;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.facebook.stetho.DumperPluginsProvider;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.dumpapp.DumperPlugin;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.squareup.okhttp.OkHttpClient;
//import com.synnapps.carouselview.CarouselView;
//import com.synnapps.carouselview.ImageListener;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
//    CarouselView carouselView;
    RelativeLayout rl;
    GridView grid;
    List<String> list;
    DataItems dataItems;
    ArrayList<DataItems> arrayList= new ArrayList<>();
    ArrayList<DataItems> database_list= new ArrayList<>();
    DatabaseOperations dop;
    CustomGridViewAdapter ad;
    String device_name="";
    String device_id="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
        Button b= (Button) menu.getMenu().findViewById(R.id.add_device);
       b.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent= new Intent(MainActivity.this,InhouseMqttconnection.class);
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
        list=new ArrayList<String> ();
        dop= new DatabaseOperations(this);

        database_list=dop.readData(dop);

        Log.d("check", "");
//        dop.eraseData(dop, TableData.Tableinfo.TABLE_NAME);
//        database_list.clear();
        if (database_list.isEmpty()){
//            dop.putInformation(dop,"Ac","ac","0");
//            dop.putInformation(dop,"Fan","fan","1");
//            dop.putInformation(dop,"Light","light","2");
           arrayList= dop.readData(dop);
        }else {
            arrayList=database_list;
        }




        ad= new CustomGridViewAdapter(this,R.layout.row_grid,arrayList);


        grid.setAdapter(ad);

        grid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                dataItems = arrayList.get(position);
                device_name=dataItems.getTitle();
                device_id=dataItems.getAppid();

                Toast.makeText(getBaseContext(), dataItems.getTitle() + "LOng pressed" + device_id,
                        Toast.LENGTH_SHORT).show();
                open();

                return false;
            }
        });

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
                                    long arg3) {
                // TODO Auto-generated method stub

                dataItems = arrayList.get(pos);

                Toast.makeText(getBaseContext(), dataItems.getTitle()+"////"+dataItems.getAppid(),
                        Toast.LENGTH_SHORT).show();

            }
        });

    }

    public void open(){
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
                intent.putExtra("class_name","main");
                startActivity(intent);
                finish();
            }
        });
        final View schedule = (View) dialogView.findViewById(R.id.schedule);
        schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(MainActivity.this,ScheduleActivity.class);
                intent.putExtra("device_name",device_name);
                startActivity(intent);
                finish();
            }
        });
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
