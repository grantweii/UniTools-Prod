package unsw.uni_tools_prod;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EnrolList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enrol_list);

        Intent recv = getIntent();
        ArrayList<String> results = recv.getStringArrayListExtra("results");
        String term = recv.getExtras().getString("semester");
        String fac = recv.getExtras().getString("faculty");

        if(term.equals("U1")) {
            term = "Summer";
        }

        term = fac + " " + term;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(term + " Enrolments");
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        TableLayout table = findViewById(R.id.enrolTable);

        ArrayList<String> colNames = new ArrayList<>();
        colNames.add("  Type      ");
        colNames.add("  Section      ");
        colNames.add("  Class#      ");
        colNames.add("  Status      ");
        colNames.add("  Capacity      ");
        colNames.add("  %      ");

        TableRow headers = new TableRow(this);
        for(String s : colNames) {
            TextView t = new TextView(this);
            t.setText(s);
            t.setTypeface(t.getTypeface(), Typeface.BOLD);
            t.setTextColor(Color.BLACK);
            headers.addView(t);
        }
        table.addView(headers);

        if(results.isEmpty()) {
            TextView tmp = new TextView(this);
            tmp.setText("Currently No Enrolments For This Semester!");
            TableRow empty = new TableRow(this);
            TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
            params.span = 7;
            tmp.setLayoutParams(params);
            empty.addView(tmp);
            table.addView(empty);
        }


        for(String i : results) {
            View v1 = new View(this);
            v1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, 1));
            v1.setBackgroundColor(Color.rgb(51, 51, 51));
            table.addView(v1);

            String t[] = i.split("\\r?\\n");
            TableRow name = new TableRow(this);
            TextView t1 = new TextView(this);
            t[0] = " " + t[0] + " ";
            t[0] = t[0].replaceAll("([A-Z]{4}[0-9]{4})(.*)", "$1 -$2");

            Pattern course = Pattern.compile("([A-Z]{4}[0-9]{4})");
            Matcher m = course.matcher(t[0]);
            ArrayList<String> myList = new ArrayList<String>();
            String courseName = "";
            String html = "<a href=\"https://www.handbook.unsw.edu.au/";

            if(m.find()) {
                courseName = m.group(1);
            }

            if(isUndergrad(courseName)) {
                html += "undergraduate/courses/2019/";
            }
            else {
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
            table.addView(name);

            View v2 = new View(this);
            v2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, 1));
            v2.setBackgroundColor(Color.rgb(51, 51, 51));
            table.addView(v2);

            for(int j = 1; j < t.length; j++) {
                TableRow details = new TableRow(this);
                String dets[] = t[j].split(",");
                for(String k : dets) {
                    TextView t2 = new TextView(this);
                    t2.setText("  " + k + "  ");
                    t2.setTextColor(Color.BLACK);
                    details.addView(t2);
                }
                table.addView(details);
            }
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
        }
        catch (Exception e) {
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
 }
