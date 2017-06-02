package com.example.anita.walkietalkie;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * A class of a chat in a room.
 */

public class RoomChatActivity extends AppCompatActivity implements View.OnClickListener{
    private String roomname;
    private TextView textViewRoomname, textViewWhoIsTalking, textViewNewRecord;
    private Boolean isAdmin = false;
    private Button addParticipant, recordButton, playButton;
    private EditText newPartiName;
    private ArrayList<String> recordsToPlay; //array of paths with records to play
    private ImageView recordView, playView;

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

        textViewWhoIsTalking = (TextView) findViewById(R.id.textViewWhoIsTalking);

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
                voiceType = (byte)position;
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
        recordView = (ImageView) findViewById(R.id.imageViewRecord);
        recordButton = (Button)findViewById(R.id.recordButton);
        recordView.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    startRecording();
                    recordView.setImageResource(R.drawable.recorder2);
                    recordButton.setText("recording...");
                    return true;
                }
                else if(event.getAction() == MotionEvent.ACTION_UP){
                    stopRecording();
                    recordView.setImageResource(R.drawable.recorder1);
                    recordButton.setText("tap and hold to record");
                    try{
                        Session.getInstance(activity, handler).SendRecord(mFileName, voiceType, RecordsType.ROOM);
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                    return true;
                }
                return false;
            }
        });

        textViewNewRecord = (TextView) findViewById(R.id.textViewNewRecord);
        textViewNewRecord.setTextColor(Color.GREEN);

        recordsToPlay = new ArrayList<String>();
        playButton = (Button) findViewById(R.id.buttonPlay);
        //playView = (ImageView) findViewById(R.id.imageViewPlay);
        //playView.setOnClickListener(this);

        new Thread(new Runnable() {
            String newRecordPath;
            @Override
            public void run() {
                while (true) {
                    //check for new records
                    newRecordPath = PlayRecordsHelper.getInstance().CheckForNewRecord(roomname, RecordsType.ROOM);
                    if(newRecordPath != null){//if there is a new record
                        setNewRecordToPlay(newRecordPath);
                    }
                    //check if there are unopened records
                    /*if(recordsToPlay.size() > 0) //TODO
                        textViewNewRecord.setText("Unopened messages!!!");
                    else
                        textViewNewRecord.setText("");*/
                }
            }
        }).start();

    }

    /**
     * Initializes the media recorder and starts record
     */
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

    /**
     * Stops recording.
     */
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
                    Session.getInstance(activity, handler).AddParticipant(newPartiName.getText().toString());
                    newPartiName.setText("");
                    break;
                case R.id.buttonPlay:
                    if(playButton.getText().toString().equals("play") && recordsToPlay.size() > 0){
                        String path = recordsToPlay.get(0);
                        playAudio(path);
                        playButton.setText("pause");
                        String senderName = getSenderNameFromAFile(path);
                        if (senderName != "")//TODO - doesnt working in anonymous mode
                            textViewWhoIsTalking.setText(senderName + " is talking...");
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

    /**
     *
     * @param filePath plays an audio that locates on this path.
     * @throws Exception if media player doesn't work.
     */
    public void playAudio(String filePath) throws Exception{
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
                textViewWhoIsTalking.setText("");
            }
        });
    }

    /**
     * Pauses audio.
     * @throws Exception - if media player doesn't work.
     */
    public void pauseAudio() throws Exception{
        mPlayer.pause();
        recordPosition = mPlayer.getCurrentPosition();
    }

    /**
     * Add a new record's path to paths array.
     * @param file path to add.
     */
    public void setNewRecordToPlay(String file){
        recordsToPlay.add(file);
    }

    /**
     * Removes from paths array a path of a file that was played already.
     */
    private void deleteRecord(){
        recordsToPlay.remove(0);
    }

    /**
     * Returns a name of the owner of the playing record.
     * @param path owner of the audio that locates on this path.
     * @return owner name.
     */
    private String getSenderNameFromAFile(String path){ //path format: <folder name>/filename.wav
        String fileName = path.split("/")[path.split("/").length-1];
        return fileName.substring(String.valueOf(System.currentTimeMillis()).length(), fileName.length()-".wav".length());
    }
}
