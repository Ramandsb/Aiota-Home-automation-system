package in.tagplug.tagplug.Recievers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import in.tagplug.tagplug.Services.Service_localMqtt;

//import in.tagplug.tagplug.AwsIot.PubSubservice;

/**
 * Created by admin pc on 03-05-2016.
 */
public class RestartServicelist extends BroadcastReceiver
{

    private static final String TAG = "RestartServiceReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "onReceive");
        context.startService(new Intent(context.getApplicationContext(), Service_localMqtt.class));
//        context.startService(new Intent(context.getApplicationContext(), PubSubservice.class));

    }

}