package gan.keepsafe.atys;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;

import gan.keepsafe.MyConfig;
import gan.keepsafe.R;
import gan.keepsafe.db.AntivirusDao;
import gan.keepsafe.utils.MD5Utils;

public class AtyAntiVirusa extends AppCompatActivity {

    private ImageView mIvScan;
    private ProgressBar progressBar;
    private int maxSize;
    private PackageManager packageManager;
    private LinearLayout linearLayout;
    private Message message;
    private int process;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_anti_virusa);
        initData();
        initUi();
    }


    private void initUi() {
        mIvScan = (ImageView) findViewById(R.id.iv_scanning);
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        linearLayout = (LinearLayout) findViewById(R.id.ll_content);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        RotateAnimation am = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        am.setDuration(3000);
        am.setRepeatCount(Animation.INFINITE);
        mIvScan.setAnimation(am);
    }

    private void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                message = Message.obtain();
                message.what = MyConfig.HANDLE_START;
                process = 0;
                handler.sendMessage(message);
                packageManager = getPackageManager();
                List<PackageInfo> installedPackages = packageManager
                        .getInstalledPackages(0);
                maxSize = installedPackages.size();
                progressBar.setMax(maxSize);
                for (PackageInfo infos : installedPackages) {
                    ScanInfo scanInfo = new ScanInfo();
                    String appName = infos.applicationInfo.loadLabel(packageManager).toString();
                    String packageName = infos.packageName;
                    scanInfo.packageName = packageName;
                    scanInfo.appName = appName;
                    String sourceDir = infos.applicationInfo.sourceDir;
                    // 获取到文件的md5
                    String md5 = MD5Utils.getFileMd5(sourceDir);
                    // 判断当前的文件是否是病毒数据库里面
                    String desc = AntivirusDao.checkFileVirus(md5);
                    if (desc == null) {
                        scanInfo.desc = false;
                    } else {
                        scanInfo.desc = true;
                    }
                    process++;
                    progressBar.setProgress(process);
                    Message message = Message.obtain();
                    message.what = MyConfig.HANDLE_SCAN;
                    message.obj = scanInfo;
                    handler.sendMessage(message);
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Message message = Message.obtain();
                message.what = MyConfig.HANDLE_END;
                handler.sendMessage(message);
            }
        }).start();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MyConfig.HANDLE_START:
                    break;
                case MyConfig.HANDLE_SCAN:
                    ScanInfo info = (ScanInfo) msg.obj;
                    TextView child = new TextView(AtyAntiVirusa.this);
                    if (info.desc) {
                        child.setTextColor(Color.RED);
                        child.setText(info.appName + "有病毒");
                    } else {
                        child.setTextColor(Color.BLACK);
                        child.setText(info.appName + "扫描安全");
                        linearLayout.addView(child);
                        scrollView.post(new Runnable() {
                            @Override
                            public void run() {
                                scrollView.fullScroll(View.FOCUS_DOWN);
                            }
                        });
                        break;
                    }
                case MyConfig.HANDLE_END:
                    mIvScan.clearAnimation();
                    break;
            }
        }
    };

    static class ScanInfo {
        private boolean desc;
        private String appName;
        private String packageName;
    }

}
