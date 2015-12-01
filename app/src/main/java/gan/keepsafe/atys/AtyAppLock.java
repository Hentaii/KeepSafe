package gan.keepsafe.atys;


import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import gan.keepsafe.R;
import gan.keepsafe.fragment.LockFragment;
import gan.keepsafe.fragment.UnLockFragment;


public class AtyAppLock extends FragmentActivity implements View.OnClickListener {

    private FrameLayout mFlContent;
    private TextView mTvUnlock;
    private TextView mTvLock;
    private android.support.v4.app.FragmentManager fragmentManager;
    private FragmentTransaction mTransaction;
    private UnLockFragment mUnLockFragment;
    private LockFragment mLockFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
    }

    private void initUI() {
        setContentView(R.layout.aty_app_lock);
        mFlContent = (FrameLayout) findViewById(R.id.fl_content);
        mTvUnlock = (TextView) findViewById(R.id.tv_unlock);
        mTvLock = (TextView) findViewById(R.id.tv_lock);
        mTvUnlock.setOnClickListener(this);
        mTvLock.setOnClickListener(this);
        //获取到fragment的管理者
        fragmentManager = getSupportFragmentManager();
        //开启事务
        mTransaction = fragmentManager.beginTransaction();

        mUnLockFragment = new UnLockFragment();
        mLockFragment = new LockFragment();

        /*
         * 替换界面
         * 1 需要替换的界面的id
         * 2具体指某一个fragment的对象
         */
        mTransaction.replace(R.id.fl_content,mUnLockFragment).commit();

    }

    @Override
    public void onClick(View v) {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        switch (v.getId()) {
            case R.id.tv_unlock:
                //没有加锁
                mTvUnlock.setBackgroundResource(R.drawable.tab_left_pressed);
                mTvLock.setBackgroundResource(R.drawable.tab_right_default);

                ft.replace(R.id.fl_content, mUnLockFragment);
                System.out.println("切换到lockFragment");
                break;

            case R.id.tv_lock:
                //没有加锁
                mTvUnlock.setBackgroundResource(R.drawable.tab_left_default);
                mTvLock.setBackgroundResource(R.drawable.tab_right_pressed);
                ft.replace(R.id.fl_content, mLockFragment);
                System.out.println("切换到unlockFragment");
                break;
        }
        ft.commit();
    }
}
