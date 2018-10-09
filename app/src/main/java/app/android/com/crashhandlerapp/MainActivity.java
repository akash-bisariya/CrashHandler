package app.android.com.crashhandlerapp;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Button btnSubmit;
    EditText etInputText;
    TextView tvMessage;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etInputText = findViewById(R.id.et_input_text);
        btnSubmit = findViewById(R.id.btn_submit);
        tvMessage = findViewById(R.id.tv_message);
        if(getIntent().getBooleanExtra("RESTARTED", false))
        {
            tvMessage.setText(R.string.txt_crash_handler);
        }

        SharedPreferences sharedPreferences = getSharedPreferences("CrashData",Context.MODE_PRIVATE);
        Boolean isDataPosted =  sharedPreferences.getBoolean("isPostData",false);


        /**
         * Sending Crash-Report to server after checking whether it has been sent previously or not
         */
        if(!isDataPosted)
        {
            JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
            jobScheduler.schedule(new JobInfo.Builder(11111,new ComponentName(this,CrashJobService.class))
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .build());

        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String text = etInputText.getText().toString();

                //Throw Exception through accessing the empty string.
                text.charAt(3);

            }
        });



    }
}
