package unsw.uni_tools_prod;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.parse.ParseAnalytics;
import com.parse.ParseUser;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ParseAnalytics.trackAppOpenedInBackground(getIntent());

        Button classFinderButton = findViewById(R.id.classFinderButton);
        classFinderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ClassroomFinder.class);
                startActivity(intent);
            }
        });

        Button enrolButton = findViewById(R.id.enrolButton);
        enrolButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), EnrolmentFinder.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }


    public void logoutClicked(View view) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Do you want to Log out?")
                .setPositiveButton("Logout", new DialogInterface.OnClickListener()                 {

                    public void onClick(DialogInterface dialog, int which) {
                        ParseUser.logOut();
//                            Toast.makeText(this, "Log out", Toast.LENGTH_SHORT).show();
                        finishAffinity();
                        Intent intent = new Intent(getApplicationContext(), WelcomePageActivity.class);
                        startActivity(intent);

                    }
                }).setNegativeButton("Cancel", null);

        AlertDialog alert1 = alert.create();
        alert1.show();
    }
}
