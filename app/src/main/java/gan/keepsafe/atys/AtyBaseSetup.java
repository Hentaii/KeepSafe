package gan.keepsafe.atys;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import gan.keepsafe.R;

public abstract class AtyBaseSetup extends Activity {
    public SharedPreferences mSpref;
    private GestureDetector mGd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSpref = getSharedPreferences("config", MODE_PRIVATE);
        mGd = new GestureDetector(AtyBaseSetup.this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                //向右滑动，显示上一个界面
                if (e2.getRawX() - e1.getRawX() > 200) {
                    ShowPrePage();
                }
                //向左滑动，显示下一个界面
                if (e1.getRawX() - e2.getRawX() > 200) {
                    ShowNextPage();
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        }
        );
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGd.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    public void NextClick(View view) {
        ShowNextPage();
    }


    public void PreClick(View view) {
        ShowPrePage();
    }

    protected abstract void ShowPrePage();

    protected abstract void ShowNextPage();

}
