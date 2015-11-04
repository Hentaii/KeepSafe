package gan.keepsafe.atys;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import gan.keepsafe.R;
import gan.keepsafe.srv.SrvAddress;
import gan.keepsafe.utils.ServiceStatusUtils;
import gan.keepsafe.view.SettingItemView;

public class AtySetting extends AppCompatActivity {

    private SettingItemView mSiv_update;
    private SettingItemView mSiv_address;
    private SharedPreferences mSpref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_setting);
        mSpref = getSharedPreferences("config", MODE_PRIVATE);
        initUpdateView();
        /*来电的话使用TelephonyManager监听,去电的话使用receiver接受广播*/
        initAddressView();

    }

    public void initUpdateView() {
        mSiv_update = (SettingItemView) findViewById(R.id.siv_update);
        boolean state = mSpref.getBoolean("auto_update", true);
        if (state) {
            mSiv_update.setCheck(true);
        } else {
            mSiv_update.setCheck(false);
        }

        mSiv_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSiv_update.isChecked()) {
                    mSiv_update.setCheck(false);
                    mSpref.edit().putBoolean("auto_update", false).apply();
                } else {
                    mSiv_update.setCheck(true);
                    mSpref.edit().putBoolean("auto_update", true).apply();
                }
            }
        });
    }

    public void initAddressView() {
        mSiv_address = (SettingItemView) findViewById(R.id.siv_address);
        if (ServiceStatusUtils.isServiceRunning(AtySetting.this, "gan.keepsafe.srv.SrvAddress")) {
            mSiv_address.setCheck(true);
        } else {
            mSiv_address.setCheck(false);
        }
        mSiv_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSiv_address.isChecked()) {
                    mSiv_address.setCheck(false);
                    stopService(new Intent(AtySetting.this, SrvAddress.class));
                } else {
                    mSiv_address.setCheck(true);
                    startService(new Intent(AtySetting.this, SrvAddress.class));
                }
            }
        });
    }


}



