package gan.keepsafe.srv;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.text.format.Formatter;
import android.widget.RemoteViews;

import java.util.Timer;
import java.util.TimerTask;

import gan.keepsafe.R;
import gan.keepsafe.receiver.ReceiverAppWidget;
import gan.keepsafe.utils.SystemInfoUtils;

public class SrvKillProcesWidget extends Service {

    private AppWidgetManager manager;

    public SrvKillProcesWidget() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Timer timer = new Timer();
        manager = AppWidgetManager.getInstance(this);
        final ComponentName provider = new ComponentName(getApplicationContext(),
                ReceiverAppWidget.class);
        /*
         * 初始化一个远程的view
         * Remote 远程
         */
        final RemoteViews views = new RemoteViews(getPackageName(), R.layout.process_widget);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                /*
                 * 需要注意。这个里面findingviewyid这个方法
                 * 设置当前文本里面一共有多少个进程
                 */
                int processCount = SystemInfoUtils.getProcessCount(getApplicationContext());
                //设置文本
                views.setTextViewText(R.id.process_count,"正在运行的软件:" + String.valueOf(processCount));
                //获取到当前手机上面的可用内存
                long availMem = SystemInfoUtils.getAvailMem(getApplicationContext());

                views.setTextViewText(R.id.process_memory, "可用内存:" + Formatter.formatFileSize
                        (getApplicationContext(), availMem));
                Intent intent = new Intent();
                intent.setAction("gan.keepsafe.receiver");
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0,
                        intent, 0);

                views.setOnClickPendingIntent(R.id.btn_clear,pendingIntent);
                manager.updateAppWidget(provider,views);
            }
        };
        timer.schedule(timerTask, 0, 3000);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
