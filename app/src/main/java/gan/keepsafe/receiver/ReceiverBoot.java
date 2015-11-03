package gan.keepsafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

public class ReceiverBoot extends BroadcastReceiver {
    private SharedPreferences mSpref;

    public ReceiverBoot() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mSpref = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        boolean state = mSpref.getBoolean("protect", false);
        if (state) {
            String mSimNum = mSpref.getString("sim_num", null);
            if (!TextUtils.isEmpty(mSimNum)) {
                TelephonyManager mManager = (TelephonyManager) context.getSystemService
                        (Context.TELEPHONY_SERVICE);
                String mContent_SimNum = mManager.getSimSerialNumber();
                if (mSimNum != mContent_SimNum) {
                    Log.d("LOG", "不一样");
                } else {
                    Log.d("LOD", "一样");
                }
            }
        }
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
