package com.peihou.willgood2.custom;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Créé par liusiqian 15/11/27.
 */
public class CustomDatePicker extends DatePicker
{
    private List<NumberPicker> mPickers;

    public CustomDatePicker(Context context)
    {
        super(context);
        findNumberPicker();
    }

    public CustomDatePicker(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        findNumberPicker();
    }

    public CustomDatePicker(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        findNumberPicker();
    }

    /**
     * 得到控件里面的numberpicker组件
     */
    private void findNumberPicker()
    {
        mPickers = new ArrayList<NumberPicker>();
        LinearLayout llFirst = (LinearLayout) getChildAt(0);
        LinearLayout mSpinners = (LinearLayout) llFirst.getChildAt(0);

        for (int i = 0; i < mSpinners.getChildCount(); i++)
        {
            NumberPicker picker = (NumberPicker) mSpinners.getChildAt(i);
            mPickers.add(i, picker);
        }
    }

    /**
     * 设置时间
     * @param strDate  yyyy-mm-dd
     */
    public void setDate(String strDate)
    {
        int day, month, year;
        if (!TextUtils.isEmpty(strDate))
        {
            String[] dateValues = strDate.split("-");
            if (dateValues.length == 3)
            {
                year = Integer.parseInt(dateValues[0]);
                month = Integer.parseInt(dateValues[1]) - 1;
                day = Integer.parseInt(dateValues[2]);
                updateDate(year, month, day);
                return;
            }
        }

        //error
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);
        updateDate(year, month, day);
    }

    /**
     * 获得时间
     * @return  yyyy-mm-dd
     */
    public String getDate()
    {
        StringBuilder sbDate = new StringBuilder();
        sbDate.append(format2Digits(getYear())).append("-")
                .append(format2Digits(getMonth()+1)).append("-")
                .append(format2Digits(getDayOfMonth()));
        return sbDate.toString();
    }

    private String format2Digits(int value)
    {
        return String.format("%02d",value);
    }


    /**
     * 设置picker间隔
     *
     * @param margin
     */
    public void setPickerMargin(int margin)
    {
        for (NumberPicker picker : mPickers)
        {
            LinearLayout.LayoutParams lps = (LinearLayout.LayoutParams) picker.getLayoutParams();
            lps.setMargins(margin, 0, margin, 0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            {
                lps.setMarginStart(margin);
                lps.setMarginEnd(margin);
            }
            picker.setLayoutParams(lps);
        }
    }

    /**
     * 设置时间选择器的分割线颜色
     */
    public void setDividerColor(int color)
    {
        for (int i = 0; i < mPickers.size(); i++)
        {
            NumberPicker picker = mPickers.get(i);

            try
            {
                Field pf = NumberPicker.class.getDeclaredField("mSelectionDivider");
                pf.setAccessible(true);
                pf.set(picker, new ColorDrawable(color));
            }
            catch (NoSuchFieldException e)
            {
                e.printStackTrace();
            }
            catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }

        }
    }
}