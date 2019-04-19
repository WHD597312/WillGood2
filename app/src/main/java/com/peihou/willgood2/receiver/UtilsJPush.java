package com.peihou.willgood2.receiver;

import android.content.Context;

import cn.jpush.android.api.JPushInterface;

public class UtilsJPush {
    /**
     * 恢复极光推送
     * @param context
     */
    public static void resumeJpush(Context context){
        if (JPushInterface.isPushStopped(context)){
            JPushInterface.resumePush(context);
//            JPushInterface.stopPush(context);
        }
    }

    /**
     * 停止极光推送
     * @param context
     */
    public static void stopJpush(Context context){
        if (!JPushInterface.isPushStopped(context)){
            JPushInterface.stopPush(context);
        }
    }
}
