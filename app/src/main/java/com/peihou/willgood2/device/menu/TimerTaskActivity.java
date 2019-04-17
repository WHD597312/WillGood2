package com.peihou.willgood2.device.menu;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.peihou.willgood2.R;
import com.peihou.willgood2.BaseActivity;
import com.peihou.willgood2.custom.ChangeDialog;
import com.peihou.willgood2.database.dao.TimerTaskDao;
import com.peihou.willgood2.database.dao.impl.DeviceLineDaoImpl;
import com.peihou.willgood2.database.dao.impl.TimerTaskDaoImpl;
import com.peihou.willgood2.pojo.Line2;
import com.peihou.willgood2.pojo.TimerTask;
import com.peihou.willgood2.service.MQService;
import com.peihou.willgood2.utils.TenTwoUtil;
import com.peihou.willgood2.utils.ToastUtil;
import com.peihou.willgood2.utils.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 定时任务
 */
public class TimerTaskActivity extends BaseActivity {


    @BindView(R.id.list_timer) RecyclerView list_timer;//定时任务
    List<TimerTask> timerTasks=new ArrayList<>();
    TimerTaskAdapter adapter;
    private TimerTaskDaoImpl timerTaskDao;
    private DeviceLineDaoImpl deviceLineDao;
     long deviceId;
    String deviceMac;
    String topicName;
    public static boolean running=false;

    @Override
    public void initParms(Bundle parms) {
        deviceId=parms.getLong("deviceId");
        deviceMac=parms.getString("deviceMac");
        online=parms.getBoolean("online");
    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_timer_task;
    }

    private boolean bind;
    @Override
    public void initView(View view) {
        list_timer.setLayoutManager(new LinearLayoutManager(this));
        timerTaskDao=new TimerTaskDaoImpl(getApplicationContext());
        deviceLineDao=new DeviceLineDaoImpl(getApplicationContext());
        topicName="qjjc/gateway/"+deviceMac+"/server_to_client";
//        topicName = "qjjc/gateway/" + deviceMac + "/client_to_server";
//        timerTasks=timerTaskDao.findDeviceTimeTask(deviceMac);
        Intent service=new Intent(this,MQService.class);
        bind=bindService(service,connection,Context.BIND_AUTO_CREATE);

        receiver=new MessageReceiver();
        IntentFilter intentFilter=new IntentFilter("TimerTaskActivity");
        intentFilter.addAction("offline");
        registerReceiver(receiver,intentFilter);
        adapter=new TimerTaskAdapter(this,timerTasks);
        list_timer.setAdapter(adapter);
    }

    @Override
    public void doBusiness(Context mContext) {

    }
    @OnClick({R.id.img_back,R.id.img_add})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.img_back:
                if (mqService!=null){
                    List<TimerTask> timerTasks=mqService.getTimerTask(deviceMac);
                    if (!timerTasks.isEmpty()){
                        List<TimerTask> timerTasks2=updateTimerTasks(timerTasks);
                        mqService.updateTimerTasks(timerTasks2);
                    }
                }
                finish();
                break;
            case R.id.img_add:
                result=1;
                Intent intent=new Intent(this,AddTimeActivity.class);
                intent.putExtra("deviceId",deviceId);
                intent.putExtra("deviceMac",deviceMac);
                startActivityForResult(intent,1001);
                break;
        }
    }

    private List<TimerTask> updateTimerTasks(List<TimerTask> timerTasks){
        for (int i = 0; i < timerTasks.size(); i++) {
            TimerTask timerTask=timerTasks.get(i);
            timerTask.setVisitity(0);
            timerTasks.set(i,timerTask);
        }
        return timerTasks;
    }

    @Override
    public void onBackPressed() {
        if (mqService!=null){
            List<TimerTask> timerTasks=mqService.getTimerTask(deviceMac);
            if (!timerTasks.isEmpty()){
                List<TimerTask> timerTasks2=updateTimerTasks(timerTasks);
                mqService.updateTimerTasks(timerTasks2);
            }
        }
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mqService!=null && returnData==0){
            timerTasks.clear();
            List<TimerTask> timerTasks2=timerTaskDao.findDeviceTimeTask(deviceMac);
            timerTasks.addAll(timerTasks2);
            adapter.notifyDataSetChanged();
        }

        running=true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        running=false;
        returnData=0;
    }


    //自定义点动时间
    ChangeDialog dialog;
    private void changeDialog(final int position){
        if (dialog!=null && dialog.isShowing()){
            return;
        }
        dialog=new ChangeDialog(this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMode(1);
        dialog.setTitle("删除定时");
        dialog.setTips("是否删除该定时?");
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
                if (mqService!=null){
                    TimerTask timerTask=timerTasks.get(position);
                    timerTask.setState(2);
                    boolean success=mqService.sendTimerTask(topicName,timerTask,0x02);
                    countTimer.start();
                    returnData=1;
                }
                dialog.dismiss();
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

    boolean online;
    MessageReceiver receiver;
    class MessageReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String action=intent.getAction();
                if ("offline".equals(action)){
                    String macAddress=intent.getStringExtra("macAddress");
                    if (intent.hasExtra("all") ||macAddress.equals(deviceMac))
                        online=false;
                }else {
                    String macAddress=intent.getStringExtra("macAddress");
                    int operate=intent.getIntExtra("operate",0);
                    if (macAddress.equals(deviceMac)){
                        boolean online2=intent.getBooleanExtra("online",false);
                        online=online2;
                        if (mqService!=null){
                            if (returnData==1){
                                returnData=0;
                                if (operate==1){
                                    mqService.starSpeech(deviceMac,"删除成功");

                                }else {
                                    mqService.starSpeech(deviceMac,"设置成功");
                                }

                            }
                            List<TimerTask> timerTasks2 = mqService.getTimerTask(deviceMac);
                            timerTasks.clear();
                            timerTasks.addAll(timerTasks2);
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
            if (mqService!=null) {
                mqService.getData(topicName,0x22);
                countTimer.start();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    int returnData;
    int result=0;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==1001){
            if (mqService!=null){
                TimerTask timerTask= (TimerTask) data.getSerializableExtra("timerTask");
                if (timerTask!=null){
                    mqService.sendTimerTask(topicName,timerTask,0x01);
                    returnData=1;
                    countTimer.start();
                }
            }

        }
    }

    class TimerTaskAdapter extends RecyclerView.Adapter<MyViewHoler>{

        private Context context;
        private List<TimerTask> timerTasks;

        public TimerTaskAdapter(Context context, List<TimerTask> timerTasks) {
            this.context = context;
            this.timerTasks = timerTasks;
        }

        @NonNull
        @Override
        public MyViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view=View.inflate(context,R.layout.item_timer,null);
            return new MyViewHoler(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHoler holder, final int position) {
            final TimerTask timerTask=timerTasks.get(position);
            if (timerTask!=null){
                boolean isOpen=timerTask.isOpen();
                int controlState=timerTask.getControlState();
                int choice=timerTask.getChoice();
                int state=timerTask.getState();

                String name=timerTask.getName();
                int hour=timerTask.getHour();
                int min=timerTask.getMin();
                String hour2=""+hour;
                String min2=""+min;
                if (hour<10){
                    hour2="0"+hour;
                }

                if (min < 10) {
                    min2 = "0" + min;
                }
                String switchState="";
                if (controlState==1){
                    switchState="开启";
                }else {
                    switchState="关闭";
                }
                String s2=switchState+" "+name;

//                SpannableStringBuilder style=new SpannableStringBuilder(s2);
//                style.setSpan(new ForegroundColorSpan(Color.parseColor("#09c585")),0,2,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                holder.tv_name.setText(s2);
                Log.i("TimerTask","-->"+isOpen);
                if (state==1){
                    holder.img_open.setImageResource(R.mipmap.img_open);
                    holder.img_timer.setImageResource(R.mipmap.timer_open);
                }else {
                    holder.img_open.setImageResource(R.mipmap.img_close);
                    holder.img_timer.setImageResource(R.mipmap.timer_close);
                }
                if (choice==0x11){
                    int year=timerTask.getYear();
                    int month=timerTask.getMonth();
                    int day=timerTask.getDay();
                    String month2=""+month;
                    if (month < 10) {
                        month2 = "0" + month;
                    }
                    String day1=""+day;
                    if (day<10){
                        day1="0"+day;
                    }


                    String time=year+"-"+month2+"-"+day1+" "+hour2+":"+min2;
                    holder.tv_timer.setText(time);
                    holder.tv_timers.setText("单次定时");
                    holder.tv_week.setText("");
                }else if (choice==0x22){
                    String time=hour2+":"+min2;
                    holder.tv_timer.setText(time);
                    int week=timerTask.getWeek();
                    String mon="";
                    String tue="";
                    String wen="";
                    String thr="";
                    String fri="";
                    String sat="";
                    String sun="";
                    String s="";
                    int[] weeks=TenTwoUtil.changeToTwo(week);
                    for (int i = 0; i < weeks.length; i++) {
                        if (weeks[i]==1){
                            if (i==0){
                                mon="一";
                                s=s+mon+"      ";
                            }else if (i==1){
                                tue="二";
                                s=s+tue+"      ";
                            }else if (i==2){
                                wen="三";
                                s=s+wen+"      ";
                            }else if (i==3){
                                thr="四";
                                s=s+thr+"      ";
                            }else if (i==4){
                                fri="五";
                                s=s+fri+"      ";
                            }else if (i==5){
                                sat="六";
                                s=s+sat+"      ";
                            }else if (i==6){
                                sun="日";
                                s=s+sun;
                            }
                        }
                    }
                    holder.tv_timers.setText("循环定时");
                    holder.tv_timer.setText(time);
                    holder.tv_week.setText(s);
                }
                holder.img_open.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!online){
                            ToastUtil.showShort(TimerTaskActivity.this,"设备已离线");
                            if (mqService!=null){
                                mqService.getData(topicName,0x11);
                            }
                            return;
                        }

                        if (timerTask.getState()==1){
                            timerTask.setState(0);
                        }else {
                            timerTask.setState(1);
                        }
                        if (mqService!=null){
                            boolean success=mqService.sendTimerTask(topicName,timerTask,0x02);
                            returnData=1;
                            countTimer.start();
                        }
                    }
                });
                holder.rl_item.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        changeDialog(position);
                        return false;
                    }
                });

            }
        }

        @Override
        public int getItemCount() {
            return timerTasks.size();
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
        popupWindow2.showAtLocation(list_timer, Gravity.CENTER, 0, 0);
        //添加按键事件监听
    }

    class MyViewHoler extends RecyclerView.ViewHolder{

        @BindView(R.id.rl_item) RelativeLayout rl_item;
        @BindView(R.id.img_timer) ImageView img_timer;
        @BindView(R.id.tv_timer) TextView tv_timer;
        @BindView(R.id.tv_timers) TextView tv_timers;
        @BindView(R.id.tv_week) TextView tv_week;
        @BindView(R.id.img_open) ImageView img_open;
        @BindView(R.id.tv_name) TextView tv_name;
        public MyViewHoler(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

}
