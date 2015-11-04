package gan.keepsafe.utils;


import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

public class ServiceStatusUtils {
    public static boolean isServiceRunning(Context ctx, String srv) {
        ActivityManager mAm = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices = mAm.getRunningServices(100);
        for (ActivityManager.RunningServiceInfo runningServiceInfo : runningServices) {
            String srvName = runningServiceInfo.service.getClassName();
            if (srvName.equals(srv)) {
                return true;
            }
        }
        return false;
    }
}
