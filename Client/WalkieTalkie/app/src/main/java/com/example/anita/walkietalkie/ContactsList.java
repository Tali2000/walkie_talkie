package com.example.anita.walkietalkie;


import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ContactsList {
    private ListView contactList;
    private String[] names; // Defined Array values to show in ListView
    private ArrayAdapter<String> adapter;

    public ContactsList(Activity activity, String[] values) {
        contactList = (ListView) activity.findViewById(R.id.contactList);;
        names = values;

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

                // ListView Clicked item index
                int itemPosition = position;

                // ListView Clicked item value
                String itemValue = (String) contactList.getItemAtPosition(position);
            }

        });
    }
}
