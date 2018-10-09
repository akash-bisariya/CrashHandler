package app.android.com.crashhandlerapp;

import android.app.Activity;
import android.app.Application;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by akash on 1/11/17.
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private Activity lastStartedActivity;
    private Thread.UncaughtExceptionHandler exceptionHandler;
    private Context context;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CrashHandler(Thread.UncaughtExceptionHandler exceptionHandler, Application application) {
        this.exceptionHandler = exceptionHandler;
        context= application.getApplicationContext();

        /* *
         Registering the lifecycle callback of activities
        * */
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

    /**
     * @param thread
     * @param throwable
     *
     * Restarting activity if the exception is thrown for the first time only.
     * The Deafault Exception handler will handle the exception if the same exception within the same activity occurs second time to avoid the loop.
     *
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {

        /**
         * Writing stacktrace into String
         */
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        String stackTraceString = sw.toString();

        /**
         * Storing error log into shared-preferences
         */
        SharedPreferences sharedPreferences = context.getSharedPreferences("CrashData",Context.MODE_PRIVATE);
        SharedPreferences.Editor  edit = sharedPreferences.edit();
        edit.putBoolean("isPostData",false);
        edit.putString("crashData",stackTraceString);
        edit.apply();


        Log.d("App Crash Handler", "Exception caught by App Crash Handler "+stackTraceString);
        boolean isRestarted=false;
        Throwable lastException=null;



        if(lastStartedActivity!=null) {

            isRestarted = lastStartedActivity.getIntent().getBooleanExtra("RESTARTED", false);

            lastException = (Throwable) lastStartedActivity.getIntent().getSerializableExtra("LAST_EXCEPTION");
        }

        if (isRestarted || isSameException(throwable, lastException) || lastStartedActivity==null) {

            Log.d("App Crash Handler", "This crash is handled by system");

            //Setting the default system exception if the same exception occurred second time.

            exceptionHandler.uncaughtException(thread, throwable);

        } else if(lastStartedActivity!=null){

            Log.d("App Crash Handler", "This crash is handled by app itself"+throwable.getStackTrace().toString());
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


    /**
     * @param currentException
     * @param lastException
     * @return
     * Comparing the exception with the previous exception.
     * Return True if exception repeats otherwise False.
     */
    private boolean isSameException(Throwable currentException, Throwable lastException) {
        return lastException != null && (currentException.getClass() == lastException.getClass() && currentException.getStackTrace() == lastException.getStackTrace() && currentException.getMessage().equals(lastException.getMessage()));
    }
}
