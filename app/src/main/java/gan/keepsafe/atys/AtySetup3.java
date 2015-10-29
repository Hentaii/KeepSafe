package gan.keepsafe.atys;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import gan.keepsafe.R;

public class AtySetup3 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_setup3);
    }

    public void NextClick(View view) {
        startActivity(new Intent(AtySetup3.this, AtySetup4.class));
        finish();
        overridePendingTransition(R.anim.trans_in, R.anim.trans_out);
    }

    public void PreClick(View view) {
        startActivity(new Intent(AtySetup3.this, AtySetup2.class));
        finish();
        overridePendingTransition(R.anim.pre_trans_in, R.anim.pre_trans_out);
    }
}
