package com.peihou.willgood2.custom;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.peihou.willgood2.R;
import com.wang.avi.AVLoadingIndicatorView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class DialogLoad extends Dialog {

    Unbinder unbinder;
    private String load;
    Context context;
    @BindView(R.id.avi)
    AVLoadingIndicatorView avi;//加载图片
    @BindView(R.id.tv_load) TextView tv_load;
    public DialogLoad(@NonNull Context context) {
        super(context,R.style.MyDialog);
        this.context=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_load);
        unbinder= ButterKnife.bind(this);
    }
//    AnimationDrawable animationDrawable;
    @Override
    protected void onStart() {
        super.onStart();
       startAnim();
//        img_load.setImageResource(R.drawable.load);
//        animationDrawable = (AnimationDrawable) img_load.getDrawable();
//        animationDrawable.start();
        if (!TextUtils.isEmpty(load)){
            tv_load.setText(load);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
//        if (animationDrawable!=null){
//            animationDrawable.stop();
//        }
       stopAnim();
        unbinder.unbind();
    }

    public void setLoad(String load) {
        this.load = load;
    }

    public String getLoad() {
        return load;
    }
    void startAnim(){
        avi.show();
        // or avi.smoothToShow();
    }

    void stopAnim(){
        avi.hide();
        // or avi.smoothToHide();
    }
}
