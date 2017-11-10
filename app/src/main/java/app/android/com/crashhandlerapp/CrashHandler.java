package app.android.com.crashhandlerapp;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.util.Log;

/**
 * Created by akash on 1/11/17.
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private Activity lastStartedActivity;
    private Thread.UncaughtExceptionHandler exceptionHandler;

    public CrashHandler(Thread.UncaughtExceptionHandler exceptionHandler, Application application) {
        this.exceptionHandler = exceptionHandler;


        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {
                Log.e("","App Crash Handler:" + activity.getLocalClassName());
            }

            @Override
            public void onActivityStarted(Activity activity) {
                Log.d("App Crash Handler","onActivityStarted:" + activity.getLocalClassName());
                lastStartedActivity = activity;
            }

            @Override
            public void onActivityResumed(Activity activity) {
                Log.d("App Crash Handler","onActivityResumed:" + activity.getLocalClassName());
            }

            @Override
            public void onActivityPaused(Activity activity) {
                Log.d("App Crash Handler","onActivityPaused:" + activity.getLocalClassName());
            }

            @Override
            public void onActivityStopped(Activity activity) {
                Log.d("App Crash Handler","onActivityStopped:" + activity.getLocalClassName());
                lastStartedActivity = null;
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
                Log.d("App Crash Handler","onActivitySaveInstanceState:" + activity.getLocalClassName());
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                Log.d("App Crash Handler","onActivityDestroyed:" + activity.getLocalClassName());
            }
        });

    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {

        Log.d("App Crash Handler", "Exception caught by App Crash Handler");
        boolean isRestarted=false;
        Throwable lastException=null;
        if(lastStartedActivity!=null) {

            isRestarted = lastStartedActivity.getIntent().getBooleanExtra("RESTARTED", false);

            lastException = (Throwable) lastStartedActivity.getIntent().getSerializableExtra("LAST_EXCEPTION");
        }

        if (isRestarted || isSameException(throwable, lastException) || lastStartedActivity==null) {

            Log.d("App Crash Handler", "This crash is handled by system");
            exceptionHandler.uncaughtException(thread, throwable);

        } else if(lastStartedActivity!=null){

            Log.d("App Crash Handler", "This crash is handled by app itself");
            Intent intent = lastStartedActivity.getIntent()
                    .putExtra("RESTARTED", true)
                    .putExtra("LAST_EXCEPTION", throwable)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_CLEAR_TASK);

            lastStartedActivity.startActivity(intent);
            lastStartedActivity.finish();
            Process.killProcess(Process.myPid());
            System.exit(1);

        }
    }


    private boolean isSameException(Throwable currentException, Throwable lastException) {
        return lastException != null && (currentException.getClass() == lastException.getClass() && currentException.getStackTrace() == lastException.getStackTrace() && currentException.getMessage().equals(lastException.getMessage()));
    }
}
