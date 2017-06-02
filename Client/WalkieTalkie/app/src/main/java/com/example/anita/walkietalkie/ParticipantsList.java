package com.example.anita.walkietalkie;

import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * A class that creates a list with the names of the participants in the room.
 */

public class ParticipantsList {
    private ListView participantsList;
    private ArrayAdapter<String> adapter;

    public ParticipantsList(Activity activity, String[] values) {
        participantsList = (ListView) activity.findViewById(R.id.ParticipantsList);

        // Define a new Adapter
        // First parameter - Context
        // Second parameter - Layout for the row
        // Third parameter - ID of the TextView to which the data is written
        // Forth - the Array of data

        adapter = new ArrayAdapter<String>(activity,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);

        // Assign adapter to ListView
        participantsList.setAdapter(adapter);

        // ListView Item Click Listener
        participantsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index
                int itemPosition = position;

                // ListView Clicked item value
                String itemValue = (String) participantsList.getItemAtPosition(position);
            }

        });
    }
}
