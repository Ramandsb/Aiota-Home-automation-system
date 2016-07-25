package in.tagplug.tagplug.LoginSignup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import in.tagplug.tagplug.Database.DatabaseOperations;
import in.tagplug.tagplug.R;
import in.tagplug.tagplug.Services.GPSTracker;

public class Signup extends AppCompatActivity {

    EditText username,password;
    String home_lat="",home_long="";
    CheckBox home_location_checkbox;

    DatabaseOperations dop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        username= (EditText) findViewById(R.id.username);
        password= (EditText) findViewById(R.id.password);
        dop=new DatabaseOperations(this);
        home_location_checkbox= (CheckBox) findViewById(R.id.checkBox);
        home_location_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                getLocation();
            }
        });



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               String name =username.getText().toString();
                String pass =password.getText().toString();
                dop.putSignupdetails(dop,name,pass,home_lat,home_long);
                Intent i = new Intent(Signup.this,LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    public void getLocation(){
        GPSTracker gpsTracker = new GPSTracker(this);

        if (gpsTracker.getIsGPSTrackingEnabled())
        {
            String stringLatitude = String.valueOf(gpsTracker.latitude);


            String stringLongitude = String.valueOf(gpsTracker.longitude);
            home_lat=stringLatitude;
            home_long=stringLongitude;
            Log.d("latlong",home_lat+"///"+home_long);



            String country = gpsTracker.getCountryName(this);


            String city = gpsTracker.getLocality(this);


            String postalCode = gpsTracker.getPostalCode(this);


            String addressLine = gpsTracker.getAddressLine(this);

        }
        else
        {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gpsTracker.showSettingsAlert();
        }
    }
    }

