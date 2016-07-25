package in.tagplug.tagplug.AwsIot;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import in.tagplug.tagplug.R;

public class VisualActivity extends Activity {

    Button sub, pub;
    EditText topic, message;
    TextView status, mes;
    String topicstr, data;
    WifiManager wifiManager;

    public static String SEND_MESSAGE="SEND_MESSAGE",CONNECTION_STATUS="CONNECTION_STATUS";
    ListView lv;
    WifiManager wifi;
    String wifis[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visual);
        sub = (Button) findViewById(R.id.subscribe);
        pub = (Button) findViewById(R.id.publish);
        topic = (EditText) findViewById(R.id.topic);
        message = (EditText) findViewById(R.id.message);
        status = (TextView) findViewById(R.id.tvstatus);
        mes = (TextView) findViewById(R.id.mes);
        startService(new Intent(this, PubSubservice.class));
        registerReceiver(RecieveMessage, new IntentFilter(SEND_MESSAGE));
        registerReceiver(ConnectionStatus, new IntentFilter(CONNECTION_STATUS));
        sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                topicstr = topic.getText().toString();

                Intent subscribe = new Intent(PubSubservice.SUBSCRIBE_DATA);
                subscribe.putExtra("topic", topicstr);
                sendBroadcast(subscribe);

            }
        });
//        String ret=obj.statusis();
//        status.setText(ret);
        pub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                topicstr = topic.getText().toString();
                data = message.getText().toString();
                Intent publish = new Intent(PubSubservice.PUBLISH_DATA);
                publish.putExtra("topic", topicstr);
                publish.putExtra("data", data);
                sendBroadcast(publish);

            }
        });

    }

    protected void onResume() {
        super.onResume();
    }

    private BroadcastReceiver RecieveMessage = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String data = intent.getStringExtra("message");

            Log.d("recievedmessage", data + "///////");
            mes.setText(data);

        }
    };

    private BroadcastReceiver ConnectionStatus = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String statusStr = intent.getStringExtra("status");
            if(statusStr.equals("Connected")){
                pub.setEnabled(true);
            }else pub.setEnabled(false);

            Log.d("recievedmessage", status + "///////");
            status.setText(statusStr);

        }
    };

    @Override
    protected void onPause() {
        super.onPause();

    }
}
