package com.yeucheng.openim.util.inject;

import android.app.Activity;
import android.view.View;

/**
 * Created by Administrator on 2018/2/5.
 */

public class ViewFinder {
    private Activity mActivity;
    private View mView;

    public ViewFinder(Activity activity) {
        super();
        this.mActivity = activity;
    }

    public ViewFinder(View view) {
        super();
        this.mView = view;
    }

    public View findView(int id) {
        return mActivity != null?mActivity.findViewById(id):mView.findViewById(id);
    }

}
