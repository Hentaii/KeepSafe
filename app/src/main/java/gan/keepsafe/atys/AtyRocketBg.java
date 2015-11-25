package gan.keepsafe.atys;

import android.app.Activity;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import gan.keepsafe.R;

public class AtyRocketBg extends Activity {
    private ImageView mIvTop;
    private ImageView mIvBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_rocket_bg);
        mIvTop = (ImageView) findViewById(R.id.iv_TopSmoke);
        mIvBottom = (ImageView) findViewById(R.id.iv_bottomSmoke);

        AlphaAnimation anim = new AlphaAnimation(0, 1);
        anim.setDuration(1000);
        anim.setFillAfter(true);

        mIvTop.startAnimation(anim);
        mIvBottom.startAnimation(anim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 1000);
    }



}
