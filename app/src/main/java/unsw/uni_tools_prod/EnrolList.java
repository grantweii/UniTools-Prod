package unsw.uni_tools_prod;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EnrolList extends AppCompatActivity {
    private boolean followSuccess = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enrol_list);

        Intent recv = getIntent();
        ArrayList<String> results = recv.getStringArrayListExtra("results");
        String term = recv.getExtras().getString("semester");
        String fac = recv.getExtras().getString("faculty");

        if (term.equals("U1")) {
            term = "Summer";
        }

        term = fac + " " + term;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(term + " Enrolments");
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        final TableLayout table = findViewById(R.id.enrolTable);

        ArrayList<String> colNames = new ArrayList<>();
        colNames.add("  Type      ");
        colNames.add("  Class#      ");
        colNames.add("  Status      ");
        colNames.add("  Capacity      ");
        colNames.add("  Follow      ");

        TableRow headers = new TableRow(this);
        for (String s : colNames) {
            TextView t = new TextView(this);
            t.setText(s);
            t.setTypeface(t.getTypeface(), Typeface.BOLD);
            t.setTextColor(Color.BLACK);
            headers.addView(t);
        }
        table.addView(headers);

        if (results.isEmpty()) {
            TextView tmp = new TextView(this);
            tmp.setText("Currently No Enrolments For This Semester!");
            TableRow empty = new TableRow(this);
            TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
            params.span = 7;
            tmp.setLayoutParams(params);
            empty.addView(tmp);
            table.addView(empty);
        }

        for (String i : results) {
            View v1 = new View(this);
            v1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, 1));
            v1.setBackgroundColor(Color.rgb(51, 51, 51));
            table.addView(v1);

            String t[] = i.split("\\r?\\n");
            TableRow name = new TableRow(this);
            final TextView t1 = new TextView(this);
            t[0] = " " + t[0].replace("|", "") + " ";
            t[0] = t[0].replaceAll("([A-Z]{4}[0-9]{4})(.*)", "$1 -$2");

            Pattern course = Pattern.compile("([A-Z]{4}[0-9]{4})");
            Matcher m = course.matcher(t[0]);
            ArrayList<String> myList = new ArrayList<String>();
            String courseName = "";
            String html = "<a href=\"https://www.handbook.unsw.edu.au/";

            if (m.find()) {
                courseName = m.group(1);
            }

            if (isUndergrad(courseName)) {
                html += "undergraduate/courses/2019/";
            } else {
                html += "postgraduate/courses/2019/";
            }

            html += (courseName + "/\">" + t[0] + "</a>");
            t1.setMovementMethod(LinkMovementMethod.getInstance());
            t1.setText(Html.fromHtml(html));

            TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
            params.span = 7;
            t1.setLayoutParams(params);
            t1.setTypeface(t1.getTypeface(), Typeface.BOLD);
            name.addView(t1);

            final TextView expand = new TextView(this);
            expand.setText(" + ");
            expand.setClickable(true);
            final ArrayList<TableRow> elements = new ArrayList();
            name.addView(expand);

            table.addView(name);

            View v2 = new View(this);
            v2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, 1));
            v2.setBackgroundColor(Color.rgb(51, 51, 51));
            table.addView(v2);

            for (int j = 1; j < t.length; j++) {
                final TableRow details = new TableRow(this);
                final String dets[] = t[j].split("\\|");

                if(dets.length < 6) {
                    continue;
                }

                for (String k : dets) {
                    if(k.equals(dets[1]) || k.equals(dets[5])) {
                        continue;
                    }

                    TextView t2 = new TextView(this);
                    t2.setText("  " + k + "  ");
                    t2.setTextColor(Color.BLACK);
                    details.addView(t2);
                }

                Set<String> courseSet = new HashSet<String>();
                if (ParseUser.getCurrentUser().getList("followedCourses") != null) {
                    List<String> followedCourses = ParseUser.getCurrentUser().getList("followedCourses");
                    courseSet.addAll(followedCourses);
                }


                String enrolRatio[] = dets[4].split("/");
                int enrols = Integer.valueOf(enrolRatio[0].replaceAll(" .*", ""));
                int capacity = Integer.valueOf(enrolRatio[1].replaceAll(" .*", ""));
                if(capacity - enrols <= 0) {
                    final Button followCourse = new Button(this);
                    final String followId = courseName + "_" + recv.getExtras().getString("semester") + "_" + dets[2];
                    if (courseSet.contains(followId)) {
                        followCourse.setText(" UNFOLLOW ");
                    } else {
                        followCourse.setText(" FOLLOW ");
                    }

                    followCourse.setTag(followId);
                    followCourse.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String info[] = ((String) view.getTag()).split("_");
                            String course = info[0];
                            String term = info[1];
                            String classId = info[2];

                            if (followCourse.getText().equals(" UNFOLLOW ")) {
                                unfollow(course, term, classId);
                                followCourse.setText(" FOLLOW ");
                            } else {
                                follow(course, term, classId);
                                followCourse.setText(" UNFOLLOW ");
                            }
                        }
                    });

                    details.addView(followCourse);
                }

                details.setVisibility(View.GONE);
                table.addView(details);
                elements.add(details);
            }

            View.OnClickListener onclicklistener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    /* REMOVE

                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open("finalCourseList.txt")));
                        String line;
                        String name = "";
                        String type = "";
                        int i = 0;
                        while ((line = reader.readLine()) != null) {
                            if (i % 2 == 0) {
                                name = line.replaceAll(" *", "");
                            } else {
                                type = line.replaceAll(" *", "");
                                ParseObject newCourse = new ParseObject("UpdatedCourses");
                                newCourse.put("UG", type);
                                newCourse.put("courseCode", name);
                                newCourse.saveInBackground();
                            }
                            i++;

                        }
                    } catch (IOException ex){
                        ex.printStackTrace();
                    }

                    REMOVE
                    */


                    Integer visib = View.GONE;
                    if(expand.getText() == " - ") {
                        expand.setText(" + ");
                    }
                    else {
                        expand.setText(" - ");
                        visib = View.VISIBLE;
                    }

                    for(TableRow i : elements) {
                        i.setVisibility(visib);
                    }
                }
            };
            expand.setOnClickListener(onclicklistener);

            View v3 = new View(this);
            v3.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, 1));
            v3.setBackgroundColor(Color.rgb(51, 51, 51));
            table.addView(v3);
        }
    }

    public Boolean isUndergrad(String str) {

        Boolean result = false;
        //Map<String, String> map = new HashMap<String, String>();
        ParseQuery<ParseObject> courseQuery = new ParseQuery<ParseObject>("Courses");
        courseQuery.setLimit(6000);
        courseQuery.whereEqualTo("courseCode", str);
        try {
            ParseObject objects = courseQuery.getFirst();
            result = (Boolean) objects.get("UG");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
        finish();

        return true;
    }

    public void follow(String courseCode, String term, String classId) {
        //save in user's followedCourses array field
        if (ParseUser.getCurrentUser().getList("followedCourses") != null) {
            List<String> followedCourses = ParseUser.getCurrentUser().getList("followedCourses");
            followedCourses.add(courseCode + "_" + term + "_" + classId);
            ParseUser.getCurrentUser().put("followedCourses", followedCourses);
            ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Log.i("society", "added to user's followed courses list");
                        followSuccess = true;
                    } else {
                        Log.i("society", "failed to add to user's followed courses list");
                    }
                }
            });
        } else {
            //else user has not followed any societies so the list is null
            List<String> followedCourses = new ArrayList<String>();
            followedCourses.add(courseCode + "_" + term + "_" + classId);
            ParseUser.getCurrentUser().put("followedCourses", followedCourses);
        }

    }

    public void unfollow(String courseCode, String term, String classId) {
        //remove from user's followed courses list
        if (ParseUser.getCurrentUser().getList("followedCourses") != null) {
            List<String> followedCourses = ParseUser.getCurrentUser().getList("followedCourses");
            followedCourses.remove(courseCode + "_" + term + "_" + classId);
            ParseUser.getCurrentUser().put("followedCourses", followedCourses);
            Log.i("followedCourses", "is not null");
        }

        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.i("courses", "removed from user's followed list");
                } else {
                    Log.i("courses", "failed to remove from user's followed list");
                }
            }
        });

    }


}
