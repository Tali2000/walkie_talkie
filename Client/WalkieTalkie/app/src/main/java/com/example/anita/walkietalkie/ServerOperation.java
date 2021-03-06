package com.example.anita.walkietalkie;
/**
 * A class of enums with numeration of server's messages to the client and treat it.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
                            Intent intent = new Intent(activity, ContactsActivity.class);
                            activity.startActivity(intent);
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
                    }
                }
            });
        }
    },
    ADD_CONTACT (2){
        @Override
        public void handle(InPacket packet, final Activity activity, final Handler handler) throws Exception {
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
                        case 11: //user is not in your contacts
                            messageView.setText
                                    ("user is not in your contacts");
                            break;
                    }
                }
            });
        }
    },
    SEND_CURR_ROOM (5){
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
    GETCONTACTS(6) {
        @Override
        public void handle(final InPacket packet, final Activity activity, Handler handler) throws Exception {
            final short contactsNum = packet.readShort();
            final String[] values = new String[contactsNum];
            Short contactLen = 0;
            String username = "";
            //generate values for the contacts list
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
                new ContactsList(activity, values);
                }
            });
        }
    },
    GETROOMS(7) {
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
    GETPARTICIPANTS(8) {
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
    SEND_RECORD(9) {
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
    GET_ROOM_RECORD(10) {
        @Override
        public void handle(final InPacket packet, final Activity activity, Handler handler) throws Exception {
            String roomName = packet.readString();
            String senderName = packet.readString();
            final byte[] record = packet.readByteBuffer();
            if(senderName == "")
                senderName = "ano";
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
    SEND_CURR_CHAT(11){
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
    GET_CLIENT_RECORD(12){
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
    DEFAULT(-1) {
        @Override
        public void handle(InPacket packet, final Activity activity, final Handler handler){}
    };

    private byte value;

    private ServerOperation(int value) {
        this.value = (byte) value;
    }

    public byte getValue() {
        return value;
    }

    public abstract void handle(InPacket packet, final Activity activity, final Handler handler) throws IOException, Exception;

}