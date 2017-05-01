package com.example.anita.walkietalkie;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class RoomsActivity extends Activity implements View.OnClickListener {
    Button createRoomButton, generalButton, contactsButton, refreshButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rooms);

        createRoomButton = (Button) findViewById(R.id.createRoomButton);
        createRoomButton.setOnClickListener(this);

        generalButton = (Button) findViewById(R.id.generalButton);
        generalButton.setOnClickListener(this);

        contactsButton = (Button) findViewById(R.id.contactsButton);
        contactsButton.setOnClickListener(this);

        refreshButton = (Button) findViewById(R.id.buttonRefresh) ;
        refreshButton.setOnClickListener(this);

        final Activity activity = this;
        final Handler handler = new Handler();
        try {
            Session.getInstance(activity, handler).GetRooms();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(final View v) {
        // detect the view that was "clicked"
        final Activity activity = this;
        final Handler handler = new Handler();

        try {
            switch (v.getId()) {
                case R.id.createRoomButton:
                    startActivity(new Intent(this, CreateRoomActivity.class));
                    break;
                case R.id.generalButton:
                    startActivity(new Intent(this, GeneralActivity.class));
                    break;
                case R.id.contactsButton:
                    startActivity(new Intent(this, ContactsActivity.class));
                    break;
                case R.id.buttonRefresh:
                    Session.getInstance(activity, handler).GetRooms();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
