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
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcelable;
import android.provider.Settings;
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

import com.google.gson.Gson;
import com.jwenfeng.library.pulltorefresh.BaseRefreshListener;
import com.jwenfeng.library.pulltorefresh.PullToRefreshLayout;
import com.peihou.willgood2.BaseActivity;
import com.peihou.willgood2.MyApplication;
import com.peihou.willgood2.R;
import com.peihou.willgood2.custom.AppUpdateDialog;
import com.peihou.willgood2.custom.ChangeDialog;
import com.peihou.willgood2.custom.DialogLoad;
import com.peihou.willgood2.custom.ExitLoginDialog;
import com.peihou.willgood2.custom.MyHeadRefreshView;
import com.peihou.willgood2.custom.MyLoadMoreView;
import com.peihou.willgood2.database.dao.impl.DeviceDaoImpl;
import com.peihou.willgood2.login.LoginActivity;
import com.peihou.willgood2.pojo.Device;
import com.peihou.willgood2.receiver.MQTTMessageReveiver;
import com.peihou.willgood2.receiver.UtilsJPush;
import com.peihou.willgood2.service.MQService;
import com.peihou.willgood2.service.ServiceUtils;
import com.peihou.willgood2.utils.DisplayUtil;
import com.peihou.willgood2.utils.ToastUtil;
import com.peihou.willgood2.utils.WeakRefHandler;
import com.peihou.willgood2.utils.http.BaseWeakAsyncTask;
import com.peihou.willgood2.utils.http.HttpUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
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

    Map<String, Object> params = new HashMap<>();
    MyApplication application;
    @BindView(R.id.swipeRefresh)
    PullToRefreshLayout swipeRefresh;


    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_device_list2;
    }


    MQService mqService;
    boolean bind = false;
    MessageReceiver messageReceiver;
    int userId;
    SharedPreferences preferences;
    MQTTMessageReveiver reveiver;
    private int load = 0;
    int refresh=0;
    @Override
    public void initView(View view) {
        application = (MyApplication) getApplication();
        deviceDao = new DeviceDaoImpl(getApplicationContext());
        list = deviceDao.findAllDevice();

//        requestOverlayPermission();


        reveiver = new MQTTMessageReveiver();

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
//        filter.addAction(Intent.ACTION_BOOT_COMPLETED);
        filter.addAction("mqttmessage2");
//        filter.addAction(Intent.ACTION_TIME_TICK);
        this.registerReceiver(reveiver, filter);
        messageReceiver = new MessageReceiver();
        IntentFilter intentFilter = new IntentFilter("DeviceListActivity");
        intentFilter.addAction("offline");
        registerReceiver(messageReceiver, intentFilter);
        Intent service = new Intent(this, MQService.class);
        bind = bindService(service, connection, Context.BIND_AUTO_CREATE);

        preferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        userId = preferences.getInt("userId", 0);

        Log.i("userIddddd","-->"+userId);
        UtilsJPush.resumeJpush(this);
        Intent intent = getIntent();
        if (intent.hasExtra("login")) {
            int login = intent.getIntExtra("login", 0);
            if (login == 1) {
//                if ("cancel".equals(MyApplication.update)) {
                try {
//                    String appName=getString(R.string.app_name);
//                    if ("迈科智联".equals(appName)){
//                        PackageManager packageManager = application.getPackageManager();
//                        try {
//                            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
//
//                            versionName = packageInfo.versionName;
//                            versionCode = packageInfo.versionCode;
//                            params.put("appType",6);
////                            new UpdateAppAsync(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,params);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
                    load = 1;
                    params.clear();
                    params.put("userId", userId);
                    new LoadDeviceListAsync(DeviceListActivity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,params);
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                }


            }
        }
        adapter = new MyAdapter(list, this);
        grid_list.setAdapter(adapter);
        swipeRefresh.setHeaderView(new MyHeadRefreshView(this));
        swipeRefresh.setFooterView(new MyLoadMoreView(this));
        swipeRefresh.setRefreshListener(new BaseRefreshListener() {
            @Override
            public void refresh() {
                countTimer.start();
                new LoadDataAsync(DeviceListActivity.this).execute(topicNames);
            }

            @Override
            public void loadMore() {
                params.clear();
                params.put("userId", userId);
                refresh=1;
                new LoadDeviceListAsync(DeviceListActivity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,params);
            }
        });

    }

    /**
     * 判断最后listView中最后一个item是否完全显示出来
     * listView 是集合的那个ListView
     *
     * @return true完全显示出来，否则false
     */


    List<Device> devices = new ArrayList<>();

    public void insert(Device device, String macAddress) {

    }

    int versionCode;
    String versionName;
    String appUrl = HttpUtils.ipAddress+"user/getAPPVersion";
    String updateAppUrl = "https://pgyer.com/OSRU";
    AppUpdateDialog appUpdateDialog;

    String updateAppVersion;

    class UpdateAppAsync extends BaseWeakAsyncTask<Map<String, Object>, Void, Integer, DeviceListActivity> {

        public UpdateAppAsync(DeviceListActivity activity) {
            super(activity);
        }

        @Override
        protected Integer doInBackground(DeviceListActivity activity, Map<String, Object>... maps) {
            int code = 0;
            try {
                Map<String,Object> params=maps[0];
                String result = HttpUtils.requestPost(appUrl, params);
                Log.i("UpdateAppAsync", "-->" + result);
                if (!TextUtils.isEmpty(result)) {
                    JSONObject jsonObject = new JSONObject(result);
                    int resultCode = jsonObject.getInt("returnCode");
                    if (resultCode == 100) {
                        JSONObject returnData = jsonObject.getJSONObject("returnData");
                        String appVersion = returnData.getString("appVersion");
                        updateAppVersion=appVersion;
                        Log.i("appversion", "-->" + appVersion + "," + versionName);
                        if (appVersion.equals(versionName)) {
                            code = -2000;
                        } else {
                            code = 2000;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            return code;
        }

        @Override
        protected void onPostExecute(DeviceListActivity activity, Integer integer) {
            if (integer == 2000) {
                appUpdateDialog = new AppUpdateDialog(DeviceListActivity.this);
                appUpdateDialog.setCanceledOnTouchOutside(false);
                appUpdateDialog.setName(updateAppVersion);
                appUpdateDialog.setOnNegativeClickListener(new AppUpdateDialog.OnNegativeClickListener() {
                    @Override
                    public void onNegativeClick() {
                        MyApplication.update = "refudsed";
                        appUpdateDialog.dismiss();
                    }
                });
                appUpdateDialog.setOnPositiveClickListener(new AppUpdateDialog.OnPositiveClickListener() {
                    @Override
                    public void onPositiveClick() {

                        MyApplication.update = "update";
                        Intent intent = new Intent();
                        intent.setData(Uri.parse(updateAppUrl));//Url 就是你要打开的网址
                        intent.setAction(Intent.ACTION_VIEW);
                        DeviceListActivity.this.startActivity(intent); //启动浏览器
                        appUpdateDialog.dismiss();
                    }
                });
                appUpdateDialog.show();
            }else {
                load = 1;
                params.clear();
                params.put("userId", userId);
                new LoadDeviceListAsync(DeviceListActivity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,params);
            }
        }
    }

    class AddOperationLogAsync extends BaseWeakAsyncTask<Map<String, Object>, Void, Integer, DeviceListActivity> {

        public AddOperationLogAsync(DeviceListActivity activity) {
            super(activity);
        }

        @Override
        protected Integer doInBackground(DeviceListActivity activity, Map<String, Object>... maps) {
            Map<String, Object> params = maps[0];
            String url = HttpUtils.ipAddress + "data/addOperationLog";
            String result = HttpUtils.requestPost(url, params);
            Log.i("AddOperationLogAsync", "-->" + result);
            return null;
        }

        @Override
        protected void onPostExecute(DeviceListActivity activity, Integer integer) {

        }
    }

    class LoadDeviceListAsync extends BaseWeakAsyncTask<Map<String, Object>, Void, Integer, DeviceListActivity> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        public LoadDeviceListAsync(DeviceListActivity activity) {
            super(activity);
        }

        @Override
        protected Integer doInBackground(DeviceListActivity activity, Map<String, Object>... maps) {
            Map<String, Object> params = maps[0];
            int code = 0;
            try {
                String url = HttpUtils.ipAddress + "device/getDeviceList";
                String result = HttpUtils.requestPost(url, params);
                Log.i("LoadDeviceListAsync", "-->" + result);
                if (TextUtils.isEmpty(result)) {
                    result = HttpUtils.requestPost(url, params);
                }
                if (!TextUtils.isEmpty(result)) {
                    JSONObject jsonObject = new JSONObject(result);
                    code = jsonObject.getInt("returnCode");
                    if (code == 100) {
                        devices.clear();
                        deviceDao.deleteAll();
                        topicNames.clear();
                        JSONObject returnData = jsonObject.getJSONObject("returnData");
                        JSONArray deviceList = returnData.getJSONArray("deviceList");

                        for (int i = 0; i < deviceList.length(); i++) {
                            String s = deviceList.getJSONObject(i).toString();
                            Gson gson = new Gson();
                            Device device = gson.fromJson(s, Device.class);
                            String deviceMac = device.getDeviceOnlyMac();
                            String topicName = "qjjc/gateway/" + deviceMac + "/server_to_client";
                            topicNames.add(topicName);
                            deviceDao.insert(device);
                            devices.add(device);
                        }
                        JSONArray deviceShareList = returnData.getJSONArray("deviceShareList");
                        for (int i = 0; i < deviceShareList.length(); i++) {
                            String s2 = deviceShareList.getJSONObject(i).toString();
                            Gson gson = new Gson();
                            Device device = gson.fromJson(s2, Device.class);
                            device.setShare("share");
                            deviceDao.insert(device);
                            String deviceMac = device.getDeviceOnlyMac();
                            Log.i("deviceMac", "-->" + deviceMac);
                            String topicName = "qjjc/gateway/" + deviceMac + "/server_to_client";
                            topicNames.add(topicName);
                            devices.add(device);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return code;
        }

        @Override
        protected void onPostExecute(DeviceListActivity activity, Integer code) {
            if (swipeRefresh!=null){
                swipeRefresh.finishLoadMore();
            }
            switch (code) {
                case 100:
                    Log.i("topicNames","-->"+topicNames.size());
                    Log.i("subscribeAll", "--------"+mqService);
                    list.clear();
                    list.addAll(devices);
                    adapter.notifyDataSetChanged();
                    handler.sendEmptyMessage(100);

                    break;
                default:
                    break;
            }
        }
    }

    private boolean click = false;
    List<String> topicNames = new ArrayList<>();
    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MQService.LocalBinder binder = (MQService.LocalBinder) service;
            mqService = binder.getService();
            if (mqService!=null){
                mqService.setUserId(userId);
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    class LoadDataAysnc2 extends BaseWeakAsyncTask<Void, Void, Integer, DeviceListActivity> {

        public LoadDataAysnc2(DeviceListActivity deviceListActivity) {
            super(deviceListActivity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected Integer doInBackground(DeviceListActivity deviceListActivity, Void... voids) {
            int code = 0;
            List<Device> devices = deviceDao.findAllDevice();
            try {
                int i = 0;
                for (Device device : devices) {
                    String deviceMac = device.getDeviceOnlyMac();
                    String topicName = "qjjc/gateway/" + deviceMac + "/server_to_client";
                    Log.i("topicName","-->"+topicName);
                    if (mqService != null) {
                        mqService.getData(topicName, 0x11);
                        Thread.currentThread().sleep(500);
                        i++;
                        if (i == devices.size()) {
                            code = 100;
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return code;
        }

        @Override
        protected void onPostExecute(DeviceListActivity deviceListActivity, Integer code) {
        }
    }

    class LoadDataAsync extends BaseWeakAsyncTask<List<String>, Void, List<String>, DeviceListActivity> {

        public LoadDataAsync(DeviceListActivity deviceListActivity) {
            super(deviceListActivity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!list.isEmpty()) { int n = list.size();
                int total = 0;
                if (0 < n && n <= 2) {
                    total = 2000;
                } else if (n > 2 && n <= 6) {
                    total = 4000;
                } else if (n > 6) {
                    total = 6000;
                }
                CountTimer2 countTimer = new CountTimer2(3000, 1000);
                countTimer.start();

            }
        }

        @Override
        protected List<String> doInBackground(DeviceListActivity deviceListActivity, List<String>... lists) {
            try {

                if (mqService != null) {
                    List<Device> devices=deviceDao.findAllDevice();
                    mqService.subscribeAll(devices);
                    List<String> topicNames = lists[0];
                    for (String topicName : topicNames) {
                        String macAddress = topicName.substring(13, topicName.lastIndexOf("/"));
                        Log.i("subscribeAll", "-->" + topicName);
//                        mqService.addCountTimer(macAddress);
                        Thread.sleep(500);
                        Log.i("deviceMac", "-->" + macAddress);
                        mqService.getData(topicName, 0x11);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return lists[0];
        }

        @Override
        protected void onPostExecute(DeviceListActivity deviceListActivity, List<String> list1) {
            Log.i("onPostExecute", "-->" + list.size());
            if (swipeRefresh!=null){
                swipeRefresh.finishRefresh();
            }

        }
    }


    class   MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            try {
                if ("offline".equals(action)) {
                    if (intent.hasExtra("all")) {
                        for (int i = 0; i < list.size(); i++) {
                            Device device2 = list.get(i);
                            device2.setOnline(false);
                            deviceDao.update(device2);
                            device2.setChoice(0);
                            if (mqService != null) {
                                mqService.updateDevice(device2);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        String macAddress = intent.getStringExtra("macAddress");
                        for (int i = 0; i < list.size(); i++) {
                            Device device2 = list.get(i);
                            if (device2 != null && macAddress.equals(device2.getDeviceOnlyMac())) {
                                if (dialogLoad != null && dialogLoad.isShowing()) {
                                    dialogLoad.dismiss();
                                }
                                device2.setOnline(false);
                                deviceDao.update(device2);
                                device2.setChoice(0);
                                list.set(i,device2);
                                adapter.notifyDataSetChanged();
                                break;
                            }
                        }
                    }
                } else {
                    try {
                        int funCode = intent.getIntExtra("funCode", 0);
                        if (funCode == 0x11) {
                            String macAddress = intent.getStringExtra("macAddress");
                            Device device = (Device) intent.getSerializableExtra("device");
                            String lines = intent.getStringExtra("lines");
                            device.setLines(lines);
                            Message msg = handler.obtainMessage();
                            msg.what = 1001;
                            msg.obj = device;
                            handler.sendMessage(msg);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    Map<String, Object> operateLog = new HashMap<>();
    Handler.Callback mCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            int what = msg.what;
            if (what == 1001) {

                Device device = (Device) msg.obj;
                for (int i = 0; i < list.size(); i++) {
                    Device device2 = list.get(i);
                    if (device2 != null && device.getDeviceOnlyMac().equals(device2.getDeviceOnlyMac())) {
                        if (onClick == 1) {
                            if (dialogLoad != null && dialogLoad.isShowing()) {
                                dialogLoad.dismiss();
                            }
                            mqService.starSpeech(device.getDeviceOnlyMac(), 0);
                            operateLog.clear();
                            operateLog.put("deviceMac", device.getDeviceOnlyMac());
                            operateLog.put("deviceControll", 1);
                            operateLog.put("deviceLogType", 1);
                            operateLog.put("deviceLine", device.getLines());
                            operateLog.put("userId", userId);
                            new AddOperationLogAsync(DeviceListActivity.this).execute(operateLog);
                            onClick = 0;
                        } else if (onClick == 2) {
                            if (dialogLoad != null && dialogLoad.isShowing()) {
                                dialogLoad.dismiss();
                            }
                            operateLog.clear();
                            operateLog.put("deviceMac", device.getDeviceOnlyMac());
                            operateLog.put("deviceControll", 1);
                            operateLog.put("deviceLogType", 2);
                            operateLog.put("deviceLine", device.getLines());
                            operateLog.put("userId", userId);
                            new AddOperationLogAsync(DeviceListActivity.this).execute(operateLog);
                            mqService.starSpeech(device.getDeviceOnlyMac(), 1);
                            onClick = 0;
                        }
                        int deviceState = device.getDeviceState();
                        device2.setDeviceState(deviceState);
                        device2.setOnline(device.getOnline());
                        device2.setChoice(0);
                        list.set(i, device2);
                        adapter.notifyDataSetChanged();
                        break;
                    }
                }
            }else if (what==100){
                Log.i("handler","-->"+mqService);
                if (!topicNames.isEmpty()){
                    setLoadDialog();
                    new LoadDataAsync(DeviceListActivity.this).execute(topicNames);
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
        requestOverlayPermission();
//        Log.i("devicehhhhhhhhhhhh","-->"+onResult);
//        if (!running && mqService!=null && onResult==0){
//            List<Device> devices=mqService.getDevices();
//            list.clear();
//            list.addAll(devices);
//            adapter.notifyDataSetChanged();
//        }

        running = true;
    }
    int reload=0;
    @Override
    protected void onResume() {
        super.onResume();
        if (mqService!=null && onResult==0){
            List<Device> devices=mqService.getDevices();
            list.clear();
            list.addAll(devices);
            adapter.notifyDataSetChanged();
//            popupmenuWindow3();
            countTimer.start();
            new LoadDataAsync(DeviceListActivity.this).execute(topicNames);

        }
        onResult=0;
    }


    @Override
    public void onBackPressed() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            return;
        }
        if (dialogLoad != null && dialogLoad.isShowing()) {
            ToastUtil.showShort(this,"请稍后");
            return;
        }
        if (mqService != null) {
            mqService.removeOfflineDevices();
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
        if (popupWindow!=null && popupWindow.isShowing()){
            popupWindow.dismiss();
        }
        if (dialogLoad!=null && dialogLoad.isShowing()){
            dialogLoad.dismiss();
        }
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

    int onResult = 0;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 100) {
            onResult = 1;
            Device device = (Device) data.getSerializableExtra("device");
            if (device != null) {
                if (mqService != null) {
                    countTimer.start();
                    String deviceMac = device.getDeviceOnlyMac();
                    String tioncName2 = "qjjc/gateway/" + deviceMac + "/lwt";
                    String topicName = "qjjc/gateway/" + deviceMac + "/client_to_server";
                    String topicName3 = "qjjc/gateway/" + deviceMac + "/server_to_client";
                    mqService.subscribe(topicName, 1);
                    mqService.subscribe(tioncName2, 1);
                    mqService.getData(topicName3, 0x11);
//                    mqService.addCountTimer(deviceMac);
                }
                int insert=data.getIntExtra("insert",0);
                if (insert==1){
                    list.add(device);
                    adapter.notifyDataSetChanged();
                }
            }
        }else if (resultCode==1002){
            onResult=1;
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
                        String macAddress = device.getDeviceOnlyMac();
                        if (mqService != null) {
                            mqService.revoveOfflineDevice(macAddress);
                            mqService.deleteDevice(macAddress);
                            mqService.revoveOfflineDevice(macAddress);
//                            mqService.revoveCountTimer(macAddress);

                        }
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
                list.remove(updateDevicePosition);
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

        UtilsJPush.stopJpush(this);
        if (mqService!=null){
//            mqService.clearCountTimer();
            mqService.clearAllData();
        }
        application.removeActiviies(list);
        Intent intent = new Intent(DeviceListActivity.this, LoginActivity.class);
        intent.putExtra("exit", 1);
        startActivity(intent);
    }

    int oneKey = -1;//设备聊表一键开关状态，0是开的，1是关的
    int choices = 0;
    boolean success = false;


    int onClick = 0;

    CountTimer countTimer = new CountTimer(2000, 1000);

    @OnClick({R.id.img_exit, R.id.img_share, R.id.img_add_device, R.id.img_all_close, R.id.img_all_open, R.id.img_search})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_exit:
                if (dialogLoad!=null && dialogLoad.isShowing()){
                    ToastUtil.showShort(this,"请稍后...");
                    break;
                }
                exitLoginDialog();
                break;
            case R.id.img_share:
                startActivity(DeviceRecordActivity.class);
                break;
            case R.id.img_search:
                Intent serch = new Intent(DeviceListActivity.this, SearchDeviceActivity.class);
                serch.putExtra("list", (Serializable) list);
                startActivity(serch);
                break;
            case R.id.img_add_device:
                Intent intent = new Intent(this, QRScannerActivity.class);
                startActivityForResult(intent, 100);
                break;
            case R.id.img_all_close:
                if (dialogLoad != null && dialogLoad.isShowing()) {
                    ToastUtil.showShort(this, "请稍后...");
                    break;
                }
                oneKey = 0;
                success = false;
                int choices = 0;

                for (int i = 0; i < list.size(); i++) {
                    Device device = list.get(i);
                    if (device.getChoice() == 1) {
                        device.setPrelineswitch(0);
                        device.setLastlineswitch(0);
                        device.setDeviceState(0);
                        device.setPrelinesjog(0);
                        device.setLastlinesjog(0);
                        choices++;
                        if (mqService != null) {
                            String deviceMac = device.getDeviceOnlyMac();
                            String topicName = "qjjc/gateway/" + deviceMac + "/server_to_client";
//                            String topicName = "qjjc/gateway/" + deviceMac + "/client_to_server";
                            success = mqService.sendBasic(topicName, device,0x01);
                        }
                    }
                }
                if (choices > 0) {
                    onClick = 2;
                    countTimer.start();
                } else {
                    ToastUtil.showShort(this, "请选择要关闭的设备");
                }
                break;
            case R.id.img_all_open:
                if (dialogLoad != null && dialogLoad.isShowing()) {
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
                        device.setPrelineswitch(255);
                        device.setLastlineswitch(255);
                        device.setDeviceState(1);
                        device.setPrelinesjog(0);
                        device.setLastlinesjog(0);
                        if (mqService != null) {
                            String deviceMac = device.getDeviceOnlyMac();
                            String topicName = "qjjc/gateway/" + deviceMac + "/server_to_client";
//                            String topicName = "qjjc/gateway/" + deviceMac + "/client_to_server";
                            success = mqService.sendBasic(topicName, device,0x01);
                        }
                    }
                }
                if (choices > 0) {
                    countTimer.start();
                    onClick = 1;
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
                exitLoginDialog.dismiss();
                if (mqService != null) {
                    mqService.clearAllData();
                    mqService.cancelAllsubscibe();
//                    mqService.clearCountTimer();
                }
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
                if (dialogLoad != null && dialogLoad.isShowing()) {
                    dialogLoad.dismiss();
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

    class CountTimer2 extends CountDownTimer {

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public CountTimer2(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {

            Log.e("CountDownTimer", "-->" + millisUntilFinished);
        }

        @Override
        public void onFinish() {
            if (dialogLoad != null && dialogLoad.isShowing()) {
                dialogLoad.dismiss();
            }
            if (mqService != null) {
                Map<String, Device> deviceMap = mqService.getOfflineDevices();
                for (int i = 0; i < list.size(); i++) {
                    Device device = list.get(i);
                    String deviceMac = device.getDeviceOnlyMac();
                    if (deviceMap.containsKey(deviceMac)) {
                        Device device2 = deviceMap.get(deviceMac);
                        list.set(i, device2);
                    } else {
                        String topicName = "qjjc/gateway/" + deviceMac + "/server_to_client";
                        Log.e("CountDownTimer", topicName);
                        mqService.getData(topicName, 0x11);
                    }
                }
            }
        }
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
            setLoadDialog();
            Log.e("CountDownTimer", "-->" + millisUntilFinished);
        }

        @Override
        public void onFinish() {
            if ( dialogLoad!= null && dialogLoad.isShowing()) {
                dialogLoad.dismiss();
            }
        }
    }
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

//    private PopupWindow popupWindow2;

//    public void popupmenuWindow3() {
//        try {
//            if (popupWindow2 != null && popupWindow2.isShowing()) {
//                return;
//            }
//            View view = View.inflate(this, R.layout.progress, null);
//            TextView tv_load = view.findViewById(R.id.tv_load);
//            tv_load.setTextColor(getResources().getColor(R.color.white));
//
//            popupWindow2 = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
//
//            //添加弹出、弹入的动画
//            popupWindow2.setAnimationStyle(R.style.Popupwindow);
//            popupWindow2.setFocusable(false);
//            popupWindow2.setOutsideTouchable(false);
//            backgroundAlpha(0.6f);
//            popupWindow2.setOnDismissListener(new PopupWindow.OnDismissListener() {
//                @Override
//                public void onDismiss() {
//                    backgroundAlpha(1.0f);
//                }
//            });
////        ColorDrawable dw = new ColorDrawable(0x30000000);
////        popupWindow.setBackgroundDrawable(dw);
////        popupWindow2.showAsDropDown(et_wifi, 0, -20);
//            popupWindow2.showAtLocation(grid_list, Gravity.CENTER, 0, 0);
//        } catch (Resources.NotFoundException e) {
//            e.printStackTrace();
//        }
//        //添加按键事件监听
//    }

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

            if (online) {
                viewHolder.img_lamp.setImageResource(R.mipmap.lamp_open);
                viewHolder.tv_imei.setText(devicePassword);
            } else {
                viewHolder.img_lamp.setImageResource(R.mipmap.lamp_close);
                viewHolder.tv_imei.setText("离线");
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
                        adapter.notifyDataSetChanged();
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
                    View view = finalConvertView.findViewById(R.id.rl_item);
                    Log.i("heigth", "-->" + view.getBottom() + "," + (grid_list.getMeasuredHeight() - grid_list.getPaddingBottom()));
                    if (view != null && view.getBottom() >= grid_list.getMeasuredHeight() - grid_list.getPaddingBottom()) {
                        Log.i("LongClickListener", "-->到底部");
                        if ((position + 1) % 3 != 0) {
                            View item3 = finalConvertView.findViewById(R.id.view3);
                            popupNote(2, item3, 0, position);
                        } else if ((position + 1) % 3 == 0) {
                            View item3 = finalConvertView.findViewById(R.id.view3);
                            popupNote(3, item3, 0, position);
                        }
                    } else {
                        Log.i("LongClickListener", "-->没到底部");
                        if ((position + 1) % 3 == 0) {
                            int xoff = DisplayUtil.px2dip(DeviceListActivity.this, -350);
                            View item2 = finalConvertView.findViewById(R.id.view2);
                            popupNote(1, item2, xoff, position);
                        } else if ((position + 1) % 3 != 0) {
                            popupNote(0, item, -10, position);
                        }
                    }


//                    if (list.size() > 9) {
//
//                    } else {
//                        if (position < 6 && (position + 1) % 3 == 0) {
//                            int xoff = DisplayUtil.px2dip(DeviceListActivity.this, -350);
//                            View item2 = finalConvertView.findViewById(R.id.view2);
//                            popupNote(1, item2, xoff, position);
//                        } else if (position < 6 && (position + 1) % 3 != 0) {
//                            popupNote(0, item, -10, position);
//                        } else if (position >= 6 && (position + 1) % 3 != 0) {
//                            View item3 = finalConvertView.findViewById(R.id.view3);
//                            popupNote(2, item3, 0, position);
//                        } else if (position >= 6 && (position + 1) % 3 == 0) {
//                            View item3 = finalConvertView.findViewById(R.id.view3);
//                            popupNote(3, item3, 0, position);
//                        }
//
//                    }
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
                        mqService.getData(topicName, 0x11);
                    }
//                    long deviceId = device.getDeviceId();
//                    String name = device.getDeviceName();
//                    Intent intent = new Intent(DeviceListActivity.this, DeviceItemActivity.class);
//                    intent.putExtra("deviceId", deviceI;
//                    intent.putExtra("name", name);d)
//                    startActivityForResult(intent, 100);
                    if (device.getOnline()) {
                        long deviceId = device.getDeviceId();
                        String name = device.getDeviceName();
                        Intent intent = new Intent(DeviceListActivity.this, DeviceItemActivity.class);
                        intent.putExtra("userId", userId);
                        intent.putExtra("deviceId", deviceId);
                        intent.putExtra("name", name);
                        startActivityForResult(intent, 100);
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
            viewHolder.tv_name.setText(name);
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
                MyApplication.floating=0;
                changeDialog();
            } else {
                MyApplication.floating=1;
            }
        }
    }


    private void changeDialog() {
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        dialog = new ChangeDialog(this);
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
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
                        deviceDao.update(device);
                        list.set(updateDevicePosition, device);
                        adapter.notifyDataSetChanged();
                    } else if (updateDeviceType == 2) {
                        Device device = list.get(updateDevicePosition);
                        device.setDevicePassword(updateDeviceName);
                        deviceDao.update(device);
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
                Log.i("DeleteDeviceAsync","-->"+result);
                if (!TextUtils.isEmpty(result)) {
                    JSONObject jsonObject = new JSONObject(result);
                    code = jsonObject.getInt("returnCode");
                    if (code == 100) {
                        Device device = list.get(updateDevicePosition);
                        deviceDao.delete(device);
                        if (mqService != null) {
                            String macAddress = device.getDeviceOnlyMac();
                            mqService.revoveOfflineDevice(macAddress);
                            mqService.deleteDevice(macAddress);
                            mqService.revoveOfflineDevice(macAddress);
//                            mqService.revoveCountTimer(macAddress);
                        }
//                        list.remove(updateDevicePosition);
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
