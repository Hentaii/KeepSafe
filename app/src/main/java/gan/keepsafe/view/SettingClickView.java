package gan.keepsafe.view;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import gan.keepsafe.MyConfig;
import gan.keepsafe.R;

public class SettingClickView extends RelativeLayout {
    private TextView mTv_title;
    private TextView mTv_desc;
    private String mTitle;

    public SettingClickView(Context context) {
        super(context);
        init();
    }

    public SettingClickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTitle = attrs.getAttributeValue(MyConfig.NAMESPACE, "set_title");
        init();
    }

    public SettingClickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        View.inflate(getContext(), R.layout.view_click_item, this);
        mTv_title = (TextView) findViewById(R.id.tv_title);
        mTv_desc = (TextView) findViewById(R.id.tv_desc);
        setTitle(mTitle);
    }

    public void setTitle(String title) {
        mTv_title.setText(title);
    }

    public void setDesc(String desc) {
        mTv_desc.setText(desc);
    }

}

