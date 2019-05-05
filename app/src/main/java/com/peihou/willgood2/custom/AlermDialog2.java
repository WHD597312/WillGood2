package com.peihou.willgood2.custom;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.peihou.willgood2.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class AlermDialog2 extends Dialog {
    Unbinder unbinder;
    @BindView(R.id.tv_title) TextView tv_title;
    @BindView(R.id.et_1) EditText et_1;
    @BindView(R.id.tv_2) TextView tv_2;
    @BindView(R.id.et_2) EditText et_2;
    @BindView(R.id.img_point)
    ImageView img_point;
    @BindView(R.id.img_point2) ImageView img_point2;
    public AlermDialog2(@NonNull Context context) {
        super(context, R.style.MyDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_alerm2);
        unbinder=ButterKnife.bind(this);
    }


    @OnClick({R.id.img_point,R.id.tv_point,R.id.img_point2,R.id.tv_point2,R.id.btn_cancel,R.id.btn_ensure})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.img_point:
                if (open==1)
                    break;
                open=1;
                setSwitch();
                break;
            case R.id.tv_point:
                if (open==1)
                    break;
                open=1;
                setSwitch();
                break;
            case R.id.img_point2:
                if (open==0)
                    break;
                open=0;
                setSwitch();
                break;
            case R.id.tv_point2:
                if (open==0)
                    break;
                open=0;
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
                    value=et_2.getText().toString();
                    open=getOpen();
                    onPositiveClickListener.onPositiveClick();
                }
                break;
        }
    }
    int mode=0;//为0时，温度报警 1为湿度报警，2为电压报警，3，电流报警，4，功率报警
    String content;//显示内容
    String value;

    int open=1;

    public int getOpen() {
        return open;
    }

    public void setOpen(int open) {
        this.open = open;
    }

    private void setSwitch(){
        if (open==1){
            img_point.setImageResource(R.mipmap.img_point);
            img_point2.setImageResource(R.mipmap.img_point2);
        }else {
            img_point.setImageResource(R.mipmap.img_point2);
            img_point2.setImageResource(R.mipmap.img_point);
        }
    }
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

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
            tv_title.setText("温度报警");
            et_1.setHint("温度报警中,请注意");
            et_1.setText("温度报警中,请注意");
            tv_2.setText("请输入报警数值,-128.0~512.9℃");
            et_2.setText(value);
            et_2.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL|InputType.TYPE_NUMBER_FLAG_SIGNED);
        }else if (mode==1){
           tv_title.setText("湿度报警");
            et_1.setHint("湿度报警中,请注意");
            et_1.setText("湿度报警中,请注意");
            tv_2.setText("请输入报警数值,0.0~99.9%");
            et_2.setText(value);
            et_2.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL|InputType.TYPE_NUMBER_FLAG_SIGNED);
        }else if (mode==2){
            tv_title.setText("电压报警");
            et_1.setText("电压报警中,请注意");
            et_1.setHint("电压报警中,请注意");
            tv_2.setText("请输入报警数值,0.0~1024.9V");
            et_2.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL|InputType.TYPE_NUMBER_FLAG_SIGNED);
            et_2.setText(value);
        } else if (mode==3){
            tv_title.setText("电流报警");
            et_1.setText("电流报警中,请注意");
            et_1.setHint("电流报警中,请注意");
            tv_2.setText("请输入报警数值,0.0~1024.9A");
            et_2.setText(value);
            et_2.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL|InputType.TYPE_NUMBER_FLAG_SIGNED);
        }else if (mode==4){
            tv_title.setText("功率报警");
            et_1.setText("功率报警中,请注意");
            et_1.setHint("功率报警中,请注意");
            tv_2.setText("请输入报警数值,0.0~131071.9W");
            et_2.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL|InputType.TYPE_NUMBER_FLAG_SIGNED);
            et_2.setText(value);
        }
        setSwitch();
        et_1.requestFocus();
        et_1.setSelection(et_1.getText().length());
        et_2.requestFocus();
        et_2.setSelection(et_2.getText().length());
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
