package com.example.anita.walkietalkie;

/*A class that manages the connection to the server*/

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Handler;

import java.io.IOException;
import java.net.Socket;

public class Session {
    private static Session instance;
    private static final String address = "192.168.43.20";
    private static final int port = 4242;
    private Socket socket;
    private Activity currentActivity;
    private static String appName;

    public static Session getInstance(final Activity activity, final Handler handler) {
        if (instance == null)
            instance = new Session(activity, handler);
        instance.setActivity(activity);
        return instance;
    }

    private void setActivity(final Activity activity) {
        currentActivity = activity;
    }

    private Session(final Activity activity, final Handler handler) {
        try {
            socket = new Socket(address, port);
        } catch (Exception e) {
            e.printStackTrace();
        }
        appName = getApplicationName(activity);
        new Thread(new Runnable() {
            @Override
            public void run() {
            while (true) {
                try {
                    if(socket.getInputStream().available() != 0) {
                        InPacket packet = new InPacket(socket.getInputStream());
                        byte op = packet.readByte();
                        //handle server's message
                        ServerOperation opcode = ServerOperation.DEFAULT;
                        for (ServerOperation operation : ServerOperation.values())
                            if (op == operation.getValue()) {
                                opcode = operation;
                                break;
                            }
                        if (opcode == ServerOperation.DEFAULT)
                            System.out.println("Unhandled operation " + op);
                        opcode.handle(packet, currentActivity, handler);
                        System.out.println("message from server");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            }
        }).start();
    }

    public static String getApplicationName(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : context.getString(stringId);
    }

    public static String getApplicationName(){
        return appName;
    }

    public void Send(OutPacket packet) throws IOException {
        socket.getOutputStream().write(packet.toByteArray());
    }
    
    public void SignIn(String username, String password) throws IOException {
        try (OutPacket packet = new OutPacket(ClientOperation.SIGNIN)) {
            packet.writeString(username);
            packet.writeString(password);
            Send(packet);
        }
    }

    public void SignUp(String username, String password) throws IOException {
        try (OutPacket packet = new OutPacket(ClientOperation.SIGNUP)) {
            packet.writeString(username);
            packet.writeString(password);
            Send(packet);
        }
    }

    public void AddContact(String username) throws IOException {
        try(OutPacket packet = new OutPacket(ClientOperation.ADDCONTACT)) {
            packet.writeString(username);
            Send((packet));
        }
    }

    public void GetContacts() throws IOException {
        try(OutPacket packet = new OutPacket(ClientOperation.GETCONTACTS)) {
            Send(packet);
        }
    }

    public void GetRooms() throws IOException {
        try(OutPacket packet = new OutPacket(ClientOperation.GETROOMS)) {
            Send(packet);
        }
    }

    public void CreateRoom(String roomname, Boolean anonymousMode) throws IOException {
        try(OutPacket packet = new OutPacket(ClientOperation.CREATEROOM)){
            packet.writeString(roomname);
            packet.writeBool(anonymousMode);
            Send(packet);
        }
    }

    public void AddParticipant(String username) throws IOException {
        try(OutPacket packet = new OutPacket(ClientOperation.ADDPARTICIPANT)){
            packet.writeString(username);
            Send(packet);
        }
    }

    public void SendCurrentRoom(String roomname) throws IOException {
        try(OutPacket packet = new OutPacket(ClientOperation.SENDCURRROOM)){
            packet.writeString(roomname);
            Send(packet);
        }
    }

    public void SendCurrentClient(String username) throws IOException {
        try(OutPacket packet = new OutPacket(ClientOperation.SENDCURRCHAT)){
            packet.writeString(username);
            Send(packet);
        }
    }

    public void GetParticipants() throws IOException{
        try(OutPacket packet = new OutPacket(ClientOperation.GETPARTICIPANTS)){
            Send(packet);
        }
    }

    public void SendRecord(String filePath, byte voiceType, RecordsType chatType) throws IOException{
        try(OutPacket packet = new OutPacket(ClientOperation.SENDRECORD)){
            packet.writeByte(chatType.getValue());
            packet.writeByte(voiceType);
            packet.writeFile(filePath);
            Send(packet);
        }
    }
}
