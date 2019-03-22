package com.peihou.willgood2.custom;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.peihou.willgood2.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class AlermDialog4 extends Dialog {
    Unbinder unbinder;
    @BindView(R.id.tv_title) TextView tv_title;
    @BindView(R.id.tv_content) TextView tv_content;
    public AlermDialog4(@NonNull Context context) {
        super(context, R.style.MyDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_alerm4);
        unbinder=ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_cancel,R.id.btn_ensure})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.btn_cancel:
                if (onNegativeClickListener!=null){
                    onNegativeClickListener.onNegativeClick();
                }
                break;
            case R.id.btn_ensure:
                if (onPositiveClickListener!=null){
                    onPositiveClickListener.onPositiveClick();
                }
                break;
        }
    }
    int mode=0;//为0时，来电报警 1为断电报警,2,温度报警，3，湿度报警，4，电压报警，5，电流报警，6，功率报警，7，开关量报警
    String content;//显示内容

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("AlermDialog4","-->"+mode);
        if (mode==0){
            tv_title.setText("来电报警");
            tv_content.setText("来电报警，请注意");
        }else if (mode==1){
           tv_title.setText("断电报警");
            tv_content.setText("断电报警，请注意");
        }else if (mode==2){
            tv_title.setText("温度报警");
            tv_content.setText("温度报警，请注意");
        }else if (mode==3){
            tv_title.setText("湿度报警");
            tv_content.setText("湿度报警，请注意");
        }else if (mode==4){
            tv_title.setText("电压报警");
            tv_content.setText("电压报警，请注意");
        }else if (mode==5){
            tv_title.setText("电流报警");
            tv_content.setText("电流报警，请注意");
        }else if (mode==6){
            tv_title.setText("功率报警");
            tv_content.setText("功率报警，请注意");
        }else if (mode==7){
            tv_title.setText("开关量报警");
            tv_content.setText("开关量报警，请注意");
        }
    }
    private int inputType;

    public int getInputType() {
        return inputType;
    }

    public void setInputType(int inputType) {
        this.inputType = inputType;
    }

    private String tips;

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getMode() {
        return mode;
    }
    String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("dialog","-->onStop");
        unbinder.unbind();
    }

    private OnPositiveClickListener onPositiveClickListener;

    public void setOnPositiveClickListener(OnPositiveClickListener onPositiveClickListener) {


        this.onPositiveClickListener = onPositiveClickListener;
    }

    private OnNegativeClickListener onNegativeClickListener;

    public void setOnNegativeClickListener(OnNegativeClickListener onNegativeClickListener) {

        this.onNegativeClickListener = onNegativeClickListener;
    }

    public interface OnPositiveClickListener {
        void onPositiveClick();
    }

    public interface OnNegativeClickListener {
        void onNegativeClick();
    }
}
