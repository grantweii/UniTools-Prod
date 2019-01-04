package unsw.uni_tools_prod;


import android.util.Log;
import android.widget.TextView;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
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

public class MyJobService extends JobService {
    private static final String TAG = "MyJobService";
    private boolean jobCancelled = false;

    @Override
    public boolean onStartJob(JobParameters params) {
        doBackgroundWork(params);
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

                Log.i(TAG, "Job finished");
                jobFinished(params, false);
            }
        }).start();
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
