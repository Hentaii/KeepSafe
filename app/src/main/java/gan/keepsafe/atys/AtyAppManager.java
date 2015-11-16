package gan.keepsafe.atys;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import gan.keepsafe.R;
import gan.keepsafe.bean.AppInfo;
import gan.keepsafe.engine.AppInfos;

public class AtyAppManager extends AppCompatActivity {

    private TextView mTvRom;
    private TextView mTvSd;
    private TextView mTvApp;
    private ListView mLv;

    private List<AppInfo> mAppinfos;
    private List<AppInfo> mUserInfos;
    private List<AppInfo> mSysInfos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_app_manager);
        initUI();
        initData();
    }

    private void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mAppinfos = AppInfos.getAppInfos(AtyAppManager.this);
                mUserInfos = new ArrayList<AppInfo>();
                mSysInfos = new ArrayList<AppInfo>();
                for (AppInfo appInfo : mAppinfos) {
                    if (appInfo.isUserApp()) {
                        mUserInfos.add(appInfo);
                    } else {
                        mSysInfos.add(appInfo);
                    }
                }
                handler.sendEmptyMessage(0);
            }
        }).start();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            AppManageAdapter adapter = new AppManageAdapter();
            mLv.setAdapter(adapter);
        }
    };

    private void initUI() {
        mTvRom = (TextView) findViewById(R.id.tv_rom);
        mTvSd = (TextView) findViewById(R.id.tv_sd);
        mLv = (ListView) findViewById(R.id.list_view);

        long RomSpace = Environment.getDataDirectory().getFreeSpace();
        long SdSpace = Environment.getExternalStorageDirectory().getFreeSpace();

        mTvRom.setText("内存可用为：" + Formatter.formatFileSize(this, RomSpace));
        mTvSd.setText("SD卡可用为：" + Formatter.formatFileSize(this, SdSpace));


    }


    private class AppManageAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mAppinfos.size();
        }

        @Override
        public Object getItem(int position) {
            return mAppinfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (position == 0) {
                TextView textView = new TextView(AtyAppManager.this);
                textView.setText("用户应用(" + mUserInfos.size() + ")个");
                textView.setTextColor(Color.WHITE);
                textView.setBackgroundColor(Color.GRAY);
                return textView;
            } else if (position == mUserInfos.size() + 1) {
                TextView textView = new TextView(AtyAppManager.this);
                textView.setText("系统应用(" + mSysInfos.size() + ")个");
                textView.setTextColor(Color.WHITE);
                textView.setBackgroundColor(Color.GRAY);
                return textView;
            }

            AppInfo appInfo;
            if (position < mUserInfos.size() + 1) {
                appInfo = mUserInfos.get(position - 1);
            } else {
                int location = mUserInfos.size() + 2;
                appInfo = mSysInfos.get(position - location);
            }
            ViewHolder holder;
            View view;
            if (convertView != null && convertView instanceof LinearLayout) {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            } else {
                view = View.inflate(AtyAppManager.this, R.layout.item_app_manager, null);
                holder = new ViewHolder();
                holder.mIvIcon = (ImageView) view.findViewById(R.id.iv_icon);
                holder.mTvApkSize = (TextView) view.findViewById(R.id.tv_apk_size);
                holder.mTvLocation = (TextView) view.findViewById(R.id.tv_location);
                holder.mTvName = (TextView) view.findViewById(R.id.tv_name);
                view.setTag(holder);
            }
            holder.mTvName.setText(appInfo.getApkName());
            holder.mTvApkSize.setText(Formatter.formatFileSize(AtyAppManager.this, appInfo
                    .getApkSize()));
            holder.mIvIcon.setImageDrawable(appInfo.getIcon());
            if (appInfo.isRom()) {
                holder.mTvLocation.setText("手机内存");
            } else {
                holder.mTvLocation.setText("外部存储");
            }
            return view;
        }

    }

    static class ViewHolder {
        private ImageView mIvIcon;
        private TextView mTvName;
        private TextView mTvApkSize;
        private TextView mTvLocation;
    }
}
