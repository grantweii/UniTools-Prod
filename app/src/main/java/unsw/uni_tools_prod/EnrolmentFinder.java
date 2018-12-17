package unsw.uni_tools_prod;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EnrolmentFinder extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner spinner1, spinner2;
    ArrayAdapter<CharSequence> adaptor;
    String faculty = "";
    String semester = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {


            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_enrolment_finder);

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle("Enrolment Finder");
            setSupportActionBar(toolbar);
            android.support.v7.app.ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);


            spinner1 = findViewById(R.id.faculty_spin);
            adaptor = ArrayAdapter.createFromResource(this, R.array.faculties, android.R.layout.simple_spinner_item);
            adaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner1.setAdapter(adaptor);
            spinner1.setOnItemSelectedListener(this);

            spinner2 = findViewById(R.id.semester_spin);
            spinner2.setOnItemSelectedListener(this);

            Button b1 = findViewById(R.id.submitForm1);
            b1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        ArrayList<String> res = getResults(faculty + "_" + semester + ".html");
                        Intent results = new Intent(EnrolmentFinder.this, EnrolList.class);
                        results.putStringArrayListExtra("results", res);
                        results.putExtra("semester", semester);
                        results.putExtra("faculty", faculty);
                        startActivity(results);
                    } catch (Exception e) {
                        Intent results = new Intent(EnrolmentFinder.this, EnrolList.class);
                        results.putStringArrayListExtra("results", new ArrayList<String>());
                        results.putExtra("semester", semester);
                        results.putExtra("faculty", faculty);
                        startActivity(results);

                        e.printStackTrace();
                    }
                }
            });

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
        finish();

        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        switch (parent.getId()) {
            case R.id.faculty_spin:
                faculty = parent.getSelectedItem().toString();
                break;

            case R.id.semester_spin:
                semester = parent.getSelectedItem().toString();
                if(semester.equals("Summer")) {
                    semester = "U1";
                }
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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

    public ArrayList<String> getResults(String str) throws Exception {
        String url = "http://classutil.unsw.edu.au/" + str;
        String res = getHTML(url);
        String pattern = "a name=\"([A-Z]{4}[0-9]{4}.*?colspan=[2|\"8\"])";
        Pattern p = Pattern.compile(pattern, Pattern.DOTALL);
        Matcher m = p.matcher(res);
        ArrayList<String> myList = new ArrayList<String>();
        ArrayList<String> scrapedList = new ArrayList<String>();

        while (m.find()) {
            myList.add(m.group(0));
        }

        for (String i : myList) {
            i = i.replaceAll(">([A-Z]{3})<", ">\n$1<");
            i = i.replaceAll("a name=\"", "");
            i = i.replaceAll("\".*?center>", " ");
            i = i.replaceAll("<[^>]+>", " ");
            i = i.replaceAll("&.*", "");
            i = i.replaceAll(" Enr ", "  ");
            i = i.replaceAll("\\* ", "   ");
            i = i.replaceAll("  +", ",");
            i = i.replaceAll(",\n", "\n");
            i = i.replaceAll("([A-Z]{4}[0-9]{4})[^ ]+", "$1");

            scrapedList.add(i);
        }
        return scrapedList;
    }
}