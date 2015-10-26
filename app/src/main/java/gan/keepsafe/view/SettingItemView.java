package gan.keepsafe.view;


import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import gan.keepsafe.MyConfig;
import gan.keepsafe.R;

public class SettingItemView extends RelativeLayout {
    private TextView mTv_title;
    private TextView mTv_desc;
    private CheckBox mCb_check;
    private String mTitle;
    private String mDesc_on;
    private String mDesc_off;

    public SettingItemView(Context context) {
        super(context);
        init();
    }

    public SettingItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTitle = attrs.getAttributeValue(MyConfig.NAMESPACE, "set_title");
        mDesc_on = attrs.getAttributeValue(MyConfig.NAMESPACE, "desc_on");
        mDesc_off = attrs.getAttributeValue(MyConfig.NAMESPACE, "desc_off");
        init();
    }

    public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        View.inflate(getContext(), R.layout.view_setting_item, this);
        mTv_title = (TextView) findViewById(R.id.tv_title);
        mTv_desc = (TextView) findViewById(R.id.tv_desc);
        mCb_check = (CheckBox) findViewById(R.id.cb_check);
        mTv_title.setText(mTitle);
    }

    public boolean isChecked() {
        return mCb_check.isChecked();
    }

    public void setTitle(String title) {
        mTv_title.setText(title);
    }

    public void setDesc(String desc) {
        mTv_desc.setText(desc);
    }

    public void setCheck(boolean check) {
        mCb_check.setChecked(check);
        if (check) {
            setDesc(mDesc_on);
        } else {
            setDesc(mDesc_off);
        }
    }
}

