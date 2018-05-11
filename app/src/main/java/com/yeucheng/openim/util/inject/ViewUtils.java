package com.yeucheng.openim.util.inject;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Administrator on 2018/2/5.
 */

public class ViewUtils {

    /**
     * @param activity
     */
    public static void inject(Activity activity) {
        inject(new ViewFinder(activity), activity);
    }

    /**
     * @param view
     * @param o    传入view所在类对象,遍历该类中成员变量/方法,绑定变量/方法;
     *             如:在fragment中, ViewUtils.inject(Fragment.this.getContentView(),Fragment.this);
     */
    public static void inject(View view, Object o) {
        inject(new ViewFinder(view), o);
    }

    private static void inject(ViewFinder viewFinder, Object object) {
        //绑定控件
        injectField(viewFinder, object);
        //绑定点击事件
        injectOnclick(viewFinder, object);
        //处理事件
        init(object);
    }

    private static void init(Object object) {
        Class clazz = object.getClass();
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method :
                methods) {
            Log.d("ViewUtilsInit",method.getName());
            Init init = method.getAnnotation(Init.class);
            //如果加入该注解,则表示执行该事件
            boolean isInit = init != null;
            Log.d("ViewUtilsInithas",isInit+"");
            if (isInit){
                method.setAccessible(true);
                try {
                    method.invoke(object);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private static void injectField(ViewFinder viewFinder, Object object) {
        Class clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields
                ) {
            Log.d("field", field.getName());
            FindView findView = field.getAnnotation(FindView.class);
            if (findView != null) {
                int value = findView.value();
                View view = viewFinder.findView(value);
                if (view != null) {
                    field.setAccessible(true);
                    try {
                        field.set(object, view);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private static void injectOnclick(ViewFinder viewFinder, Object object) {
        //获取类中所有的方法
        Class clazz = object.getClass();
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods
                ) {
            Log.d("method", method.getName());
            //是否检测网络链接
            CheckNet checkNet = method.getAnnotation(CheckNet.class);
            //如果加入该注解,则表示需要检查网络链接
            boolean isCheckNet = checkNet != null;
            //获取Click里的value值
            OnClick onClick = method.getAnnotation(OnClick.class);
            if (onClick != null) {
                int[] value = onClick.value();
                boolean isRepeatClick = onClick.preventRepetClick();
                for (int id : value
                        ) {
                    View view = viewFinder.findView(id);
                    if (isRepeatClick) {
                        view.setOnClickListener(new DeclareOnClickListener(method, object,
                                isRepeatClick, isCheckNet));
                    } else {
                        view.setOnClickListener(new DeclareOnClickListener(method, object,
                                false, isCheckNet));
                    }
                }
            }
            OnLongClick onLongClick = method.getAnnotation(OnLongClick.class);
            if (onLongClick != null) {
                int[] value = onLongClick.value();
                for (int id : value
                        ) {
                    View view = viewFinder.findView(id);
                    view.setOnLongClickListener(new DeclareOnLongClickListener(method, object, isCheckNet));
                }
            }
        }
    }

    private static class DeclareOnClickListener implements View.OnClickListener {
        private Method mMethod;
        private Object mObject;
        private boolean mIsRepeatClick;
        private boolean mIsCheckNet;
        public static final int MIN_CLICK_DELAY_TIME = 1000;
        private static long lastClickTime = 0;

        public DeclareOnClickListener(Method method, Object object, boolean isRepeatClick, boolean isCheckNet) {
            super();
            this.mMethod = method;
            this.mObject = object;
            this.mIsRepeatClick = isRepeatClick;
            this.mIsCheckNet = isCheckNet;
        }

        @Override
        public void onClick(View view) {
            //需不需要检测网络
            if (mIsCheckNet) {
                //判断是否有网络
                if (!isNetworkConnected(view.getContext())) {
                    Toast.makeText(view.getContext(), "亲,网络异常哟,请检查网络连接!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            mMethod.setAccessible(true);
            try {
                if (mIsRepeatClick) {
                    long curClickTime = System.currentTimeMillis();
                    if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
                        lastClickTime = curClickTime;
                        mMethod.invoke(mObject, view);
                    }
                } else {
                    mMethod.invoke(mObject, view);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }


    private static class DeclareOnLongClickListener implements View.OnLongClickListener {
        private Method mMethod;
        private Object mObject;
        private boolean mIsCheckNet;

        public DeclareOnLongClickListener(Method method, Object object, boolean isCheckNet) {
            this.mMethod = method;
            this.mObject = object;
            this.mIsCheckNet = isCheckNet;
        }

        @Override
        public boolean onLongClick(View view) {
            //需不需要检测网络
            if (mIsCheckNet) {
                if (!isNetworkConnected(view.getContext())) {
                    Toast.makeText(view.getContext(), "亲,网络异常哟,请检查网络连接!", Toast.LENGTH_SHORT).show();
//                    ToastUtil.showShortToast(view.getContext(),"","亲,网络异常哟,请检查网络连接!");
                    return false;
                }
            }
            mMethod.setAccessible(true);
            try {
                mMethod.invoke(mObject, view);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            return true;
        }
    }

    /**
     * 检测网络是否可用
     *
     * @return
     */
    private static boolean isNetworkConnected(Context context) {
        try {
            ConnectivityManager contextSystemService = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = contextSystemService.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
