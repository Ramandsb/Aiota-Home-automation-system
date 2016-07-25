package in.tagplug.tagplug;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by admin pc on 07-05-2016.
 */
public class MyApplication extends Application {
    public void onCreate() {
        super.onCreate();
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                        .build());

    }
}