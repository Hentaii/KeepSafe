package gan.keepsafe.atys;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import gan.keepsafe.R;

public class AtyHomeAdapter extends BaseAdapter {
    private Context context;
    String[] mItems;
    int[] mPics;

    public AtyHomeAdapter(Context context, String[] mItems, int[] mPics) {
        this.context = context;
        this.mItems = mItems;
        this.mPics = mPics;
    }

    @Override
    public int getCount() {
        return mItems.length;
    }

    @Override
    public Object getItem(int position) {
        return getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = View.inflate(context, R.layout.aty_home_item, null);
        ImageView mIvItem = (ImageView) view.findViewById(R.id.iv_item);
        TextView mTvItem = (TextView) view.findViewById(R.id.tv_item);
        mIvItem.setImageResource(mPics[position]);
        mTvItem.setText(mItems[position]);
        return view;
    }
}
