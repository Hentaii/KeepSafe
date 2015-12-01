package gan.keepsafe.fragment;


import android.os.Bundle;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import gan.keepsafe.R;
import gan.keepsafe.bean.AppInfo;
import gan.keepsafe.db.AppLockDao;
import gan.keepsafe.engine.AppInfosParser;

import static gan.keepsafe.R.id.tv_unlock;

public class UnLockFragment extends Fragment {
    private View view;
    private ListView mListView;
    private TextView mTvUnlock;
    private List<AppInfo> appInfos;
    private AppLockDao dao;
    private ArrayList<AppInfo> unLockLists;
    private UnLockAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        view = View.inflate(getActivity(), R.layout.item_unlock_fragment, null);
        mListView = (ListView) view.findViewById(R.id.list_view);
        mTvUnlock = (TextView) view.findViewById(R.id.tv_unlock);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        appInfos = AppInfosParser.getAppInfos(getActivity());

        // 获取到程序锁的dao
        dao = new AppLockDao(getActivity());
        // 初始化一个没有加锁的集合
        unLockLists = new ArrayList<AppInfo>();

        for (AppInfo appInfo : appInfos) {
            // 判断当前的应用是否在程序所的数据里面
            if (dao.find(appInfo.getApkPackageName())) {
            } else {
                // 如果查询不到说明没有在程序锁的数据库里面
                unLockLists.add(appInfo);
            }
        }
        adapter = new UnLockAdapter();
        mListView.setAdapter(adapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private class UnLockAdapter extends BaseAdapter {

        private AppInfo appInfo;

        @Override
        public int getCount() {
            mTvUnlock.setText("未加锁(" + unLockLists.size() + ")个");
            return unLockLists.size();
        }

        @Override
        public Object getItem(int position) {
            return unLockLists.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final View view;
            ViewHolder viewHolder;
            if (convertView == null) {
                view = View.inflate(getActivity(), R.layout.item_unlock, null);
                viewHolder = new ViewHolder();
                viewHolder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
                viewHolder.tv_name = (TextView) view.findViewById(R.id.tv_name);
                viewHolder.iv_unlock = (ImageView) view.findViewById(R.id.iv_unlock);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }
            // 获取到当前的对象
            appInfo = unLockLists.get(position);
            viewHolder.iv_icon.setImageDrawable(unLockLists.get(position).getIcon());
            viewHolder.tv_name.setText(unLockLists.get(position).getApkName());
            // 把程序添加到程序锁数据库里面
            viewHolder.iv_unlock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 初始化一个位移动画
                    TranslateAnimation translateAnimation = new TranslateAnimation(
                            Animation.RELATIVE_TO_SELF, 0,
                            Animation.RELATIVE_TO_SELF, 1.0f,
                            Animation.RELATIVE_TO_SELF, 0,
                            Animation.RELATIVE_TO_SELF, 0);
                    // 设置动画时间
                    translateAnimation.setDuration(1000);
                    // 开始动画
                    view.startAnimation(translateAnimation);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            SystemClock.sleep(1000);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // 添加到数据库里面
                                    dao.add(unLockLists.get(position).getApkPackageName());
                                    // 从当前的页面移除对象
                                    unLockLists.remove(position);
                                    // 刷新界面
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }).start();
                }
            });
            return view;
        }

    }

    static class ViewHolder {
        ImageView iv_icon;
        TextView tv_name;
        ImageView iv_unlock;
    }
}
