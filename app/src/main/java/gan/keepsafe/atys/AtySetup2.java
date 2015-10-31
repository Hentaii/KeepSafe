package gan.keepsafe.atys;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import gan.keepsafe.R;
import gan.keepsafe.view.SettingItemView;

public class AtySetup2 extends AtyBaseSetup {


    private SettingItemView mSiv_bind;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_setup2);
        mSiv_bind = (SettingItemView) findViewById(R.id.siv_bind);
        boolean state = mSpref.getBoolean("sim_bind", false);
        if (state) {
            mSiv_bind.setCheck(true);
        } else {
            mSiv_bind.setCheck(false);
        }

        mSiv_bind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSiv_bind.isChecked()) {
                    mSiv_bind.setCheck(false);
                    mSpref.edit().putBoolean("sim_bind", false).apply();
                    mSpref.edit().remove("sim_num");
                } else {
                    mSiv_bind.setCheck(true);
                    mSpref.edit().putBoolean("sim_bind", true).apply();
                   /* TelephonyManager mTy = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                    String mTySimSerialNumber = mTy.getSimSerialNumber();
                    mSpref.edit().putString("sim_num", mTySimSerialNumber);
                    Log.d("LOG", "SIM号码为" + mTySimSerialNumber);*/
                }

            }
        });
    }


    @Override
    protected void ShowPrePage() {
        startActivity(new Intent(AtySetup2.this, AtySetup1.class));
        finish();
        overridePendingTransition(R.anim.pre_trans_in, R.anim.pre_trans_out);
    }

    @Override
    protected void ShowNextPage() {
        if (mSiv_bind.isChecked()) {
            startActivity(new Intent(AtySetup2.this, AtySetup3.class));
            finish();
            overridePendingTransition(R.anim.trans_in, R.anim.trans_out);
        }else {
            Toast.makeText(AtySetup2.this,"必须绑定SIM卡",Toast.LENGTH_SHORT).show();
        }
    }

}

