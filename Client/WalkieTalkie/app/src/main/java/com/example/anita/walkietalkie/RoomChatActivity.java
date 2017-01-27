package com.example.anita.walkietalkie;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;

//TODO
public class RoomChatActivity extends Activity implements View.OnClickListener{
    String roomname;
    TextView textViewRoomname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_chat);

        Intent intent = getIntent();
        roomname = intent.getStringExtra(RoomsList.ROOMNAME);
        textViewRoomname = (TextView)findViewById(R.id.textViewRoomname);
        textViewRoomname.setText(roomname);

        final Activity activity = this;
        final Handler handler = new Handler();
        try {
            Session.getInstance(activity, handler).SendCurrentRoom(roomname);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {

    }
}
