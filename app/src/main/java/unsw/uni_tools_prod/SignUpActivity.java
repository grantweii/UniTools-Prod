package unsw.uni_tools_prod;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.List;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {

    EditText emailEditText;
    EditText passwordEditText;
    EditText firstNameEditText;
    EditText lastNameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        setTitle("Sign Up");

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        ImageView logoImageView = findViewById(R.id.logoImageView);
        RelativeLayout backgroundLayout = findViewById(R.id.backgroundLayout);
        logoImageView.setOnClickListener(this); //setup for: if click onto logo, drop the keyboard
        backgroundLayout.setOnClickListener(this); // "    "

    }

    public void signUpClicked(View view) {
        if (firstNameEditText.getText().toString().matches("")) { //if first name is empty
            Toast.makeText(this, "Name required!", Toast.LENGTH_SHORT).show();
        } else if (emailEditText.getText().toString().matches("") || passwordEditText.getText().toString().matches("")) {
            Toast.makeText(this, "Email and Password required!", Toast.LENGTH_SHORT).show();
        } else {
            ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
            userQuery.whereEqualTo("email", emailEditText.getText().toString());
            try {
                List<ParseUser> users = userQuery.find();
                if (users != null && users.size() > 0) {
                    //email exists
                    Toast.makeText(this, "Email already exists", Toast.LENGTH_SHORT).show();
                } else {
                    //email does not exist
                    ParseUser user = new ParseUser();
                    user.setUsername(emailEditText.getText().toString());
                    user.setEmail(emailEditText.getText().toString());
                    user.setPassword(passwordEditText.getText().toString());
                    if (lastNameEditText.getText().toString().matches("")) { //if last name is empty THATS FINE
                        user.put("name", firstNameEditText.getText().toString());
                    } else if (!lastNameEditText.getText().toString().matches("") && !firstNameEditText.getText().toString().matches("")) {
                        //this else if is not really necessary, the first name was checked above
                        user.put("name", firstNameEditText.getText().toString() + " " + lastNameEditText.getText().toString());
                    }

                    user.put("verified", false);

                    //REMOVING EMAIL VERIFICATION AT SIGN UP
                /*
                //generate random 6 digit number to send user's email
                Random rnd = new Random();
                final int number = rnd.nextInt(999999) + 100000;
                //store digit in user database and set emailVerified to false
                user.put("uniqueCode", number);


                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            GMailSender sender = new GMailSender("unswunitools@gmail.com",
                                    "unitools=1");
                            sender.sendMail("UniTools email verification",
                                    "Welcome to UniTools! " + emailEditText.getText().toString() + "\n\nThis is your unique code: " + String.format("%06d", number),
                                    "unswunitools@gmail.com", "unswunitools@gmail.com");
                        } catch (Exception e) {
                            Log.e("SendMail", e.getMessage(), e);
                        }
                    }

                }).start();
                */

                    user.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                showHomePage();
                                //REMOVING EMAIL VERIFICATION AT SIGN UP
                                //if successfully sent email, have user input code
                                //insertCode();
                            } else {
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void showHomePage() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.logoImageView || view.getId() == R.id.backgroundLayout) {
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
        }
        return false;
    }

}
