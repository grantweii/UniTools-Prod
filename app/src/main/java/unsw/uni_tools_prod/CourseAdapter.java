package unsw.uni_tools_prod;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.MyViewHolder>  {

    private Context context;
    private List<Course> courses;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView courseCodeTerm;
        public ImageButton deleteCourseButton;

        public MyViewHolder(View view) {
            super(view);
            courseCodeTerm = view.findViewById(R.id.courseCodeTermTextView);
            deleteCourseButton = view.findViewById(R.id.deleteCourseButton);

        }
    }

    public CourseAdapter(List<Course> courseList) {
        this.courses = courseList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_item, parent, false);
        context = parent.getContext();
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final Course course = courses.get(position);

        holder.courseCodeTerm.setText(course.getCodeTerm());

        holder.deleteCourseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you sure you want to remove " + course.getCodeTerm())
                            .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                removeCourse(course.getCodeTerm());
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog unfollowAlert = builder.create();
                unfollowAlert.show();
            }
        });
    }

    public void removeCourse(final String courseCodeTerm) {
        if (ParseUser.getCurrentUser().getList("followedCourses") != null) {
            List<String> followedCourses = ParseUser.getCurrentUser().getList("followedCourses");
            followedCourses.remove(courseCodeTerm);
            ParseUser.getCurrentUser().put("followedCourses", followedCourses);
        }

        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.i("course", "removed from user's followed list");
                    refreshDataSet(courseCodeTerm);
                } else {
                    Log.i("course", "failed to remove from user's followed list");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    public void refreshDataSet(String courseCodeTerm) {
        ListIterator<Course> iterator = courses.listIterator();
        while (iterator.hasNext()) {
            if (iterator.next().getCodeTerm().equals(courseCodeTerm)) {
                iterator.remove();
                notifyDataSetChanged();
            }
        }
    }

}
