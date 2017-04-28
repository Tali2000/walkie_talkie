package com.example.anita.walkietalkie;

import android.app.Activity;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
//TODO show in toast messages
public enum ServerOperation {
    SIGNIN(0) {
        @Override
        public void handle(InPacket packet, final Activity activity, final Handler handler) throws Exception {
            final byte result = packet.readByte();
            //do it every time
            handler.post(new Runnable() {
                @Override
                public void run() {
                    TextView messageView = (TextView) activity.findViewById(R.id.messageView);
                    System.out.println("signin answer: " + result);
                    switch (result) {
                        case 0: //success
                            messageView.setText
                                    ("Successful connected!");
                            Intent intent = new Intent(activity, ContactsActivity.class);
                            activity.startActivity(intent);
                            break;
                        case 1: //user doesn’t exist
                            messageView.setText
                                    ("This user doesn’t exist. Enter an exist one or sign up");
                            break;
                        case 2: //wrong details
                            messageView.setText
                                    ("Username or password doesn't match. Try again");
                            break;
                        case 3: //user is already connected
                            messageView.setText
                                    ("This user is already connected");
                            break;
                    }
                }
            });
        }
    },
    SIGNUP(1) {
        @Override
        public void handle(InPacket packet, final Activity activity, final Handler handler) throws Exception {
            final byte result = packet.readByte();
            //do it every time
            handler.post(new Runnable() {
                @Override
                public void run() {
                    TextView messageView = (TextView) activity.findViewById(R.id.messageView);
                    switch (result) {
                        case 0: //success
                            messageView.setText
                                    ("Successfully registered!");
                            break;
                        case 4: //Invalid username
                            messageView.setText
                                    ("Invalid username");
                            break;
                        case 5: //Username is already exists
                            messageView.setText
                                    ("Username is already exists. Try again");
                            break;
                        case 6: //invalid password
                            messageView.setText
                                    ("invalid password");
                            break;
                        case 7: //other
                            messageView.setText
                                    ("something went wrong");
                            break;
                    }
                }
            });
        }
    },
    ADD_CONTACT (2){
        @Override
        public void handle(InPacket packet, final Activity activity, Handler handler) throws Exception {
            final byte result = packet.readByte();
            //do it every time
            handler.post(new Runnable() {
                @Override
                public void run() {
                    TextView messageView = (TextView) activity.findViewById(R.id.contactMessageView);
                    switch (result) {
                        case 0: //success
                            messageView.setText
                                    ("Successful added!");
                            break;
                        case 1: //user doesn't exist
                            messageView.setText
                                    ("User doesn't exist");
                            break;
                        case 8: //user is already in your contacts
                            messageView.setText
                                    ("User is already in your contacts");
                            break;
                        case 7: //other
                            messageView.setText
                                    ("something went wrong");
                            break;
                        case 9: //trying to add yourself
                            messageView.setText
                                    ("Are you kidding? You can't add yourself");
                            break;
                    }
                }
            });
        }
    },
    CREATE_ROOM (3){
        @Override
        public void handle(InPacket packet, final Activity activity, final Handler handler) throws Exception {
            final byte result = packet.readByte();
            //do it every time
            handler.post(new Runnable() {
                @Override
                public void run() {
                    TextView messageView = (TextView) activity.findViewById(R.id.createRoomTextView);
                    switch (result) {
                        case 0: //success
                            messageView.setText
                                    ("Successful created!");
                            break;
                        case 4: //invalid roomname
                            messageView.setText("Invalid roomname");
                            break;
                        case 5: //room already exists
                            messageView.setText("room already exists");
                            break;
                        case 7: //other
                            messageView.setText
                                    ("something went wrong");
                            break;
                        case 14: //invalid max record time
                            messageView.setText
                                    ("Invalid max record time");
                            break;
                    }
                }
            });
        }
    },
    ADD_PARTICIPANT (4){
        @Override
        public void handle(InPacket packet, final Activity activity, final Handler handler) throws Exception {
            final byte result = packet.readByte();
            //do it every time
            handler.post(new Runnable() {
                @Override
                public void run() {
                    TextView messageView = (TextView) activity.findViewById(R.id.textViewAddParticipantMessage);
                    switch (result) {
                        case 0: //success
                            messageView.setText
                                    ("Successful added!");
                            break;
                        case 1: //participant doesn’t exist
                            messageView.setText("participant doesn’t exist");
                            break;
                        case 10: //contact is already in the room
                            messageView.setText("contact is already in the room");
                            break;
                        case 7: //other
                            messageView.setText
                                    ("something went wrong");
                            break;
                        case 11: //user is not in your contacts
                            messageView.setText
                                    ("user is not in your contacts");
                            break;
                    }
                }
            });
        }
    },
    ALLOW_ENTERANCE (5){//TODO
        @Override
        public void handle(InPacket packet, final Activity activity, final Handler handler) throws Exception {
            final byte result = packet.readByte();
            //do it every time
            handler.post(new Runnable() {
                @Override
                public void run() {
                    TextView messageView = (TextView) activity.findViewById(R.id.textViewAddParticipantMessage);
                    switch (result) {
                        case 0: //success
                            break;
                        case 7: //fail
                            break;
                        case 12: //already entered
                            break;
                        case 13: //not a participant of this room
                            break;
                    }
                }
            });
        }
    },
    SEND_CURR_ROOM (6){
        @Override
        public void handle(final InPacket packet, final Activity activity, final Handler handler) throws Exception {
            final byte isRoom = packet.readByte();
            //do it every time
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if(isRoom == 0){ //if room exists
                        Boolean isAdmin = false;
                        String roomname = "";
                        try {
                            roomname = packet.readString();
                            isAdmin = packet.readBool();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Intent intent = new Intent(activity, RoomChatActivity.class);
                        intent.putExtra("ROOMNAME", roomname);
                        intent.putExtra("ISADMIN", isAdmin);
                        activity.startActivity(intent);
                    }
                }
            });
        }
    },
    GETCONTACTS(7) {
        @Override
        public void handle(final InPacket packet, final Activity activity, Handler handler) throws Exception {
            final short contactsNum = packet.readShort();
            final String[] values = new String[contactsNum];
            Short contactLen = 0;
            String username = "";
            for (byte i = 0; i < contactsNum; i++){
                try {
                    contactLen = packet.readShort();
                    username = packet.readString(contactLen);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                values[i] = username;
            }
            //do it every time
            handler.post(new Runnable() {
                @Override
                public void run() {
                ContactsList contactsList = new ContactsList(activity, values);
                }
            });
        }
    },
    GETROOMS(8) {
        @Override
        public void handle(final InPacket packet, final Activity activity, Handler handler) throws Exception {
            final short roomsNum = packet.readShort();
            final String[] values = new String[roomsNum];
            Short roomLen = 0;
            String roomname = "";
            for (byte i = 0; i < roomsNum; i++){
                try {
                    roomLen = packet.readShort();
                    roomname = packet.readString(roomLen);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                values[i] = roomname;
            }
            //do it every time
            handler.post(new Runnable() {
                @Override
                public void run() {
                    RoomsList roomsList = new RoomsList(activity, values);
                }
            });
        }
    },
    GETPARTICIPANTS(9) {
        @Override
        public void handle(final InPacket packet, final Activity activity, Handler handler) throws Exception {
            final short partiNum = packet.readShort();
            final String[] values = new String[partiNum];
            Short partiLen = 0;
            String partiname = "";
            for (byte i = 0; i < partiNum; i++){
                try {
                    partiLen = packet.readShort();
                    partiname = packet.readString(partiLen);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                values[i] = partiname;
            }
            //do it every time
            handler.post(new Runnable() {
                @Override
                public void run() {
                    ParticipantsList participantsList = new ParticipantsList(activity, values);
                }
            });
        }
    },
    SEND_RECORD(10) {
        @Override
        public void handle(final InPacket packet, final Activity activity, Handler handler) throws Exception {
            final byte result = packet.readByte();
            //do it every time
            handler.post(new Runnable() {
                @Override
                public void run() {
                    switch (result) {
                        case 0: //success
                            break;
                        case 2: //wrong details
                            break;
                        case 7: //fail
                            break;
                        case 16: ///cannot determine chat type
                            break;
                    }
                }
            });
        }
    },
    GET_ROOM_RECORD(11) {
        @Override
        public void handle(final InPacket packet, final Activity activity, Handler handler) throws Exception {
            String roomName = packet.readString();
            String senderName = packet.readString();
            if (senderName == "")
                senderName = "anonymous";
            final byte[] record = packet.readByteBuffer();
            //write to file
            String fileName = String.valueOf(System.currentTimeMillis()) + senderName + ".wav";
            String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + Session.getApplicationName() + "/Rooms/" + roomName + "/" + fileName;
            //create a directory of the records on the device
            File mydir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + Session.getApplicationName()+ "/Rooms/",
                    roomName);
            if (!mydir.exists())
                if (!mydir.mkdirs())
                    Log.d("App", "failed to create directory");
            FileOutputStream fos = new FileOutputStream(filePath);
            fos.write(record);
            fos.close();

            PlayRecordsHelper.getInstance().SetNewRecord(roomName, filePath, RecordsType.ROOM);
        }
    },
    GETDISTORTIONS(12){//TODO
        @Override
        public void handle(final InPacket packet, final Activity activity, Handler handler) throws Exception {
            handler.post(new Runnable() {
                @Override
                public void run() {

                }
            });
        }
    },
    EXIT_ROOM(13){
        @Override
        public void handle(final InPacket packet, final Activity activity, Handler handler) throws Exception {
            final byte result = packet.readByte();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    switch (result) {
                        case 0: //success
                            break;
                        case 1: //room doesn't exist
                            break;
                        case 7: //fail
                            break;
                        case 13: //you are a participant if this room
                            break;
                    }
                }
            });
        }
    },
    REMOVE_CONTACT(14){
        @Override
        public void handle(final InPacket packet, final Activity activity, Handler handler) throws Exception {
            final byte result = packet.readByte();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    switch (result) {
                        case 0: //success
                            break;
                        case 1: //username doesn't exist
                            break;
                        case 7: //fail
                            break;
                        case 11: //not in your contacts
                            break;
                    }
                }
            });
        }
    },
    SEND_CURR_CHAT(15){
        @Override
        public void handle(final InPacket packet, final Activity activity, Handler handler) throws IOException, Exception {
            final byte isUser = packet.readByte();
            //do it every time
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if(isUser == 0){ //if user exists
                        String username = "";
                        try {
                            username = packet.readString();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Intent intent = new Intent(activity, ClientChatActivity.class);
                        intent.putExtra("USERNAME", username);
                        activity.startActivity(intent);
                    }
                }
            });
        }
    },
    GET_CLIENT_RECORD(16){
        @Override
        public void handle(final InPacket packet, final Activity activity, Handler handler) throws Exception {
            String username = packet.readString();
            String senderName = packet.readString();
            final byte[] record = packet.readByteBuffer();
            //write to file
            String fileName = String.valueOf(System.currentTimeMillis()) + senderName + ".wav";
            String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + Session.getApplicationName() + "/Clients/" + username + "/" + fileName;
            //create a directory of the records on the device
            File mydir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + Session.getApplicationName()+ "/Clients/",
                    username);
            if (!mydir.exists())
                if (!mydir.mkdirs())
                    Log.d("App", "failed to create directory");
            FileOutputStream fos = new FileOutputStream(filePath);
            fos.write(record);
            fos.close();

            PlayRecordsHelper.getInstance().SetNewRecord(senderName, filePath, RecordsType.CLIENT);
        }
    },
    GET_ABOUT_INFO(17){
        @Override
        public void handle(final InPacket packet, final Activity activity, Handler handler) throws Exception {
            final String info = packet.readString();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(activity, InfoActivity.class);
                    intent.putExtra("INFO", info);
                    activity.startActivity(intent);
                }
            });
        }
    },
    DEFAULT(-1) {
        @Override
        public void handle(InPacket packet, final Activity activity, final Handler handler){}
    }
    ;

    private byte value;

    private ServerOperation(int value) {
        this.value = (byte) value;
    }

    public byte getValue() {
        return value;
    }

    public abstract void handle(InPacket packet, final Activity activity, final Handler handler) throws IOException, Exception;

}