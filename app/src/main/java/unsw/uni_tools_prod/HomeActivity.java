package unsw.uni_tools_prod;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.parse.ParseAnalytics;

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
}
