package com.peihou.willgood2.utils.http;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.peihou.willgood2.MyApplication;


public class NetWorkUtil {
    private volatile static  NetWorkUtil instance;

    public NetWorkUtil(){
        instance=this;
    }
    public synchronized static NetWorkUtil getInstance() {
        if (instance == null) {

            synchronized (NetWorkUtil.class) {
                instance = instance == null ? new NetWorkUtil() : instance;
            }
        }
        return instance;
    }
    /**
     * 这个方法是判断网络状态是否可用的
     * @param context
     * @return
     */
    public static boolean isConn(Context context){
        boolean bisConnFlag=false;
        //1.获取网络连接的管理对象
        ConnectivityManager conManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //2.通过管理者对象拿到网络的信息
        NetworkInfo network = conManager.getActiveNetworkInfo();
        if(network!=null){
            //3.网络状态是否可用的返回值
            bisConnFlag=network.isAvailable();
        }
        return bisConnFlag;
    }




    public static boolean isWifiConnected() {
        boolean isConnected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) MyApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            isConnected = info.isConnected();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isConnected;
    }

    public static boolean isMobileConnected() {
        boolean isConnected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) MyApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            isConnected = info.isConnected();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isConnected;
    }

    /*
     * 打开设置网络界面
     * */
    AlertDialog.Builder builder=null;
    public void setNetworkMethod(final Context context){
        if (builder!=null){
            builder.setCancelable(true);
        }
        //提示对话框
        builder=new AlertDialog.Builder(context);
        builder.setTitle("网络设置提示").setMessage("网络连接不可用,是否进行设置?").setPositiveButton("设置", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                Intent intent=null;
                //判断手机系统的版本  即API大于10 就是3.0或以上版本
                if(android.os.Build.VERSION.SDK_INT>10){
                    intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
                }else{
                    intent=new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                }
                context.startActivity(intent);
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        }).show();
    }
/**
 * 网络异常提示界面
 */
 }
