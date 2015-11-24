package gan.keepsafe.engine;


import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Debug;
import android.text.format.Formatter;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import gan.keepsafe.R;
import gan.keepsafe.bean.TaskInfo;

public class TaskInfoParser {

    public static List<TaskInfo> getTaskInfos(Context context) {
        PackageManager packageManager = context.getPackageManager();

        List<TaskInfo> TaskInfos = new ArrayList<TaskInfo>();

        // 获取到进程管理器
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(context.ACTIVITY_SERVICE);
        // 获取到手机上面所有运行的进程

        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();

        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : appProcesses){
            TaskInfo taskInfo = new TaskInfo();
            String processName = runningAppProcessInfo.processName;
            taskInfo.setPackageName(processName);
            // 获取到内存基本信息
            /*
             这个里面一共只有一个数据
             */
            Debug.MemoryInfo[] memoryInfo = activityManager.getProcessMemoryInfo(new
                    int[]{runningAppProcessInfo.pid});

            // 获取到总共弄脏多少内存(当前应用程序占用多少内存)
            //返回的是kb，通过*1024格式化数据大小
            int privateDirty = memoryInfo[0].getTotalPrivateDirty();
            taskInfo.setMemorySize(privateDirty*1024);
            try {
                //获取包信息
                PackageInfo packageInfo = packageManager.getPackageInfo(processName, 0);
                //获得图标
                Drawable icon = packageInfo.applicationInfo.loadIcon(packageManager);
                taskInfo.setIcon(icon);
                //获得名字
                CharSequence label = packageInfo.applicationInfo.loadLabel(packageManager);
                taskInfo.setAppName(label.toString());
                //判断是系统应用还是用户应用
                int flags = packageInfo.applicationInfo.flags;
                if ((flags & ApplicationInfo.FLAG_SYSTEM)!=1){
                    taskInfo.setUserApp(true);
                }else {
                    taskInfo.setUserApp(false);
                }
                TaskInfos.add(taskInfo);
            } catch (Exception e) {
                // 系统核心库里面有些系统没有图标。必须给一个默认的图标
                taskInfo.setAppName(processName);
                taskInfo.setIcon(context.getResources().getDrawable(R.drawable.ic_launcher,null));
                TaskInfos.add(taskInfo);
                e.printStackTrace();
            }
        }
        return TaskInfos;
    }
}
