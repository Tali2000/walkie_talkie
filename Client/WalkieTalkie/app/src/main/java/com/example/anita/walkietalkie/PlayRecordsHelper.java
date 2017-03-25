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
        records = new LinkedHashMap<>();
    }

    public String CheckForNewRecord(String roomname){
        String ret = null;
        if(records.get(roomname) != null && !records.get(roomname).isEmpty() && records.containsKey(roomname)){
            ret = records.get(roomname).get(0);
            records.get(roomname).remove(0);
            //removeFirstRecord(roomname);
        }
        return ret;
    }

    public void SetNewRecord(String roomname, String filePath){
        ArrayList temp = new ArrayList<String>();
        if(records.get(roomname) != null && !records.get(roomname).isEmpty() && records.containsKey(roomname)){
            temp = records.get(roomname);
            records.remove(roomname);
        }
        temp.add(filePath);
        records.put(roomname, temp);
    }

    private void removeFirstRecord(String roomname){
        ArrayList temp = new ArrayList<String>();
        temp = records.get(roomname);
        records.remove(roomname);
        records.get(roomname).remove(0);

    }
}
