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
    Button createRoomButton;
    ListView contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rooms);

        createRoomButton = (Button) findViewById(R.id.createRoomButton);
        createRoomButton.setOnClickListener(this);

        contactList = (ListView) findViewById(R.id.contactList);
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
                    //TODO - CreateRoomActivity
                    //startActivity(new Intent(this, CreateRoomActivity.class));
                    break;
                case R.id.generalButton:
                    //TODO - generalActivity

                    break;
                case R.id.roomsButton:
                    startActivity(new Intent(this, RoomsActivity.class));
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
