package unsw.uni_tools_prod;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;
import java.util.Random;

public class ResetPassword extends AppCompatActivity {

    EditText emailEditText;
    String email;
    EditText code;
    int number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        setTitle("Reset Password");
        emailEditText = findViewById(R.id.enterEmail);
        code = findViewById(R.id.numCode);
    }

    //when user clicks on get code button
    public void resetClicked (View view) {

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("email", emailEditText.getText().toString());
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> objects, ParseException e) {
                //should have only found ONE user
                if (e == null && objects.size() == 1) {
                    // email = user.getEmail(); FOR REAL PRODUCT
                    email = "unswunitools@gmail.com"; //FOR TESTING PURPOSES!!!!
                    //send a verification code to email before resetting password
                    Random rnd = new Random();
                    number = rnd.nextInt(999999) + 100000;

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                GMailSender sender = new GMailSender("unswunitools@gmail.com",
                                        "unitools=1");
                                sender.sendMail("UniTools: reset password",
                                        "Reset code: " + String.format("%06d", number),
                                        "unswunitools@gmail.com", email);
                            } catch (Exception e) {
                                Log.e("SendMail", e.getMessage(), e);
                            }
                        }

                    }).start();

                    ParseUser user = objects.get(0);
                    user.put("uniqueCode", number);
                    user.saveInBackground();
                    Toast.makeText(ResetPassword.this, "Reset code has been sent to your email", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(ResetPassword.this, "Email address not found! Please sign up", Toast.LENGTH_SHORT).show();
                    signupPage();
                }
            }
        });

    }

    //when user clicks on submit button
    public void submitCode(View view) {

       if (number == Integer.parseInt(code.getText().toString())) {
           inputNewPassword();
       } else {
           Toast.makeText(ResetPassword.this, "Incorrect code!", Toast.LENGTH_SHORT).show();
       }

    }

    public void inputNewPassword() {
        Intent intent = new Intent(getApplicationContext(), ResetPassword2.class);
        intent.putExtra("email", emailEditText.getText().toString());
        startActivity(intent);
    }

    public void signupPage() {
        Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
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
