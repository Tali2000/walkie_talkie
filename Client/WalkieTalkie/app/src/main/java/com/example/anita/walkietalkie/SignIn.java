package com.example.anita.walkietalkie;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * A class of the first activity in the app.
 * contains sign in and sign up functions.
 */

public class SignIn extends Activity implements OnClickListener {
    Button signInButton, signUpButton;
    EditText userName, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        signInButton = (Button) findViewById(R.id.signIn);
        signUpButton = (Button) findViewById(R.id.signUp);
        userName = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        signUpButton.setOnClickListener(this);
        signInButton.setOnClickListener(this);
    }

    @Override
    public void onClick(final View v) {
        // detect the view that was "clicked"
        final Handler handler = new Handler();
        final Activity activity = this;
        new Thread(new Runnable() {
            public void run() {
                try {
                    switch (v.getId()) {
                        case R.id.signIn:
                            Session.getInstance(activity, handler).SignIn(userName.getText().toString(), password.getText().toString());
                            break;
                        case R.id.signUp:
                            Session.getInstance(activity, handler).SignUp(userName.getText().toString(), password.getText().toString());
                            break;
                    }
                } catch (Exception e) {
                        e.printStackTrace();
                }
            }
        }).start();

    }
}
