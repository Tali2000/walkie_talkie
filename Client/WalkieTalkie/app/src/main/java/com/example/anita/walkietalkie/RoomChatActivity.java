package com.example.anita.walkietalkie;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;

public class RoomChatActivity extends AppCompatActivity implements View.OnClickListener{
    private String roomname;
    private TextView textViewRoomname;
    private Boolean isAdmin = false;
    private Button addParticipant, recordButton;
    private ListView participantsList;
    private EditText newPartiName;

    private MediaRecorder mRecorder;
    private MediaPlayer mPlayer;
    private String mFileName = null;
    private static final String LOG_TAG = "Record_log";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

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

        //check if admin
        intent = getIntent();
        //TODO isAdmin = intent.getStringExtra("IS_ADMIN") == "1" ? true : false;
        isAdmin = true;

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

        recordButton = (Button)findViewById(R.id.recordButton);

        //create a directory of the records on the device
        File mydir = new File(Environment.getExternalStorageDirectory(), Session.getApplicationName());
        if (!mydir.exists())
            if (!mydir.mkdirs())
                Log.d("App", "failed to create directory");

        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/" + Session.getApplicationName();
        mFileName += "/walkieTalkie_record.wav";

        recordButton.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN)
                    startRecording();
                else if(event.getAction() == MotionEvent.ACTION_UP){
                    stopRecording();
                    try{
                        Session.getInstance(activity, handler).SendRecord(mFileName);
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
                return false;
            }
        });
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
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

    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) finish();

    }
}
