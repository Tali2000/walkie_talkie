package com.example.anita.walkietalkie;

//A class that connects between the operation "getRecord" to a chat activity
//The variables tells about a room, but refers to a user too

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class PlayRecordsHelper {
    private static PlayRecordsHelper instance;
    private Map<String, ArrayList<String>> roomRecords; //key: roomname, values: list of paths
    private Map<String, ArrayList<String>> clientRecords; //key: username, values: list of paths

    public static PlayRecordsHelper getInstance() {
        if(instance == null)
            instance = new PlayRecordsHelper();
        return instance;
    }

    private PlayRecordsHelper() {
        roomRecords = new LinkedHashMap<>();
        clientRecords = new LinkedHashMap<>();
    }

    public String CheckForNewRecord(String name, RecordsType recordType) {
        String ret = null;
        if(recordType == RecordsType.ROOM) {
            if (roomRecords.get(name) != null && !roomRecords.get(name).isEmpty() && roomRecords.containsKey(name)) {
                ret = roomRecords.get(name).get(0);
                roomRecords.get(name).remove(0);
            }
        }
        else if(recordType == RecordsType.CLIENT){
            if(clientRecords.get(name) != null && !clientRecords.get(name).isEmpty() && clientRecords.containsKey(name)){
                ret = clientRecords.get(name).get(0);
                clientRecords.get(name).remove(0);
            }
        }
        return ret;
    }

    public void SetNewRecord(String name, String filePath, RecordsType recordType){
        ArrayList temp = new ArrayList<String>();
        if(recordType == RecordsType.ROOM) {
            if(roomRecords.get(name) != null && !roomRecords.get(name).isEmpty() && roomRecords.containsKey(name)){
                temp = roomRecords.get(name);
                roomRecords.remove(name);
            }
            temp.add(filePath);
            roomRecords.put(name, temp);
        }
        else if(recordType == RecordsType.CLIENT) {
            if(clientRecords.get(name) != null && !clientRecords.get(name).isEmpty() && clientRecords.containsKey(name)){
                temp = clientRecords.get(name);
                clientRecords.remove(name);
            }
            temp.add(filePath);
            clientRecords.put(name, temp);
        }
    }
}
