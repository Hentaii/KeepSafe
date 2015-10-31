package gan.keepsafe.atys;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import gan.keepsafe.R;

public class AtySetup3 extends AtyBaseSetup {
    private EditText mEtContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_setup3);
        mEtContact = (EditText) findViewById(R.id.et_contact);
        String mPhone = mSpref.getString("contact_phone", "");
        mEtContact.setText(mPhone);
    }

    public void ChoseContact(View view) {
        startActivityForResult(new Intent(AtySetup3.this, AtyContact.class), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            String mContactPhone = data.getStringExtra("phone");
            mEtContact.setText(mContactPhone);
            mSpref.edit().putString("contact_phone", mContactPhone).apply();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void ShowPrePage() {
        startActivity(new Intent(AtySetup3.this, AtySetup2.class));
        finish();
        overridePendingTransition(R.anim.pre_trans_in, R.anim.pre_trans_out);
    }

    @Override
    protected void ShowNextPage() {
        if (TextUtils.isEmpty(mEtContact.getText().toString())) {
            Toast.makeText(AtySetup3.this, "电话号码不能为空", Toast.LENGTH_SHORT).show();
        } else {
            startActivity(new Intent(AtySetup3.this, AtySetup4.class));
            finish();
            overridePendingTransition(R.anim.trans_in, R.anim.trans_out);
        }
    }
}
