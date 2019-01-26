package unsw.uni_tools_prod;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.nfc.Tag;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

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
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static unsw.uni_tools_prod.ParseStarter.CHANNEL_ID;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";

    private RecyclerView recyclerView;
    private CourseAdapter courseAdapter;
    private List<Course> courseList = new ArrayList<>();
    private Button scheduleJobButton;
    private TextView suggestionTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ParseAnalytics.trackAppOpenedInBackground(getIntent());
        scheduleJobButton = findViewById(R.id.scheduleJobButton);
        suggestionTextView = findViewById(R.id.suggestionTextView);

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

        /*
        recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        courseAdapter = new CourseAdapter(courseList);
        recyclerView.setAdapter(courseAdapter);
        getFollowedCourses();
        courseAdapter.notifyDataSetChanged();
        */

        getFollowedCourses();
        final TableLayout table = findViewById(R.id.followedTable);
        ArrayList<String> colNames = new ArrayList<>();
        colNames.add("  Type        ");
        colNames.add("  Class#        ");
        colNames.add("  Status        ");
        colNames.add("  Capacity      ");
        colNames.add("  Unfollow      ");

        TableRow headers = new TableRow(this);
        for (String s : colNames) {
            TextView t = new TextView(this);
            t.setText(s);
            t.setTypeface(t.getTypeface(), Typeface.BOLD);
            t.setTextColor(Color.BLACK);
            headers.addView(t);
        }
        table.addView(headers);

        ArrayList<String> headings = new ArrayList<String>();
        for(Course course : courseList) {
            String[] code = course.getCodeTerm().split("_");
            String faculty = code[0].substring(0, 4);
            String term = code[1];
            String id = code[2];
            String url = "http://classutil.unsw.edu.au/" + faculty + "_" + term + ".html";
            String html = "";
            try {
                html = getHTML(url);
            } catch (Exception e) {
                e.printStackTrace();
            }

            String pattern = "a name=\"" + code[0] + ".*?colspan=[2|\"8\"]";
            Pattern p = Pattern.compile(pattern, Pattern.DOTALL);
            Matcher m = p.matcher(html);

            while (m.find()) {
                String[] classes = m.group(0).replaceAll(">([A-Z]{3})<", ">\n$1<").split("\n");
                for(String line : classes) {
                    if(line.equals(classes[0])) {
                        line = line.replaceAll("([A-Z]{4}[0-9]{4}).*center>(.*)", "$1|$2");
                        line = line.replaceAll("a name=\"", "");
                        line = line.replaceAll("<.*", "");
                        line = line.replaceAll("\\|", " - ");
                        line = line.replaceAll("&amp;", "\\&");

                        TableRow name = new TableRow(this);
                        TextView t = new TextView(this);

                        if(headings.contains(line)) {
                            t.setText("");
                        }
                        else {
                            t.setText(line);
                            headings.add(line);
                        }

                        TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
                        params.span = 7;
                        t.setLayoutParams(params);
                        t.setTypeface(t.getTypeface(), Typeface.BOLD);
                        name.addView(t);
                        table.addView(name);
                    }
                    if(line.contains(id)) {
                        line = line.replaceAll("<[^>]+>", "|");
                        line = line.replaceAll(" |", "");
                        line = line.replaceAll("\\|\\|", "|");
                        line = line.replaceAll("\\|+<.*", "");

                        String[] fields = line.split("\\|");
                        if(fields.length >= 8) {
                            TableRow info = new TableRow(this);
                            for(String i : fields) {
                                if(i.equals(fields[1]) || i.equals(fields[3]) || i.equals(fields[6])) {
                                    continue;
                                }
                                if(i.equals(fields[7])) {
                                    table.addView(info);
                                    String[] classTimes = i.split(";");
                                    for(String j : classTimes) {
                                        TableRow times = new TableRow(this);
                                        TextView time = new TextView(this);
                                        time.setText("    - " + j + "  ");
                                        TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
                                        params.span = 7;
                                        time.setLayoutParams(params);
                                        times.addView(time);
                                        table.addView(times);
                                    }
                                    break;
                                }

                                TextView t = new TextView(this);
                                t.setText("  " + i + "  ");
                                info.addView(t);
                            }
                        }
                    }
                }
            }
        }
        Log.i(TAG, "oncreate");

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        courseList.clear();
        getFollowedCourses();
        courseAdapter.notifyDataSetChanged();
        Log.i(TAG,"onrestart");
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

        if (ParseUser.getCurrentUser().getList("followedCourses") != null) {
            List<String> followedCourses = ParseUser.getCurrentUser().getList("followedCourses");
            for (String courseCodeTerm: followedCourses) {
                Log.i("count", Integer.toString(count++));
                courseList.add(new Course(courseCodeTerm));
            }

            if (followedCourses.size() > 0) {
                scheduleJobButton.setVisibility(View.VISIBLE);
                suggestionTextView.setVisibility(View.GONE);
            } else {
                scheduleJobButton.setVisibility(View.INVISIBLE);
                suggestionTextView.setVisibility(View.VISIBLE);
            }
        }
    }

    public void scheduleJob(View view) {
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

    // All credit for the getHTML function goes to Kalpak
    // Source: https://stackoverflow.com/questions/1485708/how-do-i-do-a-http-get-in-java
    public String getHTML(String urlToRead) throws Exception {
        StringBuilder result = new StringBuilder();
        URL url = new URL(urlToRead);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        rd.close();
        return result.toString();
    }

}
