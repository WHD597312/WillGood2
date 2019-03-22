package com.peihou.willgood2.utils;


import android.util.Log;

import java.util.Locale;

/**
 * Created by shen on 2016/2/28.
 */
public class LogUtil {
    private static boolean LOGV = true;
    private static boolean LOGD = true;
    private static boolean LOGI = true;
    private static boolean LOGW = true;
    private static boolean LOGE = true;

    private static String getTag() {
        StackTraceElement[] trace = new Throwable().fillInStackTrace()
                .getStackTrace();
        String callingClass = "";
        for (int i = 2; i < trace.length; i++) {
            Class<?> clazz = trace[i].getClass();
            if (!clazz.equals(LogUtil.class)) {
                callingClass = trace[i].getClassName();
                callingClass = callingClass.substring(callingClass
                        .lastIndexOf('.') + 1);
                break;
            }
        }
        return callingClass;
    }

    private static String buildMessage(String msg) {
        StackTraceElement[] trace = new Throwable().fillInStackTrace()
                .getStackTrace();
        String caller = "";
        for (int i = 2; i < trace.length; i++) {
            Class<?> clazz = trace[i].getClass();
            if (!clazz.equals(LogUtil.class)) {
                caller = trace[i].getMethodName();
                break;
            }
        }
        return String.format(Locale.US, "[%d] %s: %s", Thread.currentThread()
                .getId(), caller, msg);
    }

    public static void v(String mess) {
        if (LOGV) { Log.v(getTag(), buildMessage(mess)); }
    }
    public static void d(String mess) {
        if (LOGD) { Log.d(getTag(), buildMessage(mess)); }
    }
    public static void i(String mess) {
        if (LOGI) { Log.i(getTag(), buildMessage(mess)); }
    }
    public static void w(String mess) {
        if (LOGW) { Log.w(getTag(), buildMessage(mess)); }
    }
    public static void e(String mess) {
        if (LOGE) { Log.e(getTag(), buildMessage(mess)); }
    }
}