package in.tagplug.tagplug.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.borax12.materialdaterangepicker.time.RadialPickerLayout;
import com.borax12.materialdaterangepicker.time.TimePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import in.tagplug.tagplug.Database.DatabaseOperations;
import in.tagplug.tagplug.Listeners.ItemClickSupport;
import in.tagplug.tagplug.R;
import in.tagplug.tagplug.Adapters.Schedule_adapter;
import in.tagplug.tagplug.pojo.DataItems;

public class ScheduleActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener{

    private RecyclerView mRecyclerview;
    Schedule_adapter adapter;
    DatabaseOperations dop;
    private List<DataItems> databaselist = new ArrayList<DataItems>();
    DataItems dataItems;
    String device_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        device_name= (String) getIntent().getExtras().get("device_name");
        mRecyclerview= (RecyclerView) findViewById(R.id.scheduleRecyclerview);
        adapter= new Schedule_adapter(ScheduleActivity.this);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        mRecyclerview.setAdapter(adapter);
        mRecyclerview.setHasFixedSize(false);
        mRecyclerview.setLayoutManager(linearLayoutManager);
        dop = new DatabaseOperations(this);
        databaselist= dop.readScheduleData(dop,device_name);
        adapter.setData((ArrayList<DataItems>) databaselist);


        ItemClickSupport.addTo(mRecyclerview).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                dataItems = databaselist.get(position);
                String device_id = dataItems.getAppid();
                String start_hour = dataItems.getStart_hour();
                String start_min = dataItems.getStart_min();
                String end_hour = dataItems.getEnd_hour();
                String end_min = dataItems.getEnd_min();
                String embedd= device_id+"\n"+start_hour+":"+start_min+"\n"+end_hour+":"+end_min;

                Toast.makeText(ScheduleActivity.this, "clicked" + position + "  //" + embedd, Toast.LENGTH_LONG).show();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i= new Intent(ScheduleActivity.this,MainActivity.class);
                startActivity(i);finish();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.schedule, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.addschedule) {
            ShowDialogTimePicker();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int hourOfDayEnd, int minuteEnd) {
        String hourString = hourOfDay < 10 ? "0"+hourOfDay : ""+hourOfDay;
        String minuteString = minute < 10 ? "0"+minute : ""+minute;
        String hourStringEnd = hourOfDayEnd < 10 ? "0"+hourOfDayEnd : ""+hourOfDayEnd;
        String minuteStringEnd = minuteEnd < 10 ? "0"+minuteEnd : ""+minuteEnd;
        String time = "You picked the following time: From - "+hourString+"h"+minuteString+" To - "+hourStringEnd+"h"+minuteStringEnd;

        Log.d("Time :", time);
        long milis= System.currentTimeMillis();
        DatabaseOperations dop = new DatabaseOperations(ScheduleActivity.this);
        dop.putScheduleInformation(dop, device_name, hourString, minuteString, hourStringEnd, minuteStringEnd, String.valueOf(milis));
        adapter.setData(dop.readScheduleData(dop,device_name));
        finish();
        startActivity(getIntent());

    }

    public void ShowDialogTimePicker(){
        Calendar now = Calendar.getInstance();
        TimePickerDialog tpd = TimePickerDialog.newInstance(
                ScheduleActivity.this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                false
        );
        tpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Log.d("TimePicker", "Dialog was cancelled");
            }
        });
        tpd.show(getFragmentManager(), "Timepickerdialog");
    }

}
