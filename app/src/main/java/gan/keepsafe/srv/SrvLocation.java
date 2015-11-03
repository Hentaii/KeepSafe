package gan.keepsafe.srv;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

public class SrvLocation extends Service {
    private LocationManager mLm;
    private SharedPreferences mSpre;
    private MyLocationListener listener;

    public SrvLocation() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSpre = getSharedPreferences("config", MODE_PRIVATE);
        mLm = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setCostAllowed(true);
        String provider = mLm.getBestProvider(criteria, true);
        listener = new MyLocationListener();
        mLm.requestLocationUpdates(provider, 0, 0, listener);
    }

    class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            mSpre.edit().putString("location", "j:" + location.getLongitude()
                    + "; w:" + location.getLatitude())
                    .apply();
            stopSelf();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle
                extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLm.removeUpdates(listener);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
