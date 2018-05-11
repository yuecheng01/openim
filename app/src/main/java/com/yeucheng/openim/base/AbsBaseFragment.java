package com.yeucheng.openim.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yeucheng.openim.util.LogUtils;
import com.yeucheng.openim.util.inject.ViewUtils;

/**
 * Created by Administrator on 2018/5/9.
 */

public abstract class AbsBaseFragment extends Fragment {
    protected Activity mActivity;
    protected Context mContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mActivity = getActivity();
        mContext = this.getContext();
        init();
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getlayoutID(), container, false);
        ViewUtils.inject(view, this);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        initData();
        initListener();
        super.onActivityCreated(savedInstanceState);
    }

    protected abstract int getlayoutID();

    protected abstract void init();

    protected abstract void initData();

    protected abstract void initListener();
}
