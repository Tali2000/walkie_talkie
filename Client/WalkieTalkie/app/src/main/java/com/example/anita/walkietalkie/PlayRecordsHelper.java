package com.example.anita.walkietalkie;

//A class that connects between the operation "getRecord" to a chat activity
//The variables tells about a room, but refers to a user too

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class PlayRecordsHelper {
    private static PlayRecordsHelper instance;
    private Map<String, ArrayList<String>> records; //key: roomname, values: list of paths

    public static PlayRecordsHelper getInstance() {
        if(instance == null)
            instance = new PlayRecordsHelper();
        return instance;
    }

    private PlayRecordsHelper() {
        records = new LinkedHashMap<String, ArrayList<String>>();
    }

    public String CheckForNewRecord(String roomName){
        String ret;
        if(records.get(roomName) != null && !records.get(roomName).isEmpty() && records.containsKey(roomName)){
            ret = records.get(roomName).get(0);
            records.get(roomName).remove(0);
            return ret;
        }
        return null;
    }

    public void SetNewRecord(String roomname, String filePath){
        ArrayList temp = new ArrayList<String>();
        if(records.containsKey(roomname)){
            if(records.get(roomname) != null){
                temp = records.get(roomname);
                records.remove(roomname);
            }
        }
        temp.add(filePath);
        records.put(roomname, temp);
    }
}
