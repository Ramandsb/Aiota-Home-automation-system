package in.tagplug.tagplug.Activities;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.internal.ConnectActionListener;


import java.util.ArrayList;

import in.tagplug.tagplug.Listeners.ItemClickSupport;
import in.tagplug.tagplug.Adapters.MyAdapter;
import in.tagplug.tagplug.R;
import in.tagplug.tagplug.Services.Service_localMqtt;
import in.tagplug.tagplug.pojo.DataItems;
import in.tagplug.tagplug.pojo.Notify;

public class InhouseMqttconnection extends AppCompatActivity implements MqttCallback{
    MqttAndroidClient client;
    String clientId;
    Intent broadcastIntent;
    Intent subscribe,publish,unsubscribe;
    int i=0;

    Bundle bundle= new Bundle();
    ConnectActionListener connectActionListener;
   public static String message="hello";
    RecyclerView mRecyclerview;
    MyAdapter myAdapter;
    Context mycontext;
   public static String BROADCAST="message_arrived";
    ArrayList<String> messageList=new ArrayList<>();
    ArrayList<DataItems> your_array_list = new ArrayList<>();
    DataItems dataItems;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inhouse_mqttconnection);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);




//            connect();


        mRecyclerview= (RecyclerView) findViewById(R.id.device_list);
        dataItems=new DataItems();
        broadcastIntent = new Intent(BROADCAST);
        subscribe = new Intent(Service_localMqtt.SUBSCRIBE_LOCAL_DATA);
        publish = new Intent(Service_localMqtt.PUBLISH_LOCAL_DATA);
        unsubscribe = new Intent(Service_localMqtt.UNSUBSCRIBE_LOCAL_DATA);
        registerReceiver(receiveMessages, new IntentFilter(BROADCAST));
        myAdapter=new MyAdapter(this);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        mRecyclerview.setAdapter(myAdapter);
        mRecyclerview.setHasFixedSize(false);
        mRecyclerview.setLayoutManager(linearLayoutManager);
        ItemClickSupport.addTo(mRecyclerview).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {


                dataItems=your_array_list.get(position);
                Intent i= new Intent(InhouseMqttconnection.this,EditAppliances.class);
                i.putExtra("app_id",position);
                i.putExtra("class_name","inhouse");
                i.putExtra("device_id",dataItems.getDevice_id());
                i.putExtra("device_name",dataItems.getDevice_name());
                startActivity(i);
                Toast.makeText(InhouseMqttconnection.this,position+"",Toast.LENGTH_LONG).show();

            }
        });
        if (your_array_list.isEmpty()){

        }else {
            myAdapter.setData(your_array_list);
        }
        Button but= (Button) findViewById(R.id.but);
        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Toast.makeText(InhouseMqttconnection.this,message+"dont know y",Toast.LENGTH_LONG).show();
//
//                broadcastIntent.putExtra("message", "hello boy");
//                sendBroadcast(broadcastIntent);


//                mytask();
            }
        });


        // This is the array adapter, it takes the context of the activity as a
        // first parameter, the type of list view as a second parameter and your
        // array as a third parameter.


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }



    @Override
    protected void onResume() {
        super.onResume();
        Log.d("beforsubs", "true");
        subscribe.putExtra("topic", "newBorn");
        subscribe.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        sendBroadcast(subscribe);
        Log.d("aftersubs", "true");

    }

    @Override
    protected void onPause() {
        super.onPause();
        unsubscribe.putExtra("topic", "newBorn");
        unsubscribe.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        sendBroadcast(unsubscribe);
        unregisterReceiver(receiveMessages);

    }

//    public void connect() {
//
//            clientId = MqttClient.generateClientId();
//
//            client = new MqttAndroidClient(this.getApplicationContext(), "tcp://192.168.1.8:1883",
//                    clientId);
//        client.setCallback(this);
////        client.registerResources(this);
//
//
//        if (client.isConnected()){
//            Log.d("already", "connected");
//
//        }else {
////            try {
////                IMqttToken token = client.connect();
////                token.setActionCallback(new IMqttActionListener() {
////                    @Override
////                    public void onSuccess(IMqttToken asyncActionToken) {
////                        // We are connected
////                        Log.d("tag", "onSuccess");
////
////                    }
////
////                    @Override
////                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
////                        // Something went wrong e.g. connection timeout or firewall problems
////                        Log.d("tag", "onFailure");
////
////                    }
////                });
////            } catch (MqttException e) {
////                e.printStackTrace();
////
//            final MqttConnectOptions options = new MqttConnectOptions();
//            options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);
////            options.setCleanSession(true);
////            options.setKeepAliveInterval(1000000);
////            options.setConnectionTimeout(1000000000);
//            try {
//                IMqttToken token = client.connect(options);
//                token.setActionCallback(new IMqttActionListener() {
//                    @Override
//                    public void onSuccess(IMqttToken asyncActionToken) {
//
//                        Log.d("connect"+i++, "onSuccess");
//                        subscribe();
//                    }
//
//                    @Override
//                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
//                        Log.d("connect", "onFailure"+exception.toString());
//                    }
//                });
//                Log.d("connect", "true");
//            } catch (MqttException e) {
//                e.printStackTrace();
//                Log.d("Notconnect", e.toString());
//            }
//        }
//    }


//    public void retainedPublish() {
//        String topic = "foo/bar";
//        String payload = "the payload";
//        byte[] encodedPayload = new byte[0];
//        try {
//
//            encodedPayload = payload.getBytes("UTF-8");
//            MqttMessage message = new MqttMessage(encodedPayload);
//            message.setRetained(true);
//            client.publish(topic, message);
//        } catch (UnsupportedEncodingException | MqttException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void publish() {
//        String topic = "newBorn";
//        String payload = "the payload";
//        byte[] encodedPayload = new byte[0];
//        try {
//            encodedPayload = payload.getBytes("UTF-8");
//            MqttMessage message = new MqttMessage(encodedPayload);
//            client.publish(topic, message);
//            Log.d("publish", "true" + message);
//        } catch (UnsupportedEncodingException | MqttException e) {
//            e.printStackTrace();
//            Log.d("publish", "nooooooo" + e);
//        }
//    }
//
//    public void subscribe() {
//        String topic = "new";
//        int qos = 0;
//        try {
//            IMqttToken subToken = client.subscribe(topic, qos);
//
//
//            subToken.setActionCallback(new IMqttActionListener() {
//                @Override
//                public void onSuccess(IMqttToken asyncActionToken) {
//                    Log.d("subs", "true" + asyncActionToken.getMessageId());
//                }
//
//                @Override
//                public void onFailure(IMqttToken asyncActionToken,
//                                      Throwable exception) {
//                    // The subscription could not be performed, maybe the user was not
//                    // authorized to subscribe on the specified topic e.g. using wildcards
//                    Log.d("subs", "noooooooo   " + exception);
//
//                }
//            });
//        } catch (MqttException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void unSuscribe() {
//        final String topic = "foo/bar";
//        try {
//            IMqttToken unsubToken = client.unsubscribe(topic);
//            unsubToken.setActionCallback(new IMqttActionListener() {
//                @Override
//                public void onSuccess(IMqttToken asyncActionToken) {
//                    // The subscription could successfully be removed from the client
//
//                }
//
//                @Override
//                public void onFailure(IMqttToken asyncActionToken,
//                                      Throwable exception) {
//                    // some error occurred, this is very unlikely as even if the client
//                    // did not had a subscription to the topic the unsubscribe action
//                    // will be successfully
//                }
//            });
//        } catch (MqttException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void disconnect() {
//        try {
//            IMqttToken disconToken = client.disconnect();
//
//        } catch (MqttException e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    public void connectionLost(Throwable cause) {
        Log.d("Connection Lost", "true");

    }

//    public Context con = this.getApplicationContext();

    @Override
    public void messageArrived(String topic, MqttMessage messa) throws Exception {
//        con= this;
        try {

            Log.d("Message: ", new String(messa.getPayload()));
            message=new String(messa.getPayload());
//
            broadcastIntent.putExtra("message", message);
            broadcastIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.sendBroadcast(broadcastIntent);
            Notify.log("notify", message);
            Notify.notifcation(this, "hi", getIntent(), 0);

//            Intent i = new Intent(this.getApplicationContext(),InhouseMqttconnection.class);
//            this.getApplicationContext().startActivity(i);

//
        }catch (Exception e){

            Notify.log("catch vala",e.toString());
        }

            }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        Log.d("deliveryComplete", "true");
    }

//    public void mytask(){
//        Notify.notifcation(getApplicationContext(), message, getIntent(), 0);
//        your_array_list.add(message);
//
//        myAdapter.setData(your_array_list);
//        myAdapter.notifyDataSetChanged();
//        Log.d("IsConnected", client.isConnected() + "");
//
//    }


     BroadcastReceiver receiveMessages = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

         String device_id=   intent.getExtras().getString("device_id");
            String device_name=   intent.getExtras().getString("device_name");
            Notify.notifcation(context, device_id, getIntent(), 0);

            DataItems dataItems= new DataItems();
            dataItems.setDevice_id(device_id);
            dataItems.setDevice_name(device_name);
            your_array_list.add(dataItems);
            myAdapter.setData(your_array_list);
            myAdapter.notifyDataSetChanged();

            Notify.log("broad", "onrecieve");


        }
    };


}
