package com.peihou.willgood2.device.menu;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.peihou.willgood2.R;
import com.peihou.willgood2.BaseActivity;
import com.peihou.willgood2.service.MQService;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 掉电记忆
 */
public class PowerLostMomoryActivity extends BaseActivity {

    @BindView(R.id.img_open)
    ImageView img_open;
    @BindView(R.id.tv_name) TextView tv_name;
    @BindView(R.id.tv_title) TextView tv_title;
    int plMemory;
    int type;
    int voice;
    String deviceMac;
    @Override
    public void initParms(Bundle parms) {
        type=parms.getInt("type");
        deviceMac=parms.getString("deviceMac");
        if (type==1){
            plMemory=parms.getInt("plMemory");
        }else {
            voice=parms.getInt("voice");
        }

    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_power_lost_momory;
    }

    @Override
    public void initView(View view) {
        Intent service=new Intent(this,MQService.class);
        bind=bindService(service,connection,Context.BIND_AUTO_CREATE);
        if (type==1){
            open=plMemory;
            tv_name.setText("掉电记忆");
            if (plMemory==1){
                img_open.setImageResource(R.mipmap.img_open);
            }else if (plMemory==0){
                img_open.setImageResource(R.mipmap.img_close);
            }
        }else if (type==2){
            tv_title.setText("控制语音");
            tv_name.setText("控制语音");
            open=voice;
            if (voice==1){
                img_open.setImageResource(R.mipmap.img_open);
            }else if (voice==0){
                img_open.setImageResource(R.mipmap.img_close);
            }
        }
    }
    int open=1;//1为打开掉电记忆，0为关闭掉电记忆
    int click=0;//1为点击过，0没有点击过开关按钮
    @OnClick({R.id.img_back,R.id.img_open})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.img_back:
                if (type==1){
                    Intent intent=new Intent();
                    intent.putExtra("plMemory",plMemory);
                    intent.putExtra("click",click);
                    setResult(8000,intent);
                }else if (type==2){
                    Intent intent=new Intent();
                    intent.putExtra("voice",voice);
                    setResult(9000,intent);
                }
                finish();
                break;
            case R.id.img_open:
                if (type==1){
                    click=1;
                    if (open==1){
                        open=0;
                        img_open.setImageResource(R.mipmap.img_close);
                        plMemory=0;
                    }else {
                        open=1;
                        img_open.setImageResource(R.mipmap.img_open);
                        plMemory=1;
                    }
                }else {
                    if (open==1){
                        open=0;
                        img_open.setImageResource(R.mipmap.img_close);
                        voice=0;
                    }else {
                        open=1;
                        img_open.setImageResource(R.mipmap.img_open);
                        voice=1;
                    }
                    mqService.updateDevice(deviceMac,voice);
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bind){
            unbindService(connection);
        }
    }

    private boolean bind;
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
    @Override
    public void onBackPressed() {
        if (type==1){
            Intent intent=new Intent();
            intent.putExtra("plMemory",plMemory);
            intent.putExtra("click",click);
            setResult(8000,intent);
            finish();
        }else if (type==2){
            Intent intent=new Intent();
            intent.putExtra("voice",voice);
            setResult(9000,intent);
            finish();
        }
    }

    @Override
    public void doBusiness(Context mContext) {

    }
}
