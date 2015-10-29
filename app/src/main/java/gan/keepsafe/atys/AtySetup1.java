package gan.keepsafe.atys;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import gan.keepsafe.R;

public class AtySetup1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_setup1);
    }

    public void Click(View view){
        startActivity(new Intent(AtySetup1.this,AtySetup2.class));
        finish();
        overridePendingTransition(R.anim.trans_in,R.anim.trans_out);
    }


}
