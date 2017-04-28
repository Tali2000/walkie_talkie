package com.example.anita.walkietalkie;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class GeneralActivity extends AppCompatActivity implements View.OnClickListener{
    private Button infoButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general);

        infoButton = (Button) findViewById(R.id.buttonInfo);
    }

    @Override
    public void onClick(View v) {
        // detect the view that was "clicked"
        final Activity activity = this;
        final Handler handler = new Handler();
        try {
            switch (v.getId()) {
                case R.id.buttonInfo:
                    Session.getInstance(activity, handler).GetAboutInfo();
                    break;
                case R.id.buttonContacts:
                    startActivity(new Intent(this, ContactsActivity.class));
                    break;
                case R.id.buttonRooms:
                    startActivity(new Intent(this, RoomsActivity.class));
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
