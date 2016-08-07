package dstest.filesearch;

import android.app.Application;
import android.os.Bundle;

/**
 * Created by Administrator on 2016/8/5.
 */
public class MyApplication extends Application {

    private static MyApplication application;
    public static MyApplication getInstance(){
        return application;
    }
    @Override
    public void onCreate(){
        super.onCreate();

        application = this;
    }
}
