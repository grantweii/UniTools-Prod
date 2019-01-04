package unsw.uni_tools_prod;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseInstallation;

public class ParseStarter extends Application {
    public static final String CHANNEL_ID = "channel";

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);

        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                .applicationId("6bff562969344fe047662c1bbb191ac5edbdd719")
                .clientKey("13f9f3814af3517969f743dce73a9b4f1ed21d03")
                .server("http://54.252.183.244:80/parse/")
                .build()
        );

        //ParseUser.enableAutomaticUser();

        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);

        ParseInstallation.getCurrentInstallation().saveInBackground();
        Log.i("parseStarter", "onCreate");

        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Enrolment Channel", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("asdf");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);

        }
    }
}
