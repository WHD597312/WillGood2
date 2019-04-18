package com.peihou.willgood2.custom;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jwenfeng.library.pulltorefresh.view.HeadView;
import com.peihou.willgood2.R;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

public class MyHeadRefreshView extends FrameLayout implements HeadView {
    private TextView tv1,tv2;
    private ImageView arrow;
    private ProgressBar progressBar;

    public MyHeadRefreshView(Context context) {
        this(context,null);
    }

    public MyHeadRefreshView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MyHeadRefreshView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    String time;
    SharedPreferences sharedPreferences;
    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_header,null);
        addView(view);
        tv1 = (TextView) view.findViewById(R.id.tv1);
        tv2 = (TextView) view.findViewById(R.id.tv2);
        arrow = (ImageView) view.findViewById(R.id.imgLoding);
        progressBar = (ProgressBar) view.findViewById(R.id.proLoding);
        sharedPreferences=context.getSharedPreferences("my",MODE_PRIVATE);
    }

    @Override
    public void begin() {
        time=sharedPreferences.getString("shopDataLoad1","0");
        tv2.setVisibility(GONE);
//        if("0".equals(time))
//            tv2.setVisibility(GONE);
//        else{
//            tv2.setVisibility(VISIBLE);
//            Date date = new Date();
//            long l = 24*60*60*1000; //每天的毫秒数
//            //date.getTime()是现在的毫秒数，它 减去 当天零点到现在的毫秒数（ 现在的毫秒数%一天总的毫秒数，取余。），理论上等于零点的毫秒数，不过这个毫秒数是UTC+0时区的。
//            //减8个小时的毫秒值是为了解决时区的问题。
//            long nowTime= (date.getTime() - (date.getTime()%l) - 8* 60 * 60 *1000);
//
//            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");//这个是你要转成后的时间的格式
//            String sd = sdf.format(new Date(Long.parseLong(String.valueOf(time))));   // 时间戳转换成时间
//            Log.e("qqqT222", sd);
//            String day;
//            if(Long.parseLong(time)>nowTime){
//                day="今天";
//            }
//            else if(((Long.parseLong(time)+24*60*60*1000)>nowTime)){
//                day="昨天";
//            }
//            else if(((Long.parseLong(time)+2*24*60*60*1000)>nowTime)){
//                day="前天";
//            }else {
//                SimpleDateFormat sdf2 = new SimpleDateFormat("MM月dd日");//这个是你要转成后的时间的格式
//                day = sdf2.format(new Date(Long.parseLong(String.valueOf(time))));   // 时间戳转换成时间
//            }
//            tv2.setText("最后更新："+day+" "+sd);
//        }
    }


    @Override
    public void progress(float progress, float all) {
        float s = progress / all;
        if (s >= 0.9f){
            arrow.setRotation(180);
        }else{
            arrow.setRotation(0);
        }
        if (progress >= all-10){
            tv1.setText("松开立即刷新");
        }else{
            tv1.setText("下拉可以刷新");
        }
    }

    @Override
    public void finishing(float progress, float all) {

    }

    @Override
    public void loading() {
        arrow.setVisibility(GONE);
        progressBar.setVisibility(VISIBLE);
        tv1.setText("正在刷新数据中...");
    }

    @Override
    public void normal() {
        arrow.setVisibility(VISIBLE);
        progressBar.setVisibility(GONE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        long timeStamp = System.currentTimeMillis();
        editor.putString("shopDataLoad1",timeStamp+"");
        Log.e("qqqT",timeStamp+"");
        boolean success=editor.commit();
    }

    @Override
    public View getView() {
        return this;
    }
}
