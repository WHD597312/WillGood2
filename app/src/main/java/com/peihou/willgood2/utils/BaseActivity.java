package com.peihou.willgood2.utils;


import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.peihou.willgood2.MyApplication;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseActivity extends FragmentActivity {
    /** 是否沉浸状态栏 **/
    private boolean isSetStatusBar = false;
    /** 是否允许全屏 **/
    private boolean mAllowFullScreen = false;
    /** 是否禁止旋转屏幕 **/
    private boolean isAllowScreenRoate = true;
    /** 当前Activity渲染的视图View **/
    private View mContextView = null;
    /** 是否输出日志信息 **/
    private boolean isDebug;
    private String APP_NAME;
    protected final String TAG = this.getClass().getSimpleName();
    SharedPreferencesHelper sharedPreferencesHelper;
    Unbinder unbinder;
    MyApplication application;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        application = (MyApplication) getApplication();
        application.addActivity(this);
        sharedPreferencesHelper=new SharedPreferencesHelper(this,"my");
        isDebug = application.isDebug;
        APP_NAME = application.APP_NAME;
        $Log(TAG + "-->onCreate()");
        try {
            Bundle bundle = getIntent().getExtras();
            if(bundle!=null)
            initParms(bundle);
            mContextView = LayoutInflater.from(this)
                    .inflate(bindLayout(), null);
            if (mAllowFullScreen) {
                this.getWindow().setFlags(
                        WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
                requestWindowFeature(Window.FEATURE_NO_TITLE);
            }



            setContentView(mContextView);
            unbinder=ButterKnife.bind(this);
            if (!isAllowScreenRoate) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
            initView(mContextView);
            doBusiness(this);

            StatusBarUtil.StatusBarLightMode(this);
//            StatusBarUtil.transparencyBar(this); //设置状态栏全透明
//            if (isSetStatusBar) {
//                steepStatusBar();
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (application!=null)
            application.removeActivity(this);
    }

    /**
     * [沉浸状态栏]
     */
    private void steepStatusBar() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            // 透明状态栏
//            getWindow().addFlags(
//                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            // 透明导航栏
//            getWindow().addFlags(
//                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//        }
//        StatusBarUtil.StatusBarLightMode(this); //设置白底黑字
    }

    /**
     * [初始化Bundle参数]
     *
     * @param parms
     */
    public abstract void initParms(Bundle parms);

    /**
     * [绑定布局]
     *
     * @return
     */
    public abstract int bindLayout();


    /**
     * [重写： 1.是否沉浸状态栏 2.是否全屏 3.是否禁止旋转屏幕]
     */
    // public abstract void setActivityPre();

    /**
     * [初始化控件]
     *
     * @param view
     */
    public abstract void initView(final View view);

    /**
     * [业务操作]
     *
     * @param mContext
     */
    public abstract void doBusiness(Context mContext);

//    /** View点击 **/
//    public abstract void widgetClick(View v);

//    @Override
//    public void onClick(View v) {
//        if (fastClick())
//            widgetClick(v);
//    }

    /**
     * [页面跳转]
     *
     * @param clz
     */
    public void startActivity(Class<?> clz) {
        startActivity(clz, null);
    }

    /**
     * [携带数据的页面跳转]
     *
     * @param clz
     * @param bundle
     */
    public void startActivity(Class<?> clz, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(this, clz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    @SuppressWarnings("unchecked")
    public <T extends View> T $(int resId) {
        return (T) super.findViewById(resId);
    }

    /**
     * [含有Bundle通过Class打开编辑界面]
     *
     * @param cls
     * @param bundle
     * @param requestCode
     */
    public void startActivityForResult(Class<?> cls, Bundle bundle,
                                       int requestCode) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        MyApplication.getQueue().cancelAll(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.e(TAG + "--->onResume()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.e(TAG + "--->onDestroy()");
        unbinder.unbind();
    }

    /**
     * [是否允许全屏]
     *
     * @param allowFullScreen
     */
    public void setAllowFullScreen(boolean allowFullScreen) {
        this.mAllowFullScreen = allowFullScreen;
    }

    /**
     * [是否设置沉浸状态栏]
     *
     * @param isSetStatusBar
     */
    public void setSteepStatusBar(boolean isSetStatusBar) {
        Log.e("qqqqqIIII",isSetStatusBar+","+this.isSetStatusBar);
        this.isSetStatusBar = isSetStatusBar;
    }

    /**
     * [是否允许屏幕旋转]
     *
     * @param isAllowScreenRoate
     */
    public void setScreenRoate(boolean isAllowScreenRoate) {
        this.isAllowScreenRoate = isAllowScreenRoate;
    }

    /**
     * [日志输出]
     *
     * @param msg
     */
    protected void $Log(String msg) {
        if (isDebug) {
            Log.d(APP_NAME, msg);
        }
    }

    /**
     * [防止快速点击]
     *
     * @return
     */
    private boolean fastClick() {
        long lastClick = 0;
        if (System.currentTimeMillis() - lastClick <= 1000) {
            return false;
        }
        lastClick = System.currentTimeMillis();
        return true;
    }

    public int getType() {
        int userId= (int) sharedPreferencesHelper.getSharedPreference("type",0);
        return userId ;
    }


    public int getUid() {
        int userId= (int) sharedPreferencesHelper.getSharedPreference("id",0);
        return userId ;
    }

    public String getName() {
        String name= (String) sharedPreferencesHelper.getSharedPreference("name","");
        return name ;
    }

    public String getNumber() {
        String number= (String) sharedPreferencesHelper.getSharedPreference("number","");
        return number ;
    }


    public String getLivingAddress() {
        String livingAddress= (String) sharedPreferencesHelper.getSharedPreference("livingAddress","");
        return livingAddress ;
    }

    public int getLoginType() {

        int loginType= (int) sharedPreferencesHelper.getSharedPreference("loginType",0);
        return loginType ;
    }

    public int getTradeId() {
        int tradeId= (int) sharedPreferencesHelper.getSharedPreference("tradeId",0);
        return tradeId ;
    }

    public String getPassword() {
        String password= (String) sharedPreferencesHelper.getSharedPreference("password","");
        return password ;
    }

    public void toast(String text) {
        ToastUtil.showLong(this,text);
    }

    public void toast(int resId) {
        ToastUtil.showShort(this,String.valueOf(resId));
    }

}
