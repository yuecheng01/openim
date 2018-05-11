package com.yeucheng.openim.base;

import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;

import com.yeucheng.openim.util.inject.ViewUtils;

/**
 * Created by Administrator on 2018/5/8.
 */

public abstract class AbsBaseActivity extends AppCompatActivity {
    protected Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(setLayoutID());
        mContext = this;
        ViewUtils.inject(this);
        init();
    }

    protected abstract void init();

    protected abstract int setLayoutID();
}
