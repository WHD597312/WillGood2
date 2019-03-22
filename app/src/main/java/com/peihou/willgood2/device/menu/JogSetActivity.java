package com.peihou.willgood2.device.menu;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import com.peihou.willgood2.BaseActivity;
import
 com.peihou.willgood2.R;
import com.peihou.willgood2.custom.JogDialog;
import com.peihou.willgood2.device.DeviceItemActivity;
import com.peihou.willgood2.service.MQService;
import com.peihou.willgood2.utils.ToastUtil;
import com.peihou.willgood2.utils.Utils;
import com.peihou.willgood2.utils.WeakRefHandler;


import java.math.BigDecimal;

import butterknife.BindView;
import butterknife.OnClick;

public class JogSetActivity extends BaseActivity {


    @BindView(R.id.cb) CheckBox cb;
    @BindView(R.id.tv_1) TextView tv_1;
    @BindView(R.id.cb2) CheckBox cb2;
    @BindView(R.id.tv_2) TextView tv_2;
    @BindView(R.id.cb3) CheckBox cb3;
    @BindView(R.id.tv_3) TextView tv_3;
    @BindView(R.id.cb4) CheckBox cb4;
    @BindView(R.id.tv_4) TextView tv_4;
    @BindView(R.id.cb5) CheckBox cb5;
    @BindView(R.id.tv_5) TextView tv_5;
    @BindView(R.id.tv_jog_value) TextView tv_jog_value;

    private String deviceMac;
    int mcuVersion;
    String topicName;
    double choices=1;//设定的点动秒，默认1s
    boolean online;

    @Override
    public void initParms(Bundle parms) {
        deviceMac=parms.getString("deviceMac");
        mcuVersion=parms.getInt("mcuVersion");
        choices=parms.getDouble("jog");
        online=parms.getBoolean("online");
    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_jog_set;
    }

    @Override
    public void initView(View view) {
        topicName="qjjc/gateway/"+deviceMac+"/server_to_client";
//        topicName = "qjjc/gateway/" + deviceMac + "/client_to_server";
        Intent service=new Intent(this,MQService.class);
        bind=bindService(service,connection,Context.BIND_AUTO_CREATE);
        IntentFilter filter=new IntentFilter("JogSetActivity");
        filter.addAction("offline");
        receiver=new MessageReceiver();
        tv_jog_value.setText(choices+"");
        registerReceiver(receiver,filter);
    }
    boolean checked=false;
    int click=0;
    @OnClick({R.id.img_back,R.id.rl_bottom,R.id.btn_submit,R.id.cb,R.id.tv_1,R.id.cb2,R.id.tv_2,R.id.cb3,R.id.tv_3,R.id.cb4,R.id.tv_4,R.id.cb5,R.id.tv_5})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.img_back:
                finish();
                break;
            case R.id.rl_bottom:
                changeDialog();
                break;
            case R.id.btn_submit:
                if (!online){
                    ToastUtil.showShort(this,"设备已离线");
                    break;
                }

                if (popupWindow2!=null && popupWindow2.isShowing()){
                    ToastUtil.showShort(this,"请稍后...");
                    break;
                }
                BigDecimal b = new BigDecimal(choices);
                choices = b.setScale(1, BigDecimal.ROUND_HALF_DOWN).doubleValue();
                if (mqService!=null){
                    int jog= (int) (choices*10);
                    boolean success=mqService.sendJog(topicName,mcuVersion,jog);
                    click=1;
                    countTimer.start();
                }
                break;
            case R.id.cb:
                checked=cb.isChecked();
                if (checked){
                    choices=1;
                    cb2.setChecked(false);
                    cb3.setChecked(false);
                    cb4.setChecked(false);
                    cb5.setChecked(false);
                }else {
                    choices=1;
                }
                break;
            case R.id.tv_1:
                checked=cb.isChecked();
                if (checked){
                    choices=1;
                    cb2.setChecked(false);
                    cb3.setChecked(false);
                    cb4.setChecked(false);
                    cb5.setChecked(false);
                }else {
                    choices=1;
                }
                break;
            case R.id.cb2:
                checked=cb2.isChecked();
                if (checked){
                    choices=2;
                    cb.setChecked(false);
                    cb3.setChecked(false);
                    cb4.setChecked(false);
                    cb5.setChecked(false);
                }else {
                    choices=1;
                }
                break;
            case R.id.tv_2:
                checked=cb2.isChecked();
                if (checked){
                    cb.setChecked(false);
                    cb3.setChecked(false);
                    cb4.setChecked(false);
                    cb5.setChecked(false);
                    choices=2;
                }else {
                    choices=1;
                }
                break;
            case R.id.cb3:
                checked=cb3.isChecked();
                if (checked){
                    choices=3;
                    cb.setChecked(false);
                    cb2.setChecked(false);
                    cb4.setChecked(false);
                    cb5.setChecked(false);
                }else {
                    choices=1;
                }
                tv_jog_value.setText(choices+"");
                break;
            case R.id.tv_3:
                checked=cb3.isChecked();
                if (checked){
                    choices=3;
                    cb.setChecked(false);
                    cb2.setChecked(false);
                    cb4.setChecked(false);
                    cb5.setChecked(false);
                }else {
                    choices=1;
                }
                tv_jog_value.setText(choices+"");
                break;
            case R.id.cb4:
                checked=cb4.isChecked();
                if (checked){
                    choices=5;
                    cb.setChecked(false);
                    cb2.setChecked(false);
                    cb3.setChecked(false);
                    cb5.setChecked(false);
                }else {
                    choices=1;
                }
                tv_jog_value.setText(choices+"");
                break;
            case R.id.tv_4:
                checked=cb4.isChecked();
                if (checked){
                    choices=5;
                    cb.setChecked(false);
                    cb2.setChecked(false);
                    cb3.setChecked(false);
                    cb5.setChecked(false);
                }else {
                    choices=1;
                }
                tv_jog_value.setText(choices+"");
                break;
            case R.id.cb5:
                checked=cb5.isChecked();
                if (checked){
                    choices=10;
                    cb.setChecked(false);
                    cb2.setChecked(false);
                    cb3.setChecked(false);
                    cb4.setChecked(false);
                }else {
                    choices=1;
                }
                tv_jog_value.setText(choices+"");
                break;
            case R.id.tv_5:
                checked=cb5.isChecked();
                if (checked){
                    choices=10;
                    cb.setChecked(false);
                    cb2.setChecked(false);
                    cb3.setChecked(false);
                    cb4.setChecked(false);
                }else {
                    choices=1;
                }
                tv_jog_value.setText(choices+"");
                break;

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bind){
            unbindService(connection);
        }
        if (receiver!=null){
            unregisterReceiver(receiver);
        }
        handler.removeCallbacksAndMessages(null);
    }
    public static boolean running=false;

    @Override
    protected void onStart() {
        super.onStart();
        running=true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        running=false;
    }
    CountTimer countTimer=new CountTimer(2000,1000);

    class CountTimer extends CountDownTimer {

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public CountTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            popupmenuWindow3();
        }

        @Override
        public void onFinish() {
            if (popupWindow2!=null && popupWindow2.isShowing()){
                popupWindow2.dismiss();
            }
        }
    }
    private PopupWindow popupWindow2;

    public void popupmenuWindow3() {
        if (popupWindow2 != null && popupWindow2.isShowing()) {
            return;
        }
        View view = View.inflate(this, R.layout.progress, null);
        TextView tv_load=view.findViewById(R.id.tv_load);
        tv_load.setTextColor(getResources().getColor(R.color.white));
        popupWindow2 = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        //添加弹出、弹入的动画
        popupWindow2.setAnimationStyle(R.style.Popupwindow);
        popupWindow2.setFocusable(false);
        popupWindow2.setOutsideTouchable(false);
        backgroundAlpha(0.6f);
        popupWindow2.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1.0f);
            }
        });
//        ColorDrawable dw = new ColorDrawable(0x30000000);
//        popupWindow.setBackgroundDrawable(dw);
//        popupWindow2.showAsDropDown(et_wifi, 0, -20);
        popupWindow2.showAtLocation(tv_1, Gravity.CENTER, 0, 0);
        //添加按键事件监听
    }

    boolean bind=false;
    MQService mqService;
    ServiceConnection connection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MQService.LocalBinder binder= (MQService.LocalBinder) service;
            mqService=binder.getService();
            if (mqService!=null){
                mqService.getData(topicName,0x44);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    MessageReceiver receiver;

    class MessageReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String action = intent.getAction();
                String macAddress = intent.getStringExtra("macAddress");
                if ("offline".equals(action)) {
                    if (macAddress.equals(deviceMac)) {
                        online = false;
                    }
                } else {
                    if (macAddress.equals(deviceMac)) {
                        double lineJog = intent.getDoubleExtra("lineJog", 0);
                        boolean online2=intent.getBooleanExtra("online",false);
                        online=online2;
                        choices = lineJog;
                        tv_jog_value.setText(lineJog + "");
                        if (click == 1) {
                            if (popupWindow2 != null && popupWindow2.isShowing()){
                                popupWindow2.dismiss();
                            }
                            Message msg=handler.obtainMessage();
                            msg.what=1;
                            msg.obj=lineJog;
                            click=0;
                            handler.sendMessage(msg);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    Handler.Callback callback=new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what==1){
                if (mqService!=null){
                    mqService.starSpeech("控制成功");
                    Intent intent=new Intent();
                    double lineJog= (double) msg.obj;
                    intent.putExtra("jog",lineJog);
                    setResult(7000,intent);
                    finish();
                }
            }
            return true;
        }
    };
    Handler handler = new WeakRefHandler(callback);

    //自定义点动时间
    JogDialog dialog;
    private void changeDialog(){
        if (dialog!=null && dialog.isShowing()){
            return;
        }
        dialog=new JogDialog(this);
        dialog.setContent(choices+"");
        dialog.setCanceledOnTouchOutside(false);
        backgroundAlpha(0.4f);
        dialog.setOnNegativeClickListener(new JogDialog.OnNegativeClickListener() {
            @Override
            public void onNegativeClick() {
                dialog.dismiss();
            }
        });
        dialog.setOnPositiveClickListener(new JogDialog.OnPositiveClickListener() {
            @Override
            public void onPositiveClick() {
                String content = dialog.getContent();
                if (TextUtils.isEmpty(content)) {
                    ToastUtil.show(JogSetActivity.this, "编辑内容不能为空", Toast.LENGTH_SHORT);
                } else {
                    boolean isNumber=Utils.isNumeric(content);
                    if (isNumber){
                        double jog=Double.parseDouble(content);
                        if (jog<0.5){
                            ToastUtil.show(JogSetActivity.this, "点动时间最小为0.5s", Toast.LENGTH_SHORT);
                        }else if (jog>1000){
                            ToastUtil.show(JogSetActivity.this, "点动时间最大为999s", Toast.LENGTH_SHORT);
                        }else {
                            dialog.dismiss();
                            choices=jog; ToastUtil.show(JogSetActivity.this, "设置成功，请提交", Toast.LENGTH_SHORT);
                        }
                    }else {
                        ToastUtil.showShort(JogSetActivity.this,"内容不是数字");
                    }
                }
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                backgroundAlpha(1.0f);
            }
        });
        dialog.show();
    }
    //设置蒙版
    private void backgroundAlpha(float f) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = f;
        getWindow().setAttributes(lp);
    }
    @Override
    public void doBusiness(Context mContext) {

    }

}
