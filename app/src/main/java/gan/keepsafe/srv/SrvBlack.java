package gan.keepsafe.srv;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.telephony.SmsMessage;

import gan.keepsafe.MyConfig;
import gan.keepsafe.db.BlackNumberDao;
import gan.keepsafe.db.BlackNumberOpenHelper;

public class SrvBlack extends Service {


    private BlackNumberDao dao;

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
        BlackReciever mBr = new BlackReciever();
        IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        intentFilter.setPriority(Integer.MAX_VALUE);
        registerReceiver(mBr,intentFilter);

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
                if (mode.equals(String.valueOf(MyConfig.MODE_PHONE_AND_SMS))){
                    abortBroadcast();
                }else if (mode.equals(String.valueOf(MyConfig.MODE_SMS))){
                    abortBroadcast();
                }

            }


        }
    }
}