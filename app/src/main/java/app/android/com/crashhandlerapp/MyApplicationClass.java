package app.android.com.crashhandlerapp;

import android.app.Application;
import android.content.ComponentCallbacks;

/**
 * Created by akash on 9/11/17.
 */
public class MyApplicationClass extends Application {
    @Override
    public void onCreate() {
        setCrashHandler();
        super.onCreate();
    }

    @Override
    public void registerComponentCallbacks(ComponentCallbacks callback) {
        super.registerComponentCallbacks(callback);

    }


    public void setCrashHandler()
    {
        Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(Thread.getDefaultUncaughtExceptionHandler(), MyApplicationClass.this));
    }
}
