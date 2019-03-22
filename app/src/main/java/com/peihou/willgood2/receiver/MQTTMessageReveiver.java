package com.peihou.willgood2.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


import com.peihou.willgood2.MyApplication;
import com.peihou.willgood2.service.MQService;
import com.peihou.willgood2.utils.ToastUtil;
import com.peihou.willgood2.utils.http.NetWorkUtil;


public class MQTTMessageReveiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        boolean isConn = NetWorkUtil.isConn(MyApplication.getContext());
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifiNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
            ToastUtil.showShort(context, "网络不可用");
            //改变背景或者 处理网络的全局变量
        } else if (mobNetInfo.isConnected() || wifiNetInfo.isConnected()) {
            Intent mqttIntent = new Intent(context, MQService.class);
            mqttIntent.putExtra("reconnect", "reconnect");
            context.startService(mqttIntent);
        }
    }

}
