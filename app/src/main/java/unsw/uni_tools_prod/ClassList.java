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
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

public class ClassList extends AppCompatActivity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_class_list);

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle("Classrooms Found");
            setSupportActionBar(toolbar);
            android.support.v7.app.ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);

            Intent recv = getIntent();
            ArrayList<String> data = recv.getStringArrayListExtra("data");

            TableLayout table = findViewById(R.id.classTable);

            TableRow header = new TableRow(this);
            TextView t1 = new TextView(this);
            t1.setText("Room");
            t1.setTypeface(t1.getTypeface(), Typeface.BOLD);
            t1.setPadding(30, 25, 25, 25);
            t1.setTextSize(18);
            t1.setTextColor(Color.BLACK);
            header.addView(t1);
            TextView t2 = new TextView(this);
            t2.setText("Description");
            t2.setPadding(80, 25, 25, 25);
            t2.setTypeface(t2.getTypeface(), Typeface.BOLD);
            t2.setTextSize(18);
            t2.setTextColor(Color.BLACK);
            header.addView(t2);
            table.addView(header);

            int i = 0;
            while(i < data.size()) {
                TableRow r = new TableRow(this);
                TextView t3 = new TextView(this);
                t3.setMovementMethod(LinkMovementMethod.getInstance());
                t3.setText(Html.fromHtml(data.get(i)));
                t3.setPadding(30, 10, 10, 10);
                t3.setTextColor(Color.BLACK);
                r.addView(t3);
                TextView t4 = new TextView(this);
                t4.setText(data.get(i + 1));
                t4.setPadding(80, 10, 10, 10);
                t4.setTextColor(Color.BLACK);
                r.addView(t4);
                table.addView(r);

                i += 2;
            }

        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);
            finish();

            return true;
        }
    }

