package gan.keepsafe.atys;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import gan.keepsafe.R;
import gan.keepsafe.db.AddressDao;

public class AtyAddress extends AppCompatActivity {


    private EditText mEtQueryNum;
    private TextView mTvResult;
    private Button mBtnQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_address);
        mEtQueryNum = (EditText) findViewById(R.id.et_query_num);
        mTvResult = (TextView) findViewById(R.id.tv_query_result);
        mBtnQuery = (Button) findViewById(R.id.btn_query);
        mBtnQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                query();
            }
        });

        mEtQueryNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                query();
            }
        });
    }


    public void query() {
        String result = mEtQueryNum.getText().toString();
        mTvResult.setText(AddressDao.getAddress(result));
    }

}
