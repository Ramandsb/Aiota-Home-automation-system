package in.tagplug.tagplug;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

public class EditAppliances extends AppCompatActivity {
    EditText iconed;
    ImageButton one,two,three,four;
    String id="";
    String class_name="";
    String image_name="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_appliances);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        id=getIntent().getExtras().getString("app_id");
        class_name =getIntent().getExtras().getString("class_name");
         iconed= (EditText) findViewById(R.id.iconname);
        one= (ImageButton) findViewById(R.id.one);
        two= (ImageButton) findViewById(R.id.two);
        three= (ImageButton) findViewById(R.id.three);
        four= (ImageButton) findViewById(R.id.four);
        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                image_name="fan";


            }
        });

        two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                image_name="ac";

            }
        });

        three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                image_name="bulb";
            }
        });

        four.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                image_name="lamp";
            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseOperations dop= new DatabaseOperations(EditAppliances.this);
                String icon="";
                       icon= iconed.getText().toString();
                if (class_name.equals("main")){
                    ContentValues dataToInsert = new ContentValues();
                    dataToInsert.put(TableData.Tableinfo.TITLE, icon);
                    dop.updateRow(dop, id, dataToInsert);
                    ContentValues data = new ContentValues();
                    data.put(TableData.Tableinfo.IMAGE_NAME, image_name);
                    dop.updateRow(dop, id, data);
                    Intent intent = new Intent(EditAppliances.this,MainActivity.class);
                    startActivity(intent);
                    finish();

                }else if (class_name.equals("inhouse")){
                    dop.putInformation(dop, icon, image_name, Device_id());
                    if (icon.equals("")){
                        Intent intent = new Intent(EditAppliances.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    }else {
                        ContentValues dataToInsert = new ContentValues();
                        dataToInsert.put(TableData.Tableinfo.TITLE, icon);
                        dop.updateRow(dop, id, dataToInsert);
                        Intent intent = new Intent(EditAppliances.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                }


            }
        });


    }

    public  String Device_id(){
        String newId=String.valueOf(System.currentTimeMillis());
        return newId;
    }


}
