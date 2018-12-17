package unsw.uni_tools_prod;

/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.ParseAnalytics;
import com.parse.ParseUser;


public class WelcomePageActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {

    TextView loginTextView;

    public void loginClicked() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        Log.i("login", "clicked");
        startActivity(intent);
    }

    public void signUpClicked(View view) {
        Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
        Log.i("sign up", "clicked");
        startActivity(intent);
    }

    public void showHomePage() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);

        setTitle("UniTools");

        loginTextView = (TextView) findViewById(R.id.loginTextView);
        loginTextView.setOnClickListener(this);
        ImageView logoImageView = (ImageView) findViewById(R.id.logoImageView);
        RelativeLayout backgroundLayout = (RelativeLayout) findViewById(R.id.backgroundLayout);
        logoImageView.setOnClickListener(this);
        backgroundLayout.setOnClickListener(this);

        if (ParseUser.getCurrentUser() != null) { //if already logged in, send to main page
            showHomePage();
        }

        ParseAnalytics.trackAppOpenedInBackground(getIntent()); //does this need to be on the activity that parse is used?

    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.loginTextView) {
            loginClicked();
        } else if (view.getId() == R.id.logoImageView || view.getId() == R.id.backgroundLayout) {
            if (this.getCurrentFocus() != null) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        }
    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if (i == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
            signUpClicked(view);
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}