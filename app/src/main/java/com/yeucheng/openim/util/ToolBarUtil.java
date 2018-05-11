package com.yeucheng.openim.util;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yeucheng.openim.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/5/8.
 */

public class ToolBarUtil {
    private List<TextView> mTextViews = new ArrayList<>();

    public void createToolBar(LinearLayout container, String[] toolBarTitle, int[] iconBar) {
        for (int i = 0; i < toolBarTitle.length; i++) {
            TextView tv = (TextView) View.inflate(container.getContext(), R.layout.inflate_bottom_btn, null);
            tv.setText(toolBarTitle[i]);
            //动态修改图片
            tv.setCompoundDrawablesWithIntrinsicBounds(0, iconBar[i], 0, 0);
            //布局均分
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout
                    .LayoutParams.MATCH_PARENT);
            //设置weight属性
            params.weight = 1;
            container.addView(tv, params);
            mTextViews.add(tv);
            //设置点击事件
            final int finalI = i;
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnToolBarClickListener.onToolBarClick(finalI);
                }
            });
        }
    }

    public void changeColor(int poi) {
        for (TextView tv :
                mTextViews) {
            tv.setSelected(false);
        }
        mTextViews.get(poi).setSelected(true);
    }

    public interface  onToolBarClickListener{
        void onToolBarClick(int position);
    }
    onToolBarClickListener mOnToolBarClickListener;

    public void setOnToolBarClickListener(onToolBarClickListener onToolBarClickListener) {
        mOnToolBarClickListener = onToolBarClickListener;
    }
}
