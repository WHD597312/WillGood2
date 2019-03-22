package com.peihou.willgood2.device;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.peihou.willgood2.BaseActivity;
import com.peihou.willgood2.MyApplication;
import com.peihou.willgood2.R;
import com.peihou.willgood2.custom.ChangeDialog;
import com.peihou.willgood2.custom.ExitLoginDialog;
import com.peihou.willgood2.database.dao.impl.DeviceDaoImpl;
import com.peihou.willgood2.login.LoginActivity;
import com.peihou.willgood2.pojo.Device;
import com.peihou.willgood2.receiver.MQTTMessageReveiver;
import com.peihou.willgood2.service.MQService;
import com.peihou.willgood2.utils.DisplayUtil;
import com.peihou.willgood2.utils.ToastUtil;
import com.peihou.willgood2.utils.WeakRefHandler;
import com.peihou.willgood2.utils.http.BaseWeakAsyncTask;
import com.peihou.willgood2.utils.http.HttpUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class DeviceListActivity extends BaseActivity {

    @BindView(R.id.grid_list)
    GridView grid_list;//设备列表以表格形式展示
    @BindView(R.id.img_all_close)
    ImageView img_all_close;//一键关闭选中设备
    @BindView(R.id.img_all_open)
    ImageView img_all_open;//一键打开选中设备
    MyAdapter adapter;//设备列表的适配器
    List<Device> list = new ArrayList<>();//设备列表集合
    DeviceDaoImpl deviceDao;//设备表操控者
    private Device device;//替代设备数据表的一个设备

    Map<String, Object> params = new HashMap<>();
    MyApplication application;


    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_device_list2;
    }

    boolean scrollToBottom = false;

    int lastPosition1 = -1;
    int lastPosition2 = -1;
    int lastPosition3 = -1;
    int lastPosition = -1;
    int firstVisiblePosition = -1;
    int lastVisiblePosition = -1;
    MQTTMessageReveiver reveiver;
    MQService mqService;
    boolean bind = false;
    MessageReceiver messageReceiver;
    int userId;
    SharedPreferences preferences;

    @Override
    public void initView(View view) {
        application = (MyApplication) getApplication();
        deviceDao = new DeviceDaoImpl(getApplicationContext());
        list = deviceDao.findAllDevice();

        requestOverlayPermission();
        reveiver = new MQTTMessageReveiver();

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction("mqttmessage2");
        reveiver = new MQTTMessageReveiver();
        this.registerReceiver(reveiver, filter);
        if (list != null && list.size() > 0) {
            device = new Device();
        }
        messageReceiver = new MessageReceiver();
        IntentFilter intentFilter = new IntentFilter("DeviceListActivity");
        intentFilter.addAction("offline");
        registerReceiver(messageReceiver, intentFilter);
        Intent service = new Intent(this, MQService.class);
        bind = bindService(service, connection, Context.BIND_AUTO_CREATE);


        preferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        userId = preferences.getInt("userId", 0);
        adapter = new MyAdapter(list, this);
        grid_list.setAdapter(adapter);
        grid_list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                Log.i("firstVisibleItem", "firstVisibleItem=" + firstVisibleItem + ",visibleItemCount=" + visibleItemCount);
                firstVisiblePosition = firstVisibleItem;
            }
        });
    }


    private boolean click = false;
    List<String> topicNames = new ArrayList<>();
    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MQService.LocalBinder binder = (MQService.LocalBinder) service;
            mqService = binder.getService();
            if (mqService != null) {
                for (Device device : list) {
                    String deviceMac = device.getDeviceOnlyMac();
                    String topicName = "qjjc/gateway/" + deviceMac + "/server_to_client";
                    topicNames.add(topicName);
//                    mqService.getData(topicName,0x11);
                }
                if (!topicNames.isEmpty())
                    new LoadDataAsync(DeviceListActivity.this).execute(topicNames);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    class LoadDataAsync extends BaseWeakAsyncTask<List<String>, Void, Void, DeviceListActivity> {

        public LoadDataAsync(DeviceListActivity deviceListActivity) {
            super(deviceListActivity);
        }

        @Override
        protected Void doInBackground(DeviceListActivity deviceListActivity, List<String>... lists) {
            try {
                if (mqService != null) {
                    List<String> topicNames = lists[0];
                    for (String topicName : topicNames) {
                        mqService.getData(topicName, 0x11);
                        Thread.sleep(300);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(DeviceListActivity deviceListActivity, Void aVoid) {

        }
    }

    class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("offline".equals(action)) {
                String macAddress = intent.getStringExtra("macAddress");
                for (int i = 0; i < list.size(); i++) {
                    Device device2 = list.get(i);
                    if (device2 != null && macAddress.equals(device2.getDeviceOnlyMac())) {
                        device2.setOnline(false);
                        deviceDao.update(device2);
                        list.set(i, device2);
                        adapter.notifyDataSetChanged();
                        break;
                    }
                }
            } else {
                int funCode = intent.getIntExtra("funCode", 0);
                if (funCode == 0x11) {
                    String macAddress = intent.getStringExtra("macAddress");
                    Device device = (Device) intent.getSerializableExtra("device");
                    if (popupWindow2 != null && popupWindow2.isShowing()) {
                        popupWindow2.dismiss();
                    }
                    for (int i = 0; i < list.size(); i++) {
                        Device device2 = list.get(i);
                        if (device2 != null && macAddress.equals(device.getDeviceOnlyMac())) {
                            if (device2.getVoice() == 1) {
                                Message msg = handler.obtainMessage();
                                msg.what = 1;
                                handler.sendMessage(msg);
                            }
                            int deviceState = device.getDeviceState();
                            device2.setDeviceState(deviceState);
                            device2.setOnline(device.getOnline());
                            list.set(i, device2);
                            adapter.notifyDataSetChanged();
                            break;
                        }
                    }
                }

            }
        }
    }

    Handler.Callback mCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            int what = msg.what;
            if (what == 1) {
                if (onClick == 2) {
                    mqService.starSpeech("开启成功");
                    onClick = 0;
                } else if (onClick == 1) {
                    mqService.starSpeech("关闭成功");
                    onClick = 0;
                }
            }
            return true;
        }
    };
    Handler handler = new WeakRefHandler(mCallback);
    public static boolean running = false;

    @Override
    protected void onStart() {
        super.onStart();
        running = true;
    }

    @Override
    public void onBackPressed() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            return;
        }
        if (popupWindow2 != null && popupWindow2.isShowing()) {
            popupWindow2.dismiss();
            return;
        }
        application.removeAllActivity();
    }

    @Override
    protected void onStop() {
        super.onStop();
        running = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (reveiver != null) {
            unregisterReceiver(reveiver);
        }
        if (messageReceiver != null) {
            unregisterReceiver(messageReceiver);
        }
        if (bind) {
            unbindService(connection);
        }
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 100) {
            Device device = (Device) data.getSerializableExtra("device");
            if (device != null) {
                if (mqService != null) {
                    String deviceMac = device.getDeviceOnlyMac();
                    String tioncName2 = "qjjc/gateway/" + deviceMac + "/server_to_client";
                    mqService.subscribe(tioncName2, 1);
                    String topicName = "qjjc/gateway/" + deviceMac + "/server_to_client";
                    mqService.getData(topicName, 0x11);
                    mqService.addCountTimer(deviceMac);
                    countTimer.start();
                }
                list.add(device);
                adapter.notifyDataSetChanged();
            }
        }
    }

    class UnbindDeviceAsync extends BaseWeakAsyncTask<Map<String, Object>, Void, Integer, DeviceListActivity> {

        public UnbindDeviceAsync(DeviceListActivity deviceRecordActivity) {
            super(deviceRecordActivity);
        }

        @Override
        protected Integer doInBackground(DeviceListActivity deviceRecordActivity, Map<String, Object>... maps) {
            int code = 0;
            try {
                Map<String, Object> params = maps[0];
                params.remove("groupPosition");
                params.remove("childPosition");
                String url = HttpUtils.ipAddress + "device/deleteShareDevice";
                String result = HttpUtils.requestPost(url, params);
                if (!TextUtils.isEmpty(result)) {
                    JSONObject jsonObject = new JSONObject(result);
                    code = jsonObject.getInt("returnCode");
                    if (code == 100) {
                        Device device = list.get(updateDevicePosition);
                        deviceDao.delete(device);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return code;
        }

        @Override
        protected void onPostExecute(DeviceListActivity deviceRecordActivity, Integer integer) {
            if (integer == 100) {
                ToastUtil.showShort(DeviceListActivity.this, "删除成功");
                adapter.notifyDataSetChanged();
            } else {
                ToastUtil.showShort(DeviceListActivity.this, "删除失败");
            }
        }
    }

    private void setExitLoginPage() {
        List<Activity> activities = application.getActivities();
        List<Activity> list = new ArrayList<>();
        for (Activity activity : activities) {
            if (!(activity instanceof LoginActivity)) {
                list.add(activity);
                Log.i("Activity22222", "-->LoginActivity");
            }
            if (!(activity instanceof DeviceListActivity)) {
                list.add(activity);
                Log.i("Activity22222", "-->MainActivity");
            }
        }
        application.removeActiviies(list);
        Intent intent = new Intent(DeviceListActivity.this, LoginActivity.class);
        intent.putExtra("exit", 1);
        startActivity(intent);
    }

    int oneKey = 0;//设备聊表一键开关状态，0是开的，1是关的
    int choices = 0;
    boolean success = false;

    int onClick = 0;

    CountTimer countTimer = new CountTimer(2000, 1000);

    @OnClick({R.id.img_exit, R.id.img_share, R.id.img_add_device, R.id.img_all_close, R.id.img_all_open, R.id.img_search})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_exit:
                exitLoginDialog();
                break;
            case R.id.img_share:
                startActivity(DeviceRecordActivity.class);
                break;
            case R.id.img_search:
                startActivity(SearchDeviceActivity.class);
                break;
            case R.id.img_add_device:
                Intent intent = new Intent(this, QRScannerActivity.class);
                startActivityForResult(intent, 100);
                break;
            case R.id.img_all_close:
                if (popupWindow2 != null && popupWindow2.isShowing()) {
                    ToastUtil.showShort(this, "请稍后...");
                    break;
                }
                oneKey = 0;
                success = false;
                choices = 0;

                for (int i = 0; i < list.size(); i++) {
                    Device device = list.get(i);
                    if (device.getChoice() == 1) {
                        device.setPrelines(0);
                        device.setDeviceState(0);
                        device.setLastlines(0);
                        choices++;
                        if (mqService != null) {
                            String deviceMac = device.getDeviceOnlyMac();
                            String topicName = "qjjc/gateway/" + deviceMac + "/server_to_client";
//                            String topicName = "qjjc/gateway/" + deviceMac + "/client_to_server";
                            success = mqService.sendBasic(topicName, device);
                            int voice = device.getVoice();
                            if (voice == 1)
                                onClick = 1;
                            else
                                onClick = 0;
                        }
                    }
                }
                if (choices > 0) {
                    countTimer.start();
                } else {
                    ToastUtil.showShort(this, "请选择要关闭的设备");
                }
                break;
            case R.id.img_all_open:
                if (popupWindow2 != null && popupWindow2.isShowing()) {
                    ToastUtil.showShort(this, "请稍后...");
                    break;
                }

                oneKey = 1;
                choices = 0;
                success = false;

                for (int i = 0; i < list.size(); i++) {
                    Device device = list.get(i);
                    if (device.getChoice() == 1) {
                        choices++;
                        device.setPrelines(255);
                        device.setLastlines(255);
                        device.setDeviceState(1);
                        if (mqService != null) {
                            String deviceMac = device.getDeviceOnlyMac();
                            String topicName = "qjjc/gateway/" + deviceMac + "/server_to_client";
//                            String topicName = "qjjc/gateway/" + deviceMac + "/client_to_server";
                            success = mqService.sendBasic(topicName, device);
                            int voice = device.getVoice();
                            if (voice == 1)
                                onClick = 2;
                            else
                                onClick = 0;
                        }
                    }
                }
                if (choices > 0) {
                    countTimer.start();
                } else {
                    ToastUtil.showShort(this, "请选择要打开的设备");
                }
                break;
        }
    }


    ExitLoginDialog exitLoginDialog;

    /**
     * 弹出退出登录页面对话框
     */
    private void exitLoginDialog() {
        if (exitLoginDialog != null && exitLoginDialog.isShowing()) {
            return;
        }
        exitLoginDialog = new ExitLoginDialog(this);
        exitLoginDialog.setCanceledOnTouchOutside(false);
        exitLoginDialog.setOnNegativeClickListener(new ExitLoginDialog.OnNegativeClickListener() {
            @Override
            public void onNegativeClick() {
                exitLoginDialog.dismiss();
            }
        });
        exitLoginDialog.setOnPositiveClickListener(new ExitLoginDialog.OnPositiveClickListener() {
            @Override
            public void onPositiveClick() {
                if (mqService != null) {
                    mqService.clearAllData();
                    mqService.cancelAllsubscibe();
                }
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
                if (popupWindow2 != null && popupWindow2.isShowing()) {
                    popupWindow2.dismiss();
                }
                setExitLoginPage();
            }
        });
        exitLoginDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                backgroundAlpha(1.0f);
            }
        });
        backgroundAlpha(0.6f);
        exitLoginDialog.show();
    }

    ChangeDialog dialog;

    /**
     * 这个对话框用于修改设备的备注，删除设备，改变设备初始码
     *
     * @param type
     * @param postion
     */
    int updateDeviceType = -1;
    int updateDevicePosition = -1;

    private void changeDialog(final int type, final int postion) {
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        dialog = new ChangeDialog(this);
        dialog.setCanceledOnTouchOutside(false);

        if (type == 0) {
            dialog.setMode(0);
            dialog.setTitle("备注");
            dialog.setTips("编辑内容");
        } else if (type == 1) {
            dialog.setMode(1);
            dialog.setTips("确定要删除吗?");
            dialog.setTitle("删除");
        } else if (type == 2) {
            dialog.setMode(0);
            dialog.setInputType(2);
            dialog.setTitle("初始码");
            dialog.setTips("编辑内容");
        }
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
                try {
                    String content = dialog.getContent();
                    if (TextUtils.isEmpty(content)) {
                        ToastUtil.show(DeviceListActivity.this, "编辑内容不能为空", Toast.LENGTH_SHORT);
                    } else {
                        if (type == 0) {
                            Device device = list.get(postion);
                            long deviceId = device.getDeviceId();
                            String deviceName = content;
                            String devicePassword = device.getDevicePassword();
                            params.clear();
                            params.put("devicePassword", devicePassword);
                            params.put("deviceName", deviceName);
                            params.put("deviceId", deviceId);
                            updateDeviceType = 0;
                            updateDevicePosition = postion;
                            new UpdateDeviceNameOrPswd().execute(params).get(3, TimeUnit.SECONDS);
                        } else if (type == 1) {
                            updateDevicePosition = postion;
                            params.clear();
                            Device device = list.get(postion);
                            String share = device.getShare();
                            if (!TextUtils.isEmpty(share)) {
                                long deviceId = device.getDeviceId();
                                params.clear();
                                params.put("deviceId", deviceId);
                                params.put("deviceSharerId", userId);
                                new UnbindDeviceAsync(DeviceListActivity.this).execute(params);
                            } else {
                                long deviceId = device.getDeviceId();
                                params.put("deviceId", deviceId);
                                new DeleteDeviceAsync().execute(params).get(3, TimeUnit.SECONDS);
                            }


                        } else if (type == 2) {
                            Device device = list.get(postion);
                            long deviceId = device.getDeviceId();
                            String deviceName = device.getDeviceName();
                            String devicePassword = content;
                            params.clear();
                            params.put("devicePassword", devicePassword);
                            params.put("deviceName", deviceName);
                            params.put("deviceId", deviceId);
                            updateDeviceType = 2;
                            updateDevicePosition = postion;
                            new UpdateDeviceNameOrPswd().execute(params).get(3, TimeUnit.SECONDS);
                        }
                        dialog.dismiss();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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

    @Override
    public void doBusiness(Context mContext) {

    }

    /***
     * 弹出网格布局的子条目的菜单
     */
    PopupWindow popupWindow;

    private void popupNote(int type, View item, int xoff, final int position) {
        if (popupWindow != null && popupWindow.isShowing()) {
            return;
        }

        View view = null;
        if (type == 0) {
            view = View.inflate(this, R.layout.popup_device_menu, null);
        } else if (type == 1) {
            view = View.inflate(this, R.layout.popup_device_menu2, null);
        } else if (type == 2) {
            view = View.inflate(this, R.layout.popup_device_menu3, null);
        } else if (type == 3) {
            view = View.inflate(this, R.layout.popup_device_menu4, null);
        }
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        //点击空白处时，隐藏掉pop窗口
        popupWindow.setFocusable(false);
        popupWindow.setOutsideTouchable(true);
        popupWindow.update();

        TextView tv_note = view.findViewById(R.id.tv_note);
        TextView tv_delete = view.findViewById(R.id.tv_delete);
        TextView tv_original_code = view.findViewById(R.id.tv_original_code);
        TextView tv_share = view.findViewById(R.id.tv_share);
        tv_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                changeDialog(0, position);
            }
        });
        tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                changeDialog(1, position);
            }
        });
        tv_original_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();

                changeDialog(2, position);
            }
        });
        tv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                Device device = list.get(position);
                long deviceId = device.getDeviceId();
                String name = device.getDeviceName();
                String share = device.getShare();
                if (!TextUtils.isEmpty(share)) {
                    ToastUtil.showShort(DeviceListActivity.this, "分享的设备不能再次分享");
                } else {
                    String devicePassword = device.getDevicePassword();
                    String deviceMac = device.getDeviceOnlyMac();
                    Intent intent = new Intent(DeviceListActivity.this, ShareDeviceActivity.class);
                    intent.putExtra("deviceId", deviceId);
                    intent.putExtra("devicePassword", devicePassword);
                    intent.putExtra("name", name);
                    intent.putExtra("deviceMac", deviceMac);
                    startActivity(intent);
                }
            }
        });
        if (type == 0 || type == 1) {
            popupWindow.showAsDropDown(item, xoff, 0);
        } else if (type == 2) {
            int[] location = new int[2];
            item.getLocationOnScreen(location);
            view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            int popupWidth = view.getMeasuredWidth();    //  获取测量后的宽度
            int popupHeight = view.getMeasuredHeight();  //获取测量后的高度
            int ss = location[1] - popupHeight;
            Log.i("popupHeight", "-->" + popupHeight);
            popupWindow.showAtLocation(item, Gravity.NO_GRAVITY, (location[0] + item.getWidth()) + popupWidth, location[1] - popupHeight - (popupHeight / 14));
        } else if (type == 3) {
            int[] location = new int[2];
            item.getLocationOnScreen(location);
            view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            int popupWidth = view.getMeasuredWidth();    //  获取测量后的宽度
            int popupHeight = view.getMeasuredHeight();  //获取测量后的高度
            int ss = location[1] - popupHeight;
            Log.i("popupHeight", "-->" + popupHeight);
            popupWindow.showAtLocation(item, Gravity.NO_GRAVITY, (location[0]) - popupWidth + item.getWidth(), location[1] - popupHeight - (popupHeight / 14));
        }

        //添加按键事件监听
        backgroundAlpha(0.6f);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1.0f);
            }
        });
    }

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
            if (popupWindow2!=null && popupWindow2.isShowing()) {
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
        TextView tv_load = view.findViewById(R.id.tv_load);
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
        popupWindow2.showAtLocation(grid_list, Gravity.CENTER, 0, 0);
        //添加按键事件监听
    }

    //设置蒙版
    private void backgroundAlpha(float f) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = f;
        getWindow().setAttributes(lp);
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

            if (deviceState == 1) {
                viewHolder.img_lamp.setImageResource(R.mipmap.lamp_open);
            } else {
                viewHolder.img_lamp.setImageResource(R.mipmap.lamp_close);
            }
            if (!online){
                viewHolder.img_lamp.setImageResource(R.mipmap.lamp_close);
            }
            if (choice == 1) {
                viewHolder.img_device_choice.setImageResource(R.mipmap.img_device_choice);
            } else {
                viewHolder.img_device_choice.setImageResource(R.mipmap.img_device_unchoice);
            }
            viewHolder.img_device_choice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (device.getOnline()) {
                        int choice = device.getChoice();
                        if (choice == 1) {
                            device.setChoice(0);
                        } else {
                            device.setChoice(1);
                        }
                        list.set(position, device);
                        notifyDataSetChanged();
                    } else {
                        if (mqService != null) {
                            String deviceMac = device.getDeviceOnlyMac();
                            ToastUtil.showShort(DeviceListActivity.this, "设备已离线");
                            String topicName = "qjjc/gateway/" + deviceMac + "/server_to_client";
                            mqService.getData(topicName, 0x11);
                        }

                    }

                }
            });
            final View finalConvertView = convertView;
            viewHolder.rl_item2.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    View item = finalConvertView.findViewById(R.id.view);
                    int counts = grid_list.getChildCount();

                    if (list.size() > 9) {

                    } else {
                        if (position < 6 && (position + 1) % 3 == 0) {
                            int xoff = DisplayUtil.px2dip(DeviceListActivity.this, -350);
                            View item2 = finalConvertView.findViewById(R.id.view2);
                            popupNote(1, item2, xoff, position);
                        } else if (position < 6 && (position + 1) % 3 != 0) {
                            popupNote(0, item, -10, position);
                        } else if (position >= 6 && (position + 1) % 3 != 0) {
                            View item3 = finalConvertView.findViewById(R.id.view3);
                            popupNote(2, item3, 0, position);
                        } else if (position >= 6 && (position + 1) % 3 == 0) {
                            View item3 = finalConvertView.findViewById(R.id.view3);
                            popupNote(3, item3, 0, position);
                        }

                    }
                    return true;
                }
            });
            viewHolder.rl_item2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    Device device = list.get(position);
                    if (!device.getOnline()) {
                        String deviceMac = device.getDeviceOnlyMac();
                        String topicName = "qjjc/gateway/" + deviceMac + "/server_to_client";
                        mqService.getData(topicName,0x11);
                    }
//                    long deviceId = device.getDeviceId();
//                    String name = device.getDeviceName();
//                    Intent intent = new Intent(DeviceListActivity.this, DeviceItemActivity.class);
//                    intent.putExtra("deviceId", deviceI;
//                    intent.putExtra("name", name);d)
//                    startActivityForResult(intent, 100);
                    if (device.getOnline()){
                        long deviceId=device.getDeviceId();
                        String name=device.getDeviceName();
                        Intent intent=new Intent(DeviceListActivity.this,DeviceItemActivity.class);
                        intent.putExtra("deviceId",deviceId);
                        intent.putExtra("name",name);
                        startActivityForResult(intent,100);
                    }else {
                        if (mqService!=null){
                            String deviceMac=device.getDeviceOnlyMac();
                            ToastUtil.showShort(DeviceListActivity.this,"设备已离线");
                            String topicName="qjjc/gateway/"+deviceMac+"/server_to_client";
                            mqService.getData(topicName,0x11);
                        }
                    }
                }
            });
            viewHolder.tv_name.setText(name);
            if (online) {
                viewHolder.tv_imei.setText(devicePassword);
            } else {
                viewHolder.tv_imei.setText("离线");
            }


            if (TextUtils.isEmpty(share)) {
                viewHolder.rl_item2.setImageResource(R.mipmap.device_back);
            } else {
                viewHolder.rl_item2.setImageResource(R.mipmap.share_back);
            }
            return convertView;
        }
    }

    private static final int REQUEST_OVERLAY = 4444;

    private void requestOverlayPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(this)) {
                changeDialog();
            } else {

            }
        }
    }
    private void changeDialog(){
        if (dialog!=null && dialog.isShowing()){
            return;
        }
        dialog=new ChangeDialog(this);
        dialog.setCanceledOnTouchOutside(false);


        dialog.setMode(1);
        dialog.setTitle("权限申请");
        dialog.setTips("请打开悬浮窗权限!");

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
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_OVERLAY);
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

    String updateDeviceName;

    class UpdateDeviceNameOrPswd extends AsyncTask<Map<String, Object>, Void, Integer> {

        @Override
        protected Integer doInBackground(Map<String, Object>... maps) {
            int code = 0;
            try {
                String url = HttpUtils.ipAddress + "device/updateDevice";
                Map<String, Object> params = maps[0];
                String result = HttpUtils.requestPost(url, params);
                if (!TextUtils.isEmpty(result)) {
                    JSONObject jsonObject = new JSONObject(result);
                    code = jsonObject.getInt("returnCode");
                    if (code == 100) {
                        if (updateDeviceType == 0)
                            updateDeviceName = (String) params.get("deviceName");
                        else if (updateDeviceType == 2) {
                            updateDeviceName = (String) params.get("devicePassword");
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return code;
        }

        @Override
        protected void onPostExecute(Integer code) {
            super.onPostExecute(code);
            switch (code) {
                case 100:
                    ToastUtil.showShort(DeviceListActivity.this, "修改成功");
                    if (updateDeviceType == 0) {
                        Device device = list.get(updateDevicePosition);
                        device.setDeviceName(updateDeviceName);
                        list.set(updateDevicePosition, device);
                        adapter.notifyDataSetChanged();
                    } else if (updateDeviceType == 2) {
                        Device device = list.get(updateDevicePosition);
                        device.setDevicePassword(updateDeviceName);
                        list.set(updateDevicePosition, device);
                        adapter.notifyDataSetChanged();
                    }
                    break;
                default:
                    ToastUtil.showShort(DeviceListActivity.this, "修改失败");
                    break;
            }
        }
    }

    class DeleteDeviceAsync extends AsyncTask<Map<String, Object>, Void, Integer> {

        @Override
        protected Integer doInBackground(Map<String, Object>... maps) {
            Map<String, Object> params = maps[0];
            int code = 0;
            try {
                String url = HttpUtils.ipAddress + "device/deleteDeviceByApp";
                String result = HttpUtils.requestPost(url, params);
                if (!TextUtils.isEmpty(result)) {
                    JSONObject jsonObject = new JSONObject(result);
                    code = jsonObject.getInt("returnCode");
                    if (code == 100) {
                        Device device = list.get(updateDevicePosition);
                        deviceDao.delete(device);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return code;
        }

        @Override
        protected void onPostExecute(Integer code) {
            super.onPostExecute(code);
            switch (code) {
                case 100:
                    ToastUtil.showShort(DeviceListActivity.this, "删除成功");
                    list.remove(updateDevicePosition);
                    adapter.notifyDataSetChanged();
                    break;
                default:
                    ToastUtil.showShort(DeviceListActivity.this, "删除失败");
                    break;
            }
        }
    }

}
