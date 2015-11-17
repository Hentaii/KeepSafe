package gan.keepsafe.srv;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;

import gan.keepsafe.MyConfig;
import gan.keepsafe.db.BlackNumberDao;
import gan.keepsafe.db.BlackNumberOpenHelper;

public class SrvBlack extends Service {


    private BlackNumberDao dao;
    private TelephonyManager tm;

    public SrvBlack() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        dao = new BlackNumberDao(SrvBlack.this);
        //获取到系统的电话服务
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        MyPhoneStateListener listener = new MyPhoneStateListener();
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

        //监听短信
        BlackReciever mBr = new BlackReciever();
        IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        intentFilter.setPriority(Integer.MAX_VALUE);
        registerReceiver(mBr, intentFilter);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private class BlackReciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Object[] objects = (Object[]) intent.getExtras().get("pdus");

            for (Object object : objects) {
                SmsMessage message = SmsMessage.createFromPdu((byte[]) object);
                String originatingAddress = message.getOriginatingAddress();// 短信来源号码
                String messageBody = message.getMessageBody();// 短信内容
                String mode = dao.findNumber(originatingAddress);
                if (mode.equals(String.valueOf(MyConfig.MODE_PHONE_AND_SMS))) {
                    abortBroadcast();
                } else if (mode.equals(String.valueOf(MyConfig.MODE_SMS))) {
                    abortBroadcast();
                }

            }
        }
    }

    private class MyPhoneStateListener extends PhoneStateListener {
        //电话状态改变的监听
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
//            * @see TelephonyManager#CALL_STATE_IDLE  电话闲置
//            * @see TelephonyManager#CALL_STATE_RINGING 电话铃响的状态
//            * @see TelephonyManager#CALL_STATE_OFFHOOK 电话接通
            switch (state) {
                //电话铃响的状态
                case TelephonyManager.CALL_STATE_RINGING:

                    String mode = dao.findNumber(incomingNumber);
                    /**
                     * 黑名单拦截模式
                     * 1 全部拦截 电话拦截 + 短信拦截
                     * 2 电话拦截
                     * 3 短信拦截
                     */
                    if (mode.equals(String.valueOf(MyConfig.MODE_PHONE)) || mode.equals(String
                            .valueOf(MyConfig.MODE_PHONE_AND_SMS))) {
                        System.out.println("挂断黑名单电话号码");

                        Uri uri = Uri.parse("content://call_log/calls");

                        getContentResolver().registerContentObserver(uri, true, new
                                MyContentObserver(new Handler(), incomingNumber));

                        //挂断电话
                        endCall();

                    }
                    break;
            }
        }
    }

    private class MyContentObserver extends ContentObserver {
        String incomingNumber;

        /**
         * Creates a content observer.
         *
         * @param handler        The handler to run {@link #onChange} on, or null if none.
         * @param incomingNumber
         */
        public MyContentObserver(Handler handler, String incomingNumber) {
            super(handler);
            this.incomingNumber = incomingNumber;
        }

        //当数据改变的时候调用的方法
        @Override
        public void onChange(boolean selfChange) {

            getContentResolver().unregisterContentObserver(this);

            deleteCallLog(incomingNumber);

            super.onChange(selfChange);
        }
    }

    //删掉电话号码
    private void deleteCallLog(String incomingNumber) {

        Uri uri = Uri.parse("content://call_log/calls");

        getContentResolver().delete(uri, "number=?", new String[]{incomingNumber});

    }

    /**
     * 挂断电话
     */
    private void endCall() {

        try {
            //通过类加载器加载ServiceManager
            Class<?> clazz = getClassLoader().loadClass("android.os.ServiceManager");
            //通过反射得到当前的方法
            Method method = clazz.getDeclaredMethod("getService", String.class);

            IBinder iBinder = (IBinder) method.invoke(null, TELEPHONY_SERVICE);

            ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);

            iTelephony.endCall();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}