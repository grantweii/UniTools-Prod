package unsw.uni_tools_prod;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;

public class ParseStarter extends Application {

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
    }
}
