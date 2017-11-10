package app.android.com.crashhandlerapp;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etInputText = findViewById(R.id.et_input_text);
        btnSubmit = findViewById(R.id.btn_submit);
        tvMessage = findViewById(R.id.tv_message);
        if(getIntent().getBooleanExtra("RESTARTED", false))
        {
            tvMessage.setText("Crash Handler Restarted Activity");
        }


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = etInputText.getText().toString();

                //Through Exception
                text.charAt(3);

            }
        });



    }
}
