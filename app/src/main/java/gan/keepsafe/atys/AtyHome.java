package gan.keepsafe.atys;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import gan.keepsafe.MyConfig;
import gan.keepsafe.R;
import gan.keepsafe.utils.MD5Utils;

public class AtyHome extends AppCompatActivity {

    private GridView mGvList;
    private SharedPreferences mSpref;
    private String getPassword;

    private String[] mItem = new String[]{"手机防盗", "通讯卫士", "软件管理", "进程管理",
            "流量统计", "手机杀毒", "缓存清理", "高级工具", "设置中心"};
    private int[] mPics = new int[]{R.drawable.home_safe,
            R.drawable.home_callmsgsafe, R.drawable.home_apps,
            R.drawable.home_taskmanager, R.drawable.home_netmanager,
            R.drawable.home_trojan, R.drawable.home_sysoptimize,
            R.drawable.home_tools, R.drawable.home_settings};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_home);
        mGvList = (GridView) findViewById(R.id.gv_list);
        mSpref = getSharedPreferences("config", MODE_PRIVATE);
        mGvList.setAdapter(new AtyHomeAdapter(this, mItem, mPics));
        mGvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case MyConfig.HOME_SAFE: {
                        getPassword = mSpref.getString("password", null);
                        if (!TextUtils.isEmpty(getPassword)) {
                            showChaekPassWordDialog();
                        } else {
                            showSetPassWordDialog();
                        }
                        break;
                    }
                    case MyConfig.HOME_CALLMSGSAFE:{
                        startActivity(new Intent(AtyHome.this,AtyCallSafe.class));
                        break;
                    }
                    case MyConfig.HOME_APPS:{
                        startActivity(new Intent(AtyHome.this,AtyAppManager.class));
                        break;
                    }
                    case MyConfig.HOME_TOOLS: {
                        startActivity(new Intent(AtyHome.this, AtyAtools.class));
                        break;
                    }

                    case MyConfig.HOME_SETTINGS: {
                        startActivity(new Intent(AtyHome.this, AtySetting.class));
                        break;
                    }

                }
            }
        });
    }

    private void showChaekPassWordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(this, R.layout.dialog_cheak_password, null);
        dialog.setView(view, 0, 0, 0, 0);
        dialog.show();
        final EditText mEtPassword = (EditText) view.findViewById(R.id.et_password);
        Button mBtnComfirm = (Button) view.findViewById(R.id.btn_confirm);
        Button mBtnCancel = (Button) view.findViewById(R.id.btn_cancel);
        mBtnComfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MD5Utils.encode(mEtPassword.getText().toString()).equals(getPassword)) {
                    dialog.dismiss();
                    startActivity(new Intent(AtyHome.this, AtyLostFind.class));
                } else {
                    Toast.makeText(AtyHome.this, "密码错误,请重新输入", Toast.LENGTH_LONG).show();
                }
            }
        });
        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void showSetPassWordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(this, R.layout.dialog_set_password, null);
        dialog.setView(view, 0, 0, 0, 0);
        dialog.show();
        final EditText mEtPassword = (EditText) view.findViewById(R.id.et_password);
        final EditText mEtConfirmPassword = (EditText) view.findViewById(R.id.et_confirm_password);
        final Button mBtnConfirm = (Button) view.findViewById(R.id.btn_confirm);
        final Button mBtnCancel = (Button) view.findViewById(R.id.btn_cancel);

        mBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mPassword = mEtPassword.getText().toString();
                String mConfirmPassword = mEtConfirmPassword.getText().toString();
                if (!TextUtils.isEmpty(mPassword) && !TextUtils.isEmpty(mConfirmPassword)) {
                    if (mPassword.equals(mConfirmPassword)) {
                        mSpref.edit().putString("password", MD5Utils.encode(mPassword)).apply();
                        dialog.dismiss();
                        startActivity(new Intent(AtyHome.this, AtyLostFind.class));
                    } else {
                        Toast.makeText(AtyHome.this, "两次密码设置不一致，请重新设置", Toast.LENGTH_LONG).show();
                        mEtPassword.setText("");
                        mEtConfirmPassword.setText("");
                    }
                } else {
                    Toast.makeText(AtyHome.this, "密码不能为空", Toast.LENGTH_LONG).show();
                }

            }
        });

        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }


}
