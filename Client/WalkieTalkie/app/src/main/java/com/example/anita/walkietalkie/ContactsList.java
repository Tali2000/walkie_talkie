package com.example.anita.walkietalkie;

import android.app.Activity;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;

public class ContactsList {
    private ListView contactList;
    private ArrayAdapter<String> adapter;

    public ContactsList(final Activity activity, String[] values) {
        contactList = (ListView) activity.findViewById(R.id.contactList);

        // Define a new Adapter
        // First parameter - Context
        // Second parameter - Layout for the row
        // Third parameter - ID of the TextView to which the data is written
        // Forth - the Array of data

        adapter = new ArrayAdapter<String>(activity,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);

        // Assign adapter to ListView
        contactList.setAdapter(adapter);

        // ListView Item Click Listener
        contactList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // ListView Clicked item value
                String username = (String) contactList.getItemAtPosition(position);
                final Handler handler = new Handler();
                try {
                    Session.getInstance(activity, handler).SendCurrentClient(username);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });
    }
}
