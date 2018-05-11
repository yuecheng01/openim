package com.yeucheng.openim.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yeucheng.openim.R;
import com.yeucheng.openim.adapter.MainPageradapter;
import com.yeucheng.openim.base.AbsBaseActivity;
import com.yeucheng.openim.fragment.ContactsFragment;
import com.yeucheng.openim.fragment.SessionFragment;
import com.yeucheng.openim.util.ToolBarUtil;
import com.yeucheng.openim.util.inject.FindView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AbsBaseActivity {

    @FindView(R.id.main_viewpager)
    private ViewPager mViewPager;
    @FindView(R.id.main_bottombar)
    private LinearLayout mBottomBar;
    @FindView(R.id.main_title)
    private TextView mTitle;

    private List<Fragment> mFragments;

    private ToolBarUtil mToolBarUtil;
    private String[] mToolBarTitle;
    @Override
    protected int setLayoutID() {
        return R.layout.activity_main;
    }

    public static void jumpMainActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void init() {
        //添加fragments
        addFragments();
        //设置适配器
        mViewPager.setAdapter(new MainPageradapter(getSupportFragmentManager(),mFragments));
        //底部按钮
        mToolBarUtil = new ToolBarUtil();
        //文字内容
        mToolBarTitle =new String[]{"会话","联系人"};
        //图标
        int[] iconBar = {R.drawable.message_selector,R.drawable.contacts_selector};
        mToolBarUtil.createToolBar(mBottomBar,mToolBarTitle,iconBar);
        //设置默认
        mToolBarUtil.changeColor(0);
        mTitle.setText(mToolBarTitle[0]);
        //设置页面滑动监听
        initVeiewPagerListener();
        //设置底部按钮点击事件
        mToolBarUtil.setOnToolBarClickListener(new ToolBarUtil.onToolBarClickListener() {
            @Override
            public void onToolBarClick(int position) {
                mViewPager.setCurrentItem(position);
            }
        });
    }

    private void initVeiewPagerListener() {
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mToolBarUtil.changeColor(position);
                //设置标题栏
                mTitle.setText(mToolBarTitle[position]);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void addFragments() {
        mFragments = new ArrayList<Fragment>() {
            {
                add(new SessionFragment());
                add(new ContactsFragment());
            }
        };
    }
}
