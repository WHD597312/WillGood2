package com.peihou.willgood2.utils;

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    private static SimpleDateFormat sdf = null;
    public  static String formatUTC(long l, String strPattern) {
        if (TextUtils.isEmpty(strPattern)) {
            strPattern = "yyyy-MM-dd HH:mm:ss";
        }
        if (sdf == null) {
            try {
                sdf = new SimpleDateFormat(strPattern, Locale.CHINA);
            } catch (Throwable e) {
            }
        } else {
            sdf.applyPattern(strPattern);
        }
        return sdf == null ? "NULL" : sdf.format(l);
    }

    public static boolean isBase64(String str) {
        String base64Pattern = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$";
        return Pattern.matches(base64Pattern, str);
    }
    /**
     * 判断一个字符串是否是数字类型的
     * @param str
     * @return
     */
    public static boolean isNumeric(String str){
        try {
            double num=Double.parseDouble(str);//把字符串强制转换为数字
            return true;//如果是数字，返回True
        } catch (Exception e) {
            return false;//如果抛出异常，返回False
        }
    }
    public static boolean isNumeric2(String str){
        try {
            double num=Integer.parseInt(str);//把字符串强制转换为数字
            return true;//如果是数字，返回True
        } catch (Exception e) {
            return false;//如果抛出异常，返回False
        }
    }
    public static String week(int i){
        String s="";
        switch (i){
            case 0:
                s="一";
                break;
            case 1:
                s="二";
                break;
            case 2:
                s="三";
                break;
            case 3:
                s="四";
                break;
            case 4:
                s="五";
                break;
            case 5:
                s="六";
                break;
            case 6:
                s="七";
                break;
        }
        return s;
    }
}
