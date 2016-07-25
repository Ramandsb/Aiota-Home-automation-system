package in.tagplug.tagplug.Services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import in.tagplug.tagplug.Activities.InhouseMqttconnection;
import in.tagplug.tagplug.Activities.MainActivity;
import in.tagplug.tagplug.pojo.Notify;
import in.tagplug.tagplug.AwsIot.PubSubservice;

/**
 * Created by RamanDeep on 17-05-2016.
 */
public class Service_localMqtt extends Service {
//
    MqttAndroidClient client;
    String clientId;

    boolean bool=false;
    Intent broadcastIntent;
    Intent MainActivityBroadcast;
    public static String PUBLISH_LOCAL_DATA="PUBLISH_LOCAL_DATA",SUBSCRIBE_LOCAL_DATA="SUBSCRIBE_LOCAL_DATA",UNSUBSCRIBE_LOCAL_DATA="UNSUBSCRIBE_LOCAL_DATA";
    public static String LOCAL_CONNECT="LOCAL_CONNECT",LOCAL_DISCONNECT="LOCAL_DISCONNECT";

    @Override
    public void onCreate() {
//        registerReceiver(RecievePublish, new IntentFilter("PUBLISH_DATA"));
//        registerReceiver(RecieveSubscribe, new IntentFilter("SUBSCRIBE_DATA"));
        super.onCreate();
        Log.d("Service", "Called");
        registerReceiver(RecievePublish, new IntentFilter(PUBLISH_LOCAL_DATA));
        registerReceiver(RecieveSubscribe, new IntentFilter(SUBSCRIBE_LOCAL_DATA));
        registerReceiver(UnSubscribe, new IntentFilter(UNSUBSCRIBE_LOCAL_DATA));
        registerReceiver(RecieveSubscribe, new IntentFilter(LOCAL_CONNECT));
        registerReceiver(UnSubscribe, new IntentFilter(LOCAL_DISCONNECT));
        broadcastIntent = new Intent(InhouseMqttconnection.BROADCAST);
        MainActivityBroadcast = new Intent(MainActivity.BROADCAST_MAIN);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        connect();
        Notify.log("onstartCommand", "true");
        return Service.START_STICKY;
    }


    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub

        sendBroadcast(new Intent("YouWillNeverKillMe"));
        super.onDestroy();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public void connect() {

        clientId = MqttClient.generateClientId();

        client = new MqttAndroidClient(this.getApplicationContext(), "tcp://192.168.0.163:1883",
                clientId);
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

Notify.log("connection lost","true");
                connect();

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                try {
                    String messagePayload = new String(message.getPayload());
                    JSONObject message_in_json = new JSONObject(messagePayload);
                    Notify.log("Arrived Messages", message_in_json.toString());
                parseArrivedMessages(message_in_json);
                }catch (Exception e){
                    Notify.log("error ",e.toString());

                }

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                try {
                    Notify.log("deliveryComplete", token.getMessage().getPayload().toString());
                } catch (MqttException e) {
                    e.printStackTrace();
                }


            }
        });



        if (client.isConnected()){
            Log.d("already", "connected");

        }else {
//            try {
//                IMqttToken token = client.connect();
//                token.setActionCallback(new IMqttActionListener() {
//                    @Override
//                    public void onSuccess(IMqttToken asyncActionToken) {
//                        // We are connected
//                        Log.d("tag", "onSuccess");
//
//                    }
//
//                    @Override
//                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
//                        // Something went wrong e.g. connection timeout or firewall problems
//                        Log.d("tag", "onFailure");
//
//                    }
//                });
//            } catch (MqttException e) {
//                e.printStackTrace();
//
            final MqttConnectOptions options = new MqttConnectOptions();
            options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);
            options.setKeepAliveInterval(240);
            try {
                IMqttToken token = client.connect(options);
                token.setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {

                        Notify.log("service", "connected");
                        Intent disconnect_iot= new Intent(PubSubservice.AWS_DISCONNECT);
                        sendBroadcast(disconnect_iot);

//                        subscribe("new");
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Log.d("serice", "onFailure"+exception.toString());

                        connect();
                    }
                });
                Log.d("Service_connect", "true");
            } catch (MqttException e) {
                e.printStackTrace();
                Log.d("Notconnect", e.toString());
            }
        }
    }

    public void parseArrivedMessages(JSONObject message) {

        try {
            String mess_cry = message.getString("message");
            if (mess_cry.equals("cry")) {
                String device_id = message.getString("device_id");
                String device_name = message.getString("device_name");
//                publish(device_id, device_name);
                broadcastIntent.putExtra("device_id", device_id);
                broadcastIntent.putExtra("device_name", device_name);
                broadcastIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                this.sendBroadcast(broadcastIntent);
            }else {
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

                MainActivityBroadcast.putExtra("device_id",device_id);
                MainActivityBroadcast.putExtra("device_location",device_location);
                MainActivityBroadcast.putExtra("device_description",device_description);
                MainActivityBroadcast.putExtra("device_strength",device_strength);
                MainActivityBroadcast.putExtra("device_instantaneous_current",device_instantaneous_current);
                MainActivityBroadcast.putExtra("event_timestamp",event_timestamp);
                MainActivityBroadcast.putExtra("event_description",event_description);
                MainActivityBroadcast.putExtra("event_mode",event_mode);
                MainActivityBroadcast.putExtra("event_name",event_name);
                MainActivityBroadcast.putExtra("event_device_id",event_device_id);
                MainActivityBroadcast.putExtra("event_device_name",event_device_name);
                MainActivityBroadcast.putExtra("event_path",event_path);
                MainActivityBroadcast.putExtra("outside_temp",outside_temp);
                MainActivityBroadcast.putExtra("inside_temp",inside_temp);
                MainActivityBroadcast.putExtra("clock_time",clock_time);
                sendBroadcast(MainActivityBroadcast);


            }

        }catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void retainedPublish() {
        String topic = "foo/bar";
        String payload = "the payload";
        byte[] encodedPayload = new byte[0];
        try {
            encodedPayload = payload.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);
            message.setRetained(true);
            client.publish(topic, message);
        } catch (UnsupportedEncodingException | MqttException e) {
            e.printStackTrace();
        }
    }

    public void publish(String topic, String messageValue) {
        String state="";
        String formedMessage="";

        if (topic.equals("1234")) {
            if (bool) {
                state = "on";
                bool = false;

            } else {
                state = "off";
                bool = true;
            }
             formedMessage="{device_id:1234,device_name:ac, state:"+state+"}";
        }else {

            formedMessage =messageValue;
//                    "{device_id:1234,device_name:ac}";

        }
        try {
            MqttMessage message = new MqttMessage();
            message.setPayload(formedMessage.getBytes());
            client.publish(topic, message);
            Log.d("publish", "true" + message);
        } catch (MqttException e) {
            e.printStackTrace();
            Log.d("publish", "nooooooo" + e);
        }
    }

    public void subscribe(String topic) {

        int qos = 0;
        try {
            IMqttToken subToken = client.subscribe(topic, qos);


            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d("subs", "true" + asyncActionToken.getMessageId());
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    // The subscription could not be performed, maybe the user was not
                    // authorized to subscribe on the specified topic e.g. using wildcards
                    Log.d("subs", "noooooooo   " + exception);

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void unSuscribe(String topic) {
        try {
            IMqttToken unsubToken = client.unsubscribe(topic);
            unsubToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // The subscription could successfully be removed from the client
                    Notify.log("unsubscribe", "success");

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    // some error occurred, this is very unlikely as even if the client
                    // did not had a subscription to the topic the unsubscribe action
                    // will be successfully
//                    Notify.log("unsubscribe","fail"+exception.toString());
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void publishAc(String switchA, String mes) {
        String state="";
        String formedMessage=mes;
        try {
            MqttMessage message = new MqttMessage();
            message.setPayload(formedMessage.getBytes());
            client.publish("/aiota/test", message);
            Log.d("Ac_publish", "true" + message);
        } catch (MqttException e) {
            e.printStackTrace();
            Log.d("ACpublish", "nooooooo" + e);
        }
    }
    public void disconnect() {
        try {
            IMqttToken disconToken = client.disconnect();

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private BroadcastReceiver RecievePublish = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String status= intent.getExtras().getString("status");
            if (status.equals("change_temp")){
          String data=intent.getExtras().getString("data");
                publishAc(status,data);
            }else if (status.equals("on")){

                String data=intent.getExtras().getString("data");
                publishAc(status,data);
            }else if (status.equals("off")){
                String data=intent.getExtras().getString("data");
                publishAc(status,data);
            }else{
                String topic = intent.getExtras().getString("topic");
                String message = intent.getExtras().getString("message");
                publish(topic, message);
            }



        }
    };
    private BroadcastReceiver RecieveSubscribe = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String topic= intent.getExtras().getString("topic");
            Log.d("topicTo Subscribe",topic);
            subscribe(topic);
        }
    };
    private BroadcastReceiver UnSubscribe = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String topic=intent.getStringExtra("topic");
            unSuscribe(topic);


        }
    };

    private BroadcastReceiver Local_connect = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            connect();


        }
    };
    private BroadcastReceiver local_disconnect = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            disconnect();


        }
    };



}
