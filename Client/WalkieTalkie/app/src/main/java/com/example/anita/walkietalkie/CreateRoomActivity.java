package com.example.anita.walkietalkie;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class CreateRoomActivity extends Activity implements View.OnClickListener {
    Spinner spinner;
    ArrayAdapter<CharSequence> adapter;
    Button createRoomButton;
    CheckBox checkBox;
    boolean checkedAnonymousMode = false;
    EditText roomName;
    Short maxSec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);

        createRoomButton = (Button)findViewById(R.id.buttonSendCreateRoom);
        createRoomButton.setOnClickListener(this);

        checkBox = (CheckBox)findViewById(R.id.checkBoxAnonymousMode);
        checkBox.setOnClickListener(this);

        roomName = (EditText)findViewById(R.id.roomName);
    }

    @Override
    public void onClick(View v) {
        // detect the view that was "clicked"
        final Activity activity = this;
        final Handler handler = new Handler();

        try {
            switch (v.getId()) {
                case R.id.checkBoxAnonymousMode:
                    checkedAnonymousMode = checkBox.isChecked();
                    System.out.println();
                    break;
                case R.id.buttonSendCreateRoom:
                    Session.getInstance(activity, handler).CreateRoom(roomName.getText().toString(), checkedAnonymousMode);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
