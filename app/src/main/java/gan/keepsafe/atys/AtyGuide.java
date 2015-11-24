package gan.keepsafe.atys;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import gan.keepsafe.MyConfig;
import gan.keepsafe.R;
import gan.keepsafe.srv.SrvLocation;
import gan.keepsafe.srv.SrvRocket;
import gan.keepsafe.utils.StreamUtils;

public class AtyGuide extends AppCompatActivity {

    private RelativeLayout mRlGuide;
    private SharedPreferences mSpref;
    private TextView mTvVersion;
    private TextView mTvProgress;
    private String mVersionName;
    private int mVersionCode;
    private String mDescription;
    private String mDownloadUrl;
    private AlertDialog.Builder builder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_guide);
        createShortCut(AtyGuide.this);
        mTvVersion = (TextView) findViewById(R.id.tv_ver);
        mTvProgress = (TextView) findViewById(R.id.tv_progress);
        mRlGuide = (RelativeLayout) findViewById(R.id.rl_guide);
        mTvVersion.append(getVersionName());
        mSpref = getSharedPreferences("config", MODE_PRIVATE);
        copyDB("address.db");
        boolean state = mSpref.getBoolean("auto_update", true);
        if (state) {
            cheakVersion();
        } else {
            mHandler.sendEmptyMessageDelayed(MyConfig.ENTER_HOME, 2000);
        }
        AlphaAnimation anim = new AlphaAnimation(0.3f, 1f);
        anim.setDuration(3000);
        mRlGuide.startAnimation(anim);
    }

    private MyHandler mHandler = new MyHandler(this);


    private void showDialog() {
        builder = new AlertDialog.Builder(this);
        builder.setTitle("最新版本" + mVersionCode);
        builder.setMessage(mDescription);
        builder.setPositiveButton("立刻更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "开始更新", Toast.LENGTH_LONG).show();
                download();
            }
        });
        builder.setNegativeButton("暂不更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                enterHome();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                enterHome();
            }
        });
        builder.show();
    }

    private void enterHome() {
        Intent intent = new Intent(this, AtyHome.class);
        startActivity(intent);
        finish();
    }

    private void download() {

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String target = Environment.getExternalStorageDirectory() + "/update.apk";
            mTvProgress.setVisibility(View.VISIBLE);
            HttpUtils httpUtils = new HttpUtils();
            httpUtils.download(mDownloadUrl, target, new RequestCallBack<File>() {
                @Override
                public void onLoading(long total, long current, boolean isUploading) {
                    super.onLoading(total, current, isUploading);
                    mTvProgress.setText("下载进度" + current / total * 100 + "%");
                }

                @Override
                public void onSuccess(ResponseInfo<File> responseInfo) {
                    Toast.makeText(AtyGuide.this, "下载成功", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.setDataAndType(Uri.fromFile(responseInfo.result),
                            "application/vnd.android.package-archive");
                    startActivityForResult(intent, 0);// 如果用户取消安装的话,会返回结果,回调方法onActivityResult
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    Toast.makeText(AtyGuide.this, "失败", Toast.LENGTH_LONG).show();
                    mTvProgress.setVisibility(View.INVISIBLE);
                }
            });
        } else {
            Toast.makeText(AtyGuide.this, "未挂载SD卡", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        enterHome();
        super.onActivityResult(requestCode, resultCode, data);
    }

    private String getVersionName() {
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    private int getVersionCode() {
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void cheakVersion() {
        final Long mStartTime = System.currentTimeMillis();
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn = null;
                Message msg = Message.obtain();
                try {
                    URL url = new URL(MyConfig.JSON_URL);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(5000);
                    conn.setReadTimeout(5000);
                    conn.connect();
                    int respon = conn.getResponseCode();
                    if (respon == 200) {
                        InputStream inputStream = conn.getInputStream();
                        String result = StreamUtils.readFromStream(inputStream);
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            mVersionName = jsonObject.getString("versionName");
                            mVersionCode = jsonObject.getInt("versionCode");
                            mDescription = jsonObject.getString("description");
                            mDownloadUrl = jsonObject.getString("downloadUrl");
                            if (mVersionCode - getVersionCode() > 0) {
                                msg.what = MyConfig.UPDATE_DIALOG;
                            } else {
                                msg.what = MyConfig.ENTER_HOME;
                            }
                        } catch (JSONException e) {
                            msg.what = MyConfig.JSON_ERROR;
                            e.printStackTrace();
                        }
                    }

                } catch (MalformedURLException e) {
                    msg.what = MyConfig.URL_ERROR;
                    e.printStackTrace();
                } catch (IOException e) {
                    msg.what = MyConfig.NET_ERROR;
                    e.printStackTrace();
                } finally {
                    Long mEndTime = System.currentTimeMillis();
                    Long mUsedTime = mEndTime - mStartTime;
                    if (mUsedTime < 2000) {
                        try {
                            Thread.sleep(2000 - mUsedTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    mHandler.sendMessage(msg);
                    if (conn != null) {
                        conn.disconnect();
                    }
                }
            }
        }).start();

    }

    public void createShortCut(Context context) {
        final Intent addIntent = new Intent(
                "com.android.launcher.action.INSTALL_SHORTCUT");
        //final Parcelable icon = Intent.ShortcutIconResource.fromContext(
        //      context, context.getApplicationInfo().icon); // 获取快捷键的图标
//      如果为ture，可重复创建快捷图标
        addIntent.putExtra("duplicate", false);
        final Intent myIntent = new Intent(context,
                AtyGuide.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME,
                context.getApplicationInfo().name);// 快捷方式的标题
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, context.getApplicationInfo()
                .icon);// 快捷方式的图标
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, myIntent);// 快捷方式的动作
        context.sendBroadcast(addIntent);
    }

    static class MyHandler extends Handler {
        WeakReference<AtyGuide> mActivity;

        MyHandler(AtyGuide activity) {
            mActivity = new WeakReference<AtyGuide>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            AtyGuide theActivity = mActivity.get();
            switch (msg.what) {
                case MyConfig.ENTER_HOME: {
                    theActivity.enterHome();
                    break;
                }

                case MyConfig.UPDATE_DIALOG: {
                    theActivity.showDialog();
                    break;
                }
                case MyConfig.JSON_ERROR: {
                    Toast.makeText(theActivity.getApplicationContext(), "Json has wrong", Toast
                            .LENGTH_LONG).show();
                    theActivity.enterHome();
                    break;
                }
                case MyConfig.URL_ERROR: {
                    Toast.makeText(theActivity.getApplicationContext(), "URL has wrong", Toast
                            .LENGTH_LONG).show();
                    theActivity.enterHome();
                    break;
                }
                case MyConfig.NET_ERROR: {
                    Toast.makeText(theActivity.getApplicationContext(), "Net has wrong", Toast
                            .LENGTH_LONG).show();
                    theActivity.enterHome();
                    break;
                }

            }
        }
    }

    public void copyDB(String name) {
        File file = new File(getFilesDir(), name);
        if (file.exists()) {
            Log.d("LOG", "已经存在");
            return;
        }
        InputStream in = null;
        FileOutputStream out = null;
        try {
            in = getAssets().open(name);
            out = new FileOutputStream(file);

            int len = 0;
            byte[] buffer = new byte[1024];

            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

}


