package com.peihou.willgood2.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jwenfeng.library.pulltorefresh.view.FooterView;
import com.peihou.willgood2.R;

public class MyLoadMoreView extends FrameLayout implements FooterView {

    private TextView tv;
    private ImageView arrow;
    private ProgressBar progressBar;

    public MyLoadMoreView(Context context) {
        this(context,null);
    }

    public MyLoadMoreView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MyLoadMoreView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_footer,null);
        addView(view);
        tv = (TextView) view.findViewById(R.id.tv1);
        arrow = (ImageView)view.findViewById(R.id.imgLoding);
        progressBar = (ProgressBar) view.findViewById(R.id.proLoding);
    }

    @Override
    public void begin() {

    }

    @Override
    public void progress(float progress, float all) {
        float s = progress / all;
        if (s >= 0.9f){
            arrow.setRotation(0);
        }else{
            arrow.setRotation(180);
        }
        if (progress >= all-10){
            tv.setText("松开立即加载更多");
        }else{
            tv.setText("上拉可以加载更多");
        }
    }

    @Override
    public void finishing(float progress, float all) {

    }

    @Override
    public void loading() {
        arrow.setVisibility(GONE);
        progressBar.setVisibility(VISIBLE);
        tv.setText("正在加载更多的数据...");
    }

    @Override
    public void normal() {
        arrow.setVisibility(VISIBLE);
        progressBar.setVisibility(GONE);
        tv.setText("上拉可以加载更多");
    }

    @Override
    public View getView() {
        return this;
    }
}
