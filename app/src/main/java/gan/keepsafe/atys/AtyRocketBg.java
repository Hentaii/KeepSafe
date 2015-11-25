package gan.keepsafe.atys;

import android.app.Activity;
import android.app.ActivityManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import gan.keepsafe.R;
import gan.keepsafe.bean.TaskInfo;
import gan.keepsafe.engine.TaskInfoParser;
import gan.keepsafe.utils.SystemInfoUtils;
import gan.keepsafe.utils.UIUtils;

public class AtyRocketBg extends Activity {
    private ImageView mIvTop;
    private ImageView mIvBottom;
    private List<TaskInfo> infos;
    private List<TaskInfo> mUserInfos;
    private List<TaskInfo> mSysInfos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDate();
        setContentView(R.layout.aty_rocket_bg);
        mIvTop = (ImageView) findViewById(R.id.iv_TopSmoke);
        mIvBottom = (ImageView) findViewById(R.id.iv_bottomSmoke);
        AlphaAnimation anim = new AlphaAnimation(0, 1);
        anim.setDuration(1000);
        anim.setFillAfter(true);

        mIvTop.startAnimation(anim);
        mIvBottom.startAnimation(anim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 1000);
    }

    public void initDate() {
        infos = TaskInfoParser.getTaskInfos(this);
        mUserInfos = new ArrayList<>();
        mSysInfos = new ArrayList<>();
        for (TaskInfo taskInfo : infos) {
            if (taskInfo.isUserApp()) {
                mUserInfos.add(taskInfo);
            } else {
                mSysInfos.add(taskInfo);
            }
        }
        clean();
    }

    // 清理进程
    public void clean() {
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
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
        UIUtils.showToast(
                AtyRocketBg.this,
                "共清理"
                        + totalCount
                        + "个进程,释放"
                        + Formatter.formatFileSize(AtyRocketBg.this,
                        killMem) + "内存");

    }


}
