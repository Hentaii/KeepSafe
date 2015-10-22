package gan.keepsafe.atys;


import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import gan.keepsafe.utils.StreamUtils;

public class Net {
    public static void connect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn = null;
                try {
                    URL url = new URL("http://113.251.160.166:8080/update.json");
                    conn = (HttpURLConnection) url.openConnection();
                    Log.d("111111","1");
                    conn.setRequestMethod("GET");
                    Log.d("111111", "2");
                    conn.setConnectTimeout(5000);
                    conn.setReadTimeout(5000);
                    Log.d("111111", "3");
                    conn.connect();
                    Log.d("111111", "4");
                    int respon = conn.getResponseCode();
                    if (respon == 200) {
                        InputStream inputStream = conn.getInputStream();
                        String result = StreamUtils.readFromStream(inputStream);
                        Log.d("111111","5" + result);
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}

