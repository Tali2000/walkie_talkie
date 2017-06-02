package com.example.anita.walkietalkie;
/**
 * A class of an activity that sends to the server a message of adding a contact.
 */

import android.app.Activity;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.view.View.OnClickListener;


public class AddContactActivity extends Activity implements OnClickListener {
    EditText userName;
    Button serachButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        serachButton = (Button) findViewById(R.id.searchContactButton);
        serachButton.setOnClickListener(this);
        userName = (EditText) findViewById(R.id.searchBar);
    }

    @Override
    public void onClick(View v) {
        // detect the view that was "clicked"
        final Activity activity = this;
        final Handler handler = new Handler();
        try {
            switch (v.getId()) {
                case R.id.searchContactButton:
                    Session.getInstance(activity, handler).AddContact(userName.getText().toString());
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
