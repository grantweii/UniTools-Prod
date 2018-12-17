package unsw.uni_tools_prod;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class ResetPassword2 extends AppCompatActivity {

    EditText passwordOne;
    EditText passwordTwo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password2);

        setTitle("Reset Password");
        passwordOne = findViewById(R.id.passwordOne);
        passwordTwo = findViewById(R.id.passwordTwo);
    }

    public void newPassword(View view) {
        //check if passwords match
        if (passwordOne.getText().toString().matches(passwordTwo.getText().toString())) {
            String email = getIntent().getStringExtra("email");

            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.whereEqualTo("email", email);
            query.findInBackground(new FindCallback<ParseUser>() {
                public void done(List<ParseUser> objects, ParseException e) {
                    //should have only found ONE user
                    if (e == null && objects.size() == 1) {
                        //String email = objects.get(0).getEmail();
                        ParseUser user = objects.get(0);
                        user.setPassword(passwordOne.getText().toString());
                        user.saveInBackground();
                        Toast.makeText(ResetPassword2.this, "Password has been reset", Toast.LENGTH_SHORT).show();
                        showLoginPage();
                    } else {
                        //should NOT go here as email has been validated in previous page
                        Toast.makeText(ResetPassword2.this, "Error! Email address not found!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } else {
            Toast.makeText(ResetPassword2.this, "Passwords don't match!", Toast.LENGTH_SHORT).show();
        }

    }

    public void showLoginPage() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }

    public void onClick(View view) {
        if (view.getId() == R.id.backgroundLayout) {
            if (this.getCurrentFocus() != null) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        }
    }
}
