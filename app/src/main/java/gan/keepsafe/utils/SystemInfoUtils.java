package gan.keepsafe.utils;


import android.app.ActivityManager;
import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;

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

}


