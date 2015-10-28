package gan.keepsafe.atys;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import gan.keepsafe.R;

public class AtyLostFind extends AppCompatActivity {
    private SharedPreferences mSpref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSpref = getSharedPreferences("config", MODE_PRIVATE);
        boolean state = mSpref.getBoolean("configed", false);
        if(state){
            setContentView(R.layout.aty_lostfind);
        }else {
            startActivity(new Intent(AtyLostFind.this,AtySetup1.class));
            finish();
        }
    }


}
