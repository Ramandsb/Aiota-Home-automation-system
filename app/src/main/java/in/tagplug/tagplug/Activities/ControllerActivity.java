package in.tagplug.tagplug.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import org.w3c.dom.Text;

import in.tagplug.tagplug.R;
import in.tagplug.tagplug.Services.Service_localMqtt;

public class ControllerActivity extends AppCompatActivity {
    TextView temp,device_name;
   int i=25;
    Intent sendAcData;
    String AcData="37190003",on_code="37190003",off_code="2154543585";
    boolean device_status=false;
    Switch device;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sendAcData = new Intent(Service_localMqtt.PUBLISH_LOCAL_DATA);
        Button plus= (Button) findViewById(R.id.plus_temp);
        Button minus= (Button) findViewById(R.id.minus_temp);
        device= (Switch) findViewById(R.id.switch1);
        device.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){

                    sendAcData.putExtra("status","on");
                    sendAcData.putExtra("data",on_code);
                    sendBroadcast(sendAcData);
                    Log.d("isChecked","true");
                }else {
                    sendAcData.putExtra("status","off");
                    sendAcData.putExtra("data",off_code);
                    sendBroadcast(sendAcData);
                    Log.d("isChecked", "false");
                }

            }
        });
         temp= (TextView) findViewById(R.id.temp);
        device_name= (TextView) findViewById(R.id.device_name);
        device_name.setText(""+getIntent().getExtras().getString("device_name"));
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (i>=16 && i<30){
                    i++;
                    temp.setText("" + i);
                 String data=   getAcData(i);
                    sendAcData.putExtra("status","change_temp");
                    sendAcData.putExtra("data",data);
                    sendBroadcast(sendAcData);

                }
            }
        });
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (i>16 && i<=30){
                    i--;
                    temp.setText(""+i);
                    String data=   getAcData(i);
                    sendAcData.putExtra("status","change_temp");
                    sendAcData.putExtra("data",data);
                    sendBroadcast(sendAcData);
                }
            }
        });
    }

    public String getAcData(int i){

        switch (i){
            case 16:
                AcData="876631033";
                on_code="876631033";
                off_code="919446699";
                break;
            case 17:
                AcData="2088257335";
                on_code="2088257335";
                off_code="4205610917";
                break;
            case 18:
                AcData="4294967295";
                on_code="1255807619";
                off_code="4095240985";
                break;
            case 19:
                AcData="3504702879";
                on_code="4294967295";
                off_code="2049168949";
                break;
            case 20:
                AcData="23571735";
                on_code="23571735";
                off_code="4294967295";
                break;
            case 21:
                AcData="3309735953";
                on_code="1874477187";
                off_code="3309735953";
                break;
            case 22:
                AcData="1801057171";
                on_code="1801057171";
                off_code="2440336321";
                break;
            case 23:
                AcData="4049952431";
                on_code="4049952431";
                off_code="394264285";
                break;
            case 24:
                AcData="3120530997";
                on_code="3120530997";
                off_code="3163346663";
                break;
            case 25:
                AcData="37190003";
                on_code="37190003";
                off_code="2154543585";
                break;
            case 26:
                AcData="3499707583";
                on_code="4294967295";
                off_code="2044173653";
                break;
            case 27:
                AcData="1453635547";
                on_code="1453635547";
                off_code="4293068913";
                break;
            case 28:
                AcData="2565703441";
                on_code="2565703441";
                off_code="4294967295";
                break;
            case 29:
                AcData="3777329743";
                on_code="3777329743";
                off_code="1599716029";
                break;
            case 30:
                AcData="48221581";
                on_code="48221581";
                off_code="91037247";
                break;

        }


        return AcData;
    }

}
