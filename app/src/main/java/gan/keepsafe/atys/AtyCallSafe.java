package gan.keepsafe.atys;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import gan.keepsafe.R;
import gan.keepsafe.adp.MyBaseAdapter;
import gan.keepsafe.bean.BlackNumberInfo;
import gan.keepsafe.db.BlackNumberDao;

public class AtyCallSafe extends AppCompatActivity {
    private List<BlackNumberInfo> mBlackNumberInfo;
    private ListView mLv;
    private ProgressDialog progressDialog;
    private EditText mEtJump;
    private TextView mTvPageNum;
    private ImageView mIvDelete;


    private int mContentPage = 0;
    private int mMaxCount = 20;

    private int mTotalPage = 10;
    private int mTotalNumber;
    private BlackNumberDao mDb;
    private MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_call_safe);
        initUI();
        initData();
    }

    private void initUI() {
        mLv = (ListView) findViewById(R.id.list_view);
        mEtJump = (EditText) findViewById(R.id.et_jump);
        mTvPageNum = (TextView) findViewById(R.id.tv_page_num);
        progressDialog = new ProgressDialog(AtyCallSafe.this);
        progressDialog.setTitle("加载中");
        progressDialog.setMessage("正在玩命加载，请稍后");
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            progressDialog.dismiss();
            mTvPageNum.setText(mContentPage + 1 + "/" + mTotalPage);
            myAdapter = new MyAdapter(AtyCallSafe.this, mBlackNumberInfo);
            mLv.setAdapter(myAdapter);
        }
    };

    private void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mDb = new BlackNumberDao(AtyCallSafe.this);
                mTotalNumber = mDb.getTotalNumber();
                if (mTotalNumber % mMaxCount == 0) {
                    mTotalPage = mTotalNumber / mMaxCount;
                } else {
                    mTotalPage = mTotalNumber / mMaxCount + 1;
                }
                mBlackNumberInfo = mDb.findPar(mContentPage, mMaxCount);
                mHandler.sendEmptyMessage(0);
            }
        }).start();
    }

    public void prePage(View view) {
        if (mContentPage + 1 <= 1) {
            Toast.makeText(AtyCallSafe.this, "已经到达第一页", Toast.LENGTH_SHORT).show();
        } else {
            mContentPage--;
            initData();
        }
    }

    public void nextPage(View view) {
        if (mContentPage + 1 >= mTotalPage) {
            Toast.makeText(AtyCallSafe.this, "已经到达最后一页", Toast.LENGTH_SHORT).show();
        } else {
            mContentPage++;
            initData();
        }
    }

    public void jump(View view) {
        int page = Integer.parseInt(mEtJump.getText().toString().trim());
        if (TextUtils.isEmpty(page + "")) {
            Toast.makeText(AtyCallSafe.this, "请输入想要跳转的页码", Toast.LENGTH_SHORT).show();
        }
        if (page >= 1 && page <= mTotalPage) {
            mContentPage = page - 1;
            initData();
        } else {
            Toast.makeText(AtyCallSafe.this, "请输入正确的的页码", Toast.LENGTH_SHORT).show();

        }
    }

    private class MyAdapter extends MyBaseAdapter<BlackNumberInfo> {

        public MyAdapter(Context context, List<BlackNumberInfo> list) {
            super(context, list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder mHolder;
            if (convertView == null) {
                convertView = View.inflate(AtyCallSafe.this, R.layout.item_call_safe, null);
                mHolder = new ViewHolder();
                mHolder.mPhoneNum = (TextView) convertView.findViewById(R.id.tv_phone_num);
                mHolder.mMode = (TextView) convertView.findViewById(R.id.tv_mode);
                mHolder.mDelete = (ImageView) convertView.findViewById(R.id.iv_delete);
                convertView.setTag(mHolder);
            } else {
                mHolder = (ViewHolder) convertView.getTag();
            }
            mHolder.mPhoneNum.setText(mBlackNumberInfo.get(position).getNumber());
            switch (Integer.parseInt(mBlackNumberInfo.get(position).getMode())) {
                case 1:
                    mHolder.mMode.setText("来电加短信拦截");
                    break;
                case 2:
                    mHolder.mMode.setText("电话拦截");
                    break;
                case 3:
                    mHolder.mMode.setText("短信拦截");
                    break;
            }
            final BlackNumberInfo info = mBlackNumberInfo.get(position);
            mHolder.mDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String num = info.getNumber();
                    if (mDb.delete(num)) {
                        Toast.makeText(AtyCallSafe.this, "成功删除", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AtyCallSafe.this, "删除失败", Toast.LENGTH_SHORT).show();
                    }
                    myAdapter.notifyDataSetChanged();
                    initData();
                }
            });
            return convertView;
        }

        private class ViewHolder {
            TextView mPhoneNum;
            TextView mMode;
            ImageView mDelete;
        }
    }
}
