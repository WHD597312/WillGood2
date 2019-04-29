package com.peihou.willgood2.device;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.peihou.willgood2.BaseActivity;
import com.peihou.willgood2.R;
import com.peihou.willgood2.utils.ToastUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

public class ShareDeviceActivity extends BaseActivity {



    @BindView(R.id.tv_timer) TextView tv_timer;//定时控制
    @BindView(R.id.tv_linked) TextView tv_linked;//联动控制
    @BindView(R.id.tv_switch_check) TextView tv_switch_check;//开关量测量
    @BindView(R.id.tv_alerm) TextView tv_alerm;//报警设置
    @BindView(R.id.tv_location) TextView tv_location;//地图定位
    @BindView(R.id.tv_moni_check) TextView tv_moni_check;//模拟量测量
    @BindView(R.id.tv_inter_lock) TextView tv_inter_lock;//互锁模式
    @BindView(R.id.tv_device) TextView tv_device;


    int timer=0,linked=0,switchCheck=0,alerm=0,location=0,moniCheck=0,interLock=0;//为0时表示没有选中，为1时表示选中状态
    long deviceId;
    String name;
    String devicePassword;
    Map<Integer,Integer> map=new HashMap<>();
    String deviceMac;
    @Override
    public void initParms(Bundle parms) {

       deviceId=parms.getLong("deviceId");
       name=parms.getString("name");
       devicePassword=parms.getString("devicePassword");
        deviceMac=parms.getString("deviceMac");
    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_share_device;
    }

    @Override
    public void initView(View view) {
        tv_device.setText(name+"");
    }

    @Override
    public void doBusiness(Context mContext) {

    }
    @OnClick({R.id.img_back,R.id.tv_timer,R.id.tv_linked,R.id.tv_switch_check,R.id.tv_alerm,R.id.tv_location,R.id.tv_moni_check,R.id.tv_inter_lock,R.id.btn_share})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.img_back:
                finish();
                break;
            case R.id.tv_timer:
                if (timer==0){
                    timer=1;
                    setTextViewBk(tv_timer,timer);
                }else if (timer==1){
                    timer=0;
                    setTextViewBk(tv_timer,timer);
                }
                break;
            case R.id.tv_linked:
                if (linked==0){
                    linked=1;
                    setTextViewBk(tv_linked,linked);
                }else if (linked==1){
                    linked=0;
                    setTextViewBk(tv_linked,linked);
                }
                break;
            case R.id.tv_switch_check:
                if (switchCheck==0){
                    switchCheck=1;
                    setTextViewBk(tv_switch_check,switchCheck);
                }else if (switchCheck==1){
                    switchCheck=0;
                    setTextViewBk(tv_switch_check,switchCheck);
                }
                break;
            case R.id.tv_alerm:
                if (alerm==0){
                    alerm=1;
                    setTextViewBk(tv_alerm,alerm);
                }else if (alerm==1){
                    alerm=0;
                    setTextViewBk(tv_alerm,alerm);
                }
                break;
            case R.id.tv_location:
                if (location==0){
                    location=1;
                    setTextViewBk(tv_location,location);
                }else if (location==1){
                    location=0;
                    setTextViewBk(tv_location,location);
                }
                break;
            case R.id.tv_moni_check:
                if (moniCheck==0){
                    moniCheck=1;
                    setTextViewBk(tv_moni_check,moniCheck);
                }else if (moniCheck==1){
                    moniCheck=0;
                    setTextViewBk(tv_moni_check,moniCheck);
                }
                break;
            case R.id.tv_inter_lock:
                if (interLock==0){
                    interLock=1;
                    setTextViewBk(tv_inter_lock,interLock);
                }else if (interLock==1){
                    interLock=0;
                    setTextViewBk(tv_inter_lock,interLock);
                }
                break;
            case R.id.btn_share:
                //shared/deviceId=0/deviceTimer=1/deviceLinked=0/deviceSwitch=0/deviceAlarm=0/deviceMap=0/deviceControl=0/deviceAnalog=0
//                            shared/deviceId=2/deviceTimer=1/deviceLinked=1/deviceSwitch=1/deviceAlarm=1/deviceMap=1/deviceControl=1/deviceAnalog=1/deviceMac=8655330313814
                String share="shared/deviceId="+deviceId+"/deviceTimer="+timer+"/deviceLinked="+linked+"/deviceSwitch="+switchCheck+"/deviceAlarm="+alerm+"/deviceMap="+location+"/deviceControl="+moniCheck+"/deviceAnalog="+interLock+"/deviceMac="+deviceMac+"/deviceName="+name;
                Log.i("sharer","-->"+share);
                Intent intent=new Intent(this,ShareDeviceQRCodeActivity.class);
                intent.putExtra("share",share);
                intent.putExtra("name",name);
                startActivity(intent);
                break;
        }
    }
    private void setTextViewBk(TextView tv,int state){
        if (state==1){
            tv.setBackgroundResource(R.drawable.bg_fill5_green);
        }else if (state==0){
            tv.setBackgroundResource(R.drawable.bg_fill5_gray);
        }
    }
}
