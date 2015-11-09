package gan.keepsafe.atys;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import gan.keepsafe.R;
import gan.keepsafe.srv.SrvAddress;
import gan.keepsafe.utils.ServiceStatusUtils;
import gan.keepsafe.view.SettingClickView;
import gan.keepsafe.view.SettingItemView;

public class AtySetting extends AppCompatActivity {

    private SettingItemView mSiv_update;
    private SettingItemView mSiv_address;
    private SettingClickView mScv_address_style;
    private SharedPreferences mSpref;
    private int address_style;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_setting);
        mSpref = getSharedPreferences("config", MODE_PRIVATE);
        initUpdateView();
        /*来电的话使用TelephonyManager监听,去电的话使用receiver接受广播*/
        initAddressView();
        initAddressStyle();
        initAddressLocation();
    }

    private void initAddressLocation() {
        SettingClickView mScv_address_location = (SettingClickView) findViewById(R.id.scv_address_location);
        mScv_address_location.setDesc("进入可以选择提示框的位置");
        mScv_address_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AtySetting.this, AtyDrawAddress.class));
            }
        });
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

    final String[] items = new String[]{"半透明", "活力橙", "卫士蓝", "金属灰", "苹果绿"};

    public void initAddressStyle() {
        mScv_address_style = (SettingClickView) findViewById(R.id.scv_address_style);
        address_style = mSpref.getInt("address_style", 0);
        mScv_address_style.setDesc(items[address_style]);
        mScv_address_style.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AtySetting.this,R.style.DialogFullScrenn);
                address_style = mSpref.getInt("address_style", 0);
                builder.setTitle("请选择提示框的风格");
                builder.setSingleChoiceItems(items, address_style, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mSpref.edit().putInt("address_style", which).apply();
                        mScv_address_style.setDesc(items[which]);
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("取消选择", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });
    }
}



