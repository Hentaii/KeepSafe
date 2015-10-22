package gan.keepsafe.utils;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamUtils {
    public static String readFromStream(InputStream in) throws IOException {
        int len = 0;
        byte[] bytes = new byte[1024];
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        while ((len = in.read(bytes)) != -1) {
            out.write(bytes, 0, len);
        }
        String result = out.toString();
        in.close();
        out.close();
        return result;
    }
}
