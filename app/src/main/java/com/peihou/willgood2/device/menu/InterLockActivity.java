package com.peihou.willgood2.device.menu;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.peihou.willgood2.BaseActivity;
import com.peihou.willgood2.R;
import com.peihou.willgood2.custom.ChangeDialog;
import com.peihou.willgood2.database.dao.impl.DeviceInterLockDaoImpl;
import com.peihou.willgood2.database.dao.impl.DeviceLineDaoImpl;
import com.peihou.willgood2.pojo.InterLock;
import com.peihou.willgood2.pojo.Line2;
import com.peihou.willgood2.service.MQService;
import com.peihou.willgood2.utils.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InterLockActivity extends BaseActivity {

    @BindView(R.id.grid_lock)
    GridView grid_lock;//互锁线路，以网格布局布局
    @BindView(R.id.list_lock)
    ListView list_lock;//以列表形式展示以互锁的线路
    List<Line2> lockLineList = new ArrayList<>();//线路集合
    LockLineAdapter lockLineAdapter;//线路适配器
    LockAdapter lockAdapter;//互锁线路适配器
    private DeviceLineDaoImpl deviceLineDao;//设备线路表的操作对象
    private String deviceMac;
    boolean online;
    private List<InterLock> interLocks=new ArrayList<>();//互锁线路
    private DeviceInterLockDaoImpl deviceInterLockDao;
    @Override
    public void initParms(Bundle parms) {
        deviceMac = parms.getString("deviceMac");
        online=parms.getBoolean("online");
    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_inter_lock;
    }

    int interLock = 0;//设置互锁的数量 只有2时，才可以设置互锁
    int deviceLineNum = 0;//线路一
    int deviceLineNum2 = 0;//线路二
    String topicName;
    int mcuVersion = 0;


    @Override
    public void initView(View view) {
        topicName="qjjc/gateway/"+deviceMac+"/server_to_client";
//        topicName = "qjjc/gateway/" + deviceMac + "/client_to_server";
        deviceLineDao = new DeviceLineDaoImpl(getApplicationContext());

        deviceInterLockDao=new DeviceInterLockDaoImpl(getApplicationContext());

        lockLineList = deviceLineDao.findDeviceOnlineLines(deviceMac);
//        interLocks=deviceInterLockDao.findDeviceVisityInterLock(deviceMac);
        lockLineAdapter = new LockLineAdapter(this, lockLineList);
        grid_lock.setAdapter(lockLineAdapter);
        grid_lock.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!online){
                    ToastUtil.showShort(InterLockActivity.this, "设备已离线");
                    mqService.getData(topicName,0x11);
                    return;
                }
                Line2 line2 = lockLineList.get(position);
                int lock = line2.getLock();
                if (lock == 1) {
                    ToastUtil.showShort(InterLockActivity.this, "该线路已与其他线路互锁");
                } else {
                    boolean onClick = line2.isOnClick();
                    if (onClick) {
                        interLock--;
                        if (interLock < 0)
                            interLock = 0;
                        line2.setOnClick(false);
                        lockLineList.set(position, line2);
                        lockLineAdapter.notifyDataSetChanged();
                    } else {
                        interLock++;
                        if (interLock > 2) {
                            ToastUtil.showShort(InterLockActivity.this, "互锁为两条线路的互锁");
                            interLock = 2;
                        } else {
                            if (interLock == 1) {
                                deviceLineNum=line2.getDeviceLineNum();
                            } else if (interLock == 2) {
                                deviceLineNum2 = line2.getDeviceLineNum();
                            }
                            line2.setOnClick(true);
                            lockLineList.set(position, line2);
                            lockLineAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });


        lockAdapter = new LockAdapter(this, interLocks);
        list_lock.setAdapter(lockAdapter);
        Intent service = new Intent(this, MQService.class);
        bind = bindService(service, connection, Context.BIND_AUTO_CREATE);

        receiver=new MessageReceiver();
        IntentFilter filter=new IntentFilter("InterLockActivity");
        filter.addAction("offline");
        registerReceiver(receiver,filter);
    }

    private void updateInterLocks(){

        for (int i = 0; i < interLocks.size(); i++) {
            InterLock interLock=interLocks.get(i);
            interLock.setVisitity(0);
            interLocks.set(i,interLock);
        }

        if (mqService!=null){
            mqService.updateLines(deviceMac);
            mqService.updateDeviceInterLock(interLocks);
        }
    }

    @Override
    public void onBackPressed() {
        updateInterLocks();
        super.onBackPressed();
    }

    int click=0;
    @OnClick({R.id.img_back, R.id.btn_lock})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                updateInterLocks();
                finish();
                break;
            case R.id.btn_lock:
                if (interLock == 2) {
//                    String inter = deviceLineNum + "&" + deviceLineNum2;
                    int[]interLines=new int[16];
//                    interLines[]
                    int i=0;
                    for (int j = 0;  j<interLocks.size() ; j++) {
                        InterLock interLock=interLocks.get(j);
                        int deviceLineNum=interLock.getDeviceLineNum();
                        int deviceLineNum2=interLock.getDeviceLineNum2();
                        interLines[i]=deviceLineNum;
                        interLines[++i]=deviceLineNum2;
                        ++i;
                    }
                    interLines[i]=deviceLineNum;
                    interLines[++i]=deviceLineNum2;
                    if (mqService != null) {
                        boolean success = mqService.sendInterLine(topicName, mcuVersion, interLines);
                        click=1;
                        countTimer.start();
                    }
                } else if (interLock == 1) {
                    ToastUtil.showShort(this, "一条线路不能设为互锁");
                } else {
                    ToastUtil.showShort(this, "请选择两个互锁的线路");
                }
                break;
        }
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    private boolean bind = false;
    MQService mqService;
    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MQService.LocalBinder binder = (MQService.LocalBinder) service;
            mqService = binder.getService();
            if (mqService!=null){
                mqService.getData(topicName,0x46);
                countTimer.start();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    public static boolean running = false;

    @Override
    protected void onStart() {
        super.onStart();
        running = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        running = false;
    }

    MessageReceiver receiver;

    class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String macAddress = intent.getStringExtra("macAddress");
                String action = intent.getAction();

                if ("offline".equals(action)) {
                    if (intent.hasExtra("all") || macAddress.equals(deviceMac)) {
                        online = false;
                    }
                }else {
                    if (macAddress.equals(deviceMac)) {
                        boolean online2=intent.getBooleanExtra("online",false);
                        online=online2;
                        if (click==1){
                            if (interLock==2)
                                interLock = 0;
                        }
                        if (click==1){
//                        mqService.starSpeech("控制成功");
                            click=0;
                        }
                        List<InterLock> interLocks2=mqService.getDeviceVisityInterLock(deviceMac);
                        interLocks.clear();
                        interLocks.addAll(interLocks2);
                        lockLineList.clear();
                        List<Line2> line2List=mqService.getDeviceOnlineLiens(deviceMac);
                        lockLineList.addAll(line2List);
                        lockLineAdapter.notifyDataSetChanged();
                        lockAdapter.notifyDataSetChanged();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (popupWindow2!=null && popupWindow2.isShowing()){
            popupWindow2.dismiss();
        }

        if (bind) {
            unbindService(connection);
        }
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }

    class LockLineAdapter extends BaseAdapter {

        private Context context;
        private List<Line2> list;

        public LockLineAdapter(Context context, List<Line2> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Line2 getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.item_lock_line, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            Line2 lockLine = getItem(position);
            String name = lockLine.getName();
            boolean click = lockLine.isOnClick();
            int lock = lockLine.getLock();
            if (click || lock==1) {
                viewHolder.tv_lock_line.setBackgroundResource(R.drawable.shape_lock_line);
            } else {
                viewHolder.tv_lock_line.setBackgroundResource(R.drawable.shape_lock_line2);
            }
            viewHolder.tv_lock_line.setText(name);

            return convertView;
        }
    }

    class ViewHolder {
        @BindView(R.id.tv_lock_line)
        TextView tv_lock_line;//互锁线路的子项

        public ViewHolder(View v) {
            ButterKnife.bind(this, v);
        }
    }

    class LockAdapter extends BaseAdapter {

        private Context context;
        private List<InterLock> list;

        public LockAdapter(Context context, List<InterLock> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public InterLock getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            HolderView holderView = null;
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.item_lock, null);
                holderView = new HolderView(convertView);
                convertView.setTag(holderView);
            } else {
                holderView = (HolderView) convertView.getTag();
            }
            InterLock lock = getItem(position);
            String name = lock.getName();
            String name2 = lock.getName2();
            holderView.tv_line.setText(name);
            holderView.tv_line2.setText(name2);
            holderView.btn_unbind.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!online){
                        ToastUtil.showShort(InterLockActivity.this, "设备已离线");
                        mqService.getData(topicName,0x11);
                        return;
                    }
                    changeDialog(position);
                }
            });
            return convertView;
        }
    }

    //自定义点动时间
    ChangeDialog dialog;

    private void changeDialog(final int position) {
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        dialog = new ChangeDialog(this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMode(1);
        dialog.setTitle("解除互锁");
        dialog.setTips("是否解除该两线路的互锁?");
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
                if (!online){
                    ToastUtil.showShort(InterLockActivity.this, "设备已离线");
                    mqService.getData(topicName,0x11);
                    dialog.dismiss();
                    return;
                }

                if (mqService != null) {
                    InterLock lock = interLocks.get(position);
                    lock.setDeviceLineNum(0);
                    lock.setDeviceLineNum2(0);
                    interLocks.set(position,lock);
                    if (mqService != null) {
                        int[]interLines=new int[16];
                        int i=0;
                        for (int j = 0; j <interLocks.size() ; j++) {
                            InterLock interLock=interLocks.get(j);
                            int deviceLineNum=interLock.getDeviceLineNum();
                            int deviceLineNum2=interLock.getDeviceLineNum2();
                            interLines[i]=deviceLineNum;
                            interLines[++i]=deviceLineNum2;
                            ++i;
                        }
                        if (mqService != null) {
                            boolean success = mqService.sendInterLine(topicName, mcuVersion, interLines);
                            click=1;
                            countTimer.start();
                        }
                    }
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

    class HolderView {
        @BindView(R.id.tv_line)
        TextView tv_line;//互锁线路一
        @BindView(R.id.tv_line2)
        TextView tv_line2;//互锁线路二
        @BindView(R.id.btn_unbind)
        Button btn_unbind;

        public HolderView(View view) {
            ButterKnife.bind(this, view);
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
        popupWindow2.showAtLocation(list_lock, Gravity.CENTER, 0, 0);
        //添加按键事件监听
    }

}
