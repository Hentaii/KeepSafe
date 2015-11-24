package gan.keepsafe.engine;


import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import gan.keepsafe.bean.AppInfo;

public class AppInfosParser {

    public static List<AppInfo> getAppInfos(Context context) {
        List<AppInfo> list = new ArrayList<AppInfo>();

        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        for (PackageInfo installedPackaged : packageInfos) {
            AppInfo appInfo = new AppInfo();
            Drawable icon = installedPackaged.applicationInfo.loadIcon(packageManager);
            String name = installedPackaged.applicationInfo.loadLabel(packageManager).toString();
            String packageName = installedPackaged.packageName;
            String dir = installedPackaged.applicationInfo.sourceDir;
            File file = new File(dir);
            long size = file.length();
            int flags = installedPackaged.applicationInfo.flags;
            if ((flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                appInfo.setUserApp(false);
            } else {
                appInfo.setUserApp(true);
            }

            if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0) {
                appInfo.setIsRom(false);
            } else {
                appInfo.setIsRom(true);
            }
            appInfo.setApkName(name);
            appInfo.setApkPackageName(packageName);
            appInfo.setApkSize(size);
            appInfo.setIcon(icon);
            list.add(appInfo);
        }
        return list;
    }

}

