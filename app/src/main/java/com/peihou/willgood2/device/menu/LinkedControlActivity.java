package com.peihou.willgood2.device.menu;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.peihou.willgood2.R;
import com.peihou.willgood2.BaseActivity;
import com.peihou.willgood2.database.dao.impl.DeviceLinkDaoImpl;
import com.peihou.willgood2.database.dao.impl.DeviceLinkedTypeDaoImpl;
import com.peihou.willgood2.pojo.Linked;
import com.peihou.willgood2.pojo.LinkedType;
import com.peihou.willgood2.pojo.MoniLink;
import com.peihou.willgood2.pojo.TimerTask;
import com.peihou.willgood2.service.MQService;
import com.peihou.willgood2.utils.TenTwoUtil;
import com.peihou.willgood2.utils.ToastUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 联动控制
 */
public class LinkedControlActivity extends BaseActivity {


    @BindView(R.id.list_linked) RecyclerView list_linked;//联动列表视图
    private List<LinkedType> list=new ArrayList<>();//联动列表
    @BindView(R.id.tv_title) TextView tv_title;//页面标题
    MyAdapter adapter;//联动列表适配器
    Map<Integer,Boolean> checkMap=new HashMap<>();
    long deviceId;//设备Id
    String deviceMac;//设备的mac地址
    private DeviceLinkDaoImpl deviceLinkDao;
    private DeviceLinkedTypeDaoImpl deviceLinkedTypeDao;
    String topicName;
    boolean online;

    @Override
    public void initParms(Bundle parms) {
        deviceId=parms.getLong("deviceId");
        deviceMac=parms.getString("deviceMac");
        online=parms.getBoolean("online");
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_linked_control;
    }

    private boolean bind=false;
    @Override
    public void initView(View view) {
        topicName="qjjc/gateway/"+deviceMac+"/server_to_client";
//        topicName = "qjjc/gateway/" + deviceMac + "/client_to_server";
        deviceLinkDao=new DeviceLinkDaoImpl(getApplicationContext());
        deviceLinkedTypeDao=new DeviceLinkedTypeDaoImpl(getApplicationContext());

        list=deviceLinkedTypeDao.findLinkdType(deviceMac);
        if (list!=null && list.size()!=6){
            deviceLinkedTypeDao.deleteLinkedTypes(deviceMac);
            list.add(new LinkedType(deviceMac,0,"温度联动",0,0));
            list.add(new LinkedType(deviceMac,1,"湿度联动",0,0));
            list.add(new LinkedType(deviceMac,2,"开关量联动",0,0));
            list.add(new LinkedType(deviceMac,3,"电流联动",0,0));
            list.add(new LinkedType(deviceMac,4,"电压联动",0,0));
            list.add(new LinkedType(deviceMac,5,"模拟量联动",0,0));
            deviceLinkedTypeDao.insertLinkedTypes(list);
        }


        list_linked.setLayoutManager(new LinearLayoutManager(this));
        adapter=new MyAdapter(this,list);
        list_linked.setAdapter(adapter);

        Intent service=new Intent(this,MQService.class);
        bind=bindService(service,connection,Context.BIND_AUTO_CREATE);
        receiver=new MessageReceiver();
        IntentFilter intentFilter=new IntentFilter("LinkedControlActivity");
        intentFilter.addAction("offline");
        registerReceiver(receiver,intentFilter);
    }

    public static boolean running=false;

    @Override
    protected void onStart() {
        super.onStart();
        running=true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mqService!=null){
            List<LinkedType> linkedTypes= deviceLinkedTypeDao.findLinkdType(deviceMac);
            list.clear();
            list.addAll(linkedTypes);
            adapter.notifyDataSetChanged();
            mqService.connectMqtt(deviceMac);
            mqService.getData(topicName, 0x33);
            countTimer.start();
        }
        click=0;

        returnData=0;
    }

    int returnData=0;
    @Override
    protected void onStop() {
        super.onStop();
        running=false;
        click=0;
        returnData=0;
    }


    @OnClick({R.id.img_back})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.img_back:
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
    }

    MQService mqService;
    ServiceConnection  connection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MQService.LocalBinder binder= (MQService.LocalBinder) service;
            mqService=binder.getService();
            if (mqService!=null){
                mqService.getData(topicName,0x33);
                countTimer.start();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    public void doBusiness(Context mContext) {

    }

    int click=0;
    MessageReceiver receiver;
    class MessageReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String action=intent.getAction();
                if ("offline".equals(action)){
                    String macAddress=intent.getStringExtra("macAddress");
                    if (intent.hasExtra("all") || macAddress.equals(deviceMac)) {
                        online=false;
                    }
                }else {
                    String macAddress=intent.getStringExtra("macAddress");
                    if (macAddress.equals(deviceMac)){
                        boolean online2=intent.getBooleanExtra("online",false);
                        online=online2;
                        if (intent.hasExtra("linkedTypes")){
                            List<LinkedType> linkedTypes= (List<LinkedType>) intent.getSerializableExtra("linkedTypes");
                            Collections.sort(linkedTypes, new Comparator<LinkedType>() {
                                @Override
                                public int compare(LinkedType o1, LinkedType o2) {
                                    if (o1.getType()>o2.getType()){
                                        return 1;
                                    }else if (o1.getType()<o2.getType()){
                                        return -1;
                                    }
                                    return 0;
                                }
                            });
                            if (click==1){
                                mqService.starSpeech(deviceMac,3);
                                click=0;
                            }
                            list.clear();
                            list.addAll(linkedTypes);
                            adapter.notifyDataSetChanged();
                        }
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class MyAdapter extends RecyclerView.Adapter{

        private Context context;
        private List<LinkedType> list;

        public MyAdapter(Context context, List<LinkedType> list) {
            this.context = context;
            this.list = list;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view=View.inflate(context,R.layout.item_linked,null);
            return new ViewHolderTop(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
            final LinkedType linked=list.get(position);
            int state=linked.getState();
            String name=linked.getName();
           final int type=linked.getType();
            Log.i("type22","-->"+type);
            TextView tv_linked=holder.itemView.findViewById(R.id.tv_linked);
            tv_linked.setText(name);
            ImageView img_open=holder.itemView.findViewById(R.id.img_open);
            if (state==1){
                img_open.setImageResource(R.mipmap.img_open);
            }else {
                img_open.setImageResource(R.mipmap.img_close);
            }
            img_open.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!online){
                        mqService.getData(topicName,0x11);
                        ToastUtil.showShort(LinkedControlActivity.this,"设备已离线");
                        return;
                    }
                    int mcuVerion=linked.getMcuVersion();
                    int x[]=new int[8];
                    for (int i = 0; i <list.size() ; i++) {
                        if (i==position){
                            int state2=0;
                            if (linked.getState()==1){
                                state2=0;
                            }else {
                                state2=1;
                            }
                            x[7-position]=state2;
                        }else {
                            LinkedType linkedType=list.get(i);
                            x[7-i]=linkedType.getState();
                        }
                    }

                    int data=TenTwoUtil.changeToTen(x);
                    boolean success=mqService.sendLinkedSwitch(topicName,mcuVerion,data);
                    countTimer.start();
                    click=1;
                }
            });

            ImageView img_linked=holder.itemView.findViewById(R.id.img_linked);
            if (type==0){
                img_linked.setImageResource(R.mipmap.img_linked_temp);
            }else if (type==1){
                img_linked.setImageResource(R.mipmap.img_linked_hum);
            }else if (type==2){
                img_linked.setImageResource(R.mipmap.img_linked_switch);
            }else if (type==3){
                img_linked.setImageResource(R.mipmap.img_linked_cur);
            }else if (type==4){
                img_linked.setImageResource(R.mipmap.img_linked_val);
            }else if (type==5){
                img_linked.setImageResource(R.mipmap.img_linked_moni);
            }
            holder.itemView.findViewById(R.id.rl).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position==5){

                        Intent intent=new Intent(LinkedControlActivity.this,MoniLinkItemActivity.class);
                        intent.putExtra("deviceId",deviceId);
                        intent.putExtra("deviceMac",deviceMac);
                        intent.putExtra("online",online);
                        startActivityForResult(intent,1000);
                    }else {
                        Intent intent=new Intent(LinkedControlActivity.this,LinkItemActivity.class);
                        intent.putExtra("type",type);
                        intent.putExtra("deviceId",deviceId);
                        intent.putExtra("deviceMac",deviceMac);
                        intent.putExtra("online",online);
                        startActivityForResult(intent,1000);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==1002){
            returnData=2;
        }
    }

    class ViewHolderTop extends RecyclerView.ViewHolder{
        public ViewHolderTop(View itemView) {
            super(itemView);
        }
    }
    class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    class LinkViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.check) CheckBox check;
        @BindView(R.id.tv_name) TextView tv_name;
        public LinkViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }


    //设置蒙版
    private void backgroundAlpha(float f) {
        WindowManager.LayoutParams lp =getWindow().getAttributes();
        lp.alpha = f;
        getWindow().setAttributes(lp);
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
        popupWindow2.showAtLocation(tv_title, Gravity.CENTER, 0, 0);
        //添加按键事件监听
    }

}
