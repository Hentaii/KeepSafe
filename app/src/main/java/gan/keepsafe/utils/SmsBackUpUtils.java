package gan.keepsafe.utils;


import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;

public class SmsBackUpUtils {

    public interface SmsBackUpCallBack {
        void before(int count);

        void onBackUpSms(int process);
    }

    public static boolean backUpSms(Context context, SmsBackUpCallBack callBack) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Uri uri = Uri.parse("content://sms/");
            ContentResolver resolver = context.getContentResolver();
            Cursor cursor = resolver.query(uri, new String[]{"address",
                    "date", "type", "body"}, null, null, null);
            int count = cursor.getCount();
            try {
                File file = new File(Environment.getExternalStorageDirectory(), "backSms.xml");
                FileOutputStream os = new FileOutputStream(file);
                XmlSerializer serializer = Xml.newSerializer();
                serializer.setOutput(os, "utf-8");
                serializer.startDocument("utf-8", true);
                serializer.startTag(null, "smss");
                serializer.attribute(null, "size", String.valueOf(count));
                callBack.before(count);
                int process = 0;
                while (cursor.moveToNext()) {
                    /*
                    Log.d("Log", "address = " + cursor.getString(0));
                    Log.d("Log", "date = " + cursor.getString(1));
                    Log.d("Log", "type = " + cursor.getString(2));
                    Log.d("Log", "body = " + cursor.getString(3));
                    */
                    serializer.startTag(null, "sms");

                    serializer.startTag(null, "address");
                    serializer.text(cursor.getString(0));
                    serializer.endTag(null, "address");

                    serializer.startTag(null, "date");
                    serializer.text(cursor.getString(1));
                    serializer.endTag(null, "date");

                    serializer.startTag(null, "type");
                    serializer.text(cursor.getString(2));
                    serializer.endTag(null, "type");

                    serializer.startTag(null, "body");
                    serializer.text(cursor.getString(3));
                    serializer.endTag(null, "body");

                    serializer.endTag(null, "sms");
                    process++;
                    callBack.onBackUpSms(process);
                    SystemClock.sleep(1000);
                }
                cursor.close();
                serializer.endTag(null, "smss");
                serializer.endDocument();
                os.flush();
                os.close();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

}

