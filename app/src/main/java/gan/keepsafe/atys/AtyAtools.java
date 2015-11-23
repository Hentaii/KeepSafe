package gan.keepsafe.atys;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import gan.keepsafe.R;
import gan.keepsafe.utils.SmsBackUpUtils;
import gan.keepsafe.utils.UIUtils;

public class AtyAtools extends Activity {

    private ProgressDialog pd;
    private ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_atools);
    }

    public void numAddQuery(View view) {
        startActivity(new Intent(AtyAtools.this, AtyAddress.class));
    }

    //短信备份
    public void backUp(View view) {
        pd = new ProgressDialog(AtyAtools.this);
        pd.setTitle("努力加载中");
        pd.setMessage("正在加载");
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.show();
        pb = (ProgressBar) findViewById(R.id.pb_backup);
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean result = SmsBackUpUtils.backUpSms(AtyAtools.this, new SmsBackUpUtils
                        .SmsBackUpCallBack() {
                    @Override
                    public void before(int count) {
                        pd.setMax(count);
                        pb.setMax(count);
                    }

                    @Override
                    public void onBackUpSms(int process) {
                        pd.setProgress(process);
                        pb.setProgress(process);
                    }
                });
                if (result) {
                    UIUtils.showToast(AtyAtools.this, "备份成功");
                } else {
                    UIUtils.showToast(AtyAtools.this, "备份失败");
                }
            }
        }).start();
        pd.dismiss();
    }

//建立手机联系人快捷方式
    public void setShortCut(View view){
        choseShortCutPerson();
    }

    public void choseShortCutPerson() {
        String[] items = new String[]{"手动输入", "从通讯录选择"};
        AlertDialog.Builder builder = new AlertDialog.Builder(AtyAtools.this);
        builder.setTitle("请选择添加的方式");
        builder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        dialog.dismiss();
                        Intent intent1 = new Intent(AtyAtools.this, AtyInputPhoneNum.class);
                        startActivityForResult(intent1, 0);
                        break;
                    case 1:
                        dialog.dismiss();
                        Intent intent = new Intent(AtyAtools.this, AtyContact.class);
                        startActivityForResult(intent, 0);
                        break;
                }

            }
        });
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            String mContactPhone = data.getStringExtra("phone");
            String mContactName = data.getStringExtra("name");
            setShortCut(mContactPhone, mContactName);
            UIUtils.showToast(AtyAtools.this, "快捷方式创建成功");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void setShortCut(String num, String name) {
        Intent intent = new Intent();
        intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");

        /**
         * 1  你想干什么事情
         * 2  叫什么名字
         * 3  长什么样子
         */
        Intent dowhtIntent = new Intent();
        //告诉系统哥想打电话
        dowhtIntent.setAction(Intent.ACTION_CALL);
        //给谁打电话
        dowhtIntent.setData(Uri.parse("tel://" + num));
        //叫什么名字
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
        //长什么样子
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON,
                BitmapFactory.decodeResource(getResources(), R.drawable.phone));

        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, dowhtIntent);
        sendBroadcast(intent);

    }
}
