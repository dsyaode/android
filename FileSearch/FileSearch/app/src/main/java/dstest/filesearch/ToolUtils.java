package dstest.filesearch;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/5.
 */
public class ToolUtils {

    private static String getPackageNameByUser(String user, List<ActivityManager.RunningAppProcessInfo> ps){
        for (ActivityManager.RunningAppProcessInfo info : ps){
            int uid = info.uid;
            String str = String.valueOf(uid%1000);
            if(str.length() < 3){
                str = "0" + str;
            }
            if(user.contains(str)){
                return info.processName;
            }
        }
        return user;
    }

    public static List<AppFile> getCurAppFiles(List<ActivityManager.RunningAppProcessInfo> ps){
        List<String> commands = new ArrayList<>();
        for (ActivityManager.RunningAppProcessInfo info : ps){
            int pid = info.pid;
            String command = "ls -l /proc/" + pid + "/fd";
            commands.add(command);
        }

        List<AppFile> appFiles = new ArrayList<>();
        try {
            ShellUtils.CommandResult result = ShellUtils.execCommand(commands, true);
            String[] strs = result.successMsg.split(".{4}------");
            for (int i = 1 ; i < strs.length ; i=i+1){
                String[] proStrs = strs[i].split("\\s+");
                if(proStrs.length > 7){
                    String processName = proStrs[1];
                    String filePath = proStrs[7];
                    appFiles.add(new AppFile(getPackageNameByUser(processName, ps), filePath));
//                    Log.i(TAG, processName + "," + filePath);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return appFiles;
    }

    public static List<String> getFileCounts(File dirFile){
        List<String> paths = new ArrayList<>();
        File flist[] = dirFile.listFiles();
        paths.add(dirFile.getAbsolutePath());
        if(flist != null){
            for (int i = 0; i < flist.length; i++) {
                if (flist[i].isDirectory()) {
                    paths.addAll(getFileCounts(flist[i]));
                }else{
                    paths.add(flist[i].getAbsolutePath());
                }
            }
        }
        return paths;
    }

    /**
     * 判断某个应用程序是 不是三方的应用程序
     * @param info
     * @return 如果是第三方应用程序则返回true，如果是系统程序则返回false
     */
    public static boolean filterApp(ApplicationInfo info) {
        if ((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
            return true;
        } else if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
            return true;
        }
        return false;
    }

    public static List<ActivityManager.RunningAppProcessInfo> updateRunningAppInfo(Context context){
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        PackageManager pm = context.getPackageManager();
        List<ActivityManager.RunningAppProcessInfo> ps = new ArrayList<>();
        List<ActivityManager.RunningAppProcessInfo> processes = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : processes){
            try {
                PackageInfo packageInfo = pm.getPackageInfo(info.processName, 0);
                if(ToolUtils.filterApp(packageInfo.applicationInfo)){
//                    Log.i(TAG,"updateRunningAppInfo " + info.processName);
                    ps.add(info);
                }
            } catch (PackageManager.NameNotFoundException e) {
            }
        }
        return ps;
    }
}
