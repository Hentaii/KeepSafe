package gan.keepsafe.srv;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

import gan.keepsafe.R;
import gan.keepsafe.atys.AtyRocketBg;

public class SrvRocket extends Service {
    private WindowManager mWM;
    private ImageView mIvRocket;

    private int width;
    private int height;
    private View view;
    private int startX;
    private int startY;
    private WindowManager.LayoutParams params;

    public SrvRocket() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        showRocket();

    }

    public void showRocket() {
        mWM = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        width = mWM.getDefaultDisplay().getWidth();
        height = mWM.getDefaultDisplay().getHeight();
        params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;
        params.type = WindowManager.LayoutParams.TYPE_PHONE;
        params.gravity = Gravity.LEFT + Gravity.TOP;

        view = View.inflate(this, R.layout.toast_rocket, null);
        mIvRocket = (ImageView) view.findViewById(R.id.iv_rocket);
        mIvRocket.setBackgroundResource(R.drawable.anim_rocket);
        AnimationDrawable anim  = (AnimationDrawable) mIvRocket.getBackground();
        anim.start();
        mWM.addView(view, params);
        view.setOnTouchListener(new View.OnTouchListener() {
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
                                                params.x += dX;
                                                params.y += dY;

                                                if (params.x < 0) {
                                                    params.x = 0;
                                                }

                                                if (params.y < 0) {
                                                    params.y = 0;
                                                }

                                                if (params.y > height - view.getHeight()) {
                                                    params.y = height - view.getHeight();
                                                }
                                                if (params.x > height - view.getWidth()) {
                                                    params.x = height - view.getWidth();
                                                }
                                                mWM.updateViewLayout(view, params);
                                                startX = (int) event.getRawX();
                                                startY = (int) event.getRawY();
                                                break;
                                            case MotionEvent.ACTION_UP:
                                                if (params.y > height - 200) {
                                                    sendRocket();
                                                    Intent intent = new Intent(SrvRocket.this,
                                                            AtyRocketBg.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intent);
                                                }
                                                break;

                                            default:
                                                break;
                                        }

                                        return false;
                                    }
                                }

        );
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            params.y = msg.arg1;
            mWM.updateViewLayout(view, params);
        }
    };

    private void sendRocket() {
        params.x = width/2-view.getWidth()/2;
        mWM.updateViewLayout(view,params);
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 9; i >= 0; i--) {
                    int y = height / 10 * i;
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Message message = Message.obtain();
                    message.arg1 = y;
                    mHandler.sendMessage(message);
                }
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mWM != null && view != null) {
            mWM.removeView(view);
            view = null;
        }
    }
}
