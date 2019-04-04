package com.peihou.willgood2.location;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.CountDownTimer;
import android.os.IBinder;
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
import com.peihou.willgood2.R;
import com.peihou.willgood2.custom.ChangeDialog;
import com.peihou.willgood2.custom.JogDialog;
import com.peihou.willgood2.service.MQService;
import com.peihou.willgood2.utils.ToastUtil;
import com.peihou.willgood2.utils.http.BaseWeakAsyncTask;
import com.peihou.willgood2.utils.http.HttpUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

public class LocationSetActivity extends BaseActivity {

    @BindView(R.id.tv_title) TextView tv_title;//页面标题
    @BindView(R.id.tv_default_time) TextView tv_default_time;//地图默认刷新频率
    @BindView(R.id.tv_jog_auto) TextView tv_jog_auto;//清除运动轨迹
    @BindView(R.id.cb)
    CheckBox cb;
    @BindView(R.id.tv_1) TextView tv_1;
    @BindView(R.id.cb2) CheckBox cb2;
    @BindView(R.id.tv_2) TextView tv_2;
    @BindView(R.id.cb3) CheckBox cb3;
    @BindView(R.id.tv_3) TextView tv_3;
    @BindView(R.id.cb4) CheckBox cb4;
    @BindView(R.id.tv_4) TextView tv_4;
    @BindView(R.id.cb5) CheckBox cb5;
    @BindView(R.id.tv_5) TextView tv_5;
    int mcuVersion;
    String deviceMac;
    String topicName;
    long deviceId;
    @Override
    public void initParms(Bundle parms) {
        deviceMac=parms.getString("deviceMac");
        mcuVersion=parms.getInt("mcuVersion");
        deviceId=parms.getLong("deviceId");
    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_location_set;
    }
    Map<String, Object> params = new HashMap<>();

    @Override
    public void initView(View view) {
        topicName="qjjc/gateway/"+deviceMac+"/server_to_client";
        tv_title.setText("地图设置");
        tv_default_time.setText("刷新频率");
        tv_jog_auto.setText("清除运动轨迹");
        params.put("deviceId",deviceId);
        Intent service=new Intent(this,MQService.class);
        bind=bindService(service,connection,Context.BIND_AUTO_CREATE);

    }
    boolean checked=false;
    int choices=10;
    boolean success=false;
    @OnClick({R.id.img_back,R.id.rl_bottom,R.id.btn_submit,R.id.linear_1,R.id.linear_2,R.id.linear_3,R.id.linear_4,R.id.linear_5})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.img_back:
                finish();
                break;
            case R.id.rl_bottom:
                changeDialog();
                break;
            case R.id.btn_submit:
                if (mqService!=null){
                    success=mqService.sendLocation(topicName,mcuVersion,choices);
                    if (success){
                        countTimer.start();
                    }else {
                        ToastUtil.showShort(this,"提交失败");
                    }
                }
                break;
            case R.id.linear_1:
                checked=cb.isChecked();
                if (checked){
                    cb.setChecked(false);
                    choices=10;
                }else {
                    choices=10;
                    cb.setChecked(true);
                    cb2.setChecked(false);
                    cb3.setChecked(false);
                    cb4.setChecked(false);
                    cb5.setChecked(false);
                }
                break;
            case R.id.linear_2:
                checked=cb2.isChecked();
                if (checked){
                    cb2.setChecked(false);

                    choices=10;
                }else {
                    cb.setChecked(false);
                    cb2.setChecked(true);
                    cb3.setChecked(false);
                    cb4.setChecked(false);
                    cb5.setChecked(false);
                    choices=20;

                }
                break;
            case R.id.linear_3:
                checked=cb3.isChecked();
                if (checked){
                    choices=10;
                    cb3.setChecked(false);
                }else {
                    cb.setChecked(false);
                    cb2.setChecked(false);
                    cb3.setChecked(true);
                    cb4.setChecked(false);
                    cb5.setChecked(false);

                    choices=30;
                }
                break;
            case R.id.linear_4:
                checked=cb4.isChecked();
                if (checked){
                    cb4.setChecked(false);
                    choices=10;

                }else {
                    cb.setChecked(false);
                    cb2.setChecked(false);
                    cb3.setChecked(false);
                    cb4.setChecked(true);
                    cb5.setChecked(false);
                    choices=60;
                }
                break;
            case R.id.linear_5:
                checked=cb5.isChecked();
                if (checked){
                    choices=10;
                    cb5.setChecked(false);
                }else {
                    cb.setChecked(false);
                    cb2.setChecked(false);
                    cb3.setChecked(false);
                    cb4.setChecked(false);
                    cb5.setChecked(true);
                    choices=120;
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (popupWindow2!=null && popupWindow2.isShowing()){
            popupWindow2.dismiss();
        }

        if (bind){
            unbindService(connection);
        }
    }

    private boolean bind=false;
    MQService mqService;
    ServiceConnection connection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MQService.LocalBinder binder= (MQService.LocalBinder) service;
            mqService=binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    //自定义点动时间
    ChangeDialog dialog;
    private void changeDialog(){
        if (dialog!=null && dialog.isShowing()){
            return;
        }
        dialog=new ChangeDialog(this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMode(1);
        dialog.setTips("是否清除运动轨迹?");
        backgroundAlpha(0.4f);
        dialog.setOnNegativeClickListener(new ChangeDialog.OnNegativeClickListener() {
            @Override
            public void onNegativeClick() {
                dialog.dismiss();
            }
        });
        dialog.setOnPositiveClickListener(new ChangeDialog.OnPositiveClickListener() {
            @Override
            public void onPositiveClick() {
                dialog.dismiss();
                new DeleteDeviceTrajectoryaAsync(LocationSetActivity.this).execute(params);
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
    class DeleteDeviceTrajectoryaAsync extends BaseWeakAsyncTask<Map<String,Object>,Void,Integer,LocationSetActivity>{

        public DeleteDeviceTrajectoryaAsync(LocationSetActivity locationSetActivity) {
            super(locationSetActivity);
        }

        @Override
        protected Integer doInBackground(LocationSetActivity locationSetActivity, Map<String, Object>... maps) {
            Map<String,Object> params= null;
            int code=0;
            try {
                params = maps[0];
                String url=HttpUtils.ipAddress+"data/deleteDeviceTrajectory";
                String result=HttpUtils.requestPost(url,params);
                if (!TextUtils.isEmpty(result)){
                    JSONObject jsonObject=new JSONObject(result);
                    code=jsonObject.getInt("returnCode");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return code;
        }

        @Override
        protected void onPostExecute(LocationSetActivity locationSetActivity, Integer integer) {
            if (integer==100){
                ToastUtil.showShort(LocationSetActivity.this,"清除成功");
                setResult(1000);
                finish();
            }
        }
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
                ToastUtil.showShort(LocationSetActivity.this,"提交成功");
                finish();
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
        if (popupWindow2==null)
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

}
