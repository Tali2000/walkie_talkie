package com.example.anita.walkietalkie;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;

public class RoomChatActivity extends Activity implements View.OnClickListener{
    String roomname;
    TextView textViewRoomname;
    Boolean isAdmin = false;
    Button addParticipant;
    ListView participantsList;
    EditText newPartiName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_chat);

        //get roomname
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

        intent = getIntent();
        isAdmin = intent.getStringExtra("IS_ADMIN") == "1" ? true : false;

        addParticipant = (Button)findViewById(R.id.ButtonAddParticipantToRoom);
        newPartiName = (EditText)findViewById(R.id.editTextNewParticipant);
        if(isAdmin){
            //show the button for admin only
            addParticipant.setVisibility(View.VISIBLE);
            newPartiName.setVisibility(View.VISIBLE);
            addParticipant.setOnClickListener(this);
        }
        //initialize paricipants list
        participantsList = (ListView)findViewById(R.id.ParticipantsList);
        try {
            Session.getInstance(activity, handler).GetParticipants();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        // detect the view that was "clicked"
        final Activity activity = this;
        final Handler handler = new Handler();
        try {
            switch (v.getId()) {
                case R.id.ButtonAddParticipantToRoom:
                    //show popup window
                    //TODO
                    Session.getInstance(activity, handler).AddParticipant(newPartiName.getText().toString());
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
