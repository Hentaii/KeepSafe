package gan.keepsafe.atys;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import gan.keepsafe.R;

public class AtyLostFind extends AppCompatActivity {
    private SharedPreferences mSpref;
    private TextView mTvPhoneNum;
    private ImageView mIvLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSpref = getSharedPreferences("config", MODE_PRIVATE);
        boolean state = mSpref.getBoolean("configed", false);
        boolean protect = mSpref.getBoolean("protect", false);
        if (state) {
            setContentView(R.layout.aty_lostfind);
            mIvLock = (ImageView) findViewById(R.id.iv_lock);
            mTvPhoneNum = (TextView) findViewById(R.id.tv_phone_num);
            String mPhoneNum = mSpref.getString("contact_phone", "");
            mTvPhoneNum.setText(mPhoneNum);
            if (protect) {
                mIvLock.setImageResource(R.drawable.lock);
            } else {
                mIvLock.setImageResource(R.drawable.unlock);
            }
        } else {
            startActivity(new Intent(AtyLostFind.this, AtySetup1.class));
            finish();
        }
    }

    public void Reset(View view) {
        startActivity(new Intent(AtyLostFind.this, AtySetup1.class));
        finish();
    }


}
