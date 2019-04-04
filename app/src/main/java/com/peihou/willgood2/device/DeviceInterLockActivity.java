package com.peihou.willgood2.device;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.peihou.willgood2.BaseActivity;
import com.peihou.willgood2.R;
import com.peihou.willgood2.database.dao.impl.DeviceDaoImpl;
import com.peihou.willgood2.database.dao.impl.DeviceLineDaoImpl;
import com.peihou.willgood2.pojo.Device;
import com.peihou.willgood2.pojo.InterLock;
import com.peihou.willgood2.pojo.Line2;
import com.peihou.willgood2.pojo.Lock;
import com.peihou.willgood2.service.MQService;
import com.peihou.willgood2.utils.TenTwoUtil;
import com.peihou.willgood2.utils.ToastUtil;
import com.peihou.willgood2.utils.WeakRefHandler;
import com.peihou.willgood2.utils.decoding.Intents;
import com.peihou.willgood2.utils.http.BaseWeakAsyncTask;
import com.peihou.willgood2.utils.http.HttpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeviceInterLockActivity extends BaseActivity {


    @BindView(R.id.list_inter_lock) RecyclerView listInterLock;//设备互锁列表视图
    InterLockAdapter adapter;//设备互锁适配器
    List<InterLock> list=new ArrayList<>();
    long deviceId;
    String deviceMac;//设备mac
    String topicName;//设备主题
    private DeviceLineDaoImpl deviceLineDao;//设备线路表的操纵对象
    private Device device;//设备
    private DeviceDaoImpl deviceDao;//设备表的操作对象

    private int userId;
    private boolean online;
    @Override
    public void initParms(Bundle parms) {
        deviceId=parms.getLong("deviceId");
        deviceMac=parms.getString("deviceMac");
        online=parms.getBoolean("online");
        userId=parms.getInt("userId");
    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_device_inter_lock;
    }

    @Override
    public void initView(View view) {
        deviceDao=new DeviceDaoImpl(getApplicationContext());
        deviceLineDao=new DeviceLineDaoImpl(getApplicationContext());
        device=deviceDao.findDeviceById(deviceId);
        List<Line2> lockLineList=deviceLineDao.findDeviceLines(deviceId);
        Map<String,String> map=deviceLineDao.findInterLockLine(deviceMac);
        topicName = "qjjc/gateway/" + deviceMac + "/server_to_client";
//        topicName = "qjjc/gateway/" + deviceMac + "/client_to_server";
        for(Map.Entry<String,String> entry:map.entrySet()){
            String key=entry.getKey();
            String [] s=key.split("&");
            int deviceLineNum=Integer.parseInt(s[0]);
            int deviceLineNum2=Integer.parseInt(s[1]);
            Line2 line=lockLineList.get(deviceLineNum-1);
            Line2 line2=lockLineList.get(deviceLineNum2-1);
            String name=line.getName();
            String name2=line2.getName();
            boolean open1=line.getOpen();
            boolean open2=line2.getOpen();
            int state=open1?1:0;
            int state2=open2?1:0;
            int operate=0;//互锁线路的正，停，反 0停，1正，2反
            if (state==1 && state2==0)
                operate=1;
            else if (state==0 && state2==1){
                operate=2;
            }else if (state==0 && state2==0){
                operate=0;
            }
            list.add( new InterLock(name,name2,deviceLineNum,deviceLineNum2,operate));
        }

        listInterLock.setLayoutManager(new LinearLayoutManager(this));
        adapter=new InterLockAdapter(this,list);
        listInterLock.setAdapter(adapter);
        Intent service=new Intent(this,MQService.class);
        bind=bindService(service,connection,Context.BIND_AUTO_CREATE);

        receiver=new MessageReceiver();
        IntentFilter filter=new IntentFilter("DeviceInterLockActivity");
        filter.addAction("offline");
        registerReceiver(receiver,filter);
    }
    @OnClick({R.id.img_back})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.img_back:
                finish();
                break;
        }
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    int num1=0;
    int num2=0;
    int click=0;
    @Override
    public void doBusiness(Context mContext) {

    }
    class InterLockAdapter extends RecyclerView.Adapter<ViewHolder>{

        private Context context;
        private List<InterLock> list;

        public InterLockAdapter(Context context, List<InterLock> list) {
            this.context = context;
            this.list = list;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view=View.inflate(context,R.layout.item_inter_lock,null);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
            final InterLock interLock=list.get(position);
            String name=interLock.getName();
            String name2=interLock.getName2();
            final int operate=interLock.getOperate();
            holder.tv_lock.setText(name);
            holder.tv_lock2.setText(name2);
            if (operate==0){
                holder.tv_lock_state.setText("关");
                holder.tv_lock_state.setTextColor(Color.parseColor("#777777"));
                holder.tv_lock_state2.setText("关");
                holder.tv_lock_state2.setTextColor(Color.parseColor("#777777"));
            }else if (operate==1){
                holder.tv_lock_state.setText("开");
                holder.tv_lock_state.setTextColor(Color.parseColor("#09C585"));
                holder.tv_lock_state2.setText("关");
                holder.tv_lock_state2.setTextColor(Color.parseColor("#FBA538"));
            }else if (operate==2){
                holder.tv_lock_state.setText("关");
                holder.tv_lock_state.setTextColor(Color.parseColor("#FBA538"));
                holder.tv_lock_state2.setText("开");
                holder.tv_lock_state2.setTextColor(Color.parseColor("#09C585"));
            }
            holder.tv_positive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!online){
                        mqService.getData(topicName,0x11);
                        ToastUtil.showShort(DeviceInterLockActivity.this,"设备已离线");
                        return;
                    }
                    int deviceLineNum=interLock.getDeviceLineNum();
                    int deviceLineNum2=interLock.getDeviceLineNum2();
                    int prelineSwitch=device.getPrelineswitch();
                    int lastlineSwitch=device.getLastlineswitch();
                    int[] preLineSwitch=TenTwoUtil.changeToTwo(prelineSwitch);
                    int[] lastLineSwitch=TenTwoUtil.changeToTwo(lastlineSwitch);
                    if (deviceLineNum<=8){
                        preLineSwitch[deviceLineNum-1]=1;
                        num1=deviceLineNum;
                    }else if (deviceLineNum>8){
                        lastLineSwitch[deviceLineNum-9]=1;
                        num1=deviceLineNum;

                    }
                    if (deviceLineNum2<=8){
                        preLineSwitch[deviceLineNum2-1]=0;
                        num2=deviceLineNum2;

                    }else if (deviceLineNum2>8){
                        lastLineSwitch[deviceLineNum2-9]=0;
                        num2=deviceLineNum2;

                    }

                    prelineSwitch=TenTwoUtil.changeToTen2(preLineSwitch);
                    lastlineSwitch=TenTwoUtil.changeToTen2(lastLineSwitch);
                    device.setPrelineswitch(prelineSwitch);
                    device.setLastlineswitch(lastlineSwitch);
                    device.setPrelinesjog(0);
                    device.setLastlinesjog(0);
                    if (mqService!=null){
                        boolean success=mqService.sendBasic(topicName,device);
                        countTimer.start();
                        click=1;
                        if (success){
//                            interLock.setOperate(1);
//                            list.set(position,interLock);
//                            notifyDataSetChanged();
                        }
                    }
                }
            });
            holder.tv_stop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int deviceLineNum=interLock.getDeviceLineNum();
                    int deviceLineNum2=interLock.getDeviceLineNum2();
                    int prelineSwitch=device.getPrelineswitch();
                    int lastlineSwitch=device.getLastlineswitch();
                    int[] preLineSwitch=TenTwoUtil.changeToTwo(prelineSwitch);
                    int[] lastLineSwitch=TenTwoUtil.changeToTwo(lastlineSwitch);
                    if (deviceLineNum<=8){
                        preLineSwitch[deviceLineNum-1]=0;

                    }else if (deviceLineNum>8){
                        lastLineSwitch[deviceLineNum-9]=0;

                    }
                    if (deviceLineNum2<8){
                        preLineSwitch[deviceLineNum2-1]=0;

                    }else if (deviceLineNum2>=8){
                        lastLineSwitch[deviceLineNum2-9]=0;
                    }

                    prelineSwitch=TenTwoUtil.changeToTen2(preLineSwitch);
                    lastlineSwitch=TenTwoUtil.changeToTen2(lastLineSwitch);
                    device.setPrelineswitch(prelineSwitch);
                    device.setLastlineswitch(lastlineSwitch);
                    device.setPrelinesjog(0);
                    device.setLastlinesjog(0);
                    if (mqService!=null){
                        boolean success=mqService.sendBasic(topicName,device);
                        click=1;
                        countTimer.start();

                        if (success){
//                            interLock.setOperate(0);
//                            list.set(position,interLock);
//                            notifyDataSetChanged();
                        }
                    }
                }
            });
            holder.tv_reverse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int deviceLineNum=interLock.getDeviceLineNum();
                    int deviceLineNum2=interLock.getDeviceLineNum2();
                    int prelineSwitch=device.getPrelineswitch();
                    int lastlineSwitch=device.getLastlineswitch();
                    int[] preLineSwitch=TenTwoUtil.changeToTwo(prelineSwitch);
                    int[] lastLineSwitch=TenTwoUtil.changeToTwo(lastlineSwitch);
                    if (deviceLineNum<=8){
                        preLineSwitch[deviceLineNum-1]=0;
                        num2=deviceLineNum;

                    }else if (deviceLineNum>8){
                        lastLineSwitch[deviceLineNum-9]=0;
                        num2=deviceLineNum;

                    }
                    if (deviceLineNum2<=8){
                        preLineSwitch[deviceLineNum2-1]=1;
                        num1=deviceLineNum2;

                    }else if (deviceLineNum2>8){
                        lastLineSwitch[deviceLineNum2-9]=1;
                        num1=deviceLineNum2;
                    }

                    prelineSwitch=TenTwoUtil.changeToTen2(preLineSwitch);
                    lastlineSwitch=TenTwoUtil.changeToTen2(lastLineSwitch);
                    device.setPrelineswitch(prelineSwitch);
                    device.setLastlineswitch(lastlineSwitch);
                    device.setPrelinesjog(0);
                    device.setLastlinesjog(0);
                    if (mqService!=null){
                        boolean success=mqService.sendBasic(topicName,device);
                        countTimer.start();
                        click=1;
//                        if (success){
//                            interLock.setOperate(2);
//                            list.set(position,interLock);
//                            notifyDataSetChanged();
//                        }
                    }
                }
            });
        }
        @Override
        public int getItemCount() {
            return list.size();
        }
    }
    class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.tv_lock) TextView tv_lock;//互锁1
        @BindView(R.id.tv_lock_state) TextView tv_lock_state;//开关状态1

        @BindView(R.id.tv_lock2) TextView tv_lock2;//互锁2
        @BindView(R.id.tv_lock_state2) TextView tv_lock_state2;//开关状态2

        @BindView(R.id.tv_positive) TextView tv_positive;//正
        @BindView(R.id.tv_stop) TextView tv_stop;//停
        @BindView(R.id.tv_reverse) TextView tv_reverse;//反
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
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
        if (receiver!=null){
            unregisterReceiver(receiver);
        }
        handler.removeCallbacksAndMessages(null);
    }

    class AddOperationLogAsync extends BaseWeakAsyncTask<Map<String, Object>, Void, Integer, DeviceInterLockActivity> {

        public AddOperationLogAsync(DeviceInterLockActivity activity) {
            super(activity);
        }

        @Override
        protected Integer doInBackground(DeviceInterLockActivity activity, Map<String, Object>... maps) {
            try {
                Map<String, Object> params = maps[0];
                int open= (int) params.get("open");
                int close= (int) params.get("close");
                params.clear();
                params.put("deviceMac", deviceMac);
                params.put("deviceControll", 1);
                params.put("deviceLogType", 1);
                params.put("deviceLine",open+"");
                params.put("userId", userId);
                String url = HttpUtils.ipAddress + "data/addOperationLog";
                String result = HttpUtils.requestPost(url, params);


                params.put("deviceControll", 2);
                params.put("deviceLine",close+"");
                String result2 = HttpUtils.requestPost(url, params);
                Log.i("AddOperationLogAsync", "-->" + result);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(DeviceInterLockActivity activity, Integer integer) {

        }
    }

    StringBuffer sb=new StringBuffer();
    StringBuffer sb2=new StringBuffer();
    MessageReceiver receiver;
    class MessageReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String macAddress=intent.getStringExtra("macAddress");
                String action=intent.getAction();
                if ("offline".equals(action)){
                    if (intent.hasExtra("all") || deviceMac.equals(macAddress))
                    online=false;
                }else {
                    if (macAddress.equals(deviceMac)){
                        online=true;
                        if (mqService!=null){
                            List<Line2> lockLineList=mqService.getDeviceLines(deviceMac);
                            Map<String,String> map=mqService.getDeviceInterLock(deviceMac);
                            list.clear();
                            if (click==1){
                                handler.sendEmptyMessage(0);
                                mqService.starSpeech(macAddress,"控制成功");
                                click=0;
                            }
                            sb.setLength(0);
                            sb2.setLength(0);
                            for(Map.Entry<String,String> entry:map.entrySet()){
                                String key=entry.getKey();
                                String [] s=key.split("&");
                                int deviceLineNum=Integer.parseInt(s[0]);
                                int deviceLineNum2=Integer.parseInt(s[1]);
                                Line2 line=lockLineList.get(deviceLineNum-1);
                                Line2 line2=lockLineList.get(deviceLineNum2-1);
                                String name=line.getName();
                                String name2=line2.getName();
                                boolean open1=line.getOpen();
                                boolean open2=line2.getOpen();
                                int state=open1?1:0;
                                int state2=open2?1:0;
                                int operate=0;//互锁线路的正，停，反 0停，1正，2反
                                if (state==1 && state2==0) {
                                    operate = 1;
                                }
                                else if (state==0 && state2==1){
                                    operate=2;
                                }else if (state==0 && state2==0){
                                    operate=0;
                                }
                                list.add( new InterLock(name,name2,deviceLineNum,deviceLineNum2,operate));
                            }
                            adapter.notifyDataSetChanged();
                        }

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    Map<String,Object> operateLog=new HashMap<>();
    Handler.Callback mCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
                operateLog.clear();
                if (num1!=0 && num2!=0){
                    operateLog.put("open",num1);
                    operateLog.put("close",num2);
                    num1=0;
                    num2=0;
                    new AddOperationLogAsync(DeviceInterLockActivity.this).execute(operateLog);
            }

            return true;
        }
    };
    Handler handler = new WeakRefHandler(mCallback);
    private boolean bind=false;
    MQService mqService;
    ServiceConnection connection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MQService.LocalBinder binder= (MQService.LocalBinder) service;
            mqService=binder.getService();
            if (mqService!=null){
                mqService.getData(topicName,0x46);
                countTimer.start();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
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
        if (popupWindow2==null)
            popupWindow2 = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        //添加弹出、弹入的动画
        popupWindow2.setAnimationStyle(R.style.Popupwindow);
        popupWindow2.setFocusable(false);
        popupWindow2.setOutsideTouchable(false);
        backgroundAlpha(0.5f);
        popupWindow2.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1.0f);
            }
        });
//        ColorDrawable dw = new ColorDrawable(0x30000000);
//        popupWindow.setBackgroundDrawable(dw);
//        popupWindow2.showAsDropDown(et_wifi, 0, -20);
        popupWindow2.showAtLocation(listInterLock, Gravity.CENTER, 0, 0);
        //添加按键事件监听
    }

    private void backgroundAlpha(float f) {
        WindowManager.LayoutParams lp =getWindow().getAttributes();
        lp.alpha = f;
        getWindow().setAttributes(lp);
    }

}
