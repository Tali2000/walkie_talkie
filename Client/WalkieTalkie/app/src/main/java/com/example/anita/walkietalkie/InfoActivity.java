package com.example.anita.walkietalkie;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class InfoActivity extends AppCompatActivity {
    private String info;
    private TextView infoTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        infoTextView = (TextView) findViewById(R.id.textViewAboutUs);
        //get info
        Bundle extras = getIntent().getExtras();
        info = extras.getString("INFO");
        infoTextView.setText(info);
    }
}
