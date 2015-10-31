package gan.keepsafe.atys;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import gan.keepsafe.R;

public class AtySetup4 extends AtyBaseSetup {

    private CheckBox mCbProtect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_setup4);
        mCbProtect = (CheckBox) findViewById(R.id.cb_protect);
        if (mSpref.getBoolean("protect", false)) {
            mCbProtect.setText("防盗保护已经开启");
            mCbProtect.setChecked(true);
        } else {
            mCbProtect.setText("防盗保护未开启");
            mCbProtect.setChecked(false);
        }

        mCbProtect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mCbProtect.setText("防盗保护已经开启");
                    mSpref.edit().putBoolean("protect", true).apply();
                } else {
                    mCbProtect.setText("防盗保护未开启");
                    mSpref.edit().putBoolean("protect", false).apply();
                }
            }
        });


    }


    @Override
    protected void ShowPrePage() {
        startActivity(new Intent(AtySetup4.this, AtySetup3.class));
        finish();
        overridePendingTransition(R.anim.pre_trans_in, R.anim.pre_trans_out);
    }

    @Override
    protected void ShowNextPage() {
        mSpref.edit().putBoolean("configed", true).apply();
        startActivity(new Intent(AtySetup4.this, AtyLostFind.class));
        finish();
        overridePendingTransition(R.anim.trans_in, R.anim.trans_out);
    }
}
