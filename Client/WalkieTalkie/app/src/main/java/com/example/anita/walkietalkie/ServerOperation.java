package com.example.anita.walkietalkie;


import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.io.DataInputStream;
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
                                    ("Successful connected!");
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
                        case 7: //other
                            messageView.setText
                                    ("something went wrong");
                            break;
                    }
                }
            });
        }
    },
    ADDCONTACT (2){
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