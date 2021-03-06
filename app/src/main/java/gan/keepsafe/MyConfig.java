package gan.keepsafe;


public class MyConfig {
    public static final String NAMESPACE = "http://schemas.android.com/apk/res-auto";
    public static final String JSON_URL = "http://113.251.171.224:8080/update.json";
    public static final String JSON_VIR_URL = "http://113.251.171.224:8080/update.json";


    public static final int UPDATE_DIALOG = 1;
    public static final int ENTER_HOME = 2;
    public static final int JSON_ERROR = 3;
    public static final int URL_ERROR = 4;
    public static final int NET_ERROR = 5;

    public static final int HOME_SAFE = 0;
    public static final int HOME_CALLMSGSAFE = 1;
    public static final int HOME_APPS = 2;
    public static final int HOME_TASKMANAGER = 3;
    public static final int HOME_NETMANAGER = 4;
    public static final int HOME_TROJAN = 5;
    public static final int HOME_SYSOPTIMIZE = 6;
    public static final int HOME_TOOLS = 7;
    public static final int HOME_SETTINGS = 8;

    public static final int MODE_PHONE_AND_SMS = 1;
    public static final int MODE_PHONE = 2;
    public static final int MODE_SMS = 3;

    public static final int HANDLE_START = 0;
    public static final int HANDLE_SCAN = 1;
    public static final int HANDLE_END = 2;




}
