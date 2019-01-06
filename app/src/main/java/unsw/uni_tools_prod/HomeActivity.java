package unsw.uni_tools_prod;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.Tag;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static unsw.uni_tools_prod.ParseStarter.CHANNEL_ID;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";

    private RecyclerView recyclerView;
    private CourseAdapter courseAdapter;
    private List<Course> courseList = new ArrayList<>();


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

        recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        courseAdapter = new CourseAdapter(courseList);
        recyclerView.setAdapter(courseAdapter);
        getFollowedCourses();
        courseAdapter.notifyDataSetChanged();

        scheduleJob();
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    public void logoutClicked(View view) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Do you want to Log out?")
                .setPositiveButton("Logout", new DialogInterface.OnClickListener() {

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

    public void getFollowedCourses() {
        int count = 0;
        Log.i("eneters", "true");

        if (ParseUser.getCurrentUser().getList("followedCourses") != null) {
            Log.i("asd", "true");
            List<String> followedCourses = ParseUser.getCurrentUser().getList("followedCourses");
            for (String courseCodeTerm: followedCourses) {
                Log.i("count", Integer.toString(count++));
                courseList.add(new Course(courseCodeTerm));
            }
        }
    }

    public void scheduleJob() {
        ComponentName componentName = new ComponentName(this, MyJobService.class);
        JobInfo info = new JobInfo.Builder(123, componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .setPeriodic(15 * 60 * 1000)
                .build();

        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        int resultCode = scheduler.schedule(info);
        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            Log.i(TAG, "Job scheduled");
        } else {
            Log.i(TAG, "Job scheduling failed");
        }

    }

    public void cancelJob(View v) {
        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        scheduler.cancel(123);
        Log.i(TAG, "Job cancelled");
    }

}
