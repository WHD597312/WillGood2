package com.peihou.willgood2.utils;

public class YearUtils {
    /**
     * 是否是闰年
     * [公元年数可被4整除为闰年,但是正百的年数必须是可以被400整除的才是闰年.其他都是平年]
     * @param year
     * @return
     */
    public static boolean isLeapyear(int year) {
        if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
            return true;
        } else {
            return false;
        }
    }
}
