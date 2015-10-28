package gan.keepsafe.atys;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import gan.keepsafe.R;
import gan.keepsafe.view.SettingItemView;

public class AtySetting extends AppCompatActivity {

    private SettingItemView mSiv_update;
    private SharedPreferences mSpref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_setting);
        mSpref = getSharedPreferences("config", MODE_PRIVATE);
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
                }else {
                    mSiv_update.setCheck(true);
                    mSpref.edit().putBoolean("auto_update", true).apply();
                }

            }
        });
    }


}
