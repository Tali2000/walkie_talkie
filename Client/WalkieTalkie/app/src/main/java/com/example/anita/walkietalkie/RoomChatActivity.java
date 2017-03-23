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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class RoomChatActivity extends AppCompatActivity implements View.OnClickListener{
    private String roomname;
    private TextView textViewRoomname;
    private Boolean isAdmin = false;
    private Button addParticipant, recordButton, playButton;
    private ListView participantsList;
    private EditText newPartiName;
    private ArrayList<String> recordsToPlay; //array of paths with records to play

    private MediaRecorder mRecorder;
    private MediaPlayer mPlayer;
    private String mFileName = null;
    private static final String LOG_TAG = "Record_log";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    Spinner spinner;
    ArrayAdapter<CharSequence> adapter;
    byte voiceType;
    int recordPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_chat);

        final Activity activity = this;
        final Handler handler = new Handler();

        //get roomname and isAdmin
        Bundle extras = getIntent().getExtras();
        roomname = extras.getString("ROOMNAME");
        isAdmin = extras.getBoolean("ISADMIN");
        textViewRoomname = (TextView)findViewById(R.id.textViewRoomname);
        textViewRoomname.setText(roomname);

        //spinner initialize
        spinner = (Spinner)findViewById(R.id.spinnerVoiceType);
        adapter = ArrayAdapter.createFromResource(this, R.array.voiceType,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getBaseContext(), parent.getItemAtPosition(position)+" selected", Toast.LENGTH_LONG).show();
                voiceType = (byte)position; //TODO get from server
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        //admin's buttons
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

        //create a directory of the records on the device
        File mydir = new File(Environment.getExternalStorageDirectory(), Session.getApplicationName());
        if (!mydir.exists())
            if (!mydir.mkdirs())
                Log.d("App", "failed to create directory");

        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/" + Session.getApplicationName();
        mFileName += "/walkieTalkie_record.3gp";

        //initialize record button
        recordButton = (Button)findViewById(R.id.recordButton);
        recordButton.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    startRecording();
                    recordButton.setText("recording...");
                }
                else if(event.getAction() == MotionEvent.ACTION_UP){
                    stopRecording();
                    recordButton.setText("tap and hold to record");
                    try{
                        Session.getInstance(activity, handler).SendRecord(mFileName, voiceType);
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
                return false;
            }
        });

        recordsToPlay = new ArrayList<String>();
        playButton = (Button) findViewById(R.id.buttonPlay);
        playButton.setOnClickListener(this);

        new Thread(new Runnable() {
            String newRecordPath;
            @Override
            public void run() {
                while (true) {
                    newRecordPath = PlayRecordsHelper.getInstance().CheckForNewRecord(roomname);
                    if(newRecordPath != null){
                        setNewRecordToPlay(newRecordPath);
                    }
                }
            }
        }).start();
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);

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
                case R.id.buttonPlay:
                    if(playButton.getText().toString().equals("play") && recordsToPlay.size() > 0){
                        playAudio(recordsToPlay.get(0));
                        playButton.setText("pause");
                    }
                    else if(playButton.getText().toString().equals("pause")){
                        pauseAudio();
                        playButton.setText("play");
                    }
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
        if (!permissionToRecordAccepted) finish();
    }

    public void playAudio(String filePath) throws Exception{
        if(mPlayer != null){
            mPlayer.stop();
            mPlayer.release();
        }
        mPlayer = new MediaPlayer();
        mPlayer.setDataSource(filePath);
        mPlayer.prepare();
        mPlayer.seekTo(recordPosition);
        mPlayer.start();
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {//when sound ends
            @Override
            public void onCompletion(MediaPlayer mp) {
                recordPosition = 0;
                deleteRecord();
                mPlayer.release();
                playButton.setText("play");
            }
        });
    }

    public void pauseAudio() throws Exception{
        mPlayer.pause();
        recordPosition = mPlayer.getCurrentPosition();
    }

    public void setNewRecordToPlay(String file){
        //TODO - sound notification
        recordsToPlay.add(file);
    }

    private void deleteRecord(){
        recordsToPlay.remove(0);
    }
}
