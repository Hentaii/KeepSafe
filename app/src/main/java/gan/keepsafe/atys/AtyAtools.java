package gan.keepsafe.atys;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import gan.keepsafe.R;

public class AtyAtools extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_atools);
    }

    public void numAddQuery(View view){
        startActivity(new Intent(AtyAtools.this,AtyAddress.class));
    }
}
