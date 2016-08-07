package dstest.filesearch;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileObserver;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/8/5.
 */
public class FileObsService extends Service {

    private static final String TAG = FileObsService.class.getSimpleName();

    public static final String ACTION_START_OBS_FILE = "start_obs_file";
    private List<FileObserver> obss;
    private boolean hasObs = false;

    private List<ActivityManager.RunningAppProcessInfo> ps;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){
        obss = new ArrayList<>();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){

        if(intent.getAction().equals(ACTION_START_OBS_FILE)){
            updateRunningAppInfo();
            createObs();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void updateRunningAppInfo(){
        ps = ToolUtils.updateRunningAppInfo(this);
    }

    private void createObs(){
        if(hasObs){
            return;
        }

        hasObs = true;
        String str = Environment.getExternalStorageDirectory().getPath();
        List<String> allPath = getScanPath(str);
        for (String path : allPath){
            FileObserver obs = startObs(path);
            obss.add(obs);
        }
    }

    private List<String> getScanPath(String path){
        List<String> files = ToolUtils.getFileCounts(new File(path));
        return files;
    }

    private FileObserver startObs(String path){
        Log.i(TAG, "startObs " + path);
        FileObserver obs = new MyFileObserver(path);
        obs.startWatching();
        return obs;
    }

    private class MyFileObserver extends FileObserver {

        String path;
        public MyFileObserver(String path) {
            super(path, FileObserver.CREATE );
            this.path = path;
        }
        @Override
        public void onEvent(int i, String s) {
            updatePath(path + "/" + s);
        }
    }

    private void addAppFileChangeData(AppFileChangeData data){
        DBAccessor.createOrUpdate(data);
        Intent intent = new Intent();
        intent.setAction(FileObsReceiver.ACTION_FILE_OBS);
        this.sendBroadcast(intent);
    }

    private void updatePath(String path){
        Log.i(TAG,"updatePath " + path);
        List<AppFile> appFiles = ToolUtils.getCurAppFiles(ps);
        for (AppFile file : appFiles){
            if(file.filepath.equals(path)){
                Log.i(TAG, "process " + file.processName + " create file " + path);
                addAppFileChangeData(new AppFileChangeData(file.processName, path, FileObserver.CREATE, System.currentTimeMillis()));
            }
        }
    }


}
