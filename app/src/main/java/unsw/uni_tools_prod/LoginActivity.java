package unsw.uni_tools_prod;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {

    EditText emailEditText;
    EditText passwordEditText;
    TextView resetPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setTitle("Login");

        Log.i("login", "page");

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        resetPass = findViewById(R.id.resetPassword);
        resetPass.setClickable(true);
        ImageView logoImageView = findViewById(R.id.logoImageView);
        RelativeLayout backgroundLayout = findViewById(R.id.backgroundLayout);
        logoImageView.setOnClickListener(this); //setup for: if click onto logo, drop the keyboard
        backgroundLayout.setOnClickListener(this); // "    "

    }

    public void loginClicked(View view) {

        if (emailEditText.getText().toString().matches("") || passwordEditText.getText().toString().matches("")) {
            Toast.makeText(this, "Email and Password required!", Toast.LENGTH_SHORT).show();
        } else {

//            ParseUser user = new ParseUser();
//            user.setUsername(emailEditText.getText().toString());
////            user.setEmail(emailEditText.getText().toString());
//            user.setPassword(passwordEditText.getText().toString());

        ParseUser.logInInBackground(emailEditText.getText().toString(), passwordEditText.getText().toString(), new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    showHomePage();
                } else {
                    if (e != null) {
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        }
    }

    public void showHomePage() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.logoImageView || view.getId() == R.id.backgroundLayout) {
            //if either the logo or background was clicked (essentially everything else), hide keyboard
            if (this.getCurrentFocus() != null) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        }
    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if (i == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
            loginClicked(view);
        }
        return false;
    }

    public void resetPassword(View view) {
        Intent intent = new Intent(getApplicationContext(), ResetPassword.class);
        startActivity(intent);
    }


}
