package com.peihou.willgood2.custom;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.peihou.willgood2.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class AlermDialog extends Dialog {
    Unbinder unbinder;
    @BindView(R.id.tv_title) TextView tv_title;
    @BindView(R.id.et_name) EditText et_name;
    public AlermDialog(@NonNull Context context) {
        super(context, R.style.MyDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_alerm);
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
                    content=et_name.getText().toString();
                    onPositiveClickListener.onPositiveClick();
                }
                break;
        }
    }
    int mode=0;//为0时，来电报警 1为断电报警
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
        Log.i("dialog","-->onStart");
        if (mode==0){
            tv_title.setText("来电报警");
            et_name.setHint("设备已来电");
            et_name.setText("设备已来电");
        }else if (mode==1){
            et_name.setHint("设备已断电");
           tv_title.setText("断电报警");
            et_name.setText("设备已断电");
        }
        et_name.requestFocus();
        et_name.setSelection(et_name.getText().length());
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
