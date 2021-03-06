package gan.keepsafe.srv;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import gan.keepsafe.R;
import gan.keepsafe.db.AddressDao;

public class SrvAddress extends Service {

    private TelephonyManager mTm;
    private MyListener myListener;
    private OutCallReceiver myReceiver;
    private WindowManager mWM;
    private View view;
    private SharedPreferences mSpref;
    private int startX;
    private int startY;
    private int width;
    private int height;

    public SrvAddress() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        myListener = new MyListener();
        mSpref = getSharedPreferences("config", MODE_PRIVATE);
        mTm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        mTm.listen(myListener, PhoneStateListener.LISTEN_CALL_STATE);
        myReceiver = new OutCallReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
        registerReceiver(myReceiver, filter);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mTm.listen(myListener, PhoneStateListener.LISTEN_NONE);
        unregisterReceiver(myReceiver);
    }


    class MyListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    String address = AddressDao.getAddress(incomingNumber);// 根据来电号码查询归属地
                    showToast(address);
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    if (mWM != null && view != null) {
                        mWM.removeView(view);
                        view = null;
                    }
                    break;
                default:
                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }

    public void showToast(String text) {
        mWM = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        width = mWM.getDefaultDisplay().getWidth();
        height = mWM.getDefaultDisplay().getHeight();
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;
        params.type = WindowManager.LayoutParams.TYPE_PHONE;
        params.setTitle("Toast");
        params.gravity = Gravity.LEFT + Gravity.TOP;
        int x = mSpref.getInt("endX", 0);
        int y = mSpref.getInt("endY", 0);
        params.x = x;
        params.y = y;

        view = View.inflate(this, R.layout.toast_address, null);

        int[] bgs = new int[]{R.drawable.call_locate_white,
                R.drawable.call_locate_orange, R.drawable.call_locate_blue,
                R.drawable.call_locate_gray, R.drawable.call_locate_green};
        int style = mSpref.getInt("address_style", 0);

        view.setBackgroundResource(bgs[style]);// 根据存储的样式更新背景

        TextView tvText = (TextView) view.findViewById(R.id.tv_number);
        tvText.setText(text);
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
                                                // 记录坐标点
                                                SharedPreferences.Editor edit = mSpref.edit();
                                                edit.putInt("endX", params.x);
                                                edit.putInt("endY", params.y);
                                                edit.apply();
                                                break;

                                            default:
                                                break;
                                        }

                                        return false;
                                    }
                                }

        );
        mWM.addView(view, params);// 将view添加在屏幕上(Window)
    }


    class OutCallReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String num = getResultData();
            String addrss = AddressDao.getAddress(num);
            showToast(addrss);
        }
    }
}


