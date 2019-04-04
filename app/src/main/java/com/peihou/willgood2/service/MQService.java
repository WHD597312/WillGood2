package com.peihou.willgood2.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.google.gson.Gson;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.peihou.willgood2.custom.AlermDialog;
import com.peihou.willgood2.custom.AlermDialog4;
import com.peihou.willgood2.database.dao.impl.DeviceAlermDaoImpl;
import com.peihou.willgood2.database.dao.impl.DeviceAnalogDaoImpl;
import com.peihou.willgood2.database.dao.impl.DeviceDaoImpl;
import com.peihou.willgood2.database.dao.impl.DeviceLineDaoImpl;
import com.peihou.willgood2.database.dao.impl.DeviceLinkDaoImpl;
import com.peihou.willgood2.database.dao.impl.DeviceLinkedTypeDaoImpl;
import com.peihou.willgood2.database.dao.impl.DeviceMoniLinkDaoDaoImpl;
import com.peihou.willgood2.database.dao.impl.TimerTaskDaoImpl;
import com.peihou.willgood2.device.DeviceInterLockActivity;
import com.peihou.willgood2.device.DeviceItemActivity;
import com.peihou.willgood2.device.DeviceListActivity;
import com.peihou.willgood2.device.SearchDeviceActivity;
import com.peihou.willgood2.device.menu.AlermActivity;
import com.peihou.willgood2.device.menu.InterLockActivity;
import com.peihou.willgood2.device.menu.JogSetActivity;
import com.peihou.willgood2.device.menu.LinkItemActivity;
import com.peihou.willgood2.device.menu.LinkedControlActivity;
import com.peihou.willgood2.device.menu.MoniCheckActivity;
import com.peihou.willgood2.device.menu.MoniLinkItemActivity;
import com.peihou.willgood2.device.menu.RS485Activity;
import com.peihou.willgood2.device.menu.SwichCheckActivity;
import com.peihou.willgood2.device.menu.TimerTaskActivity;
import com.peihou.willgood2.location.LocationActivity;
import com.peihou.willgood2.pojo.Alerm;
import com.peihou.willgood2.pojo.AlermName;
import com.peihou.willgood2.pojo.AnalogName;
import com.peihou.willgood2.pojo.Device;
import com.peihou.willgood2.pojo.DeviceTrajectory;
import com.peihou.willgood2.pojo.Line2;
import com.peihou.willgood2.pojo.Link;
import com.peihou.willgood2.pojo.Linked;
import com.peihou.willgood2.pojo.LinkedType;
import com.peihou.willgood2.pojo.MoniLink;
import com.peihou.willgood2.pojo.SwitchCheck;
import com.peihou.willgood2.pojo.SwtichState;
import com.peihou.willgood2.pojo.Table;
import com.peihou.willgood2.pojo.TimerTask;
import com.peihou.willgood2.utils.NoFastClickUtils;
import com.peihou.willgood2.utils.TenTwoUtil;
import com.peihou.willgood2.utils.ToastUtil;
import com.peihou.willgood2.utils.UUID;
import com.peihou.willgood2.utils.Utils;
import com.peihou.willgood2.utils.WeakRefHandler;
import com.peihou.willgood2.utils.http.BaseWeakAsyncTask;
import com.peihou.willgood2.utils.http.HttpUtils;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MQService extends Service {

    private String TAG = "MQService";
    private String host = "tcp://47.98.131.11:1883";//mqtt连接服务端ip
    private String userName = "admin";//mqtt连接用户名
    private String passWord = "Xr7891122";//mqtt连接密码


    private MqttClient client;//mqtt客户端

    public String myTopic = "rango/dc4f220aa96e/transfer";
    private LinkedList<String> offlineList = new LinkedList<String>();//离线主题

    private MqttConnectOptions options;

    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private LocalBinder binder = new LocalBinder();
    String clientId;

    private DeviceLineDaoImpl deviceLineDao;//设备线路表操作者
    private DeviceDaoImpl deviceDao;//设备别欧操作者
    private TimerTaskDaoImpl timerTaskDao;

    private DeviceAnalogDaoImpl deviceAnalogDao;
    private DeviceAlermDaoImpl deviceAlermDao;
    private DeviceLinkedTypeDaoImpl deviceLinkedTypeDao;//设备联动类型操作对象
    private DeviceLinkDaoImpl deviceLinkDao;//设备温度，湿度，开关量，电流，电压联动操作对象
    private DeviceMoniLinkDaoDaoImpl deviceMoniLinkDaoDao;//设备模拟量联动操纵对象
    private List<Line2> lines = new ArrayList<>();//线路集合
    StringBuffer sb = new StringBuffer();
    SpeechReceiver reciiver;//语音播报广播

    SharedPreferences preferences;
    int userId;

    @Nullable
    @Override

    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化即创建语音配置对象，只有初始化后才可以使用MSC的各项服务
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=5c73ac21");

        mTts = SpeechSynthesizer.createSynthesizer(this,
                mTtsInitListener);

        Log.i(TAG, "onCreate");
        clientId = UUID.getUUID(this);
        Log.i("clientId", "-->" + clientId);
        IntentFilter intentFilter = new IntentFilter("SpeechReceiverAlerm");
        reciiver = new SpeechReceiver();
        registerReceiver(reciiver, intentFilter);
        preferences = getSharedPreferences("my", Context.MODE_PRIVATE);
        deviceDao = new DeviceDaoImpl(getApplicationContext());
        List<Device> devices = deviceDao.findAllDevice();
        for (Device device : devices) {
            String deviceMac = device.getDeviceOnlyMac();
            addCountTimer(deviceMac);
        }
        deviceAlermDao = new DeviceAlermDaoImpl(getApplicationContext());
        deviceLineDao = new DeviceLineDaoImpl(getApplicationContext());
        timerTaskDao = new TimerTaskDaoImpl(getApplicationContext());
        deviceAnalogDao = new DeviceAnalogDaoImpl(getApplicationContext());
        deviceLinkedTypeDao = new DeviceLinkedTypeDaoImpl(getApplicationContext());
        deviceLinkDao = new DeviceLinkDaoImpl(getApplicationContext());
        deviceMoniLinkDaoDao = new DeviceMoniLinkDaoDaoImpl(getApplicationContext());
        preferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        userId = preferences.getInt("userId", 0);
        init();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent!=null && intent.hasExtra("restart")){
            int restart=intent.getIntExtra("restart",0);
            Log.i("restart","-->"+restart);
            if (restart==1){
                connect(1);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public class LocalBinder extends Binder {
        public MQService getService() {
            Log.i(TAG, "Binder");
//            connect(0);
            return MQService.this;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
        try {
            unregisterReceiver(reciiver);
            Log.i(TAG, "onDestroy");
            scheduler.shutdown();
            client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    public void connect(int state) {
        try {
            if (client != null && client.isConnected() == false) {
                client.connect(options);
            }
            if (state==1) {
                new ConAsync(MQService.this).execute();
                countTime2.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearAllData() {
        deviceDao.deleteAll();
        deviceLineDao.deleteAll();
        timerTaskDao.deleteAll();
        deviceLinkedTypeDao.deleteAll();
        deviceLinkDao.deleteAll();
        deviceMoniLinkDaoDao.deleteAll();
        deviceAnalogDao.deleteAll();
        deviceAlermDao.deleteAll();
        alerms.clear();
        list.clear();
        moniMap.clear();
        removeOfflineDevices();
    }

    public void deleteDevice(String deviceMac) {
        try {
            timerTaskDao.deleteTimers(deviceMac);
            deviceLineDao.deleteDeviceLines(deviceMac);
            deviceLinkDao.deleteLinekeds(deviceMac);
            deviceAnalogDao.deleteDeviceTables(deviceMac);
            deviceMoniLinkDaoDao.deletes(deviceMac);
            deviceAlermDao.deleteDeviceAlerms(deviceMac);
            deviceLinkedTypeDao.deleteLinkedTypes(deviceMac);
            String server = "qjjc/gateway/" + deviceMac + "/client_to_server";
            String lwt = "qjjc/gateway/" + deviceMac + "/lwt";
            unsubscribe(server);
            unsubscribe(lwt);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    class ConAsync extends BaseWeakAsyncTask<Void, Void, Void, MQService> {

        public ConAsync(MQService mqService) {
            super(mqService);
        }

        @Override
        protected Void doInBackground(MQService mqService, Void... voids) {

            try {
                if (client.isConnected() == false) {
                    client.connect(options);
                }
                List<String> topicNames = getTopicNames();
                Log.i("ConAsync","-->"+topicNames.size());
                if (client.isConnected() && !topicNames.isEmpty()) {
                    for (String topicName : topicNames) {
                        if (!TextUtils.isEmpty(topicName)) {
                            client.subscribe(topicName, 1);
                            Log.i("client", "-->" + topicName);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(MQService mqService, Void aVoid) {

        }
    }

    public void connectMqtt(String deviceMac){
        try {
            if (client!=null && !client.isConnected()){
                client.connect(options);
            }
            String server = "qjjc/gateway/" + deviceMac + "/client_to_server";
            String lwt = "qjjc/gateway/" + deviceMac + "/lwt";

            if (client.isConnected()){
                subscribe(server,1);
                subscribe(lwt,1);
                String topicName = "qjjc/gateway/" + deviceMac + "/server_to_client";
                getData(topicName,0x11);
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    public void subscribeAll(List<Device> devices) {
        try {
            for (Device device : devices) {
                String deviceMac = device.getDeviceOnlyMac();
                String server = "qjjc/gateway/" + deviceMac + "/client_to_server";
                String lwt = "qjjc/gateway/" + deviceMac + "/lwt";
                subscribe(server, 1);
                subscribe(lwt, 1);
                Log.i("subscribeAll", "-->" + server);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String result;

    Map<String, Object> operateLog = new HashMap<>();
    private Map<String, Object> params = new HashMap<>();


    private Map<String, Device> offlineDevices = new LinkedHashMap<>();

    public Map<String, Device> getOfflineDevices() {
        return offlineDevices;
    }

    public void removeOfflineDevices() {
        offlineDevices.clear();
    }

    public void revoveOfflineDevice(String macAddress) {
        if (offlineDevices.containsKey(macAddress))
            offlineDevices.remove(macAddress);
    }

    /**
     * 初始化MQTT
     */
    private void init() {
        try {
            //host为主机名，test为clientid即连接MQTT的客户端ID，一般以客户端唯一标识符表示，MemoryPersistence设置clientid的保存形式，默认为以内存保存

            client = new MqttClient(host, clientId,
                    new MemoryPersistence());
            //MQTT的连接设置
            options = new MqttConnectOptions();
            //设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，这里设置为true表示每次连接到服务器都以新的身份连接
            options.setCleanSession(true);
            //设置连接的用户名
            options.setUserName(userName);
            //设置连接的密码
            options.setPassword(passWord.toCharArray());
            // 设置超时时间 单位为秒
            options.setConnectionTimeout(15);
            // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
            options.setKeepAliveInterval(60);
            options.setAutomaticReconnect(true);//打开重连机制


//            options.setWill("sssssssss","rangossssss".getBytes("UTF-8"),1,false);

            //设置回调
            client.setCallback(new MqttCallback() {

                @Override
                public void connectionLost(Throwable cause) {
                    //连接丢失后，一般在这里面进行重连
                    System.out.println("connectionLost----------");
                    startReconnect();
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    //publish后会执行到这里
                    System.out.println("deliveryComplete---------"
                            + token.isComplete());
                }

                @Override
                public void messageArrived(String topicName, MqttMessage message) {
                    try {
//                        new LoadAsyncTask().execute(topicName, message.toString());
                        byte[] bytes = message.getPayload();
//                        String s=bytes.toString();
                        Map<String, Object> params = new HashMap<>();
                        params.put("topicName", topicName);
                        params.put("bytes", bytes);
                        Log.i("topicNamehhhhhhhhh", "-->" + topicName);
                        if (topicName.contains("client_to_server")) {
                            new LoadAsyncTask(MQService.this).execute(params);
                        } else if (topicName.contains("lwt")) {
                            String macAddress = topicName.substring(13, topicName.lastIndexOf("/"));
                            Device device = deviceDao.findDeviceByMac(macAddress);
                            device.setOnline(false);
                            deviceDao.update(device);
                            offlineDevices.remove(macAddress);
                            List<Line2> line2List = deviceLineDao.findDeviceLines(macAddress);
                            for (Line2 line2 : line2List) {
                                line2.setOpen(false);
                                line2.setJog(false);
                                line2.setOpen(false);
                                deviceLineDao.update(line2);
                            }
                            String topicName2 = "qjjc/gateway/" + macAddress + "/server_to_client";
                            getData(topicName2, 0x11);
                            Intent intent = new Intent("offline");
                            intent.putExtra("macAddress", macAddress);
                            sendBroadcast(intent);
                            Message msg = handler.obtainMessage();
                            msg.what = 1001;
                            msg.obj = device.getDeviceName();
                            handler.sendMessage(msg);
                            return;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    List<Table> list = new ArrayList<>();
    List<Alerm> alerms = new ArrayList<>();
    int[] moniLinkSwitch = new int[8];

    Map<String, String> map = new HashMap<>();

    public List<TimerTask> getTimerTask(String deviceMac) {
        return timerTaskDao.findDeviceTimeTask(deviceMac);
    }

    public List<MoniLink> getMoniLink(String deviceMac, int type, int num) {
        return deviceMoniLinkDaoDao.findMoniLinks(deviceMac, type, num);
    }

    public List<Linked> getLinkeds(String deviceMac, int type) {
        return deviceLinkDao.findLinkeds(deviceMac, type);
    }

    public void updateDevice(Device device) {
        deviceDao.update(device);
    }
    public List<Device> getDevices(){
        return deviceDao.findAllDevice();
    }

    /**
     * 处理mqtt接收到的消息
     */
    class LoadAsyncTask extends BaseWeakAsyncTask<Map<String, Object>, Void, Object, MQService> {
        public LoadAsyncTask(MQService mqService) {
            super(mqService);
        }


        @Override
        protected void onPostExecute(MQService mqService, Object o) {

        }

        @Override
        protected Object doInBackground(MQService mqService, Map<String, Object>... maps) {

            Map params = maps[0];
            String topicName = (String) params.get("topicName");
            byte[] bytes = (byte[]) params.get("bytes");
            int[] data = new int[bytes.length];

            int sum = 0;
            int sum2 = 0;
            for (int i = 0; i < data.length; i++) {
                int k = bytes[i];
                data[i] = k < 0 ? k + 256 : k;
                if (i < data.length - 2) {
                    sum += data[i];
                }
                if (i >= 4 && i < data.length - 2) {
                    sum2 += data[i];
                }
            }

            if (sum2 == 0) {
                return null;
            }
            int check = sum % 256;
            if (check != data[data.length - 2]) {
                return null;
            }
            String lines2 = "";

            String rs485 = "";
            String macAddress = topicName.substring(13, topicName.lastIndexOf("/"));
            long timerTaskId = -1;//定时任务的唯一标识符
            int timerTaskFlag = -1;//定时任务的标记，0为添加，1为更新，2为删除
            int linkedFlag = -1;//联动标记，0为添加，1为更新，2删除
            long linkedId = -1;//联动唯一标识符
            int linkType = -1;//设备联动
            int linkTypeNum = -1;
            int moniType = -1;
            int switchCheck = -1;
            int switchCheck2 = -1;
            int switchState1 = 0;
            int switchState2 = 0;
            int switchState3 = 0;
            int switchState4 = 0;
            int switchState5 = 0;
            int switchState6 = 0;
            int switchState7 = 0;
            int switchState8 = 0;
            double lineJog = 0;
            int operateState = 0;
            Device device = null;
            double latitude = 0;
            double longitude = 0;
            try {
                if (topicName.contains("client_to_server")) {
                    int funCode = data[1];
                    TimerTask timerTask = null;
                    List<LinkedType> linkedTypes = null;
                    Linked linked = null;
                    int len = data.length;
                    Log.i("len", "-->" + len);
                    if (funCode == 0x11 && data.length == 59) {
                        device = deviceDao.findDeviceByMac(macAddress);

                        if (device != null) {
                            int mcuVersion = data[2];
                            int state = data[4];//设备状态
                            int prelines = data[5];//前8路在线
                            int lastlines = data[6];//后8路在线
                            int prelineswitch = data[7];//前8路开关
                            int lastlineswitch = data[8];//后8路开关
                            int prelinesjog = data[9];//前8路点动
                            int lastlinesjog = data[10];//后8路点动
                            int plMemory = data[11];


                            int lineHigh = data[12];

                            int lineLow = data[13];
                            double line = ((lineHigh * 256) + lineLow) / 10;
                            int lineHigh2 = data[14];
                            int lineLow2 = data[15];
                            double line2 = ((lineHigh2 * 256) + lineLow2) / 10;
                            int lineHigh3 = data[16];
                            int lineLow3 = data[17];
                            double line3 = ((lineHigh3 * 256) + lineLow3) / 10;
                            int lineHigh4 = data[18];
                            int lineLow4 = data[19];
                            double line4 = ((lineHigh4 * 256) + lineLow4) / 10;
                            int lineHigh5 = data[20];
                            int lineLow5 = data[21];
                            double line5 = ((lineHigh5 * 256) + lineLow5) / 10;
                            int lineHigh6 = data[22];
                            int lineLow6 = data[23];
                            double line6 = ((lineHigh6 * 256) + lineLow6) / 10;
                            int lineHigh7 = data[24];
                            int lineLow7 = data[25];
                            double line7 = ((lineHigh7 * 256) + lineLow7) / 10;
                            int lineHigh8 = data[26];
                            int lineLow8 = data[27];
                            double line8 = ((lineHigh8 * 256) + lineLow8) / 10;
                            int lineHigh9 = data[28];
                            int lineLow9 = data[29];
                            double line9 = ((lineHigh9 * 256) + lineLow9) / 10;
                            int lineHigh10 = data[30];
                            int lineLow10 = data[31];
                            double line10 = ((lineHigh10 * 256) + lineLow10) / 10;
                            int lineHigh11 = data[32];
                            int lineLow11 = data[33];
                            double line11 = ((lineHigh11 * 256) + lineLow11) / 10;
                            int lineHigh12 = data[34];
                            int lineLow12 = data[35];
                            double line12 = ((lineHigh12 * 256) + lineLow12) / 10;
                            int lineHigh13 = data[36];
                            int lineLow13 = data[37];
                            double line13 = ((lineHigh13 * 256) + lineLow13) / 10;
                            int lineHigh14 = data[38];
                            int lineLow14 = data[39];
                            double line14 = ((lineHigh14 * 256) + lineLow14) / 10;
                            int lineHigh15 = data[40];
                            int lineLow15 = data[41];
                            double line15 = ((lineHigh15 * 256) + lineLow15) / 10;
                            int lineHigh16 = data[42];
                            int lineLow16 = data[43];
                            double line16 = ((lineHigh16 * 256) + lineLow16) / 10;
                            int tempInt = data[44] * 256;
                            int tempFloat = data[45];
                            double temp = 1.0 * (tempInt + tempFloat) / 10 - 100;
                            int humInt = data[46] * 256;
                            int humFloat = data[47];
                            double hum = 1.0 * (humInt + humFloat) / 10;
                            int currentInt = data[48] * 256;
                            int currentFloat = data[49];
                            double current = 1.0 * (currentInt + currentFloat) / 10;
                            int valtageInt = data[50] * 256;
                            int voltageFloat = data[51];
                            double voltage = 1.0 * (valtageInt + voltageFloat) / 10;


                            int[] x = TenTwoUtil.changeToTwo(prelines);
                            int[] x2 = TenTwoUtil.changeToTwo(lastlines);
                            int[] x3 = TenTwoUtil.changeToTwo(prelineswitch);
                            int[] x4 = TenTwoUtil.changeToTwo(lastlineswitch);
                            int[] x5 = TenTwoUtil.changeToTwo(prelinesjog);
                            int[] x6 = TenTwoUtil.changeToTwo(lastlinesjog);
                            long deviceId = device.getDeviceId();
                            List<Line2> line2List = deviceLineDao.findDeviceLines(macAddress);
                            if (line2List.isEmpty()) {
                                List<Line2> list2 = deviceLineDao.findDeviceLines(deviceId);
                                if ((list2 != null && list2.size() != 16)) {
                                    deviceLineDao.deleteDeviceLines(list2);
                                    for (int i = 1; i <= 16; i++) {
                                        Line2 line21 = new Line2(false, "线路", 0, false, i, deviceId, macAddress);
                                        line2List.add(line21);
                                    }
                                    deviceLineDao.insertDeviceLines(line2List);
                                }
                            }
                            sb.setLength(0);
                            if (line2List != null && line2List.size() == 16) {
                                for (int i = 0; i < 16; i++) {
                                    Line2 line21 = line2List.get(i);
                                    String name = line21.getName();
                                    int deviceLineNum = line21.getDeviceLineNum();
                                    if (i < 8) {
                                        if (x[i] == 1) {
                                            line21.setOnline(true);
                                            sb.append(deviceLineNum + ",");
                                        } else {
                                            line21.setOnline(false);
                                        }
                                    } else if (i >= 8) {
                                        if (x2[i - 8] == 1) {
                                            line21.setOnline(true);
                                            sb.append(deviceLineNum + ",");
                                        } else {
                                            line21.setOnline(false);
                                        }
                                    }
                                    if (i < 8) {
                                        if (x3[i] == 1) {
                                            line21.setOpen(true);
                                        } else {
                                            line21.setOpen(false);
                                        }
                                    } else if (i >= 8) {
                                        if (x4[i - 8] == 1) {
                                            line21.setOpen(true);
                                        } else {
                                            if (state == 0) {
                                                line21.setOpen(false);
                                            }
                                        }
                                    }
                                    if (i < 8) {
                                        if (x5[i] == 0) {
                                            line21.setJog(true);
                                        } else {
                                            line21.setJog(false);
                                        }
                                    } else if (i >= 8) {
                                        if (x6[i - 8] == 0) {
                                            line21.setJog(true);
                                        } else {
                                            line21.setJog(false);
                                        }
                                    }
                                    deviceLineDao.update(line21);
                                }
                            }
                            device.setMcuVersion(mcuVersion);
                            device.setDeviceState(state);
                            device.setPrelines(prelines);
                            device.setLastlines(lastlines);
                            device.setPrelineswitch(prelineswitch);
                            device.setLastlineswitch(lastlineswitch);
                            device.setPrelinesjog(prelinesjog);
                            device.setLastlinesjog(lastlinesjog);
//                            device.setPlMemory(plMemory);
                            device.setLine(line);
                            device.setLine2(line2);
                            device.setLine3(line3);
                            device.setLine4(line4);
                            device.setLine5(line5);
                            device.setLine6(line6);
                            device.setLine7(line7);
                            device.setLine8(line8);
                            device.setLine9(line9);
                            device.setLine10(line10);
                            device.setLine11(line11);
                            device.setLine12(line12);
                            device.setLine13(line13);
                            device.setLine14(line14);
                            device.setLine15(line15);
                            device.setLine16(line16);
                            device.setTemp(temp);
                            device.setHum(hum);
                            device.setCurrent(current);
                            device.setVotage(voltage);
//                            device.setPlMemory(plMemory);
                            device.setOnline(true);
                            deviceDao.update(device);
                            offlineDevices.put(macAddress, device);
                            Message msg = handler.obtainMessage();
                            CountTimer countTimer2=null;
                            for (CountTimer countTimer:countTimers){
                                if (countTimer.getMacArress().equals(macAddress) && countTimer.getMillisUntilFinished()/1000==0){
                                    countTimer2=countTimer;
                                    break;
                                }
                            }
                            if (countTimer2!=null){
                                msg.obj = countTimer2;
                                msg.what = 101;
                                handler.sendMessage(msg);
                            }

                            lines2 = sb.toString() + "";
                            if (!TextUtils.isEmpty(lines2)) {
                                char ch = lines2.charAt(lines2.length() - 1);
                                if (',' == ch) {
                                    lines2 = lines2.substring(0, lines2.length() - 1);
                                }
                            } else {
                                lines2 = "";
                            }
                        }
                    } else if (funCode == 0x22) {
                        int mcuVersion = data[2];
                        int choice = data[4];
                        int yearHigh = data[5];
                        int lowLow = data[6];
                        int year = yearHigh * 256 + lowLow;
                        int month = data[7];
                        int day = data[8];
                        int week = data[9];
                        int hour = data[10];
                        int min = data[11];
                        int prelines = data[12];
                        int lastlines = data[13];
                        int controlState = data[14];
                        int state = data[15];

                        if (choice == 0x11) {
                            timerTask = timerTaskDao.findUniqueTimerTask(macAddress, year, month, day, hour, min, prelines, lastlines);
//                            Calendar calendar=Calendar.getInstance();
//                            int year2=calendar.get(Calendar.YEAR);
//                            int month2=calendar.get(Calendar.MONTH)+1;
//                            int day2=calendar.get(Calendar.DAY_OF_MONTH);
//                            int hour2=calendar.get(Calendar.HOUR_OF_DAY);
//                            int min2=calendar.get(Calendar.MINUTE);
//                            String timer=year2+"-"+month2+"-"+day2+" "+hour2+":"+min2;
//                            SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm");
//                            Date date=format.parse(timer);
//                            long time2=date.getTime();
//                            List<TimerTask> timerTasks=timerTaskDao.findPreTimers(macAddress,time2);
//                            if (timerTasks!=null && !timerTasks.isEmpty()){
//                                timerTaskDao.deleteTimers(timerTasks);
//                            }
                        } else if (choice == 0x22) {
                            timerTask = timerTaskDao.findUniqueTimerTask(macAddress, hour, min, week, prelines, lastlines);
                        }

                        if (state == 4) {
                            Message msg = handler.obtainMessage();
                            msg.what = 10001;
                            msg.obj = "定时已达上限";
                            handler.sendMessage(msg);
                            return null;
                        }
                        if (state == 2) {
                            if (timerTask != null) {
                                timerTaskId = timerTask.getId();
                                timerTaskFlag = 2;
                                timerTaskDao.delete(timerTask);
                                operateState = 1;
                            }
                        } else if (state==5){
                            timerTaskDao.deleteTimers(macAddress);
                            operateState = 1;
                        }else {
                            if (timerTask == null) {
                                operateState = 0;
                                if (choice == 0x11) {
                                    timerTask = new TimerTask(macAddress, 0x11, year, month, day, hour, min, controlState, prelines, lastlines, 1);
                                } else if (choice == 0x22) {
                                    timerTask = new TimerTask(macAddress, 0x22, week, hour, min, controlState, prelines, lastlines, 1);
                                }
                                timerTask.setVisitity(1);
                                lines.clear();
                                lines = deviceLineDao.findDeviceLines(macAddress);
                                int[] pre = TenTwoUtil.changeToTwo(prelines);
                                int[] last = TenTwoUtil.changeToTwo(lastlines);
                                sb.setLength(0);
                                for (int i = 0; i < lines.size(); i++) {
                                    Line2 line2 = lines.get(i);
                                    String name = line2.getName();
                                    if (i < 8) {
                                        int checked = pre[i];
                                        if (checked == 1) {
                                            sb.append(name + ",");
                                        }
                                    } else if (i >= 8) {
                                        int checked = last[i - 8];
                                        if (checked == 1) {
                                            sb.append(name + ",");
                                        }
                                    }
                                }
                                String lines = sb.toString() + "";
                                if (!TextUtils.isEmpty(lines)) {
                                    char ch = lines.charAt(lines.length() - 1);
                                    if (',' == ch) {
                                        lines = lines.substring(0, lines.length() - 1);
                                    }
                                } else {
                                    lines = "";
                                }
                                timerTask.setState(state);
                                timerTask.setName(lines);
                                timerTaskFlag = 0;
                                timerTaskDao.insert(timerTask);
                            } else {
                                lines.clear();
                                lines = deviceLineDao.findDeviceLines(macAddress);
                                int[] pre = TenTwoUtil.changeToTwo(prelines);
                                int[] last = TenTwoUtil.changeToTwo(lastlines);
                                sb.setLength(0);
                                for (int i = 0; i < lines.size(); i++) {
                                    Line2 line2 = lines.get(i);
                                    String name = line2.getName();
                                    if (i < 8) {
                                        int checked = pre[i];
                                        if (checked == 1) {
                                            sb.append(name + ",");
                                        }
                                    } else if (i >= 8) {
                                        int checked = last[i - 8];
                                        if (checked == 1) {
                                            sb.append(name + ",");
                                        }
                                    }
                                }
                                String lines = sb.toString() + "";
                                if (!TextUtils.isEmpty(lines)) {
                                    char ch = lines.charAt(lines.length() - 1);
                                    if (',' == ch) {
                                        lines = lines.substring(0, lines.length() - 1);
                                    }
                                } else {
                                    lines = "";
                                }
                                if (choice == 0x11) {
                                    timerTask.setYear(year);
                                    timerTask.setMonth(month);
                                    timerTask.setDay(day);
                                    timerTask.setWeek(0);
                                } else if (choice == 0x22) {
                                    timerTask.setYear(0);
                                    timerTask.setMonth(0);
                                    timerTask.setDay(0);
                                    timerTask.setWeek(week);
                                }
                                timerTask.setName(lines);
                                timerTask.setHour(hour);
                                timerTask.setMin(min);
                                timerTask.setPrelines(prelines);
                                timerTask.setLastlines(lastlines);
                                timerTask.setControlState(controlState);
                                timerTask.setState(state);
                                timerTaskId = timerTask.getId();
                                timerTaskFlag = 1;
                                timerTask.setVisitity(1);
                                timerTaskDao.update(timerTask);
                            }
                        }
                    } else if (funCode == 0x33 && data.length == 12) {
                        int mcuVersion = data[2];
                        int linkControl = data[4];
                        int[] x = TenTwoUtil.changeToTwo(linkControl);
                        linkedTypes = deviceLinkedTypeDao.findLinkdType(macAddress);
                        if (linkedTypes != null && linkedTypes.size() != 6) {
                            deviceLinkedTypeDao.deleteLinkedTypes(macAddress);
                            linkedTypes.add(new LinkedType(macAddress, 0, "温度联动", mcuVersion, 0));
                            linkedTypes.add(new LinkedType(macAddress, 1, "湿度联动", mcuVersion, 0));
                            linkedTypes.add(new LinkedType(macAddress, 2, "开关量联动", mcuVersion, 0));
                            linkedTypes.add(new LinkedType(macAddress, 3, "电流联动", mcuVersion, 0));
                            linkedTypes.add(new LinkedType(macAddress, 4, "电压联动", mcuVersion, 0));
                            linkedTypes.add(new LinkedType(macAddress, 5, "模拟量联动", mcuVersion, 0));
                            deviceLinkedTypeDao.insertLinkedTypes(linkedTypes);
                        }
                        for (int i = 0; i < 6; i++) {
                            int state = x[i];
                            LinkedType linkedType = linkedTypes.get(i);
                            linkedType.setState(state);
                            linkedType.setMcuVersion(mcuVersion);
                            linkedTypes.set(i, linkedType);
                        }
                        deviceLinkedTypeDao.updateLinkedTypes(linkedTypes);
                    } else if (funCode == 0x34 || funCode == 0x35 || funCode == 0x36 || funCode == 0x37 || funCode == 0x38) {
                        int type = 4 - (0x38 - funCode);
                        linkType = type;

                        int condition = data[4];//触发条件
                        int conditionState = 0;
                        int preLines = data[5];//前8路
                        int lastLines = data[6];//后8路
                        int triType2 = data[7];//触发类型
                        int[] x = TenTwoUtil.changeToTwo(triType2);
                        int triState = 0;

                        int triType = 0;
                        int x0 = x[0];
                        int x1 = x[1];
                        int x2 = x[2];
                        int x3 = x[3];
                        int x4 = x[4];
                        triType = x4;
                        String ss = "";
                        int switchLine = 0;

                        if (funCode == 0x34) {
                            ss = "温度";
                            if (type == 0) {
                                Log.i("condition", "-->" + condition);
                                condition = condition - 128;
                            }
                        } else if (funCode == 0x35) {
                            ss = "湿度";
                        } else if (funCode == 0x36) {
                            ss = "开关量";
                            switchLine = data[9];
                        } else if (funCode == 0x37) {
                            ss = "电流";
                        } else if (funCode == 0x38) {
                            ss = "电压";
                        }
                        if (x0 == 1 && x1 == 1) {
                            triState = 1;
                        } else if (x0 == 1 && x1 == 0) {
                            triState = 0;
                        }
                        if (x2 == 1 && x3 == 1) {
                            conditionState = 1;
                        } else if (x2 == 1 && x3 == 0) {
                            conditionState = 0;
                        }

                        int state = data[8];//联动状态 0关闭 1打开 2删除
                        if (type == 2) {
                            linked = deviceLinkDao.findLinked(macAddress, triState, preLines, lastLines, triType, switchLine);
                        }else {
                            linked = deviceLinkDao.findLinked(macAddress, type, condition, triState, preLines, lastLines, triType);
                        }
                        if (state == 4) {
                            Message msg = handler.obtainMessage();
                            msg.what = 10001;
                            msg.obj = ss + "联动已达上限";
                            handler.sendMessage(msg);
                            return null;
                        }

                        if (state == 2) {
                            if (linked != null) {
                                linkedId = linked.getId();
                                linkedFlag = 2;
                                deviceLinkDao.delete(linked);
                                operateState = 1;
                            }
                        }else if (state==5){
                            deviceLinkDao.deleteLinekeds(macAddress);
                            operateState = 1;
                        }else {
                            if (linked == null) {
                                lines.clear();
                                lines = deviceLineDao.findDeviceLines(macAddress);
                                int[] pre = TenTwoUtil.changeToTwo(preLines);
                                int[] last = TenTwoUtil.changeToTwo(lastLines);
                                sb.setLength(0);
                                for (int i = 0; i < lines.size(); i++) {
                                    Line2 line2 = lines.get(i);
                                    String name = line2.getName();
                                    if (i < 8) {
                                        int checked = pre[i];
                                        if (checked == 1) {
                                            sb.append(name + ",");
                                        }
                                    } else if (i >= 8) {
                                        int checked = last[i - 8];
                                        if (checked == 1) {
                                            sb.append(name + ",");
                                        }
                                    }
                                }
                                String lines = sb.toString() + "";
                                if (!TextUtils.isEmpty(lines)) {
                                    char ch = lines.charAt(lines.length() - 1);
                                    if (',' == ch) {
                                        lines = lines.substring(0, lines.length() - 1);
                                    }
                                } else {
                                    lines = "";
                                }
                                List<Linked> linkeds = deviceLinkDao.findLinkeds(macAddress, type);
                                int size = linkeds.size() + 1;
                                String name = "";
                                if (type == 0) {
                                    name = "温度" + size;
                                } else if (type == 1) {
                                    name = "湿度" + size;
                                } else if (type == 2) {
                                    switchLine = data[9];
                                    name = "开关量" + size;
                                } else if (type == 3) {
                                    name = "电流" + size;
                                } else if (type == 4) {
                                    name = "电压" + size;
                                }
                                if (type == 2) {
                                    operateState = 0;
                                    name = "开关量" + switchLine;
                                    linked = new Linked(macAddress, type, name, condition, conditionState, 1, preLines, lastLines, triType);
                                    linked.setSwitchLine(switchLine);
                                    linked.setVisitity(1);
                                } else {
                                    operateState = 0;
                                    linked = new Linked(macAddress, type, name, condition, triState, conditionState, 1, preLines, lastLines, triType);
                                    linked.setVisitity(1);
                                }
                                linked.setLines(lines);
                                deviceLinkDao.insert(linked);
                            } else {
                                lines.clear();
                                lines = deviceLineDao.findDeviceLines(macAddress);
                                int[] pre = TenTwoUtil.changeToTwo(preLines);
                                int[] last = TenTwoUtil.changeToTwo(lastLines);
                                sb.setLength(0);
                                for (int i = 0; i < lines.size(); i++) {
                                    Line2 line2 = lines.get(i);
                                    String name = line2.getName();
                                    if (i < 8) {
                                        int checked = pre[i];
                                        if (checked == 1) {
                                            sb.append(name + ",");
                                        }
                                    } else if (i >= 8) {
                                        int checked = last[i - 8];
                                        if (checked == 1) {
                                            sb.append(name + ",");
                                        }
                                    }
                                }
                                String lines = sb.toString() + "";
                                if (!TextUtils.isEmpty(lines)) {
                                    char ch = lines.charAt(lines.length() - 1);
                                    if (',' == ch) {
                                        lines = lines.substring(0, lines.length() - 1);
                                    }
                                } else {
                                    lines = "";
                                }


                                linked.setLines(lines);
                                linked.setCondition(condition);
                                linked.setConditionState(conditionState);
                                linked.setPreLines(preLines);
                                linked.setLastLines(lastLines);
                                linked.setTriType(triType);
                                linked.setConditionState(conditionState);
                                linked.setState(state);
                                linked.setVisitity(1);
                                if (type == 2) {
                                    String name = "开关量" + switchLine;
                                    linked.setName(name);
                                }
                                deviceLinkDao.update(linked);
                            }

                        }
                    } else if (funCode == 0x3a) {
                        int mcuVersion = data[2];
                        int link = data[4];
                        moniLinkSwitch = TenTwoUtil.changeToTwo(link);
                        moniMap.put(macAddress, moniLinkSwitch);
                    } else if (funCode == 0x39) {
                        int mcuVersion = data[2];
                        int contition = data[4];
                        int preLine = data[5];
                        int lastLine = data[6];
                        int triType2 = data[7];
                        int state = data[8];
                        int type = -1;
                        int num = -1;
                        int triState = 0;
                        int controlState = 0;
                        int controlType = data[9];

                        String name = "";
                        if (controlType == 0x11) {
                            type = 0;
                            num = 0;
                            name = "电流1";
                        } else if (controlType == 0x22) {
                            type = 0;
                            num = 1;
                            name = "电流2";
                        } else if (controlType == 0x33) {
                            type = 0;
                            num = 2;
                            name = "电流3";
                        } else if (controlType == 0x44) {
                            type = 0;
                            num = 3;
                            name = "电流4";
                        } else if (controlType == 0x55) {
                            type = 1;
                            num = 0;
                            name = "电压1";
                        } else if (controlType == 0x66) {
                            type = 1;
                            num = 1;
                            name = "电压2";
                        } else if (controlType == 0x77) {
                            type = 1;
                            num = 2;
                            name = "电压3";
                        } else if (controlType == 0x88) {
                            type = 1;
                            num = 3;
                            name = "电压4";
                        }
                        if (state == 4) {
                            Message msg = handler.obtainMessage();
                            msg.what = 10001;
                            msg.obj = "模拟量联动" + name + "已达上限";
                            handler.sendMessage(msg);
                            return null;
                        }

                        moniType = type;
                        linkTypeNum = num;
                        linkType = 5;
                        int[] x = TenTwoUtil.changeToTwo(triType2);
                        if (x[0] == 1 && x[1] == 1) {
                            triState = 1;
                        } else if (x[0] == 1 && x[1] == 0) {
                            triState = 0;
                        }
                        if (x[2] == 1 && x[3] == 1) {
                            controlState = 1;
                        } else if (x[2] == 1 && x[3] == 0) {
                            controlState = 0;
                        }
                        int triType = x[4];
                        MoniLink moniLink = deviceMoniLinkDaoDao.findMoniLink(macAddress, type, num, contition, triState, preLine, lastLine, triType);
                        if (state == 2) {
                            if (moniLink != null) {
                                deviceMoniLinkDaoDao.delete(moniLink);
                                operateState = 1;
                            }
                        } else if (state==5){
                            deviceMoniLinkDaoDao.deletes(macAddress);
                            operateState = 1;
                        }else {
                            lines.clear();
                            lines = deviceLineDao.findDeviceLines(macAddress);
                            int[] pre = TenTwoUtil.changeToTwo(preLine);
                            int[] last = TenTwoUtil.changeToTwo(lastLine);
                            sb.setLength(0);
                            for (int i = 0; i < lines.size(); i++) {
                                Line2 line2 = lines.get(i);
                                String name2 = line2.getName();
                                if (i < 8) {
                                    int checked = pre[i];
                                    if (checked == 1) {
                                        sb.append(name2 + ",");
                                    }
                                } else if (i >= 8) {
                                    int checked = last[i - 8];
                                    if (checked == 1) {
                                        sb.append(name2 + ",");
                                    }
                                }
                            }
                            String lines = sb.toString() + "";
                            if (!TextUtils.isEmpty(lines)) {
                                char ch = lines.charAt(lines.length() - 1);
                                if (',' == ch) {
                                    lines = lines.substring(0, lines.length() - 1);
                                }
                            } else {
                                lines = "";
                            }
                            if (moniLink == null) {
                                moniLink = new MoniLink(name, type, num, contition, triState, preLine, lastLine, controlState, triType, state, controlType, macAddress, mcuVersion);
                                moniLink.setLines(lines);
                                moniLink.setVisitity(1);
                                deviceMoniLinkDaoDao.insert(moniLink);
                                operateState = 0;
                            } else {
                                moniLink.setLines(lines);
                                moniLink.setState(state);
                                moniLink.setControlState(controlState);
                                moniLink.setTriType(triType);
                                moniLink.setVisitity(1);
                                deviceMoniLinkDaoDao.update(moniLink);
                                operateState = 0;
                            }
                        }
                    } else if (funCode == 0x44 && data.length == 13) {
                        int jogHigh = data[4];
                        int jogLow = data[5];

                        lineJog = (1.0 * jogHigh * 256 + jogLow) / 10;
                        BigDecimal b1 = new BigDecimal(lineJog);
                        lineJog = b1.setScale(2, BigDecimal.ROUND_HALF_DOWN).doubleValue();
                        Device device2 = deviceDao.findDeviceByMac(macAddress);
                        if (device2 != null) {
                            device2.setLineJog(lineJog);
                            deviceDao.update(device2);
                            device = device2;
                        }
                    } else if (funCode == 0x46) {
                        int mcuVersion = data[2];
                        map.clear();

                        List<Line2> line2List = deviceLineDao.findDeviceLines(macAddress);
                        for (int i = 4; i < 20; i += 2) {
                            int interLock = data[i];
                            int interLock2 = data[i + 1];

                            if (interLock == 0 && interLock2 == 0) {
                                continue;
                            }

                            Line2 line = deviceLineDao.findDeviceLine(macAddress, interLock);
                            Line2 line2 = deviceLineDao.findDeviceLine(macAddress, interLock2);

                            if (line != null && line2 != null) {
                                String lock = interLock + "&" + interLock2;
                                line.setLock(1);
                                line.setOnClick(false);
                                line.setInterLock(lock);
                                line.setVisitity(1);
                                line2.setLock(1);
                                line2.setOnClick(false);
                                line2.setInterLock(lock);
                                line2.setVisitity(1);
                                line2List.remove(line);
                                line2List.remove(line2);
                                deviceLineDao.update(line);
                                deviceLineDao.update(line2);
                                map.put(lock, lock);
                            }
                        }
                        for (Line2 line2 : line2List) {
                            line2.setOnClick(false);
                            line2.setInterLock(null);
                            line2.setLock(0);
                            line2.setVisitity(0);
                            deviceLineDao.update(line2);
                        }


                    } else if (funCode == 0x55) {
                        switchCheck = data[4];
                        switchCheck2 = data[5];
                        int[] x = TenTwoUtil.changeToTwo(switchCheck);
                        int[] x2 = TenTwoUtil.changeToTwo(switchCheck2);

                        if (x[0] == 1 && x[1] == 1) {
                            switchState1 = 1;
                        }
                        if (x[0] == 1 && x[1] == 0) {
                            switchState1 = 2;
                        } else if (x[0] == 0 && x[1] == 0) {
                            switchState1 = 0;
                        }
                        if (x[2] == 1 && x[3] == 1) {
                            switchState2 = 1;
                        } else if (x[2] == 1 && x[3] == 0) {
                            switchState2 = 2;
                        } else if (x[2] == 0 && x[3] == 0) {
                            switchState2 = 0;
                        }
                        if (x[4] == 1 && x[5] == 1) {
                            switchState3 = 1;
                        } else if (x[4] == 1 && x[5] == 0) {
                            switchState3 = 2;
                        } else if (x[4] == 0 && x[5] == 0) {
                            switchState3 = 0;
                        }
                        if (x[6] == 1 && x[7] == 1) {
                            switchState4 = 1;
                        } else if (x[6] == 1 && x[7] == 0) {
                            switchState4 = 2;
                        } else if (x[6] == 0 && x[7] == 0) {
                            switchState4 = 0;
                        }

                        if (x2[0] == 1 && x2[1] == 1) {
                            switchState5 = 1;
                        }
                        if (x2[0] == 1 && x2[1] == 0) {
                            switchState5 = 2;
                        } else if (x2[0] == 0 && x2[1] == 0) {
                            switchState5 = 0;
                        }
                        if (x2[2] == 1 && x2[3] == 1) {
                            switchState6 = 1;
                        } else if (x2[2] == 1 && x2[3] == 0) {
                            switchState6 = 2;
                        } else if (x2[2] == 0 && x2[3] == 0) {
                            switchState6 = 0;
                        }
                        if (x2[4] == 1 && x2[5] == 1) {
                            switchState7 = 1;
                        } else if (x2[4] == 1 && x2[5] == 0) {
                            switchState7 = 2;
                        } else if (x2[4] == 0 && x2[5] == 0) {
                            switchState7 = 0;
                        }
                        if (x2[6] == 1 && x2[7] == 1) {
                            switchState8 = 1;
                        } else if (x2[6] == 1 && x2[7] == 0) {
                            switchState8 = 2;
                        } else if (x2[6] == 0 && x2[7] == 0) {
                            switchState8 = 0;
                        }
                        if (switchChecks.size() == 8) {
                            for (int i = 0; i < 8; i++) {
                                SwtichState swtichState = switchChecks.get(i);
                                if (i == 0) {
                                    swtichState.setState(switchState1);
                                } else if (i == 1) {
                                    swtichState.setState(switchState2);
                                } else if (i == 2) {
                                    swtichState.setState(switchState3);
                                } else if (i == 3) {
                                    swtichState.setState(switchState4);
                                } else if (i == 4) {
                                    swtichState.setState(switchState5);
                                } else if (i == 5) {
                                    swtichState.setState(switchState6);
                                } else if (i == 6) {
                                    swtichState.setState(switchState7);
                                } else if (i == 7) {
                                    swtichState.setState(switchState8);
                                }
                                switchChecks.set(i, swtichState);
                            }
                        }
                    } else if (funCode == 0x66) {
                        int mcuVerion = data[2];
                        int type = data[4];
                        int tempHigh = data[5];
                        int tempLow = data[6];
                        double temp = (1.0 * tempHigh * 256 + tempLow) / 10 - 50;
                        int tempState = data[7];
                        int humHigh = data[8];
                        int humLow = data[9];
                        double hum = (1.0 * humHigh * 256 + humLow) / 10;
                        int humState = data[10];
                        int voltageHigh = data[11];
                        int voltageLow = data[12];
                        double voltage = (1.0 * voltageHigh * 256 + voltageLow) / 10;
                        int voltageState = data[13];
                        int currentHigh = data[14];
                        int currentLow = data[15];
                        double current = (1.0 * currentHigh * 256 + currentLow) / 10;
                        int currentState = data[16];
                        int powerHigh = data[17];
                        int powerLow = data[18];
                        double power = (1.0 * powerHigh * 256 + powerLow) / 10;
                        int powerState = data[19];
                        int switchState = data[20];
                        int[] x = TenTwoUtil.changeToTwo(type);

                        Alerm alerm = deviceAlermDao.findDeviceAlerm(macAddress, 0);//来电报警
                        Alerm alerm2 = deviceAlermDao.findDeviceAlerm(macAddress, 1);//断电报警
                        Alerm alerm3 = deviceAlermDao.findDeviceAlerm(macAddress, 2);//温度报警
                        Alerm alerm4 = deviceAlermDao.findDeviceAlerm(macAddress, 3);//湿度报警
                        Alerm alerm5 = deviceAlermDao.findDeviceAlerm(macAddress, 4);//电压报警
                        Alerm alerm6 = deviceAlermDao.findDeviceAlerm(macAddress, 5);//电流报警
                        Alerm alerm7 = deviceAlermDao.findDeviceAlerm(macAddress, 6);//功率报警
                        Alerm alerm8 = deviceAlermDao.findDeviceAlerm(macAddress, 7);//开关量报警


                        alerm.setState(x[0]);
                        alerm2.setState(x[1]);
                        alerm3.setState(x[2]);
                        alerm4.setState(x[3]);
                        alerm5.setState(x[4]);
                        alerm6.setState(x[5]);
                        alerm7.setState(x[6]);
                        alerm8.setState(x[7]);
                        alerm3.setValue(temp);
                        alerm4.setValue(hum);
                        alerm5.setValue(voltage);
                        alerm6.setValue(current);
                        alerm7.setValue(power);
                        alerm3.setState2(tempState);
                        alerm4.setState2(humState);
                        alerm5.setState2(voltageState);
                        alerm6.setState2(currentState);
                        alerm7.setState2(powerState);
                        alerm8.setState2(switchState);

                        int deviceAlarmFlag = alerm.getDeviceAlarmFlag();
                        int deviceAlarmBroadcast = alerm.getDeviceAlarmBroadcast();

                        deviceAlermDao.update(alerm);
                        deviceAlermDao.update(alerm2);
                        deviceAlermDao.update(alerm3);
                        deviceAlermDao.update(alerm4);
                        deviceAlermDao.update(alerm5);
                        deviceAlermDao.update(alerm6);
                        deviceAlermDao.update(alerm7);
                        deviceAlermDao.update(alerm8);

                        alerms.clear();
                        alerms.add(alerm);
                        alerms.add(alerm2);
                        alerms.add(alerm3);
                        alerms.add(alerm4);
                        alerms.add(alerm5);
                        alerms.add(alerm6);
                        alerms.add(alerm7);
                        alerms.add(alerm8);

                        Collections.sort(alerms, new Comparator<Alerm>() {
                            @Override
                            public int compare(Alerm o1, Alerm o2) {
                                if (o1.getType() > o2.getType())
                                    return 1;
                                else if (o1.getType() < o2.getType())
                                    return -1;
                                return 0;
                            }
                        });
                    } else if (funCode == 0x77) {
                        int symbol = data[6];
                        int[] x = TenTwoUtil.changeToTwo(symbol);
                        int x1 = x[0];
                        int x2 = x[1];
                        int x3 = x[2];
                        int x4 = x[3];
                        String s = "";
                        if (x1 == 1 && x2 == 0 && x3 == 1 && x4 == 0) {

                        } else {
                            s = "-";
                        }
                        int intPart = data[7];
                        String floatPart = data[8] + "";
                        String floatPart2 = data[9] + "";
                        String floatPart3 = data[10] + "";

                        int intPart2 = data[11];
                        String floatPart4 = data[12] + "";
                        String floatPart5 = data[13] + "";
                        String floatPart6 = data[14] + "";

                        if (floatPart.length() == 1) {
                            floatPart = "0" + floatPart;
                        }
                        if (floatPart2.length() == 1) {
                            floatPart2 = "0" + floatPart2;
                        }

                        if (floatPart3.length() == 1) {
                            floatPart3 = "0" + floatPart3;
                        }
                        if (floatPart4.length() == 1) {
                            floatPart4 = "0" + floatPart4;
                        }
                        if (floatPart5.length() == 1) {
                            floatPart5 = "0" + floatPart5;
                        }

                        if (floatPart6.length() == 1) {
                            floatPart6 = "0" + floatPart6;
                        }

                        String s1 = s + intPart + "." + floatPart + floatPart2 + floatPart3;
                        String s2 = s + intPart2 + "." + floatPart4 + floatPart5 + floatPart6;

                        latitude = Double.parseDouble(s1);
                        longitude = Double.parseDouble(s2);

                    } else if (funCode == 0x88) {
                        int mcuVerion = data[2];
                        int moniInteger = data[4];
                        int moniDeci = data[5];
                        int moni2Integer = data[6];
                        int moni2Deci = data[7];
                        int moni3Integer = data[8];
                        int moni3Deci = data[9];
                        int moni4Integer = data[10];
                        int moni4Deci = data[11];
                        int moni5Integer = data[12];
                        int moni5Deci = data[13];
                        int moni6Integer = data[14];
                        int moni6Deci = data[15];
                        int moni7Integer = data[16];
                        int moni7Deci = data[17];
                        int moni8Integer = data[18];
                        int moni8Deci = data[19];

                        double moni = Double.parseDouble(moniInteger + "." + moniDeci);
                        double moni2 = Double.parseDouble(moni2Integer + "." + moni2Deci);
                        double moni3 = Double.parseDouble(moni3Integer + "." + moni3Deci);
                        double moni4 = Double.parseDouble(moni4Integer + "." + moni4Deci);
                        double moni5 = Double.parseDouble(moni5Integer + "." + moni5Deci);
                        double moni6 = Double.parseDouble(moni6Integer + "." + moni6Deci);
                        double moni7 = Double.parseDouble(moni7Integer + "." + moni7Deci);
                        double moni8 = Double.parseDouble(moni8Integer + "." + moni8Deci);
                        Table table = deviceAnalogDao.findDeviceAnalog(macAddress, 1);

                        table.setData(moni);
                        Table table2 = deviceAnalogDao.findDeviceAnalog(macAddress, 2);
                        table2.setData(moni2);
                        Table table3 = deviceAnalogDao.findDeviceAnalog(macAddress, 3);
                        table3.setData(moni3);
                        Table table4 = deviceAnalogDao.findDeviceAnalog(macAddress, 4);
                        table4.setData(moni4);
                        Table table5 = deviceAnalogDao.findDeviceAnalog(macAddress, 5);
                        table5.setData(moni5);
                        Table table6 = deviceAnalogDao.findDeviceAnalog(macAddress, 6);
                        table6.setData(moni6);
                        Table table7 = deviceAnalogDao.findDeviceAnalog(macAddress, 7);
                        table7.setData(moni7);
                        Table table8 = deviceAnalogDao.findDeviceAnalog(macAddress, 8);
                        table8.setData(moni8);
                        list.clear();
                        list.add(table);
                        list.add(table2);
                        list.add(table3);
                        list.add(table4);
                        list.add(table5);
                        list.add(table6);
                        list.add(table7);
                        list.add(table8);
                        deviceAnalogDao.updates(list);

                        Collections.sort(list, new Comparator<Table>() {
                            @Override
                            public int compare(Table o1, Table o2) {
                                if (o1.getI() > o2.getI())
                                    return 1;
                                else if (o1.getI() < o2.getI())
                                    return -1;
                                return 0;
                            }
                        });
                    } else if (funCode == 0x99) {
                        int type = 0;
                        int alermType = data[4];
                        int deviceControll = 0;
                        if (alermType == 0x11) {
                            deviceControll = 1;
                            type = 0;
                        } else if (alermType == 0x22) {
                            deviceControll = 2;
                            type = 2;
                        } else if (alermType == 0x33) {
                            deviceControll = 3;
                            type = 2;
                        } else if (alermType == 0x44) {
                            deviceControll = 4;
                            type = 3;
                        } else if (alermType == 0x55) {
                            deviceControll = 5;
                            type = 4;
                        } else if (alermType == 0x66) {
                            deviceControll = 6;
                            type = 5;
                        } else if (alermType == 0x77) {
                            deviceControll = 7;
                            type = 6;
                        } else if (alermType == 0x88) {
                            deviceControll = 8;
                            type = 7;
                        }
                        Alerm alerm = deviceAlermDao.findDeviceAlerm(macAddress, type);
                        if (alerm.getDeviceAlarmFlag() == 1) {
                            Message msg = handler.obtainMessage();
                            msg.what = 10003;
                            msg.arg1 = type;
                            handler.sendMessage(msg);
                        }
                        if (alerm.getDeviceAlarmBroadcast() == 1 || alerm.getDeviceAlarmBroadcast() == 2) {
                            String cotent = alerm.getContent();
                            Message msg = handler.obtainMessage();
                            msg.what = 10004;
                            msg.arg1 = type;
                            msg.obj = cotent;
                            handler.sendMessage(msg);
                        }
                        int line = data[5];
                        if (line != 0) {
                            if (NoFastClickUtils.isFastClick()) {
                                operateLog.clear();
                                operateLog.put("deviceMac", macAddress);
                                operateLog.put("deviceControll", deviceControll);
                                operateLog.put("deviceLogType", 2);
                                operateLog.put("deviceLine", line);
                                operateLog.put("userId", userId);
                                new AddOperationLogAsync(MQService.this).execute(operateLog);
                            }
                        }
                    } else if (funCode == 0xaa) {
                        for (int j = 4; j < data.length - 2; j++) {
                            rs485 = rs485 + "" + data[j] + " ";
                        }
                        device = deviceDao.findDeviceByMac(macAddress);
                        device.setRe485(rs485);
                        deviceDao.update(device);
                    }
                    if (DeviceListActivity.running) {
                        Intent intent = new Intent("DeviceListActivity");
                        intent.putExtra("funCode", funCode);
                        intent.putExtra("macAddress", macAddress);
                        intent.putExtra("device", device);
                        intent.putExtra("lines", lines2);
                        sendBroadcast(intent);
                    } else if (SearchDeviceActivity.running) {
                        Intent intent = new Intent("SearchDeviceActivity");
                        intent.putExtra("funCode", funCode);
                        intent.putExtra("macAddress", macAddress);
                        intent.putExtra("device", device);
                        intent.putExtra("lines", lines2);
                        sendBroadcast(intent);
                    } else if (DeviceItemActivity.running) {
                        Intent intent = new Intent("DeviceItemActivity");
                        intent.putExtra("funCode", funCode);
                        intent.putExtra("macAddress", macAddress);
                        intent.putExtra("device", device);
                        intent.putExtra("lines", lines2);
                        sendBroadcast(intent);
                    } else if (JogSetActivity.running) {
                        Intent intent = new Intent("JogSetActivity");
                        intent.putExtra("macAddress", macAddress);
                        intent.putExtra("online", true);
                        if (funCode == 0x44) {
                            intent.putExtra("lineJog", lineJog);
                        }
                        sendBroadcast(intent);
                    } else if (DeviceInterLockActivity.running) {
                        Intent intent = new Intent("DeviceInterLockActivity");
                        intent.putExtra("macAddress", macAddress);
                        intent.putExtra("device", device);
                        sendBroadcast(intent);
                    } else if (TimerTaskActivity.running) {
                        Intent intent = new Intent("TimerTaskActivity");
                        intent.putExtra("macAddress", macAddress);
                        intent.putExtra("timerTaskFlag", timerTaskFlag);
                        intent.putExtra("timerTaskId", timerTaskId);
                        intent.putExtra("timerTask", (Serializable) timerTask);
                        intent.putExtra("operate", operateState);
                        intent.putExtra("online", true);
                        sendBroadcast(intent);
                    } else if (LinkedControlActivity.running) {
                        Intent intent = new Intent("LinkedControlActivity");
                        intent.putExtra("macAddress", macAddress);
                        if (linkedTypes != null && linkedTypes.size() == 6) {
                            intent.putExtra("linkedTypes", (Serializable) linkedTypes);
                            intent.putExtra("operate", operateState);
                        }
                        intent.putExtra("online", true);
                        sendBroadcast(intent);
                    } else if (LinkItemActivity.running) {
                        Intent intent = new Intent("LinkItemActivity");
                        intent.putExtra("macAddress", macAddress);
                        intent.putExtra("linkType", linkType);
                        if (funCode == 0x39) {
                            intent.putExtra("moniType", moniType);
                            intent.putExtra("linkTypeNum", linkTypeNum);
                        }
                        intent.putExtra("operate", operateState);
                        intent.putExtra("online", true);
                        sendBroadcast(intent);
                    } else if (MoniLinkItemActivity.running) {
                        Intent intent = new Intent("MoniLinkItemActivity");
                        intent.putExtra("macAddress", macAddress);
                        intent.putExtra("moniLinkSwitch", moniLinkSwitch);
                        intent.putExtra("online", true);
                        sendBroadcast(intent);
                    } else if (SwichCheckActivity.running) {
                        Intent intent = new Intent("SwichCheckActivity");
                        intent.putExtra("macAddress", macAddress);
                        intent.putExtra("switchState1", switchState1);
                        intent.putExtra("switchState2", switchState2);
                        intent.putExtra("switchState3", switchState3);
                        intent.putExtra("switchState4", switchState4);
                        intent.putExtra("switchState5", switchState5);
                        intent.putExtra("switchState6", switchState6);
                        intent.putExtra("switchState7", switchState7);
                        intent.putExtra("switchState8", switchState8);
                        intent.putExtra("online", true);
                        sendBroadcast(intent);
                    } else if (AlermActivity.running) {
                        Intent intent = new Intent("AlermActivity");
                        intent.putExtra("macAddress", macAddress);
                        intent.putExtra("alerms", (Serializable) alerms);
                        intent.putExtra("online", true);
                        sendBroadcast(intent);
                    } else if (InterLockActivity.running) {
                        Intent intent = new Intent("InterLockActivity");
                        intent.putExtra("macAddress", macAddress);
                        intent.putExtra("map", (Serializable) map);
                        intent.putExtra("online", true);
                        sendBroadcast(intent);
                    } else if (LocationActivity.running) {
                        Intent intent = new Intent("LocationActivity");
                        intent.putExtra("macAddress", macAddress);
                        intent.putExtra("latitude", latitude);
                        intent.putExtra("longitude", longitude);
                        sendBroadcast(intent);
                    } else if (MoniCheckActivity.running) {
                        Intent intent = new Intent("MoniCheckActivity");
                        intent.putExtra("macAddress", macAddress);
                        intent.putExtra("table", (Serializable) list);
                        intent.putExtra("online", true);
                        sendBroadcast(intent);
                    } else if (RS485Activity.running) {
                        Intent intent = new Intent("RS485Activity");
                        intent.putExtra("macAddress", macAddress);
                        intent.putExtra("rs485", rs485);
                        sendBroadcast(intent);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
        }
    }


    /**
     * 重新连接mqtt，即重连机制
     */
    private void startReconnect() {

        scheduler.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                if (!client.isConnected()) {
                    connect(1);
                    Log.i("MQService","-->startReconnect");
                }
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
    }

    public void addCountTimer(String macAddress) {
        for (CountTimer timer : countTimers) {
            Log.e("addCountTimer", "-->" + macAddress);
            String deviceMac = timer.getMacArress();
            if (macAddress.equals(deviceMac)) {
                return;
            }
        }
        CountTimer countTimer = new CountTimer(1000 * 60 * 5*6, 1000);
        countTimer.setMacArress(macAddress);
        countTimers.add(countTimer);
    }

    public void clearCountTimer() {
        countTimers.clear();
    }

    public void revoveCountTimer(String deviceMac) {
        for (CountTimer timer : countTimers) {
            String macAddress = timer.getMacArress();
            if (deviceMac.equals(macAddress)) {
                countTimers.remove(timer);
                break;
            }
        }
    }

    List<CountTimer> countTimers = new LinkedList<>();

    CountTime2 countTime2=new CountTime2(2000,1000);
    class CountTime2 extends CountDownTimer {

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public CountTime2(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            Log.i("CountTime2","-->"+millisUntilFinished/1000);
        }

        @Override
        public void onFinish() {
            if (deviceDao!=null){
                List<Device> devices=deviceDao.findAllDevice();
                if (!devices.isEmpty()){
                    new LoadDataAsync(MQService.this).execute(devices);
                }
            }
        }
    }

    class LoadDataAsync extends BaseWeakAsyncTask<List<Device>, Void, Integer, MQService> {

        public LoadDataAsync(MQService mqService) {
            super(mqService);
        }

        @Override
        protected Integer doInBackground(MQService mqService, List<Device>... lists) {
            List<Device> devices = lists[0];
            try {
                for (Device device : devices) {
                    String deviceMac = device.getDeviceOnlyMac();
                    String topicName = "qjjc/gateway/" + deviceMac + "/server_to_client";
                    getData(topicName, 0x11);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(MQService mqService, Integer integer) {

        }
    }

    class CountTimer extends CountDownTimer {

        private String macArress;
        private long millisUntilFinished;

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
            this.millisUntilFinished = millisUntilFinished;
            Log.e("millisUntilFinished", macArress+"->" + millisUntilFinished/1000);
        }

        @Override
        public void onFinish() {
            Log.e("millisUntilFinished", "-->" + millisUntilFinished/1000);

            String topicName = "qjjc/gateway/" + macArress + "/server_to_client";
            getData(topicName, 0x11);

            Intent intent = new Intent("offline");
            intent.putExtra("macAddress", macArress);
            sendBroadcast(intent);
        }

        public long getMillisUntilFinished() {
            return millisUntilFinished;
        }

        public void setMillisUntilFinished(long millisUntilFinished) {
            this.millisUntilFinished = millisUntilFinished;
        }

        public String getMacArress() {
            return macArress;
        }

        public void setMacArress(String macArress) {
            this.macArress = macArress;
        }
    }

    /**
     * 发送主题
     *
     * @param topicName 主题名称
     * @param qos       消息发送次数，0为最多发一次，1为至少发送一次，2为只发一次
     * @param payload   发送的内容
     * @return
     */
    public boolean publish(String topicName, int qos, String payload) {
        boolean flag = false;
        if (client != null && client.isConnected()) {
            try {
                MqttMessage message = new MqttMessage(payload.getBytes("utf-8"));
                qos = 1;
                message.setQos(qos);
                client.publish(topicName, message);
                flag = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return flag;
    }

    public boolean publish(String topicName, int qos, byte[] bytes) {
        boolean flag = false;
        if (client != null && client.isConnected()) {
            try {
                MqttMessage message = new MqttMessage(bytes);
                message.setQos(qos);
                client.publish(topicName, message);
                flag = true;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return flag;
    }

    /**
     * 订阅所有主题
     *
     * @param topicName
     * @param qos
     * @return
     */
    public boolean subscribe(String topicName, int qos) {
        boolean flag = false;
        try {
            if (client!=null && !client.isConnected()){
                client.connect(options);
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }

        if (client != null && client.isConnected()) {
            try {
                client.subscribe(topicName, qos);
                flag = true;
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
        return flag;
    }

    public String getResult() {
        return result;
    }

    public void insert(Device device) {
        String deviceMac = device.getDeviceOnlyMac();
        Device device2 = deviceDao.findDeviceByMac(deviceMac);
        if (device2 != null) {
            deviceDao.update(device);
        } else {
            List<Device> devices = deviceDao.findDevicesByMac(deviceMac);
            if (devices != null && !devices.isEmpty()) {
                deviceDao.deleteDevices(devices);
            }
            deviceDao.insert(device);
        }
    }

    /**
     * 批量更新定时
     *
     * @param timerTasks
     */
    public void updateTimerTasks(List<TimerTask> timerTasks) {
        timerTaskDao.updateTimers(timerTasks);
    }

    /**
     * 批量更新联动
     *
     * @param linkeds
     */
    public void updateLinkeds(List<Linked> linkeds) {
        deviceLinkDao.updates(linkeds);
    }

    /**
     * 批量更新模拟量联动
     *
     * @param moniLinks
     */
    public void updateMoniLinks(List<MoniLink> moniLinks) {
        deviceMoniLinkDaoDao.updateMoniLinks(moniLinks);
    }

    public void updateSwitchCheck() {
        if (!switchChecks.isEmpty()) {
            for (int i = 0; i < switchChecks.size(); i++) {
                SwtichState swtichState = switchChecks.get(i);
                swtichState.setState(0);
                switchChecks.set(i, swtichState);
            }
        }
    }

    /**
     * 获取所有主题
     *
     * @return
     */
    public List<String> getTopicNames() {
        List<String> topicNames = new ArrayList<>();
        List<Device> devices = deviceDao.findAllDevice();

        for (Device device : devices) {
            String mac = device.getDeviceOnlyMac();
            String server = "qjjc/gateway/" + mac + "/client_to_server";
            String lwt = "qjjc/gateway/" + mac + "/lwt";
            topicNames.add(server);
            topicNames.add(lwt);
        }
        return topicNames;
    }

    /**
     * 取消订阅主题
     *
     * @param topicName
     */
    public void unsubscribe(String topicName) {
        if (client != null && client.isConnected()) {
            try {
                client.unsubscribe(topicName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /***
     * 取消所有的订阅
     */
    public void cancelAllsubscibe() {
        List<String> list = getTopicNames();
        for (int i = 0; i < list.size(); i++) {
            String s = list.get(i);
            unsubscribe(s);
        }
    }


    AlermDialog4 alermDialog4;

    private void setAlermDialog(int mode) {

        try {
            alermDialog4 = new AlermDialog4(getApplicationContext());
            if (alermDialog4 != null && alermDialog4.isShowing()) {
                return;
            }
            alermDialog4.setMode(mode);
            alermDialog4.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            alermDialog4.setCanceledOnTouchOutside(false);
            alermDialog4.setOnNegativeClickListener(new AlermDialog4.OnNegativeClickListener() {
                @Override
                public void onNegativeClick() {
                    alermDialog4.dismiss();
                }
            });
            alermDialog4.setOnPositiveClickListener(new AlermDialog4.OnPositiveClickListener() {
                @Override
                public void onPositiveClick() {
                    alermDialog4.dismiss();
                }
            });
            alermDialog4.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 初始化语音合成监听。
     */
    private InitListener mTtsInitListener = new InitListener() {
        @SuppressLint("ShowToast")
        @Override
        public void onInit(int code) {
            Log.d(TAG, "InitListener init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                // showTip("初始化失败,错误码：" + code);
                Toast.makeText(getApplicationContext(), "初始化失败,错误码：" + code,
                        Toast.LENGTH_SHORT).show();
            } else {
                // 初始化成功，之后可以调用startSpeaking方法
                // 注：有的开发者在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成，
                // 正确的做法是将onCreate中的startSpeaking调用移至这里
            }
        }
    };

    class SpeechReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String alerm = intent.getStringExtra("alerm");
            Log.i("SpeechReceiver", "-->" + alerm);
            if (!TextUtils.isEmpty(alerm)) {
//                starSpeech(alerm);
                int mode = -1;
                if ("来电报警".equals(alerm)) {
                    mode = 0;
                } else if ("断电报警".equals(alerm)) {
                    mode = 1;
                } else if ("温度报警".equals(alerm)) {
                    mode = 2;
                } else if ("湿度报警".equals(alerm)) {
                    mode = 3;
                } else if ("电压报警".equals(alerm)) {
                    mode = 4;
                } else if ("电流报警".equals(alerm)) {
                    mode = 5;
                } else if ("功率报警".equals(alerm)) {
                    mode = 6;
                } else if ("开关量报警".equals(alerm)) {
                    mode = 7;
                }
            }
        }
    }

    private AlermDialog alermDialog;


    /**
     * 初始化语音合成相关数据
     *
     * @Description:
     */
    private SpeechSynthesizer mTts;// 语音合成

    public void starSpeech(String deviceMac, String content) {

        // 2.合成参数设置，详见《科大讯飞MSC API手册(Android)》SpeechSynthesizer 类
        Device device = deviceDao.findDeviceByMac(deviceMac);
        if (device != null && device.getVlice2() == 1) {
            mTts.setParameter(SpeechConstant.VOICE_NAME, "vixy");// 设置发音人
            mTts.setParameter(SpeechConstant.SPEED, "50");// 设置语速
            mTts.setParameter(SpeechConstant.VOLUME, "80");// 设置音量，范围0~100
            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD); // 设置云端
            // 设置合成音频保存位置（可自定义保存位置），保存在“./sdcard/iflytek.pcm”
            // 保存在SD卡需要在AndroidManifest.xml添加写SD卡权限
            // 3.开始合成
            mTts.startSpeaking(content, mSynListener);
        }
        // 合成监听器
        //

    }

    public void starSpeech2(String content) {

        // 2.合成参数设置，详见《科大讯飞MSC API手册(Android)》SpeechSynthesizer 类
        mTts.setParameter(SpeechConstant.VOICE_NAME, "vixy");// 设置发音人
        mTts.setParameter(SpeechConstant.SPEED, "50");// 设置语速
        mTts.setParameter(SpeechConstant.VOLUME, "80");// 设置音量，范围0~100
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD); // 设置云端
        // 设置合成音频保存位置（可自定义保存位置），保存在“./sdcard/iflytek.pcm”
        // 保存在SD卡需要在AndroidManifest.xml添加写SD卡权限
        // 3.开始合成
        mTts.startSpeaking(content, mSynListener);
        // 合成监听器
        //

    }

    private SynthesizerListener mSynListener = new SynthesizerListener() {
        @Override
        public void onSpeakBegin() {

        }

        @Override
        public void onBufferProgress(int i, int i1, int i2, String s) {

        }

        @Override
        public void onSpeakPaused() {

        }

        @Override
        public void onSpeakResumed() {

        }

        @Override
        public void onSpeakProgress(int i, int i1, int i2) {

        }

        @Override
        public void onCompleted(SpeechError speechError) {

        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };


    public void updateLine(Line2 line2) {
        deviceLineDao.update(line2);
    }

    /**
     * @param topicName 发送主题
     * @param funCode   功能码 0x11是基本功能，0x22定时设置，0x33联动设置，0x44工作模式具体设置，0x55开关量检测
     *                  0x66报警设置，0x77地图定位上传，0x88模拟量检测
     */
    public void getData(String topicName, int funCode) {
        try {
            byte[] bytes = new byte[4];
            bytes[0] = 0x55;
            bytes[1] = (byte) funCode;
            int sum = 0;
            for (int b : bytes) {
                sum += b;
            }
            bytes[2] = (byte) (sum % 256);
            bytes[3] = (byte) 0x88;


            boolean success = publish(topicName, 1, bytes);
            if (!success) {
                publish(topicName, 1, bytes);
            }
            Log.i("success","--->"+success);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 基础设置 0x11
     *
     * @param topicName
     * @param device
     */
    public boolean sendBasic(String topicName, Device device) {
        boolean success = false;
        try {
            int mcuVersion = device.getMcuVersion();
            boolean open = device.getIsOpen();
            int deviceState = 0;
            if (open) {
                deviceState = 1;
            } else {
                deviceState = 0;
            }
            int prelines = device.getPrelines();
            int lastlines = device.getLastlines();
            int prelineswitch = device.getPrelineswitch();
            int lastlineswitch = device.getLastlineswitch();
            int prelinesjog = device.getPrelinesjog();
            int lastlinesjog = device.getLastlinesjog();
            int plMemory = device.getPlMemory();


            byte[] datas = new byte[19];

            datas[0] = (byte) 0x90;
            datas[1] = 0x11;
            datas[2] = (byte) mcuVersion;
            datas[3] = 0x0d;
            datas[4] = (byte) deviceState;
            datas[5] = (byte) prelines;
            datas[6] = (byte) lastlines;
            datas[8] = (byte) lastlineswitch;
            datas[7] = (byte) prelineswitch;
            datas[9] = (byte) prelinesjog;
            datas[10] = (byte) lastlinesjog;
            datas[11] = (byte) plMemory;
            int sum = 0;
            for (int i = 0; i < datas.length; i++) {
                sum += datas[i];
            }
            datas[17] = (byte) (sum % 256);
            datas[18] = 0x09;

            success = publish(topicName, 1, datas);
            if (!success) {
                publish(topicName, 1, datas);
            }
            Log.i("Basic","-->"+success);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return success;
    }

    /**
     * 定时设置 0x22
     *
     * @param topicName
     * @param timerTask
     */
    public boolean sendTimerTask(String topicName, TimerTask timerTask) {
        boolean success = false;
        try {
            int mcuVersion = timerTask.getMcuVersion();
            int choice = timerTask.getChoice();
            int yearHigh = timerTask.getYear() / 256;
            int yearLow = timerTask.getYear() % 256;
            int month = timerTask.getMonth();
            int day = timerTask.getDay();
            int week = timerTask.getWeek();
            int hour = timerTask.getHour();
            int min = timerTask.getMin();
            int prelines = timerTask.getPrelines();
            int lastlines = timerTask.getLastlines();
            int controlState = timerTask.getControlState();
            int state = timerTask.getState();

            byte[] data = new byte[23];
            data[0] = (byte) 0x90;
            data[1] = 0x22;
            data[2] = (byte) mcuVersion;
            data[3] = 0x11;
            data[4] = (byte) choice;
            data[5] = (byte) yearHigh;
            data[6] = (byte) yearLow;
            data[7] = (byte) month;
            data[8] = (byte) day;
            data[9] = (byte) week;
            data[10] = (byte) hour;
            data[11] = (byte) min;
            data[12] = (byte) prelines;
            data[13] = (byte) lastlines;
            data[14] = (byte) controlState;
            data[15] = (byte) state;
            int sum = 0;
            for (int i : data) {
                sum += i;
            }
            data[21] = (byte) (sum % 256);
            data[22] = 0x09;


            success = publish(topicName, 1, data);
            if (!success) {
                publish(topicName, 1, data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    class LinkedAsync extends BaseWeakAsyncTask<List<Linked>, Void, Void, MQService> {

        public LinkedAsync(MQService mqService) {
            super(mqService);
        }

        @Override
        protected Void doInBackground(MQService mqService, List<Linked>... lists) {
            try {
                List<Linked> linkeds = lists[0];
                Linked linked = linkeds.get(0);
                String deviceMac = linked.getDeviceMac();
                String topicName = "qjjc/gateway/" + deviceMac + "/server_to_client";
                for (Linked linked2 : linkeds) {
                    sendLinkedSet(topicName, linked2);
                    Thread.sleep(500);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(MQService mqService, Void aVoid) {

        }
    }

    /**
     * 发送联动控制
     *
     * @param topicName
     * @param linked
     * @return
     */
    public boolean sendLinkedSet(String topicName, Linked linked) {
        boolean success = false;
        try {
            int type = linked.getType();
            int funCode = 0;
            if (type == 0) {
                funCode = 0x34;
            } else if (type == 1) {
                funCode = 0x35;
            } else if (type == 2) {
                funCode = 0x36;
            } else if (type == 3) {
                funCode = 0x37;
            } else if (type == 4) {
                funCode = 0x38;
            }
            int mcuVersion = linked.getMcuVersion();
            int condition = linked.getCondition();//触发条件
            if (type == 0) {
                condition = condition + 128;
            }
            int triState = linked.getTriState();//触发条件状态
            int conditionState = linked.getConditionState();//控制状态
            int preLines = linked.getPreLines();
            int lastLines = linked.getLastLines();
            int triType = linked.getTriType();//0单次触发，1循环触发
            int state = linked.getState();
            int switchLine = linked.getSwitchLine();
            int[] x = new int[8];
            if (type == 2) {
                x[7] = 0;
                x[6] = 0;
            } else {
                if (triState == 1) {
                    x[7] = 1;
                    x[6] = 1;
                } else if (triState == 0) {
                    x[7] = 1;
                    x[6] = 0;
                }
            }
            if (conditionState == 1) {
                x[5] = 1;
                x[4] = 1;
            } else {
                x[5] = 1;
                x[4] = 0;
            }
            x[3] = triType;

            int triType2 = TenTwoUtil.changeToTen(x);
            byte[] bytes = new byte[16];
            bytes[0] = (byte) 0x90;
            bytes[1] = (byte) funCode;
            bytes[2] = (byte) mcuVersion;
            bytes[3] = 0x0a;
            bytes[4] = (byte) condition;
            bytes[5] = (byte) preLines;
            bytes[6] = (byte) lastLines;
            bytes[7] = (byte) triType2;
            bytes[8] = (byte) state;
            if (type == 2) {
                bytes[9] = (byte) switchLine;
            }
            int sum = 0;
            for (int i = 0; i < bytes.length; i++) {
                sum += bytes[i];
            }
            int checkCode = sum % 256;
            bytes[14] = (byte) checkCode;
            bytes[15] = 0x09;

            success = publish(topicName, 1, bytes);
            if (!success)
                publish(topicName, 1, bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 发送模拟量联动
     *
     * @param topicName
     * @param moniLink
     * @return
     */
    public boolean sendMoniLink(String topicName, MoniLink moniLink) {
        boolean success = false;
        try {
            int headCode = 0x90;
            int funCode = 0x39;
            int mcuVersion = moniLink.getMcuVersion();
            int length = 0x0a;
            int contition = moniLink.getContition();
            int triState = moniLink.getTriState();
            int preLine = moniLink.getPreLine();
            int lastLine = moniLink.getLastLine();
            int controlState = moniLink.getControlState();
            int triType = moniLink.getTriType();
            int state = moniLink.getState();
            int type = moniLink.getType();
            int num = moniLink.getNum();
            int controlType = 0;
            int triType2 = 0;
            if (type == 0) {
                if (num == 0) {
                    controlType = 0x11;
                } else if (num == 1) {
                    controlType = 0x22;
                } else if (num == 2) {
                    controlType = 0x33;
                } else if (num == 3) {
                    controlType = 0x44;
                }
            } else if (type == 1) {
                if (num == 4) {
                    controlType = 0x55;
                } else if (num == 5) {
                    controlType = 0x66;
                } else if (num == 6) {
                    controlType = 0x77;
                } else if (num == 7) {
                    controlType = 0x88;
                }
            }
            if (state == 3) {

            } else {
                int[] x = new int[8];
                if (triState == 1) {
                    x[7] = 1;
                    x[6] = 1;
                } else {
                    x[7] = 1;
                    x[6] = 0;
                }
                if (controlState == 1) {
                    x[5] = 1;
                    x[4] = 1;
                } else {
                    x[5] = 1;
                    x[4] = 0;
                }
                x[3] = triType;
                triType2 = TenTwoUtil.changeToTen(x);
            }

            byte[] bytes = new byte[16];
            bytes[0] = (byte) headCode;
            bytes[1] = (byte) 0x39;
            bytes[2] = (byte) mcuVersion;
            bytes[3] = 0x0a;
            bytes[4] = (byte) contition;
            bytes[5] = (byte) preLine;
            bytes[6] = (byte) lastLine;
            bytes[7] = (byte) triType2;
            bytes[8] = (byte) state;
            bytes[9] = (byte) controlType;

            int sum = 0;
            for (int i = 0; i < bytes.length; i++) {
                sum += bytes[i];
            }
            bytes[14] = (byte) (sum % 256);
            bytes[15] = 0x09;
            success = publish(topicName, 1, bytes);
            if (!success)
                publish(topicName, 1, bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Map<String, int[]> moniMap = new HashMap<>();

    public int[] getMoniLinkSwitch(String deviceMac) {
        return moniMap.get(deviceMac);
    }

    /**
     * 发送模拟量开关
     *
     * @param topicName
     * @param mcuVersion
     * @param data
     * @return
     */
    public boolean sendMoniLinkSwitch(String topicName, int mcuVersion, int[] data) {
        try {
            byte[] bytes = new byte[12];
            bytes[0] = (byte) 0x90;
            bytes[1] = 0x3a;
            bytes[2] = (byte) mcuVersion;
            bytes[3] = 0x06;
            int x = TenTwoUtil.changeToTen(data);
            bytes[4] = (byte) x;
            int sum = 0;
            for (int i = 0; i < bytes.length; i++) {
                sum += bytes[i];
            }
            bytes[10] = (byte) (sum % 256);
            bytes[11] = 0x09;
            boolean success = publish(topicName, 1, bytes);
            if (!success)
                publish(topicName, 1, topicName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 向设备发送联动的开关状态
     *
     * @param topicName
     * @param mcuVerion
     * @param data
     * @return
     */
    public boolean sendLinkedSwitch(String topicName, int mcuVerion, int data) {
        boolean success = false;
        try {
            byte[] bytes = new byte[12];
            bytes[0] = (byte) 0x90;
            bytes[1] = 0x33;
            bytes[2] = (byte) mcuVerion;
            bytes[3] = 0x06;
            bytes[4] = (byte) data;
            int sum = 0;
            for (int i = 0; i < bytes.length; i++) {
                sum += bytes[i];
            }
            bytes[10] = (byte) (sum % 256);
            bytes[11] = 0x09;
            success = publish(topicName, 1, bytes);
            if (!success) {
                publish(topicName, 1, bytes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return success;
    }

    /**
     * 同步设备互锁
     *
     * @param deviceMac
     * @return
     */
    public Map<String, String> getDeviceInterLock(String deviceMac) {
        return deviceLineDao.findInterLockLine(deviceMac);
    }

    public List<Line2> getDeviceLines(String deviceMac) {
        return deviceLineDao.findDeviceLines(deviceMac);
    }

    public List<Line2> getDeviceOnlineLiens(String deviceMac) {
        return deviceLineDao.findDeviceOnlineLines(deviceMac);
    }

    public void updateLines(List<Line2> lines) {
        deviceLineDao.updates(lines);
    }

    /**
     * 发送开关量联动控制
     *
     * @param topicName
     * @param mcuVersion
     * @param data
     * @return
     */
    public boolean sendSwitchLinked(String topicName, int mcuVersion, int data) {
        boolean success = false;
        try {

            byte[] bytes = new byte[12];
            bytes[0] = (byte) 0x90;
            bytes[1] = 0x39;
            bytes[2] = 0x06;
            bytes[3] = (byte) mcuVersion;
            bytes[4] = (byte) data;
            int sum = 0;
            for (int i = 0; i < bytes.length; i++) {
                sum += bytes[i];
            }
            bytes[10] = (byte) (sum % 256);
            bytes[11] = 0x09;
            success = publish(topicName, 1, bytes);
            if (!success)
                publish(topicName, 1, bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return success;
    }

    /**
     * 设置点动
     *
     * @param topicName
     * @param mcuVerion
     * @param jog
     * @return
     */
    public boolean sendJog(String topicName, int mcuVerion, int jog) {
        boolean success = false;
        try {
            int jogHigh = (jog / 256);
            int jogLow = (jog % 256);

            byte[] bytes = new byte[13];
            bytes[0] = (byte) 0x90;
            bytes[1] = 0x44;
            bytes[2] = (byte) mcuVerion;
            bytes[3] = 0x07;
            bytes[4] = (byte) jogHigh;
            bytes[5] = (byte) jogLow;
            int sum = 0;
            for (int j = 0; j < bytes.length; j++) {
                sum += bytes[j];
            }
            bytes[11] = (byte) (sum % 256);
            bytes[12] = 0x09;
            success = publish(topicName, 1, bytes);
            if (!success)
                publish(topicName, 1, bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return success;
    }

    /**
     * 发送报警设置
     *
     * @param topicName
     * @param mcuVersion
     * @param data
     * @return
     */
    public boolean sendAlerm(String topicName, int mcuVersion, int[] data) {
        boolean success = false;
        try {
            byte[] bytes = new byte[28];
            bytes[0] = (byte) 0x90;
            bytes[1] = 0x66;
            bytes[2] = (byte) mcuVersion;
            bytes[3] = 0x16;
            for (int i = 0; i < data.length; i++) {
                bytes[i + 4] = (byte) data[i];
            }
            int sum = 0;
            for (int i = 0; i < bytes.length; i++) {
                sum += bytes[i];
            }
            bytes[26] = (byte) (sum % 256);
            bytes[27] = 0x09;
            success = publish(topicName, 1, bytes);
            if (!success)
                publish(topicName, 1, bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 地图定位频率设置
     *
     * @param topicName
     * @param mctVersion
     * @param frequece
     * @return
     */
    public boolean sendLocation(String topicName, int mctVersion, int frequece) {
        boolean success = false;
        try {
            byte[] bytes = new byte[12];
            bytes[0] = (byte) 0x90;
            bytes[1] = 0x77;
            bytes[2] = (byte) mctVersion;
            bytes[3] = 0x06;
            bytes[4] = (byte) frequece;
            int sum = 0;
            for (int i = 0; i < bytes.length; i++) {
                sum += bytes[i];
            }
            bytes[10] = (byte) (sum % 256);
            bytes[11] = 0x09;
            success = publish(topicName, 1, bytes);
            if (!success)
                publish(topicName, 1, bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return success;
    }

    /**
     * 发送互锁线路
     *
     * @param topicName
     * @param mcuVersion
     * @param list
     * @return
     */
    public boolean sendInterLine(String topicName, int mcuVersion, int[] list) {
        boolean success = false;
        try {
            byte[] bytes = new byte[27];
            bytes[0] = (byte) 0x90;
            bytes[1] = 0x46;
            bytes[2] = (byte) mcuVersion;
            bytes[3] = 0x15;
            for (int i = 0; i < list.length; i++) {
                int value = list[i];
                bytes[i + 4] = (byte) value;
            }
            int sum = 0;
            for (int i = 0; i < bytes.length; i++) {
                sum += bytes[i];
            }
            bytes[25] = (byte) (sum % 256);
            bytes[26] = 0x09;
            success = publish(topicName, 1, bytes);
            if (!success)
                publish(topicName, 1, bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }



    private List<SwtichState> switchChecks = new ArrayList<>();

    public List<SwtichState> getSwitchName() {
        return switchChecks;
    }

    /**
     * 获取开关量名称
     * @param params
     */
    public void getSwitchName(Map<String, Object> params) {
        try {
            new GetSwichAsync(MQService.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    class GetSwichAsync extends BaseWeakAsyncTask<Map<String, Object>, Void, Map<String,Object>, MQService> {

        public GetSwichAsync(MQService mqService) {
            super(mqService);
        }

        @Override
        protected Map<String,Object> doInBackground(MQService mqService, Map<String, Object>... maps) {
            int code = 0;
            Map<String, Object> params = maps[0];
            String deviceMac = (String) params.get("deviceMac");
            params.remove("deviceMac");
            String url = HttpUtils.ipAddress + "device/getSwitchName";
            try {
                String result = HttpUtils.requestPost(url, params);
                if (TextUtils.isEmpty(result)) {
                    result = HttpUtils.requestPost(url, params);
                }
                if (!TextUtils.isEmpty(result)) {
                    Log.i("GetSwichAsync", "-->" + result);
                    JSONObject jsonObject = new JSONObject(result);
                    code = jsonObject.getInt("returnCode");
                    if (code == 100) {
                        switchChecks.clear();
                        JSONObject returnData = jsonObject.getJSONObject("returnData");
                        Gson gson = new Gson();
                        SwitchCheck switchCheck = gson.fromJson(returnData.toString() + "", SwitchCheck.class);
                        if (switchCheck != null) {
                            Class<SwitchCheck> clazz = (Class<SwitchCheck>) Class.forName("com.peihou.willgood2.pojo.SwitchCheck");
                            for (int i = 1; i <= 8; i++) {
                                Method method = clazz.getDeclaredMethod("getSwitchName" + i);
                                Method method2 = clazz.getDeclaredMethod("getSwitchPic" + i);
                                String switchName = (String) method.invoke(switchCheck);
                                String switchPic = (String) method2.invoke(switchCheck);
                                switchChecks.add(new SwtichState(0, switchName, switchPic, 0));
                            }
                        }
                    }
                }
                params.put("deviceMac", deviceMac);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return params;
        }

        @Override
        protected void onPostExecute(MQService mqService, Map<String,Object> params) {
            getAlarmName(params);
        }

    }

    /**
     * 获取报警
     * @param params
     */
    private void getAlarmName(Map<String, Object> params) {
        try {
            new AlermAsync(MQService.this).execute(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class AlermAsync extends BaseWeakAsyncTask<Map<String, Object>, Void,Map<String,Object>, MQService> {

        public AlermAsync(MQService mqService) {
            super(mqService);
        }

        @Override
        protected Map<String,Object> doInBackground(MQService mqService, Map<String, Object>... maps) {
            int code = 0;
            Map<String, Object> params = maps[0];
            try {
                String deviceMac = (String) params.get("deviceMac");
                long deviceId = (long) params.get("deviceId");
                params.remove("deviceMac");
                String url = HttpUtils.ipAddress + "device/getAlarmName";
                String result = HttpUtils.requestPost(url, params);
                if (!TextUtils.isEmpty(result)) {
                    Log.i("getAlarmName","->"+result);
                    JSONObject jsonObject = new JSONObject(result);
                    code = jsonObject.getInt("returnCode");
                    if (code == 100) {
                        String returnData = jsonObject.getJSONObject("returnData").toString();
                        Gson gson = new Gson();
                        AlermName alermName = gson.fromJson(returnData, AlermName.class);
                        int deviceAlarmBroadcast = alermName.getDeviceAlarmBroadcast();
                        int deviceAlarmFlag = alermName.getDeviceAlarmFlag();
                        Class<AlermName> clazz = (Class<AlermName>) Class.forName("com.peihou.willgood2.pojo.AlermName");
                        for (int i = 1; i <= 8; i++) {
                            Method method = clazz.getDeclaredMethod("getDeviceAlarmName" + i);
                            String content = (String) method.invoke(alermName);
                            String name = "";
                            if (i == 1) {
                                name = "来电报警";
                            } else if (i == 2) {
                                name = "断电报警";
                            } else if (i == 3) {
                                name = "温度报警";
                            } else if (i == 4) {
                                name = "湿度报警";
                            } else if (i == 5) {
                                name = "电压报警";
                            } else if (i == 6) {
                                name = "电流报警";
                            } else if (i == 7) {
                                name = "功率报警";
                            } else if (i == 8) {
                                name = "开关量报警";
                            }
                            Alerm alerm = deviceAlermDao.findDeviceAlerm(deviceId, i - 1);
                            if (alerm == null) {
                                alerm = new Alerm(name, i - 1, content, false, deviceId, deviceMac, 0);
                                alerm.setDeviceAlarmBroadcast(deviceAlarmBroadcast);
                                alerm.setDeviceAlarmFlag(deviceAlarmFlag);
                                deviceAlermDao.insert(alerm);
                            } else {
                                alerm.setContent(content);
                                alerm.setDeviceAlarmBroadcast(deviceAlarmBroadcast);
                                alerm.setDeviceAlarmFlag(deviceAlarmFlag);
                                alerm.setState(0);
                                deviceAlermDao.update(alerm);
                            }
                        }
                    } else {
                        List<Alerm> list = deviceAlermDao.findDeviceAlerms(deviceId);
                        if (list.size() != 8) {
                            deviceAlermDao.deleteDeviceAlerms(deviceId);
                            list.add(new Alerm("来电报警", 0, "设备已来电!", false, deviceId, deviceMac, 0));
                            list.add(new Alerm("断电报警", 1, "设备已经断电,请及时处理", false, deviceId, deviceMac, 0));
                            list.add(new Alerm("温度报警", 2, "温度报警,请注意", false, deviceId, deviceMac, 0));
                            list.add(new Alerm("湿度报警", 3, "湿度报警,请注意", false, deviceId, deviceMac, 0));
                            list.add(new Alerm("电压报警", 4, "电压报警,请注意", false, deviceId, deviceMac, 0));
                            list.add(new Alerm("电流报警", 5, "电流报警,请注意", false, deviceId, deviceMac, 0));
                            list.add(new Alerm("功率报警", 6, "功率报警,请注意", false, deviceId, deviceMac, 0));
                            list.add(new Alerm("开关量报警", 7, "开关量报警,请注意", false, deviceId, deviceMac, 50));
                            deviceAlermDao.insertDeviceAlerms(list);
                        }
                    }
                }
                params.put("deviceMac", deviceMac);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return params;
        }

        @Override
        protected void onPostExecute(MQService mqService, Map<String,Object> params) {
            getAnalogName(params);
        }
    }
    /**
     * 获取模拟量检测
     *
     * @param params
     */
    private void getAnalogName(Map<String, Object> params) {
        try {
            new AnalogCheckAcync(MQService.this).execute(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 获取设备模拟量名称
     */
    class AnalogCheckAcync extends BaseWeakAsyncTask<Map<String, Object>, Void, Integer, MQService> {

        public AnalogCheckAcync(MQService mqService) {
            super(mqService);
        }

        @Override
        protected Integer doInBackground(MQService mqService, Map<String, Object>... maps) {
            int code = 0;
            try {
                Map<String, Object> params = maps[0];
                String url = HttpUtils.ipAddress + "device/getAnalogName";
                String result = HttpUtils.requestPost(url, params);
                long deviceId = (long) params.get("deviceId");
                String deviceMac = (String) params.get("deviceMac");
                params.remove("deviceMac");
                if (TextUtils.isEmpty(result))
                    result = HttpUtils.requestPost(url, params);

                if (!TextUtils.isEmpty(result)) {
                    Log.i("getAnalogName","->"+result);
                    JSONObject jsonObject = new JSONObject(result);
                    code = jsonObject.getInt("returnCode");
                    if (code == 100) {

                        JSONObject returnData = jsonObject.getJSONObject("returnData");
                        String s = returnData.toString();
                        Gson gson = new Gson();
                        AnalogName analogName = gson.fromJson(s, AnalogName.class);
                        Class<AnalogName> clazz = (Class<AnalogName>) Class.forName("com.peihou.willgood2.pojo.AnalogName");

                        for (int i = 1; i <= 8; i++) {
                            Method method = clazz.getDeclaredMethod("getAnalogName" + i);
                            String name = (String) method.invoke(analogName);
                            if (i <= 4) {
                                Table table = deviceAnalogDao.findDeviceAnalog(deviceId, i);
                                if (table == null) {
                                    table = new Table(i, name, 0, 1, 0, "MA", deviceMac, deviceId);
                                    deviceAnalogDao.insert(table);
                                } else {
                                    table.setName(name);
                                    deviceAnalogDao.update(table);
                                }
                            } else if (i > 4) {
                                Table table = deviceAnalogDao.findDeviceAnalog(deviceId, i);
                                if (table == null) {
                                    table = new Table(i, name, 0, 1, 0, "V", deviceMac, deviceId);
                                    deviceAnalogDao.insert(table);
                                } else {
                                    table.setName(name);
                                    deviceAnalogDao.update(table);
                                }

                            }
                        }
                    }
                } else {
                    List<Table> tables = deviceAnalogDao.findDeviceAnalogs(deviceId);
                    if (tables.size() != 8) {
                        deviceAnalogDao.deleteDeviceTables(deviceId);
                        tables.add(new Table(1, "电流1", 0, 1, 0, "MA", deviceMac, deviceId));
                        tables.add(new Table(2, "电流2", 0, 1, 0, "MA", deviceMac, deviceId));
                        tables.add(new Table(3, "电流3", 0, 1, 0, "MA", deviceMac, deviceId));
                        tables.add(new Table(4, "电流4", 0, 1, 0, "MA", deviceMac, deviceId));
                        tables.add(new Table(5, "电压1", 0, 1, 0, "V", deviceMac, deviceId));
                        tables.add(new Table(6, "电压2", 0, 1, 0, "V", deviceMac, deviceId));
                        tables.add(new Table(7, "电压3", 0, 1, 0, "V", deviceMac, deviceId));
                        tables.add(new Table(8, "电流4", 0, 1, 0, "V", deviceMac, deviceId));
                        deviceAnalogDao.inserts(tables);
                    }
                    code = 100;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return code;
        }

        @Override
        protected void onPostExecute(MQService mqService, Integer integer) {

        }
    }

    private List<DeviceTrajectory> deviceTrajectories = new ArrayList<>();

    public List<DeviceTrajectory> getDeviceTrajectory() {
        return deviceTrajectories;
    }

    public void getDeviceTrajectory(Map<String, Object> params) {
        try {
            new DeviceTrajectoryAsync().execute(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class DeviceTrajectoryAsync extends AsyncTask<Map<String, Object>, Void, Integer> {

        @Override
        protected Integer doInBackground(Map<String, Object>... maps) {
            int code = 0;
            Map<String, Object> params = maps[0];
            String deviceMac = (String) params.get("deviceMac");
            params.remove("deviceMac");
            String url = HttpUtils.ipAddress + "data/getDeviceTrajectory";
            try {
                String result = HttpUtils.requestPost(url, params);
                if (TextUtils.isEmpty(result)) {
                    result = HttpUtils.requestPost(url, params);
                }
                if (!TextUtils.isEmpty(result)) {
                    JSONObject jsonObject = new JSONObject(result);
                    code = jsonObject.getInt("returnCode");
                    if (code == 100) {
                        deviceTrajectories.clear();
                        JSONObject returnData = jsonObject.getJSONObject("returnData");
                        JSONArray deviceTrajectoryList = returnData.getJSONArray("deviceTrajectoryList");
                        if (deviceTrajectoryList != null) {
                            for (int i = 0; i < deviceTrajectoryList.length(); i++) {
                                String json = deviceTrajectoryList.getJSONObject(i).toString();
                                Gson gson = new Gson();
                                DeviceTrajectory deviceTrajectory = gson.fromJson(json, DeviceTrajectory.class);
                                deviceTrajectories.add(deviceTrajectory);
                            }
                            Collections.sort(deviceTrajectories, new Comparator<DeviceTrajectory>() {
                                @Override
                                public int compare(DeviceTrajectory o1, DeviceTrajectory o2) {
                                    if (o1.getDeviceTrajectoryId() > o2.getDeviceTrajectoryId())
                                        return 1;
                                    else if (o1.getDeviceTrajectoryId() < o2.getDeviceTrajectoryId())
                                        return -1;
                                    return 0;
                                }
                            });
                        }
                    }
                }
                params.put("deviceMac", deviceMac);

//                getAnalogName(params);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return code;
        }
    }

    public void updateDevice(String deviceMac, int vloce) {
        Device device = deviceDao.findDeviceByMac(deviceMac);
        if (device != null) {
            device.setVlice2(vloce);
            deviceDao.update(device);
        }
    }

    public void update(Alerm alerm) {
        deviceAlermDao.update(alerm);
    }

    Handler.Callback mCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 1001) {
                String name = (String) msg.obj;
                if (!TextUtils.isEmpty(name)) {
                    ToastUtil.showShort(MQService.this, "设备" + name + "已离线");
                }
            } else if (msg.what == 101) {
                CountTimer countTimer = (CountTimer) msg.obj;
                if (countTimer!=null){
                    countTimer.start();
                }
            } else if (msg.what == 10001) {
                String ss = (String) msg.obj;
                ToastUtil.showShort(MQService.this, ss);
            } else if (msg.what == 10003) {
                int type = msg.arg1;
                setAlermDialog(type);
            } else if (msg.what == 10004) {
                String content = (String) msg.obj;
                sb.setLength(0);
                sb.append(content + ",");
                sb.append(content + ",");
                sb.append(content);
                starSpeech2(sb.toString());
            }
//            } else if (msg.what == 10005) {
//                Log.i("tttttttttttttttttttttt", "llllllllllllllllll");
//                Map<String, Object> params = (Map<String, Object>) msg.obj;
//                new LoadAsyncTask(MQService.this).execute(params);
//
//            }
            return true;
        }
    };
    Handler handler = new WeakRefHandler(mCallback);

    class AddOperationLogAsync extends BaseWeakAsyncTask<Map<String, Object>, Void, Integer, MQService> {

        public AddOperationLogAsync(MQService mqService) {
            super(mqService);
        }

        @Override
        protected Integer doInBackground(MQService mqService, Map<String, Object>... maps) {
            Map<String, Object> params = maps[0];
            String url = HttpUtils.ipAddress + "data/addOperationLog";
            String result = HttpUtils.requestPost(url, params);
            Log.i("AddOperationLogAsync", "-->" + result);
            return null;
        }

        @Override
        protected void onPostExecute(MQService mqService, Integer integer) {

        }
    }
}