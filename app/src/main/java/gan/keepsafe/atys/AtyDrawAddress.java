package gan.keepsafe.atys;

import android.app.Activity;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import gan.keepsafe.R;

public class AtyDrawAddress extends Activity {
    private TextView mTvTop;
    private TextView mTvBottom;
    private ImageView mIvDraw;
    private SharedPreferences mSpref;
    private int height;
    private int width;
    private long[] mHits = new long[2];
    private int startX;
    private int startY;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_draw_address);
        mTvTop = (TextView) findViewById(R.id.tv_top);
        mTvBottom = (TextView) findViewById(R.id.tv_bottom);
        mIvDraw = (ImageView) findViewById(R.id.iv_drag);
        mSpref = getSharedPreferences("config", MODE_PRIVATE);
        int lastX = mSpref.getInt("endX", 0);
        int lastY = mSpref.getInt("endY", 0);

        if (lastY > height / 2) {
            mTvTop.setVisibility(View.VISIBLE);
            mTvBottom.setVisibility(View.INVISIBLE);
        } else {
            mTvTop.setVisibility(View.INVISIBLE);
            mTvBottom.setVisibility(View.VISIBLE);
        }

        WindowManager manager = getWindowManager();

        height = manager.getDefaultDisplay().getHeight();
        width = manager.getDefaultDisplay().getWidth();


        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mIvDraw.getLayoutParams();
        layoutParams.leftMargin = lastX;
        layoutParams.topMargin = lastY;

        mIvDraw.setLayoutParams(layoutParams);


        mIvDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                mHits[mHits.length - 1] = SystemClock.uptimeMillis();// 开机后开始计算的时间
                if (mHits[0] >= (SystemClock.uptimeMillis() - 500)) {
                    // 把图片居中
                    mIvDraw.layout(width / 2 - mIvDraw.getWidth() / 2, mIvDraw.getTop(), mIvDraw.getRight() / 2 +
                            width / 2, mIvDraw.getBottom());
                }
            }
        });

        mIvDraw.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int endX = (int) event.getRawX();
                        int endY = (int) event.getRawY();
                        int dX = endX - startX;
                        int dY = endY - startY;

                        if (mIvDraw.getLeft() + dX < 0 || mIvDraw.getRight() + dX > width || mIvDraw.getTop() + dY < 0
                                || mIvDraw.getBottom() + dY > height) {
                            break;
                        }

                        if (endY > height / 2) {
                            mTvTop.setVisibility(View.VISIBLE);
                            mTvBottom.setVisibility(View.INVISIBLE);
                        } else {
                            mTvTop.setVisibility(View.INVISIBLE);
                            mTvBottom.setVisibility(View.VISIBLE);
                        }

                        mIvDraw.layout(mIvDraw.getLeft() + dX, mIvDraw.getTop() + dY, mIvDraw.getRight() + dX,
                                mIvDraw.getBottom() + dY);
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        SharedPreferences.Editor edit = mSpref.edit();
                        edit.putInt("endX", mIvDraw.getLeft());
                        edit.putInt("endY", mIvDraw.getTop());
                        edit.apply();
                        break;

                    default:
                        break;
                }

                return false;
            }
        });

    }


}
