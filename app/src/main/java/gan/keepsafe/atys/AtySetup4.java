package gan.keepsafe.atys;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import gan.keepsafe.R;

public class AtySetup4 extends AppCompatActivity {
    private SharedPreferences mSpref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_setup4);
        mSpref = getSharedPreferences("config", MODE_PRIVATE);
    }

    public void NextClick(View view) {
        mSpref.edit().putBoolean("configed",true).apply();
        startActivity(new Intent(AtySetup4.this, AtyLostFind.class));
        finish();
    }

    public void PreClick(View view) {
        startActivity(new Intent(AtySetup4.this, AtySetup3.class));
        finish();
    }
}
