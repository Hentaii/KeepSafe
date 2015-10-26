package gan.keepsafe.atys;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import gan.keepsafe.MyConfig;
import gan.keepsafe.R;

public class AtyHome extends AppCompatActivity {

    private GridView mGvList;

    private String[] mItem = new String[]{"手机防盗", "通讯卫士", "软件管理", "进程管理",
            "流量统计", "手机杀毒", "缓存清理", "高级工具", "设置中心"};
    private int[] mPics = new int[]{R.drawable.home_safe,
            R.drawable.home_callmsgsafe, R.drawable.home_apps,
            R.drawable.home_taskmanager, R.drawable.home_netmanager,
            R.drawable.home_trojan, R.drawable.home_sysoptimize,
            R.drawable.home_tools, R.drawable.home_settings};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aty_home);
        mGvList = (GridView) findViewById(R.id.gv_list);
        mGvList.setAdapter(new AtyHomeAdapter(this, mItem, mPics));
        mGvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case MyConfig.HOME_SETTINGS: {
                        startActivity(new Intent(AtyHome.this,AtySetting.class));
                    }

                }
            }
        });
    }


}
