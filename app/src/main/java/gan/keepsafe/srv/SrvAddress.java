package gan.keepsafe.srv;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.CellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import java.util.List;

import gan.keepsafe.db.AddressDao;

public class SrvAddress extends Service {

    private TelephonyManager mTm;
    private MyListener myListener;
    private OutCallReceiver myReceiver;

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
        mTm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        mTm.listen(myListener, PhoneStateListener.LISTEN_CALL_STATE);
        myReceiver = new OutCallReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
        registerReceiver(myReceiver,filter);
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
                    Toast.makeText(SrvAddress.this, address,
                            Toast.LENGTH_LONG)
                            .show();
                default:
                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }
}

class OutCallReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String num = getResultData();
        String addrss = AddressDao.getAddress(num);
        Toast.makeText(context,addrss,Toast.LENGTH_SHORT).show();
    }
}
