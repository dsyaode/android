package dstest.filesearch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.FileObserver;

/**
 * Created by Administrator on 2016/8/5.
 */
public class FileObsReceiver extends BroadcastReceiver {

    public static final String ACTION_FILE_OBS = "action_file_obs";

    public interface FileObsListener{
        public void fileChange();
    }

    FileObsListener listener;

    public FileObsReceiver(FileObsListener listener){
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        this.listener.fileChange();
    }
}
