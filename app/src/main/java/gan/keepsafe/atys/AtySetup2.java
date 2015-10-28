package gan.keepsafe.atys;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import gan.keepsafe.R;
import gan.keepsafe.view.SettingItemView;

public class AtySetup2 extends AppCompatActivity {


    private SettingItemView mSiv_bind;
    private SharedPreferences mSpref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_setup2);
        mSpref = getSharedPreferences("config", MODE_PRIVATE);
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
                } else {
                    mSiv_bind.setCheck(true);
                    mSpref.edit().putBoolean("sim_bind", true).apply();
                }

            }
        });
    }

    public void NextClick(View view) {
        startActivity(new Intent(AtySetup2.this, AtySetup3.class));
        finish();
    }

    public void PreClick(View view) {
        startActivity(new Intent(AtySetup2.this, AtySetup1.class));
        finish();
    }

}

