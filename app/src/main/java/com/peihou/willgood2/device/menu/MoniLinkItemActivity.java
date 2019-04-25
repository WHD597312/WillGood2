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
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.peihou.willgood2.BaseActivity;
import com.peihou.willgood2.R;
import com.peihou.willgood2.database.dao.impl.DeviceMoniLinkDaoDaoImpl;
import com.peihou.willgood2.pojo.MoniLink;
import com.peihou.willgood2.service.MQService;
import com.peihou.willgood2.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MoniLinkItemActivity extends BaseActivity {


    @BindView(R.id.list_linked) RecyclerView listLinked;
    private List<MoniLink> list=new ArrayList<>();
    private MoniLinkAdapter adapter;
    private DeviceMoniLinkDaoDaoImpl deviceMoniLinkDaoDao;
    String deviceMac;//设备mac地址
    long deviceId;
    String topicName;//设备主题
    private int mcuVerion;
    boolean online;

    @Override
    public void initParms(Bundle parms) {
        deviceMac=parms.getString("deviceMac");
        deviceId=parms.getLong("deviceId");
        mcuVerion=parms.getInt("mcuVerion");
        online=parms.getBoolean("online");
    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_moni_link_item;
    }

    public static boolean running=false;
    private boolean bind;
    @Override
    public void initView(View view) {

        deviceMoniLinkDaoDao=new DeviceMoniLinkDaoDaoImpl(getApplicationContext());


        list.add(new MoniLink("电流1",0,0,deviceMac));
        list.add(new MoniLink("电流2",0,1,deviceMac));
        list.add(new MoniLink("电流3",0,2,deviceMac));
        list.add(new MoniLink("电流4",0,3,deviceMac));
        list.add(new MoniLink("电压1",1,1,deviceMac));
        list.add(new MoniLink("电压2",1,2,deviceMac));
        list.add(new MoniLink("电压3",1,3,deviceMac));
        list.add(new MoniLink("电压4",1,4,deviceMac));

        topicName="qjjc/gateway/"+deviceMac+"/server_to_client";
//        topicName = "qjjc/gateway/" + deviceMac + "/client_to_server";
        Intent service=new Intent(this,MQService.class);
        bind=bindService(service,connection,Context.BIND_AUTO_CREATE);
        receiver=new MessageReceiver();
        IntentFilter filter=new IntentFilter("MoniLinkItemActivity");
        filter.addAction("offline");
        registerReceiver(receiver,filter);

        listLinked.setLayoutManager(new LinearLayoutManager(this));
        adapter=new MoniLinkAdapter(this,list);
        listLinked.setAdapter(adapter);
    }

    @OnClick({R.id.img_back})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.img_back:
                setResult(1002);
                finish();
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        running=true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mqService!=null){
            mqService.connectMqtt(deviceMac);
            mqService.getData(topicName,0x3a);
            countTimer.start();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        running=false;
    }

    @Override
    public void onBackPressed() {
        setResult(1002);
        super.onBackPressed();

    }

    @Override
    public void doBusiness(Context mContext) {

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
                        if (intent.hasExtra("moniLinkSwitch")){
                            int[]moniLinkSwitch2=intent.getIntArrayExtra("moniLinkSwitch");
                            if (click==1){
                                click=0;
                                mqService.starSpeech(deviceMac,3);
                            }
                            for (int i = 0; i <moniLinkSwitch2.length ; i++) {
                                int x=moniLinkSwitch2[i];
                                MoniLink moniLink=list.get(i);
                                moniLink.setState(x);
                                list.set(i,moniLink);
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
    MQService mqService;
    ServiceConnection connection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MQService.LocalBinder binder= (MQService.LocalBinder) service;
            mqService=binder.getService();
            if (mqService!=null){
                mqService.getData(topicName,0x3a);
                countTimer.start();
//                int[]x2=mqService.getMoniLinkSwitch(deviceMac);
//                if (x2!=null){
//                    x=x2;
//                    for (int i = 0; i <x.length ; i++) {
//                        int k=x[i];
//                        MoniLink moniLink=list.get(i);
//                        moniLink.setState(k);
//                        list.set(i,moniLink);
//                    }
//                    adapter.notifyDataSetChanged();
//                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    int[] x=new int[8];
    int click=0;
    class MoniLinkAdapter extends RecyclerView.Adapter<ViewHolder>{

        private Context context;
        private List<MoniLink> list;

        public MoniLinkAdapter(Context context, List<MoniLink> list) {
            this.context = context;
            this.list = list;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view=View.inflate(context,R.layout.item_moni_link,null);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
            final MoniLink link=list.get(position);
            int type=link.getType();
            String name=link.getName();
            int state=link.getState();
            boolean open=link.isOpen();
            holder.tv_linked.setText(name);
            if (state==1){
                holder.img_open.setImageResource(R.mipmap.img_open);
            }else {
                holder.img_open.setImageResource(R.mipmap.img_close);
            }
            holder.img_open.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!online){
                        mqService.getData(topicName,0x11);

                        ToastUtil.showShort(MoniLinkItemActivity.this,"设备已离线");
                        return;
                    }

                    if (link.getState()==1){
                        x[7-position]=0;
                    }else {
                        x[7-position]=1;
                    }
                    if (mqService!=null){
                        boolean success=mqService.sendMoniLinkSwitch(topicName,0,x);
                        click=1;
                        countTimer.start();
                    }
                }
            });
            holder.rl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   Intent intent=new Intent(MoniLinkItemActivity.this,LinkItemActivity.class);
                   intent.putExtra("deviceId",deviceId);
                   intent.putExtra("type",5);
                   intent.putExtra("deviceMac",deviceMac);
                   intent.putExtra("analog",position);
                   startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }
    class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.rl) RelativeLayout rl;
        @BindView(R.id.tv_linked) TextView tv_linked;
        @BindView(R.id.img_open) ImageView img_open;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
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
        popupWindow2.showAtLocation(listLinked, Gravity.CENTER, 0, 0);
        //添加按键事件监听
    }

    private void backgroundAlpha(float f) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = f;
        getWindow().setAttributes(lp);
    }

}
