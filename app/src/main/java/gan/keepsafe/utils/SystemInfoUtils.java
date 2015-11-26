package gan.keepsafe.utils;


import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.text.format.Formatter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import gan.keepsafe.atys.AtyRocketBg;
import gan.keepsafe.bean.TaskInfo;

public class SystemInfoUtils {

    //判断服务是否运行
    public static boolean isServiceRunning(Context context, String className) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServicesInfos = am.getRunningServices(200);
        for (ActivityManager.RunningServiceInfo info : runningServicesInfos) {
            String serviceClassName = info.service.getClassName();
            if (className.equals(serviceClassName)) {
                return true;
            }
        }
        return false;
    }

    //    返回进程总个数
    public static int getProcessCount(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //  获取到当前运行的进程
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am
                .getRunningAppProcesses();
        return runningAppProcesses.size();
    }

    //得到剩余内存
    public static long getAvailMem(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(memoryInfo);

        return memoryInfo.availMem;
    }


    //得到总内存大小
    public static long getTotalMem(Context context) {
        /*      只能用在API16以上
                memoryInfo.totalMem;
         /proc/meminfo 配置文件的路径
         */

        try {
            FileInputStream fs = new FileInputStream(new File("/proc/meminfo"));
            BufferedReader br = new BufferedReader(new InputStreamReader(fs));
            String readline = br.readLine();
            StringBuffer sb = new StringBuffer();
            for (char c : readline.toCharArray()) {
                if (c >= '0' && c <= '9') {
                    sb.append(c);
                }
            }
            return Long.parseLong(sb.toString()) * 1024;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // 清理进程
    public static String cleanMem(Context context,List<TaskInfo> infos) {
        List<TaskInfo> mUserInfos = new ArrayList<>();
        List<TaskInfo> mSysInfos = new ArrayList<>();
        for (TaskInfo taskInfo : infos) {
            if (taskInfo.isUserApp()) {
                mUserInfos.add(taskInfo);
            } else {
                mSysInfos.add(taskInfo);
            }
        }
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<TaskInfo> killInfos = new ArrayList<TaskInfo>();
        //清理的内存总数
        int totalCount = 0;
        //释放的总内存
        int killMem = 0;
        for (TaskInfo taskInfo : mUserInfos) {

            killInfos.add(taskInfo);
            totalCount++;
            killMem += taskInfo.getMemorySize();

        }
        for (TaskInfo taskInfo : mSysInfos) {

            killInfos.add(taskInfo);
            totalCount++;
            killMem += taskInfo.getMemorySize();
            // 杀死进程 参数表示包名
            activityManager.killBackgroundProcesses(taskInfo
                    .getPackageName());

        }
        for (TaskInfo taskInfo : killInfos) {
            if (taskInfo.isUserApp()) {
                mUserInfos.remove(taskInfo);
                infos.remove(taskInfo);
                activityManager.killBackgroundProcesses(taskInfo.getPackageName());
            } else {
                mSysInfos.remove(taskInfo);
                infos.remove(taskInfo);
                activityManager.killBackgroundProcesses(taskInfo.getPackageName());
            }
        }
        return "共清理"
                + totalCount
                + "个进程,释放"
                + Formatter.formatFileSize(context,
                killMem) + "内存";
//        UIUtils.showToast(
//                (Activity)context,
//                "共清理"
//                        + totalCount
//                        + "个进程,释放"
//                        + Formatter.formatFileSize(context,
//                        killMem) + "内存");

    }

}


