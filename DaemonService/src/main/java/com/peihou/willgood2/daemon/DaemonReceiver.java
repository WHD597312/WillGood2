package com.peihou.willgood2.daemon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

/**
 * @author sunfusheng on 2018/8/3.
 */
public class DaemonReceiver extends BroadcastReceiver {
    private static final String TAG = "DaemonReceiver";
//    private boolean isRegistered = false;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            Log.e(TAG, "onReceive() action: " + intent.getAction());
//            Toast.makeText(context,"设备开机了",Toast.LENGTH_SHORT).show();
        }
        DaemonHolder.startService();
    }
//    public void registerScreenBroadcastReceiver(Context context) {
//        Log.e(TAG,"-->"+isRegistered);
//        if (!isRegistered) {
//            isRegistered = true;
//            IntentFilter filter = new IntentFilter();
//            filter.addAction(Intent.ACTION_BOOT_COMPLETED);//开机
//            filter.addAction(Intent.ACTION_SCREEN_ON); // 开屏
//            filter.addAction(Intent.ACTION_SCREEN_OFF); // 锁屏
//            filter.addAction(Intent.ACTION_USER_PRESENT); // 解锁
//            filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS); // Home键
//            context.registerReceiver(DaemonReceiver.this, filter);
//        }
//    }
//    public void unregisterScreenBroadcastReceiver(Context context) {
//        if (isRegistered) {
//            isRegistered = false;
//            context.unregisterReceiver(DaemonService.DaemonReceiver.this);
//        }
//    }


}
