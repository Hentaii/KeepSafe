package gan.keepsafe.atys;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import gan.keepsafe.R;
import gan.keepsafe.srv.SrvKillProcess;
import gan.keepsafe.utils.ServiceStatusUtils;
import gan.keepsafe.utils.SharedPreferencesUtils;

public class AtyTaskManagerSetting extends AppCompatActivity {
    private CheckBox mCbStatus;
    private CheckBox mCbKillStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
    }

    private void initUI() {
        setContentView(R.layout.aty_task_manager_setting);
        mCbStatus = (CheckBox) findViewById(R.id.cb_status);
        mCbKillStatus = (CheckBox) findViewById(R.id.cb_status_kill_process);
        mCbStatus.setChecked(SharedPreferencesUtils.getBoolean(this, "is_show_system", false));
        mCbStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesUtils.saveBoolean(AtyTaskManagerSetting.this, "is_show_system",
                        isChecked);
            }
        });
        final Intent intent = new Intent(AtyTaskManagerSetting.this, SrvKillProcess.class);
        mCbKillStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    startService(intent);
                }else{
                    stopService(intent);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ServiceStatusUtils.isServiceRunning(AtyTaskManagerSetting.this,".srv.SrvKillProcess")){
            mCbKillStatus.setChecked(true);
        }else {
            mCbKillStatus.setChecked(false);
        }
    }
}
