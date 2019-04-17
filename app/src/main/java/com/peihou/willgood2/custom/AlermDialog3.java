package com.peihou.willgood2.custom;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.peihou.willgood2.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class AlermDialog3 extends Dialog {
    Unbinder unbinder;
    @BindView(R.id.tv_title) TextView tv_title;
    @BindView(R.id.et_1) EditText et_1;
    @BindView(R.id.img_point) ImageView img_point;
    @BindView(R.id.img_point2) ImageView img_point2;
    public AlermDialog3(@NonNull Context context) {
        super(context, R.style.MyDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_alerm3);
        unbinder=ButterKnife.bind(this);
    }

    @OnClick({R.id.rl_3,R.id.rl_4,R.id.btn_cancel,R.id.btn_ensure})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.rl_3:
                if (open==0){
                    break;
                }
                open=0;
                setSwitch();
                break;
            case R.id.rl_4:
                if (open==1)
                    break;
                open=1;
                setSwitch();
                break;
            case R.id.btn_cancel:
                if (onNegativeClickListener!=null){
                    onNegativeClickListener.onNegativeClick();
                }
                break;
            case R.id.btn_ensure:
                if (onPositiveClickListener!=null){
                    content=et_1.getText().toString();
                    open=getOpen();
                    onPositiveClickListener.onPositiveClick();
                }
                break;
        }
    }
    int mode=0;//为0时，来电报警 1为断电报警
    String content;//显示内容
    String value;
    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("dialog","-->onStart");
        et_1.requestFocus();
        et_1.setSelection(et_1.getText().length());
        setSwitch();
    }
    int open=0;

    public int getOpen() {
        return open;
    }

    public void setOpen(int open) {
        this.open = open;
    }

    private void setSwitch(){
        if (open==0){
            img_point.setImageResource(R.mipmap.img_point);
            img_point2.setImageResource(R.mipmap.img_point2);
        }else {
            img_point.setImageResource(R.mipmap.img_point2);
            img_point2.setImageResource(R.mipmap.img_point);
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
