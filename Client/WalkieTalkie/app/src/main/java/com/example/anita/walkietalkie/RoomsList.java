package com.example.anita.walkietalkie;

import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class RoomsList {
    private ListView roomList;
    private ArrayAdapter<String> adapter;

    public RoomsList(Activity activity, String[] values) {
        roomList = (ListView) activity.findViewById(R.id.roomList);;

        // Define a new Adapter
        // First parameter - Context
        // Second parameter - Layout for the row
        // Third parameter - ID of the TextView to which the data is written
        // Forth - the Array of data


        adapter = new ArrayAdapter<String>(activity,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);

        // Assign adapter to ListView
        roomList.setAdapter(adapter);

        // ListView Item Click Listener
        roomList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index
                int itemPosition = position;

                // ListView Clicked item value
                String itemValue = (String) roomList.getItemAtPosition(position);
            }

        });
    }
}
