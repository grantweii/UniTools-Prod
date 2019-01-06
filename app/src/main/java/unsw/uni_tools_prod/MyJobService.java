package unsw.uni_tools_prod;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.parse.ParseUser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static unsw.uni_tools_prod.ParseStarter.CHANNEL_ID;

public class MyJobService extends JobService {
    private static final String TAG = "MyJobService";
    private boolean jobCancelled = false;
    private NotificationManagerCompat notificationManager;

    @Override
    public boolean onStartJob(JobParameters params) {
        doBackgroundWork(params);
        Log.i(TAG, "onStartJob");
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i(TAG, "Job cancelled before completion");
        jobCancelled = true;
        return true;
    }

    private void doBackgroundWork(final JobParameters params) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (jobCancelled) {
                    return;
                }

                //STEP 1: get all followed course through User class
                Log.i(TAG, "Job STEP 1");
                final Set<String> courseSet = new HashSet<String>();
                if (ParseUser.getCurrentUser().getList("followedCourses") != null && ParseUser.getCurrentUser().getList("followedCourses").size() > 0) {
                    List<String> followedCourses = ParseUser.getCurrentUser().getList("followedCourses");
                    courseSet.addAll(followedCourses);
                    Log.i(TAG, "List of followed courses exist");
                } else {
                    Log.i(TAG, "No followed courses");
                    return;
                }

                //TODO: STEP 2 - GET request to check if any of the courses in courseSet have a spot available
                Log.i(TAG, "Job STEP 2");
                final Set<String> openCourses = new HashSet<String>();

                for(String course : courseSet) {
                    String courseInfo[] = course.split("_");
                    String courseId = courseInfo[0];
                    String faculty = courseId.substring(0, 4);
                    String term = courseInfo[1];
                    String classId = courseInfo[2];
                    String url = "http://classutil.unsw.edu.au/" + faculty + "_" + term + ".html";
                    String html = "";
                    try {
                        html = getHTML(url);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    String pattern = classId + "<" + ".*?([0-9]+)/(-?[0-9]+)";
                    if (classId.length() == 4) {
                        pattern = "> " + pattern;
                    } else {
                        pattern = ">" + pattern;
                    }
                    Pattern p = Pattern.compile(pattern, Pattern.DOTALL);
                    Matcher m = p.matcher(html);

                    while (m.find()) {
                        int enrols = Integer.valueOf(m.group(1));
                        int capacity = Integer.valueOf(m.group(2));

                        if (capacity - enrols > 0) {
                            openCourses.add(course);
                        }
                    }
                }

                Log.i(TAG, "Job STEP 3");
                int notificationCount = 0;
                for (String openCourse: openCourses) {
                    String[] splitString = openCourse.split("_");
                    String course = splitString[0];
                    String term = splitString[1];
                    String classId = splitString[2];

                    Log.i(TAG, course + " has a spot");
                    sendNotification(course, term, classId, notificationCount);
                    notificationCount++;
                }

                Log.i(TAG, "Job finished");
                jobFinished(params, false);
            }
        }).start();
    }

    private void sendNotification(String course, String term, String classId, int notificationCount) {
        notificationManager = NotificationManagerCompat.from(this);

        String title = course + " " + term;
        String text = "A spot has opened up in class " + classId;

        Intent activityIntent = new Intent(this, HomeActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, activityIntent, 0);

        //uses channel id if API 26 or higher, otherwise ignores this
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(contentIntent)
                .build();

        notificationManager.notify(notificationCount, notification);
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
