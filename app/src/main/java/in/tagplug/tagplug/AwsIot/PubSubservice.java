package in.tagplug.tagplug.AwsIot;
//

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;


import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.iot.AWSIotKeystoreHelper;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttClientStatusCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttLastWillAndTestament;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttManager;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttNewMessageCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.iot.AWSIotClient;
import com.amazonaws.services.iot.model.AttachPrincipalPolicyRequest;
import com.amazonaws.services.iot.model.CreateKeysAndCertificateRequest;
import com.amazonaws.services.iot.model.CreateKeysAndCertificateResult;

import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.util.UUID;

import in.tagplug.tagplug.Services.Service_localMqtt;

public class PubSubservice extends Service {
    static final String LOG_TAG = "RamanLog";

//     --- Constants to modify per your configuration ---
//
//     Endpoint Prefix = random characters at the beginning of the custom AWS
//     IoT endpoint
//     describe endpoint call returns: XXXXXXXXXX.iot.<region>.amazonaws.com,
//     endpoint prefix string is XXXXXXX
    private static final String CUSTOMER_SPECIFIC_ENDPOINT_PREFIX = "A1AHE4YQOAT0FY";
    // Cognito pool ID. For this app, pool needs to be unauthenticated pool with
    // AWS IoT permissions.
    private static final String COGNITO_POOL_ID = "ap-northeast-1:3da26745-f111-42b0-b2e4-b32cd923251c";
    // Name of the AWS IoT policy to attach to a newly created certificate
    private static final String AWS_IOT_POLICY_NAME = "IOT-ALL";

    // Region of AWS IoT
    private static final Regions MY_REGION = Regions.AP_NORTHEAST_1;
    // Filename of KeyStore file on the filesystem
    private static final String KEYSTORE_NAME = "iot_keystore_new.bks";
    // Password for the private key in the KeyStore
    private static final String KEYSTORE_PASSWORD = "123456";
    // Certificate and key aliases in the KeyStore
    private static final String CERTIFICATE_ID = "default";


    AWSIotClient mIotAndroidClient;
    AWSIotMqttManager mqttManager;
    String clientId;
    String keystorePath;
    String keystoreName;
    String keystorePassword;

    KeyStore clientKeyStore = null;
    String certificateId;
    public static String PUBLISH_DATA="PUBLISH_DATA",SUBSCRIBE_DATA="SUBSCRIBE_DATA",AWS_CONNECT="AWS_CONNECT",AWS_DISCONNECT="AWS_DISCONNECT";

    CognitoCachingCredentialsProvider credentialsProvider;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mqttInit();
        registerReceiver(RecievePublish, new IntentFilter(PUBLISH_DATA));
        registerReceiver(RecieveSubscribe, new IntentFilter(SUBSCRIBE_DATA));
        registerReceiver(RecieveConnect, new IntentFilter(AWS_CONNECT));
        registerReceiver(RecieveDisconnect, new IntentFilter(AWS_DISCONNECT));

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        connect();
        return Service.START_STICKY;
    }


    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        sendBroadcast(new Intent("YouWillNeverKillMe"));
    }
    ////////////////////////////////////////////////////
    public void mqttInit(){
        // MQTT client IDs are required to be unique per AWS IoT account.
        // This UUID is "practically unique" but does not _guarantee_
        // uniqueness.
        clientId = UUID.randomUUID().toString();

        // Initialize the AWS Cognito credentials provider
        credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(), // context
                COGNITO_POOL_ID, // Identity Pool ID
                MY_REGION // Region
        );

        Region region = Region.getRegion(MY_REGION);



        // MQTT Client
        mqttManager = new AWSIotMqttManager(clientId, region, CUSTOMER_SPECIFIC_ENDPOINT_PREFIX);

        // Set keepalive to 10 seconds.  Will recognize disconnects more quickly but will also send
        // MQTT pings every 10 seconds.
        mqttManager.setKeepAlive(10);

        // Set Last Will and Testament for MQTT.  On an unclean disconnect (loss of connection)
        // AWS IoT will publish this message to alert other clients.
        AWSIotMqttLastWillAndTestament lwt = new AWSIotMqttLastWillAndTestament("topic_1",
                "Android client lost connection", AWSIotMqttQos.QOS1);
        mqttManager.setMqttLastWillAndTestament(lwt);
        mqttManager.setFullQueueToKeepNewestMessages();
//        mqttManager.setFullQueueToKeepOldestMessages();

        // IoT Client (for creation of certificate if needed)
        mIotAndroidClient = new AWSIotClient(credentialsProvider);
        mIotAndroidClient.setRegion(region);

        keystorePath = getFilesDir().getPath();
//        keystorePath = Environment.getExternalStorageDirectory().toString();
//        keystorePath = "android.resource://" + getPackageName() + "/"+R.raw.iot_keystore_new;
        keystoreName = KEYSTORE_NAME;
        keystorePassword = KEYSTORE_PASSWORD;
        certificateId = CERTIFICATE_ID;
        Log.d("pathre",keystorePath+"");

        // To load cert/key from keystore on filesystem
        loadFromkeystore();

        if (clientKeyStore == null) {
            Log.i(LOG_TAG, "Cert/key was not found in keystore - creating new key and certificate.");

            createnewcertificates();
        }
    }
    public void loadFromkeystore(){
        try {
            if (AWSIotKeystoreHelper.isKeystorePresent(keystorePath, keystoreName)) {
                if (AWSIotKeystoreHelper.keystoreContainsAlias(certificateId, keystorePath,
                        keystoreName, keystorePassword)) {
                    Log.i(LOG_TAG, "Certificate " + certificateId
                            + " found in keystore - using for MQTT.");

                    // load keystore from file into memory to pass on connection
                    clientKeyStore = AWSIotKeystoreHelper.getIotKeystore(certificateId,
                            keystorePath, keystoreName, keystorePassword);

                } else {
                    Log.i(LOG_TAG, "Key/cert " + certificateId + " not found in keystore.");
                }
            } else {
                Log.i(LOG_TAG, "Keystore " + keystorePath + "/" + keystoreName + " not found.");
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "An error occurred retrieving cert/key from keystore.", e);
        }

    }

    public  void createnewcertificates(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Create a new private key and certificate. This call
                    // creates both on the server and returns them to the
                    // device.
                    CreateKeysAndCertificateRequest createKeysAndCertificateRequest =
                            new CreateKeysAndCertificateRequest();
                    createKeysAndCertificateRequest.setSetAsActive(true);
                    final CreateKeysAndCertificateResult createKeysAndCertificateResult;
                    createKeysAndCertificateResult =
                            mIotAndroidClient.createKeysAndCertificate(createKeysAndCertificateRequest);
                    Log.i(LOG_TAG,
                            "Cert ID: " +
                                    createKeysAndCertificateResult.getCertificateId() + "/////\nPrivate key" + createKeysAndCertificateResult.getKeyPair().getPrivateKey() + "\nPublic key  " +
                                    " created.");


                    // store in keystore for use in MQTT client
                    // saved as alias "default" so a new certificate isn't
                    // generated each run of this application
                    AWSIotKeystoreHelper.saveCertificateAndPrivateKey(certificateId,
                            createKeysAndCertificateResult.getCertificatePem(),
                            createKeysAndCertificateResult.getKeyPair().getPrivateKey(),
                            keystorePath, keystoreName, keystorePassword);

                    // load keystore from file into memory to pass on
                    // connection
                    clientKeyStore = AWSIotKeystoreHelper.getIotKeystore(certificateId,
                            keystorePath, keystoreName, keystorePassword);

                    // Attach a policy to the newly created certificate.
                    // This flow assumes the policy was already created in
                    // AWS IoT and we are now just attaching it to the
                    // certificate.
                    AttachPrincipalPolicyRequest policyAttachRequest =
                            new AttachPrincipalPolicyRequest();
                    policyAttachRequest.setPolicyName(AWS_IOT_POLICY_NAME);
                    policyAttachRequest.setPrincipal(createKeysAndCertificateResult
                            .getCertificateArn());
                    mIotAndroidClient.attachPrincipalPolicy(policyAttachRequest);
                    connect();


                } catch (Exception e) {
                    Log.e(LOG_TAG,
                            "Exception occurred when generating new private key and certificate.",
                            e);
                }
            }
        }).start();
    }
    public void connect() {
        Log.d(LOG_TAG, "clientId = " + clientId);

        try {
            mqttManager.connect(clientKeyStore, new AWSIotMqttClientStatusCallback() {
                @Override
                public void onStatusChanged(final AWSIotMqttClientStatus status,
                                            final Throwable throwable) {
                    Log.d(LOG_TAG, "Status = " + String.valueOf(status));

                    Intent statusChanged= new Intent(VisualActivity.CONNECTION_STATUS);
                    statusChanged.putExtra("status", String.valueOf(status));
                    sendBroadcast(statusChanged);
                    if (String.valueOf(status).equals("Connected")){
                        Intent disconnect_localmqtt=new Intent(Service_localMqtt.LOCAL_DISCONNECT);
                        sendBroadcast(disconnect_localmqtt);
                    }


                }
            });
        } catch (final Exception e) {
            Log.e(LOG_TAG, "Connection error.", e);
        }
    }

    private BroadcastReceiver RecievePublish = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String topic=intent.getStringExtra("topic");
            String data=intent.getStringExtra("data");
            publish(data,topic);
        }
    };
    private BroadcastReceiver RecieveSubscribe = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String topic=intent.getStringExtra("topic");
            subscribe(topic);
        }
    };
    private BroadcastReceiver RecieveConnect = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
           connect();
        }
    };
    private BroadcastReceiver RecieveDisconnect = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            disconnect();
        }
    };
    public void subscribe(String topic){
        try {
            mqttManager.subscribeToTopic(topic, AWSIotMqttQos.QOS1,
                    new AWSIotMqttNewMessageCallback() {
                        @Override
                        public void onMessageArrived(final String topic, final byte[] data) {

                            try {
                                String message = new String(data, "UTF-8");
                                Log.d(LOG_TAG, "Message arrived:");
                                Log.d(LOG_TAG, "   Topic: " + topic);
                                Log.d(LOG_TAG, " Message: " + message);
                                Intent mess = new Intent(VisualActivity.SEND_MESSAGE);
                                mess.putExtra("message", message);
                                sendBroadcast(mess);

                            } catch (UnsupportedEncodingException e) {
                                Log.e(LOG_TAG, "Message encoding error.", e);
                            }
                        }
                    });


        } catch (Exception e) {
            Log.e(LOG_TAG, "Subscription error.", e);
        }
    }
    public void publish(String msg,String topic){
        try {
            mqttManager.publishString(msg, topic, AWSIotMqttQos.QOS1);

            Log.e("Publish string.", msg);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Publish error.", e);
        }
    }

    public void disconnect(){
        try {
            mqttManager.disconnect();

            Log.e("Disconnect", "true");
        } catch (Exception e) {
            Log.e(LOG_TAG, "Disconnect error.", e);
        }
    }
}

