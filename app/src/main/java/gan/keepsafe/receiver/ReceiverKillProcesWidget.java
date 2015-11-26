package gan.keepsafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.List;

import gan.keepsafe.bean.TaskInfo;
import gan.keepsafe.engine.TaskInfoParser;
import gan.keepsafe.utils.ServiceStatusUtils;
import gan.keepsafe.utils.SystemInfoUtils;

public class ReceiverKillProcesWidget extends BroadcastReceiver {
    public ReceiverKillProcesWidget() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        List<TaskInfo> infos = TaskInfoParser.getTaskInfos(context);
        Toast.makeText(context,SystemInfoUtils.cleanMem(context,infos)
        ,Toast.LENGTH_SHORT).show();
    }
}
