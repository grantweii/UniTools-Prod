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

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClassroomFinder extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner spinner1, spinner2, spinner3, spinner4;
    ArrayAdapter<CharSequence> adaptor;
    String startTime = "16";
    String endTime = "18";
    String day = "";
    String building="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_classroom_finder);

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle("Classroom Finder");
            setSupportActionBar(toolbar);
            android.support.v7.app.ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);

            spinner1 = findViewById(R.id.start_spin);
            adaptor = ArrayAdapter.createFromResource(this, R.array.times, android.R.layout.simple_spinner_item);
            adaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner1.setAdapter(adaptor);
            spinner1.setOnItemSelectedListener(this);

            spinner2 = findViewById(R.id.end_spin);
            spinner2.setOnItemSelectedListener(this);

            spinner3 = findViewById(R.id.day_spin);
            spinner3.setOnItemSelectedListener(this);

            spinner4 = findViewById(R.id.building_spin);
            spinner4.setOnItemSelectedListener(this);

            Button b1 = findViewById(R.id.submitForm);
            b1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<String> tmp = null;
                    try {
                        tmp = getRoomList(day, startTime, endTime, building);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    int i = 0;
                    ArrayList<String> data = new ArrayList<String>();
                    while(i < tmp.size()) {
                        String room = tmp.get(i);
                        String description = tmp.get(i + 1);
                        String html = "<a href=\"https://www.learningenvironments.unsw.edu.au/spaces/rooms?keys=";
                        html += description.replaceAll(" ", "+");
                        html += "\">" + room + "</a>";

                        data.add(html);
                        data.add(description);

                        i += 6;
                    }

                    Intent results = new Intent(ClassroomFinder.this, ClassList.class);
                    results.putStringArrayListExtra("data", data);
                    startActivity(results);
                }
            });
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        switch (parent.getId()) {
            case R.id.start_spin:
                startTime = parent.getSelectedItem().toString();
                break;

            case R.id.end_spin:
                endTime = parent.getSelectedItem().toString();
                break;

            case R.id.day_spin:
                day = parent.getSelectedItem().toString();
                break;

            case R.id.building_spin:
                building = parent.getSelectedItem().toString();
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public List<String> getRoomList(String day, String start, String end, String building) throws IOException {
        HttpURLConnection con;
        String host = "https://nss.cse.unsw.edu.au/tt/find_rooms.php?dbafile=2018-KENS-COFA.DBA&campus=KENS";

        String[] start_format = start.split(":");
        String[] end_format = end.split(":");

        start = String.valueOf(Integer.parseInt(start_format[0]) * 2);
        end = String.valueOf(Integer.parseInt(end_format[0]) * 2);

        Map<String, String> map = new HashMap<String, String>();
        map.put("None Selected", "");
        map.put("ADFA Campus", "PR_ADFA");
        map.put("Ainsworth Building", "PR_MECH");
        map.put("Arts and Design", "PR_ART&DES");
        map.put("Biological Science", "PR_BIOSCI");
        map.put("Central Lecture Block", "PR_CLB");
        map.put("Chemical Sciences", "PR_APPSCI");
        map.put("Civil Engineering", "PR_CIVIL");
        map.put("College of Fine Arts", "PR_COFA");
        map.put("Colombo", "PR_COLOMBO");
        map.put("Computer Science and Eng", "PR_CSEK17");
        map.put("Electrical Engineering", "PR_ELEC");
        map.put("Goldstein", "PR_GOLD");
        map.put("Heffron Building", "PR_HEFFRON");
        map.put("Huts", "PR_HUTS");
        map.put("John Goodsell", "PR_GOODSL");
        map.put("Law", "PR_LAW");
        map.put("Library Stage 2", "PR_LIBSTG2");
        map.put("Materials Science", "PR_MATSCI");
        map.put("Mathews", "PR_MATHEWS");
        map.put("Morven Brown", "PR_MORVENBRN");
        map.put("Old Main Building", "PR_OLDMAIN");
        map.put("Quadrangle", "PR_QUAD");
        map.put("Red Centre East", "PR_REDC-E");
        map.put("Red Centre West", "PR_REDC-W");
        map.put("Rupert Myers", "PR_MYERS");
        map.put("Samuels", "PR_SAM");
        map.put("Square House", "PR_SQHS");
        map.put("Tyree Energy Technology Building", "PR_TETB");
        map.put("UNSW Business School", "PR_ASB");
        map.put("Webster", "PR_WEBSTER");
        map.put("Western Grounds", "PR_WESTGD");
        map.put("Wurth", "PR_WURTH");

        if(day.equals("None Selected")) {
            day = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(System.currentTimeMillis());
        }

        String postInfo = "days[]=" + day + "&RU[]=RU_GP-TUTSEM&fr_time=" + start
                + "&to_time=" + end;

        if(!building.isEmpty()) {
            postInfo = postInfo + "&PR[]=" + map.get(building);
        }
        postInfo += "&search_rooms=Search";

        URL myurl = new URL(host);
        con = (HttpURLConnection) myurl.openConnection();

        con.setDoOutput(true);
        con.setRequestMethod("POST");
        con.getOutputStream().write(postInfo.getBytes("UTF-8"));

        InputStreamReader tmpStream = new InputStreamReader(con.getInputStream());

        // Following two lines were found on stack overflow
        // stackoverflow.com/questions/309424/read-convert-an-inputstream-to-a-string
        Scanner s = new Scanner(tmpStream).useDelimiter("\\A");
        String result = s.hasNext() ? s.next() : "";
        //System.out.println(result);

        // Scrape the classroom times from the response
        String pattern = " {8}<td class=\"data\">([^<]*)";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(result);

        List<String> myList = new ArrayList<String>();
        while (m.find()) {
            String info = m.group(1);
            myList.add(info);
        }

        return myList;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
        finish();

        return true;
    }

}