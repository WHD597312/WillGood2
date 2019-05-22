package com.peihou.willgood2.device;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.peihou.willgood2.BaseActivity;
import com.peihou.willgood2.R;
import com.peihou.willgood2.custom.DialogLoad;
import com.peihou.willgood2.database.dao.impl.DeviceDaoImpl;
import com.peihou.willgood2.pojo.Device;
import com.peihou.willgood2.service.MQService;
import com.peihou.willgood2.utils.DisplayUtil;
import com.peihou.willgood2.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchDeviceActivity extends BaseActivity {


    @BindView(R.id.grid_list)
    GridView grid_list;
    @BindView(R.id.et_search)
    EditText et_search;
    private DeviceDaoImpl deviceDao;
    List<String> strings = new ArrayList<>();
    List<Device> list = new ArrayList<>();

    @Override
    public void initParms(Bundle parms) {
    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_search_device;
    }

    MyAdapter adapter;
    List<Device> devices;

    @Override
    public void initView(View view) {
        deviceDao = new DeviceDaoImpl(getApplicationContext());
        devices = deviceDao.findAllDevice();


        receiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter("SearchDeviceActivity");
        filter.addAction("offline");
        registerReceiver(receiver, filter);
        Intent service = new Intent(this, MQService.class);
        bind = bindService(service, connection, Context.BIND_AUTO_CREATE);
        adapter = new MyAdapter(list, this);
        grid_list.setAdapter(adapter);
        for (Device device : devices) {
            String name = device.getDeviceName();
            Log.i("SearchDeviceActivity","-->"+name);
            strings.add(name);
        }
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String s2 = s.toString();
                if (!TextUtils.isEmpty(s2) && !strings.isEmpty()) {
                    for (int i = 0; i < strings.size(); i++) {
                        String s3 = strings.get(i);
                        if (s3.contains(s2)) {
                            Device device = devices.get(i);
                            if (!list.contains(device)) {
                                list.add(device);
                            }
                        }
                    }
                } else {
                    list.clear();
                }
                adapter.notifyDataSetChanged();
            }
        });
    }


    @OnClick({R.id.img_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    private boolean bind;
    MQService mqService;
    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MQService.LocalBinder binder = (MQService.LocalBinder) service;
            mqService = binder.getService();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null)
            unregisterReceiver(receiver);
        if (bind) {
            unbindService(connection);
        }

    }

    MessageReceiver receiver;

    class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String action = intent.getAction();

                if ("offline".equals(action)) {
                    if (intent.hasExtra("all")){
                        for (int i = 0; i < list.size(); i++) {
                            Device device2 = list.get(i);
                            device2.setOnline(false);
                            deviceDao.update(device2);
                            device2.setChoice(0);
                            if (mqService!=null){
                                mqService.updateDevice(device2);
                            }
                        }
                        adapter.notifyDataSetChanged();


                    }else {

                        String macAddress = intent.getStringExtra("macAddress");
                        for (int i = 0; i < list.size(); i++) {
                            Device device2 = list.get(i);
                            if (device2 != null && macAddress.equals(device2.getDeviceOnlyMac())) {
                                device2.setOnline(false);
                                deviceDao.update(device2);
                                device2.setChoice(0);
                                adapter.notifyDataSetChanged();
                                break;
                            }
                        }
                    }
                } else {
                    int funCode = intent.getIntExtra("funCode", 0);
                    if (funCode == 0x11) {
                        String macAddress = intent.getStringExtra("macAddress");
                        Device device = (Device) intent.getSerializableExtra("device");
                        if (dialogLoad != null && dialogLoad.isShowing()) {
                            dialogLoad.dismiss();
                        }

                        for (int i = 0; i < list.size(); i++) {
                            Device device2 = list.get(i);
                            if (device2 != null && macAddress.equals(device2.getDeviceOnlyMac())) {
                                int deviceState = device.getDeviceState();
                                device2.setDeviceState(deviceState);
                                device2.setOnline(device.getOnline());
                                device2.setChoice(0);
                                devices.set(i, device2);
                                String deviceMac = device2.getDeviceOnlyMac();
                                for (int j = 0; j < list.size(); j++) {
                                    Device device3 = list.get(j);
                                    if (deviceMac.equals(device3.getDeviceOnlyMac())){
                                        device3=device2;
                                        list.set(j,device3);
                                        adapter.notifyDataSetChanged();
                                        break;
                                    }
                                }
                                break;
                            }
                        }
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

//    private PopupWindow popupWindow2;

//    public void popupmenuWindow3() {
//        if (popupWindow2 != null && popupWindow2.isShowing()) {
//            return;
//        }
//        View view = View.inflate(this, R.layout.progress, null);
//        TextView tv_load = view.findViewById(R.id.tv_load);
//        tv_load.setTextColor(getResources().getColor(R.color.white));
//
//            popupWindow2 = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
//        //添加弹出、弹入的动画
//        popupWindow2.setAnimationStyle(R.style.Popupwindow);
//        popupWindow2.setFocusable(false);
//        popupWindow2.setOutsideTouchable(false);
//        backgroundAlpha(0.6f);
//        popupWindow2.setOnDismissListener(new PopupWindow.OnDismissListener() {
//            @Override
//            public void onDismiss() {
//                backgroundAlpha(1.0f);
//            }
//        });
////        ColorDrawable dw = new ColorDrawable(0x30000000);
////        popupWindow.setBackgroundDrawable(dw);
////        popupWindow2.showAsDropDown(et_wifi, 0, -20);
//        popupWindow2.showAtLocation(grid_list, Gravity.CENTER, 0, 0);
//        //添加按键事件监听
//    }
    DialogLoad dialogLoad;
    private void setLoadDialog() {
        if (dialogLoad != null && dialogLoad.isShowing()) {
            return;
        }

        dialogLoad = new DialogLoad(this);
        dialogLoad.setCanceledOnTouchOutside(false);
        dialogLoad.setLoad("正在加载,请稍后");
        dialogLoad.show();
    }
    private void backgroundAlpha(float f) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = f;
        getWindow().setAttributes(lp);
    }



    CountDownTimer countTimer = new CountTimer(2000, 1000);

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
//            popupmenuWindow3();
        }

        @Override
        public void onFinish() {
            if (dialogLoad != null && dialogLoad.isShowing()) {
                dialogLoad.dismiss();
            }
        }
    }

    class MyAdapter extends BaseAdapter {

        private List<Device> list;
        private Context context;

        public MyAdapter(List<Device> list, Context context) {
            this.list = list;
            this.context = context;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Device getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.item_device_list, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final Device device = getItem(position);
            boolean isOpen = device.isOpen();
            int deviceState = device.getDeviceState();
            String name = device.getDeviceName();
            String devicePassword = device.getDevicePassword();
            String share = device.getShare();
            int choice = device.getChoice();
            boolean online = device.getOnline();
            viewHolder.tv_name.setText(name);
            if (online) {
                viewHolder.img_lamp.setImageResource(R.mipmap.lamp_open);
                viewHolder.tv_imei.setText(devicePassword);

            } else {
                viewHolder.img_lamp.setImageResource(R.mipmap.lamp_close);
                viewHolder.tv_imei.setText("离线");

            }
            viewHolder.img_device_choice.setVisibility(View.GONE);
            if (choice == 1) {
                viewHolder.img_device_choice.setImageResource(R.mipmap.img_device_choice);
            } else {
                viewHolder.img_device_choice.setImageResource(R.mipmap.img_device_unchoice);
            }
            viewHolder.img_device_choice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int choice = device.getChoice();
                    if (choice == 1) {
                        device.setChoice(0);
                    } else {
                        device.setChoice(1);
                    }
                    list.set(position, device);
                    notifyDataSetChanged();
                }
            });

            viewHolder.rl_item2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Device device = list.get(position);
                    if (device.getOnline()) {
                        long deviceId = device.getDeviceId();
                        String name = device.getDeviceName();
                        Intent intent = new Intent(SearchDeviceActivity.this, DeviceItemActivity.class);
                        intent.putExtra("deviceId", deviceId);
                        intent.putExtra("name", name);
                        intent.putExtra("search", "search");
                        startActivity(intent);
                    } else {
                        ToastUtil.showShort(SearchDeviceActivity.this, "该设备离线");
                        if (mqService != null) {
                                String deviceMac = device.getDeviceOnlyMac();
                                ToastUtil.showShort(SearchDeviceActivity.this, "设备已离线");
                                String topicName = "qjjc/gateway/" + deviceMac + "/server_to_client";
                                mqService.getData(topicName, 0x11);
                        }
                    }
                }
            });

            if (TextUtils.isEmpty(share)) {
                viewHolder.rl_item2.setImageResource(R.mipmap.device_back);
            } else {
                viewHolder.rl_item2.setImageResource(R.mipmap.share_back);
            }
            return convertView;
        }
    }

    class ViewHolder {
        @BindView(R.id.img_device_choice)
        ImageView img_device_choice;
        @BindView(R.id.img_back)
        ImageView rl_item2;
        @BindView(R.id.img_lamp)
        ImageView img_lamp;
        @BindView(R.id.tv_name)
        TextView tv_name;
        @BindView(R.id.tv_imei)
        TextView tv_imei;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
