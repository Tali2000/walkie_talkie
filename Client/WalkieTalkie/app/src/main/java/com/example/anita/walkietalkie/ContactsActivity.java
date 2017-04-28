package com.example.anita.walkietalkie;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.view.View.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class ContactsActivity extends Activity implements OnClickListener {

    Button addContactButton, roomsButton, generalButton; //TODO
    ListView contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        addContactButton = (Button) findViewById(R.id.addContact);
        addContactButton.setOnClickListener(this);

        generalButton = (Button) findViewById(R.id.generalButton);
        generalButton.setOnClickListener(this);

        roomsButton = (Button) findViewById(R.id.roomsButton);
        roomsButton.setOnClickListener(this);

        contactList = (ListView) findViewById(R.id.contactList);
        final Activity activity = this;
        final Handler handler = new Handler();
        try {
            Session.getInstance(activity, handler).GetContacts();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(final View v) {
        // detect the view that was "clicked"
        final Activity activity = this;
        final Handler handler = new Handler();

        try {
            switch (v.getId()) {
                case R.id.addContact:
                    startActivity(new Intent(this, AddContactActivity.class));
                    break;
                case R.id.generalButton:
                    //TODO
                    break;
                case R.id.roomsButton:
                    startActivity(new Intent(this, RoomsActivity.class));
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
     }
}
