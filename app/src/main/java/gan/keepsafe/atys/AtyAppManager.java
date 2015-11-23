package gan.keepsafe.atys;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import gan.keepsafe.R;
import gan.keepsafe.bean.AppInfo;
import gan.keepsafe.engine.AppInfos;

public class AtyAppManager extends AppCompatActivity implements View.OnClickListener {

    private TextView mTvRom;
    private TextView mTvSd;
    private TextView mTvApp;
    private ListView mLv;

    private List<AppInfo> mAppinfos;
    private List<AppInfo> mUserInfos;
    private List<AppInfo> mSysInfos;
    private AppInfo mClickAppinfo;
    private PopupWindow mPopupWindow;
    private AppManageAdapter adapter;

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
            adapter = new AppManageAdapter();
            mLv.setAdapter(adapter);
        }
    };

    private void initUI() {
        mTvRom = (TextView) findViewById(R.id.tv_rom);
        mTvSd = (TextView) findViewById(R.id.tv_sd);
        mLv = (ListView) findViewById(R.id.list_view);
        mTvApp = (TextView) findViewById(R.id.tv_app);

        long RomSpace = Environment.getDataDirectory().getFreeSpace();
        long SdSpace = Environment.getExternalStorageDirectory().getFreeSpace();

        mTvRom.setText("内存可用为：" + Formatter.formatFileSize(this, RomSpace));
        mTvSd.setText("SD卡可用为：" + Formatter.formatFileSize(this, SdSpace));

        mLv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                 int totalItemCount) {
                PopWindowDissmiss();
                if (mSysInfos != null && mUserInfos != null) {
                    if (firstVisibleItem > mUserInfos.size() + 1) {
                        mTvApp.setText("系统应用(" + mSysInfos.size() + ")");
                    } else {
                        mTvApp.setText("用户应用(" + mUserInfos.size() + ")");
                    }
                }
            }
        });

        mLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object object = mLv.getItemAtPosition(position);
                if (object != null && object instanceof AppInfo) {
                    mClickAppinfo = (AppInfo) object;
                    View ContentView = View.inflate(AtyAppManager.this, R.layout.item_popup, null);
                    LinearLayout ll_start = (LinearLayout) ContentView.findViewById(R.id.ll_start);
                    LinearLayout ll_uninstall = (LinearLayout) ContentView.findViewById(R.id
                            .ll_uninstall);
                    LinearLayout ll_share = (LinearLayout) ContentView.findViewById(R.id.ll_share);
                    LinearLayout ll_detail = (LinearLayout) ContentView.findViewById(R.id
                            .ll_detail);

                    ll_start.setOnClickListener(AtyAppManager.this);
                    ll_uninstall.setOnClickListener(AtyAppManager.this);
                    ll_share.setOnClickListener(AtyAppManager.this);
                    ll_detail.setOnClickListener(AtyAppManager.this);

                    PopWindowDissmiss();


                    mPopupWindow = new PopupWindow(ContentView, ViewGroup.LayoutParams
                            .WRAP_CONTENT, ViewGroup
                            .LayoutParams.WRAP_CONTENT);
                    mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    int[] location = new int[2];
                    view.getLocationInWindow(location);
                    mPopupWindow.showAtLocation(view, Gravity.LEFT + Gravity.TOP, 70, location[1]);
                    ScaleAnimation sa = new ScaleAnimation(0.5f, 1.0f, 0.5f, 1.0f, Animation
                            .RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    sa.setDuration(300);
                    ContentView.setAnimation(sa);
                }
            }
        });
    }

    private void PopWindowDissmiss() {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_share:
                Intent share_localIntent = new Intent("android.intent.action.SEND");
                share_localIntent.setType("text/plain");
                share_localIntent.putExtra("android.intent.extra.SUBJECT", "f分享");
                share_localIntent.putExtra("android.intent.extra.TEXT",
                        "Hi！推荐您使用软件：" + mClickAppinfo.getApkName() + "下载地址:" + "https://play" +
                                ".google.com/store/apps/details?id=" + mClickAppinfo
                                .getApkPackageName());
                this.startActivity(Intent.createChooser(share_localIntent, "分享"));
                PopWindowDissmiss();
                break;

            case R.id.ll_start:
                Intent start_localIntent = this.getPackageManager().getLaunchIntentForPackage
                        (mClickAppinfo.getApkPackageName());
                this.startActivity(start_localIntent);
                PopWindowDissmiss();
                break;

            case R.id.ll_detail:
                Intent detail_intent = new Intent();
                detail_intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                detail_intent.addCategory(Intent.CATEGORY_DEFAULT);
                detail_intent.setData(Uri.parse("package:" + mClickAppinfo.getApkPackageName()));
                startActivity(detail_intent);
                PopWindowDissmiss();
                break;

            case R.id.ll_uninstall:
                Intent uninstall_localIntent = new Intent("android.intent.action.DELETE", Uri
                        .parse("package:" + mClickAppinfo.getApkPackageName()));
                startActivity(uninstall_localIntent);
                PopWindowDissmiss();
                if (mClickAppinfo.isUserApp()) {
                    mUserInfos.remove(mClickAppinfo);
                } else {
                    mSysInfos.remove(mClickAppinfo);
                }
                mAppinfos.remove(mClickAppinfo);
                adapter.notifyDataSetChanged();
                break;
        }

    }

    private class AppManageAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mAppinfos.size() + 2;
        }

        @Override
        public Object getItem(int position) {
            AppInfo appInfo;
            if (position == 0) {
                return null;
            } else if (position == mUserInfos.size() + 1) {
                return null;
            }
            if (position < mUserInfos.size() + 1) {
                appInfo = mUserInfos.get(position - 1);
            } else {
                appInfo = mSysInfos.get(position - 2 - mUserInfos.size());
            }
            return appInfo;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (position == 0) {
                TextView textView = new TextView(AtyAppManager.this);
                textView.setText("用户应用(" + mUserInfos.size() + ")");
                textView.setTextColor(Color.WHITE);
                textView.setBackgroundColor(Color.GRAY);
                return textView;
            } else if (position == mUserInfos.size() + 1) {
                TextView textView = new TextView(AtyAppManager.this);
                textView.setText("系统应用(" + mSysInfos.size() + ")");
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
            //因为存在特殊的item，所以要这样写，否则会出现holder不能重用的情况
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
