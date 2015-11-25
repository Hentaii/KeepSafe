package gan.keepsafe.atys;

import android.app.ActivityManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import gan.keepsafe.utils.UIUtils;

public class AtyTaskManager extends AppCompatActivity {
    private TextView mTvProcessCount;
    private TextView mTvMemory;
    private ListView mLvTask;

    private List<TaskInfo> infos;
    private List<TaskInfo> mUserInfos;
    private List<TaskInfo> mSysInfos;
    private int processCount;
    private long availMem;
    private long totalMem;
    private MyAdapter adapter;

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
        mLvTask = (ListView) findViewById(R.id.list_view);
        processCount = SystemInfoUtils.getProcessCount(AtyTaskManager.this);
        availMem = SystemInfoUtils.getAvailMem(AtyTaskManager.this);
        totalMem = SystemInfoUtils.getTotalMem(AtyTaskManager.this);
        mTvProcessCount.setText("运行中进程" + processCount + "个");
        mTvMemory.setText("剩余/总内存:" + Formatter.formatFileSize(AtyTaskManager.this, availMem) +
                "/" + Formatter.formatFileSize(AtyTaskManager.this, totalMem));
        mLvTask.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object object = mLvTask.getItemAtPosition(position);
                if (object != null && object instanceof TaskInfo) {
                    TaskInfo taskInfo;
                    ViewHolder holder = (ViewHolder) view.getTag();
                    taskInfo = (TaskInfo) object;
                    if (taskInfo.isChecked()) {
                        taskInfo.setChecked(false);
                        holder.mCbStatus.setChecked(false);
                    } else {
                        taskInfo.setChecked(true);
                        holder.mCbStatus.setChecked(true);
                    }
                }

            }
        });

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
        adapter = new MyAdapter();
        mLvTask.setAdapter(adapter);
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
                int location = position - 1 - mUserInfos.size() - 1;
                taskInfo = mSysInfos.get(location);
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
                // 第0个位置显示的应该是 用户程序的个数的标签。
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
                int location = position - 1 - mUserInfos.size() - 1;
                Log.d("Log","position==========>"+position);
                Log.d("Log","location==========>"+location);
                taskInfo = mSysInfos.get(location);
            }

            mHolder.mIcon.setImageDrawable(taskInfo.getIcon());
            mHolder.mTvName.setText(taskInfo.getAppName());
            mHolder.mTvMemSize.setText("占用内存:" + Formatter.formatFileSize(AtyTaskManager.this,
                    taskInfo.getMemorySize()));


            if (taskInfo.isChecked()) {
                mHolder.mCbStatus.setChecked(true);
            } else {
                mHolder.mCbStatus.setChecked(false);
            }
            //判断当前展示的item是否是自己的程序。如果是。就把程序给隐藏
            if (taskInfo.getPackageName().equals(getPackageName())) {
                mHolder.mCbStatus.setVisibility(View.INVISIBLE);
            } else {
                mHolder.mCbStatus.setVisibility(View.VISIBLE);
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


    //    全选进程
    public void choseAll(View view) {

        for (TaskInfo taskInfo : mUserInfos) {
            // 判断当前的用户程序是不是自己的程序。如果是自己的程序。那么就把文本框隐藏
            if (taskInfo.getPackageName().equals(getPackageName())) {
                continue;
            }
            taskInfo.setChecked(true);
        }
        for (TaskInfo taskInfo : mSysInfos) {
            taskInfo.setChecked(true);
        }
        adapter.notifyDataSetChanged();
    }

    //    反选进程
    public void opposeChose(View view) {
        for (TaskInfo taskInfo : mUserInfos) {
            if (taskInfo.isChecked()) {
                // 判断当前的用户程序是不是自己的程序。如果是自己的程序。那么就把文本框隐藏
                if (taskInfo.getPackageName().equals(getPackageName())) {
                    continue;
                }
                taskInfo.setChecked(false);
            } else {
                taskInfo.setChecked(true);
            }
        }
        for (TaskInfo taskInfo : mSysInfos) {
            if (taskInfo.isChecked()) {
                taskInfo.setChecked(false);
            } else {
                taskInfo.setChecked(true);
            }
        }
        adapter.notifyDataSetChanged();
    }

    // 清理进程
    public void clean(View view) {
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<TaskInfo> killInfos = new ArrayList<TaskInfo>();
        //清理的内存总数
        int totalCount = 0;
        //释放的总内存
        int killMem = 0;
        for (TaskInfo taskInfo : mUserInfos) {
            if (taskInfo.isChecked()) {
                killInfos.add(taskInfo);
                totalCount++;
                killMem += taskInfo.getMemorySize();
            }
        }
        for (TaskInfo taskInfo : mSysInfos) {
            if (taskInfo.isChecked()) {
                killInfos.add(taskInfo);
                totalCount++;
                killMem += taskInfo.getMemorySize();
                // 杀死进程 参数表示包名
                activityManager.killBackgroundProcesses(taskInfo
                        .getPackageName());
            }
        }
        for (TaskInfo taskInfo : killInfos) {
            if (taskInfo.isUserApp()) {
                mUserInfos.remove(taskInfo);
                infos.remove(taskInfo);
                activityManager.killBackgroundProcesses(taskInfo.getPackageName());
            } else {
                mSysInfos.remove(taskInfo);
                infos.remove(taskInfo);
                activityManager.killBackgroundProcesses(taskInfo.getPackageName());
            }
        }
        UIUtils.showToast(
                AtyTaskManager.this,
                "共清理"
                        + totalCount
                        + "个进程,释放"
                        + Formatter.formatFileSize(AtyTaskManager.this,
                        killMem) + "内存");

        processCount -= totalCount;
        availMem += killMem;
        mTvProcessCount.setText("运行中进程" + processCount + "个");
        mTvMemory.setText("剩余/总内存:" + Formatter.formatFileSize(AtyTaskManager.this, availMem) +
                "/" + Formatter.formatFileSize(AtyTaskManager.this, totalMem));

        adapter.notifyDataSetChanged();
    }

    //设置

    public void setting(View view) {
        Intent intent = new Intent(AtyTaskManager.this,AtyTaskManagerSetting.class);
        startActivity(intent);
    }
}
