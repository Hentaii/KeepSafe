package gan.keepsafe.atys;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import gan.keepsafe.MyConfig;
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
    private EditText mEtPhone;
    private CheckBox mCbPhone;
    private CheckBox mCbSms;
    private Button mBtnOk;
    private Button mBtncancel;

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
        scroll();
    }

    private void scroll() {
        mLv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        int lastVisiblePosition = mLv.getLastVisiblePosition();
                        if (lastVisiblePosition == mBlackNumberInfo.size() - 1) {
                            Log.d("Log", lastVisiblePosition + "");
                            mContentPage = (lastVisiblePosition + 1) / mMaxCount;
                            if (lastVisiblePosition >= mTotalNumber) {
                                Toast.makeText(AtyCallSafe.this, "已经滑动到最后一页", Toast.LENGTH_SHORT)
                                        .show();
                                return;
                            }
                            initData();
                        }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                 int totalItemCount) {
                mTvPageNum.setText(firstVisibleItem / mMaxCount + 1 + "/" + mTotalPage);
            }
        });
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            progressDialog.dismiss();
            mTvPageNum.setText(mContentPage + 1 + "/" + mTotalPage);
            myAdapter = new MyAdapter(AtyCallSafe.this, mBlackNumberInfo);
            mLv.setAdapter(myAdapter);
            mLv.setSelection(mContentPage * mMaxCount);
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
                if (mBlackNumberInfo == null) {
                    mBlackNumberInfo = mDb.findPar(mContentPage, mMaxCount);
                } else if (mBlackNumberInfo.size() / mMaxCount - 1 < mContentPage) {
                    for (int i = mContentPage - mBlackNumberInfo.size() / mMaxCount; i >= 0;
                         i--) {
                        mBlackNumberInfo.addAll(mDb.findPar(mContentPage - i + 1, mMaxCount));
                    }
                }
                mHandler.sendEmptyMessage(0);
            }
        }).start();
    }


    public void addBlack(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AtyCallSafe.this);
        final AlertDialog dialog = builder.create();
        View dialogView = View.inflate(AtyCallSafe.this, R.layout.dialog_add_black, null);
        mEtPhone = (EditText) dialogView.findViewById(R.id.et_black_num);
        mCbPhone = (CheckBox) dialogView.findViewById(R.id.cb_phone);
        mCbSms = (CheckBox) dialogView.findViewById(R.id.cb_sms);
        mBtnOk = (Button) dialogView.findViewById(R.id.btn_ok);
        mBtncancel = (Button) dialogView.findViewById(R.id.btn_cancel);
        mBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strPhone = mEtPhone.getText().toString().trim();
                boolean boolPhone = mCbPhone.isChecked();
                boolean boolSms = mCbSms.isChecked();
                if (TextUtils.isEmpty(strPhone)) {
                    Toast.makeText(AtyCallSafe.this, "请输入号码", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
                    blackNumberInfo.setNumber(strPhone);
                    if (boolPhone && boolSms) {
                        blackNumberInfo.setMode(String.valueOf(MyConfig.MODE_PHONE_AND_SMS));
                    } else if (boolPhone) {
                        blackNumberInfo.setMode(String.valueOf(MyConfig.MODE_PHONE));
                    } else if (boolSms) {
                        blackNumberInfo.setMode(String.valueOf(MyConfig.MODE_SMS));
                    } else {
                        Toast.makeText(AtyCallSafe.this, "勾选拦截模式", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mBlackNumberInfo.add(0, blackNumberInfo);
                    mDb.add(strPhone, blackNumberInfo.getMode());
                    if (myAdapter == null) {
                        myAdapter = new MyAdapter(AtyCallSafe.this, mBlackNumberInfo);
                        mLv.setAdapter(myAdapter);
                    } else {
                        myAdapter.notifyDataSetChanged();
                    }
                    dialog.dismiss();
                }
            }
        });
        mBtncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setView(dialogView);
        dialog.show();
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
        if (TextUtils.isEmpty(mEtJump.getText().toString().trim())) {
            Toast.makeText(AtyCallSafe.this, "请输入想要跳转的页码", Toast.LENGTH_SHORT).show();
            return;
        }
        int page = Integer.parseInt(mEtJump.getText().toString().trim());
        if (page >= 1 && page <= mTotalPage) {
            mContentPage = page - 1;
            mEtJump.setText("");
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
                case MyConfig.MODE_PHONE_AND_SMS:
                    mHolder.mMode.setText("来电+短信拦截");
                    break;
                case MyConfig.MODE_PHONE:
                    mHolder.mMode.setText("电话拦截");
                    break;
                case MyConfig.MODE_SMS:
                    mHolder.mMode.setText("短信拦截");
                    break;
            }
            final BlackNumberInfo info = mBlackNumberInfo.get(position);
            mHolder.mDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String num = info.getNumber();
                    if (mDb.delete(num)) {
                        mBlackNumberInfo.remove(info);
                        Toast.makeText(AtyCallSafe.this, "成功删除", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AtyCallSafe.this, "删除失败", Toast.LENGTH_SHORT).show();
                    }

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
