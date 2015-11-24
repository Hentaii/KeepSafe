package gan.keepsafe.atys;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import gan.keepsafe.R;
import gan.keepsafe.bean.AppInfo;
import gan.keepsafe.bean.TaskInfo;
import gan.keepsafe.engine.TaskInfoParser;
import gan.keepsafe.utils.SystemInfoUtils;

public class AtyTaskManager extends AppCompatActivity {
    private TextView mTvProcessCount;
    private TextView mTvMemory;
    private ListView mLvApp;

    private List<TaskInfo> infos;
    private List<TaskInfo> mUserInfos;
    private List<TaskInfo> mSysInfos;
    private int processCount;
    private long availMem;
    private long totalMem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_task_manager);
        initUI();
        initData();
    }


    private void initUI() {
        mTvProcessCount = (TextView) findViewById(R.id.tv_task_process_count);
        mTvMemory = (TextView) findViewById(R.id.tv_task_memory);
        mLvApp = (ListView) findViewById(R.id.list_view);
        processCount = SystemInfoUtils.getProcessCount(AtyTaskManager.this);
        availMem = SystemInfoUtils.getAvailMem(AtyTaskManager.this);
        totalMem = SystemInfoUtils.getTotalMem(AtyTaskManager.this);
        mTvProcessCount.setText("运行中进程" + processCount + "个");
        mTvMemory.setText("剩余/总内存:" + Formatter.formatFileSize(AtyTaskManager.this, availMem) +
                "/" + Formatter.formatFileSize(AtyTaskManager.this, totalMem));

    }

    private void initData() {
        infos = TaskInfoParser.getTaskInfos(this);
        mUserInfos = new ArrayList<>();
        mSysInfos = new ArrayList<>();
        for (TaskInfo taskInfo : infos) {
            if (taskInfo.isUserApp()) {
                mUserInfos.add(taskInfo);
            } else {
                mSysInfos.add(taskInfo);
            }
        }
        MyAdapter adapter = new MyAdapter();
        mLvApp.setAdapter(adapter);
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return infos.size() + 2;
        }

        @Override
        public Object getItem(int position) {
            TaskInfo taskInfo;

            if (position == 0) {
                return null;
            } else if (position == mUserInfos.size() + 1) {
                return null;
            }
            if (position < mUserInfos.size() + 1) {
                taskInfo = mUserInfos.get(position - 1);
            } else {
                taskInfo = mSysInfos.get(position - 2 - mUserInfos.size());
            }
            return taskInfo;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (position == 0) {
                TextView textView = new TextView(AtyTaskManager.this);
                textView.setText("用户进程(" + mUserInfos.size() + ")");
                textView.setTextColor(Color.WHITE);
                textView.setBackgroundColor(Color.GRAY);
                return textView;
            } else if (position == mUserInfos.size() + 1) {
                TextView textView = new TextView(AtyTaskManager.this);
                textView.setText("系统进程(" + mSysInfos.size() + ")");
                textView.setTextColor(Color.WHITE);
                textView.setBackgroundColor(Color.GRAY);
                return textView;
            }
            final ViewHolder mHolder;
            View view;
            if (convertView != null && convertView instanceof LinearLayout) {
                view = convertView;
                mHolder = (ViewHolder) view.getTag();
            } else {
                view = View.inflate(AtyTaskManager.this, R.layout.list_item_task, null);
                mHolder = new ViewHolder();
                mHolder.mIcon = (ImageView) view.findViewById(R.id.iv_task_icon);
                mHolder.mTvName = (TextView) view.findViewById(R.id.tv_task_name);
                mHolder.mTvMemSize = (TextView) view.findViewById(R.id.tv_mem_size);
                mHolder.mCbStatus = (CheckBox) view.findViewById(R.id.cb_task_status);
                view.setTag(mHolder);
            }

            TaskInfo taskInfo;
            if (position < mUserInfos.size() + 1) {
                taskInfo = mUserInfos.get(position - 1);
            } else {
                int location = mUserInfos.size() + 2;
                taskInfo = mSysInfos.get(position - location);
            }

            mHolder.mIcon.setImageDrawable(taskInfo.getIcon());
            mHolder.mTvName.setText(taskInfo.getAppName());
            mHolder.mTvMemSize.setText("占用内存:" + Formatter.formatFileSize(AtyTaskManager.this,
                    taskInfo.getMemorySize()));


            if (taskInfo.isChecked()){
                mHolder.mCbStatus.setChecked(true);
            }else {
                mHolder.mCbStatus.setChecked(false);
            }

            return view;
        }

    }

    static class ViewHolder {
        ImageView mIcon;
        TextView mTvName;
        TextView mTvMemSize;
        CheckBox mCbStatus;
    }

}
