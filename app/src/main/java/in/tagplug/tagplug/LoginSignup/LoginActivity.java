package in.tagplug.tagplug.LoginSignup;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;

import in.tagplug.tagplug.Activities.MainActivity;
import in.tagplug.tagplug.Database.DatabaseOperations;
import in.tagplug.tagplug.Database.TableData;
import in.tagplug.tagplug.R;
import in.tagplug.tagplug.pojo.DataItems;

public class LoginActivity extends AppCompatActivity {

    DatabaseOperations dop;
    Context context;
    Cursor cursor;
    EditText username,password;
    String curr_username="";
    SharedPreferences login_details_shared;
    public static String LOGINDETAILS="login_details";
    boolean details_matched=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        username= (EditText) findViewById(R.id.login_username);
        password= (EditText) findViewById(R.id.login_password);
        context=LoginActivity.this;
        details_matched=false;
        login_details_shared=getSharedPreferences(LOGINDETAILS,MODE_PRIVATE);
        Button signup= (Button) findViewById(R.id.signup_button);
        Button login= (Button) findViewById(R.id.login_button);

        dop=new DatabaseOperations(context);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this,Signup.class);
                startActivity(i);
                finish();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getProfiledetails();
                if (details_matched) {
                Intent i = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(i);
                finish();
                }else username.setError("enter Correct Username");
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });




    }
    public boolean getProfiledetails() {

      curr_username=  username.getText().toString();
        cursor= dop.getSignupinformation(dop);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                if (curr_username.equals(cursor.getString(cursor.getColumnIndex(TableData.Tableinfo.USERNAME)))) {
                    SharedPreferences.Editor editor = login_details_shared.edit();
                    editor.putString("username", cursor.getString(cursor.getColumnIndex(TableData.Tableinfo.USERNAME)));
                    editor.putString("home_lat", cursor.getString(cursor.getColumnIndex(TableData.Tableinfo.HOME_LAT)));
                    editor.putString("home_long", cursor.getString(cursor.getColumnIndex(TableData.Tableinfo.HOME_LONG)));
                    editor.commit();
                    details_matched = true;
                }else details_matched=false;

            }
            while (cursor.moveToNext());

            cursor.close();

       }
        return details_matched;
    }

}
