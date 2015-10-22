package gan.keepsafe.atys;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import gan.keepsafe.R;
import gan.keepsafe.utils.StreamUtils;

public class AtyGuide extends AppCompatActivity {
    private static final int UPDATE_DIALOG = 1;
    private static final int ENTER_HOME = 2;
    private static final int JSON_ERROR = 3;
    private static final int URL_ERROR = 4;
    private static final int NET_ERROR = 5;
    private TextView mTvVersion;
    private String mVersionName;
    private int mVersionCode;
    private String mDescription;
    private String mDownloadUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aty_guide);
        mTvVersion = (TextView) findViewById(R.id.tv_ver);
        mTvVersion.append(getVersionName());
        cheakVersion();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_DIALOG: {
                    showDialog();
                    break;
                }
                case JSON_ERROR: {
                    Toast.makeText(AtyGuide.this, "Json has wrong", Toast.LENGTH_LONG).show();
                    enterHome();
                    break;
                }
                case URL_ERROR: {
                    Toast.makeText(AtyGuide.this, "URL has wrong", Toast.LENGTH_LONG).show();
                    enterHome();
                    break;
                }
                case NET_ERROR: {
                    Toast.makeText(AtyGuide.this, "Net has wrong", Toast.LENGTH_LONG).show();
                    enterHome();
                    break;
                }

            }


        }
    };

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
        builder.show();
    }

    private void enterHome() {
        Intent intent = new Intent(this,AtyHome.class);
        startActivity(intent);
        finish();
    }

    private void download() {
        
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
                    URL url = new URL("http://113.251.160.166:8080/update.json");
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
                            if (mVersionCode > getVersionCode()) {
                                msg.what = UPDATE_DIALOG;
                            } else {
                                msg.what = ENTER_HOME;
                            }
                        } catch (JSONException e) {
                            msg.what = JSON_ERROR;
                            e.printStackTrace();
                        }
                    }

                } catch (MalformedURLException e) {
                    msg.what = URL_ERROR;
                    e.printStackTrace();
                } catch (IOException e) {
                    msg.what = NET_ERROR;
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

}
