package com.peihou.willgood2.device;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.peihou.willgood2.BaseActivity;
import com.peihou.willgood2.R;
import com.peihou.willgood2.custom.ChangeDialog;
import com.peihou.willgood2.database.dao.impl.DeviceDaoImpl;
import com.peihou.willgood2.esptouch.EspWifiAdminSimple;
import com.peihou.willgood2.esptouch.EsptouchTask;
import com.peihou.willgood2.esptouch.IEsptouchListener;
import com.peihou.willgood2.esptouch.IEsptouchResult;
import com.peihou.willgood2.esptouch.IEsptouchTask;
import com.peihou.willgood2.pojo.Device;
import com.peihou.willgood2.utils.IsChinese;
import com.peihou.willgood2.utils.ToastUtil;
import com.peihou.willgood2.utils.Utils;
import com.peihou.willgood2.utils.camera.CameraManager;
import com.peihou.willgood2.utils.decoding.CaptureActivityHandler;
import com.peihou.willgood2.utils.decoding.InactivityTimer;
import com.peihou.willgood2.utils.http.BaseWeakAsyncTask;
import com.peihou.willgood2.utils.http.HttpUtils;
import com.peihou.willgood2.utils.view.ViewfinderView;


import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 扫描二维码
 */
public class QRScannerActivity extends BaseActivity implements SurfaceHolder.Callback,EasyPermissions.PermissionCallbacks {

    @BindView(R.id.viewfinder_view) ViewfinderView viewfinderView;

    private CaptureActivityHandler handler;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;
    @BindView(R.id.rl_body4)
    RelativeLayout rl_body4;
    @BindView(R.id.rl_body3) RelativeLayout rl_body3;
    @BindView(R.id.tv_wifi)
    TextView tv_wifi;//wifi添加设备
    @BindView(R.id.tv_gprs) TextView tv_gprs;//gprs添加设备
    @BindView(R.id.tv_gprs1) TextView tv_gprs1;
    @BindView(R.id.et_name)
    EditText et_name;//gprs/wifi名称
    @BindView(R.id.et_pswd) EditText et_pswd;//WiFi密码
    @BindView(R.id.et_orignal_code) EditText et_orignal_code;//wifi状态下的初始码
    @BindView(R.id.tv_title) TextView tv_title;
    @BindView(R.id.img_book) ImageView img_book;
    @BindView(R.id.bt_add_finish) Button bt_add_finish;


    private DeviceDaoImpl deviceDao;
    private Map<String,Object> params=new HashMap<>();
    String devicePassword;
    private int userId;

    ImageView back;

    private boolean isBound=false;


    int addType=0;
    int type=-1;
    @Override
    public void initParms(Bundle parms) {
        type=parms.getInt("type");
    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_qrscanner;
    }

    SharedPreferences preferences;
    @Override
    public void initView(View view) {
        preferences=getSharedPreferences("userInfo",Context.MODE_PRIVATE);
        userId=preferences.getInt("userId",0);

        mWifiAdmin = new EspWifiAdminSimple(this);
        deviceDao=new DeviceDaoImpl(getApplicationContext());
        registerBroadcastReceiver();
        init();
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    private void init() {
        CameraManager.init(getApplication());
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
    }

    String gprs;
    String gprsCode;
    @Override
    protected void onStart() {
        super.onStart();
    }


    SurfaceView surfaceView;
    @Override
    protected void onResume() {
        super.onResume();
        permissionGrantedSuccess();
        surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

    }


    int deviceModel=0;
    String imei;
    String password;
    String orignalCode;
    @OnClick({R.id.back,R.id.img_book,R.id.rl_body3,R.id.rl_body4,R.id.bt_add_finish,R.id.tv_wifi,R.id.tv_gprs,R.id.et_name})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.img_book:
                if (addType==0){
                    addType=1;
                    rl_body3.setVisibility(View.VISIBLE);
                    rl_body4.setVisibility(View.GONE);
                }else if (addType==1){
                    addType=0;
                    rl_body3.setVisibility(View.GONE);
                    rl_body4.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.tv_wifi:
                if (deviceModel==0)
                    break;
                if (deviceModel==1){
                    gprs=et_name.getText().toString();
                    gprsCode=et_pswd.getText().toString();
                }
                tv_gprs1.setText("输入要连接的无线网络,同时为了设备安全,防止他人恶意添加");
                tv_wifi.setTextColor(Color.parseColor("#09c585"));
                tv_gprs.setTextColor(Color.parseColor("#646464"));
                et_name.setHint("WiFi扫描中");

                if (!TextUtils.isEmpty(wifiName)){
                    et_name.setText(wifiName);
                }
                if (!TextUtils.isEmpty(wifiPassword)){
                    et_pswd.setText(wifiPassword);
                }else {
                    et_pswd.setText("");
                }
                et_pswd.setHint("请输入WiFi密码");
                et_orignal_code.setHint("请输入初始码");
                et_orignal_code.setVisibility(View.VISIBLE);
                et_orignal_code.setBackgroundColor(Color.parseColor("#f7f7fa"));
                deviceModel=0;
                break;
            case R.id.et_name:
                if (deviceModel==0){
                    ToastUtil.showShort(this,"WiFi名称不可编辑");
                    break;
                }
                break;
            case R.id.tv_gprs:
                if (deviceModel==1)
                    break;
                if (deviceModel==0){
                    wifiPassword=et_pswd.getText().toString();
                }
                tv_gprs1.setText("输入要连接的IMEI,同时为了设备安全,防止他人恶意添加");
                tv_wifi.setTextColor(Color.parseColor("#646464"));
                tv_gprs.setTextColor(Color.parseColor("#09c585"));
                et_name.setHint("请输入IMEI号");
                et_pswd.setHint("请输入初始码");
                if (!TextUtils.isEmpty(gprs)){
                    et_name.setText(gprs);
                }else {
                    et_name.setText("");
                }
                if (!TextUtils.isEmpty(gprsCode)){
                    et_pswd.setText(gprsCode);
                }else {
                    et_pswd.setText("");
                }
                et_orignal_code.setHint("");
                et_orignal_code.setVisibility(View.GONE);
                deviceModel=1;
                break;
            case R.id.bt_add_finish:
                String name=et_name.getText().toString();
                if (deviceModel==0){
                    if (TextUtils.isEmpty(name)){
                        ToastUtil.showShort(this,"请输入IMEI号");
                        break;
                    }
                    String password=et_pswd.getText().toString();
                    if (TextUtils.isEmpty(password)){
                        ToastUtil.showShort(this,"请输入密码");
                        break;
                    }
                    orignalCode=et_orignal_code.getText().toString();
                    if (TextUtils.isEmpty(orignalCode)){
                        ToastUtil.showShort(this,"请输入初始码");
                        break;
                    }
                    try {
                        if (!TextUtils.isEmpty(name)) {
                            popupmenuWindow3();
                            isMatching=true;
                            wifiName=name;
                            new EsptouchAsyncTask3().execute(name, bSsid, password, "1");
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else if (deviceModel==1){
                    if (TextUtils.isEmpty(name)){
                        ToastUtil.showShort(this,"请输入IMEI号");
                        break;
                    }
                    String orignalCode=et_pswd.getText().toString();
                    if (TextUtils.isEmpty(orignalCode)){
                        ToastUtil.showShort(this,"请输入初始码");
                        break;
                    }
                    Map<String,Object> params=new HashMap<>();
                    params.put("deviceOnlyMac",name);
                    params.put("devicePassword",orignalCode);
                    params.put("deviceUserId",userId);
                    params.put("deviceModel",0);
                    try {
                        new AddDeivceAsync(QRScannerActivity.this).execute(params).get(5,TimeUnit.SECONDS);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    int insert=0;
    class AddDeivceAsync extends BaseWeakAsyncTask<Map<String,Object>,Void,Integer,QRScannerActivity> {

        public AddDeivceAsync(QRScannerActivity activity) {
            super(activity);
        }

        @Override
        protected Integer doInBackground(QRScannerActivity activity,Map<String, Object>... maps) {
            String url=HttpUtils.ipAddress+ "device/addDeviceByAPP";
            Map<String,Object> params=maps[0];
            int code=0;
            try {
                String result=HttpUtils.requestPost(url,params);
                Log.i("result","-->"+result);
                if (!TextUtils.isEmpty(result)){
                    JSONObject jsonObject=new JSONObject(result);
                    code=jsonObject.getInt("returnCode");
                    if (code==100){
                        JSONObject returnData=jsonObject.getJSONObject("returnData");
                        String s=returnData.toString();
                        Gson gson=new Gson();
                        device=gson.fromJson(s,Device.class);
                        String deviceMac=device.getDeviceOnlyMac();
                        Device device2=deviceDao.findDeviceByMac(deviceMac);
                        if (device2==null){
                            deviceDao.insert(device);
                            insert=1;
                        }else {
                            deviceDao.update(device);
                            insert=0;
                        }
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return code;
        }

        @Override
        protected void onPostExecute(QRScannerActivity activity,Integer code) {
            switch (code){
                case 100:
                    ToastUtil.showShort(QRScannerActivity.this,"添加成功");
                    Intent intent=new Intent();
                    intent.putExtra("device",device);
                    intent.putExtra("insert",insert);
                    setResult(100,intent);
                    finish();
                    break;
                case 10007:
                    ToastUtil.showShort(QRScannerActivity.this,"对不起您的设备初始码错误，请重置后重新添加");
                    break;
                default:
                    ToastUtil.showShort(QRScannerActivity.this,"添加失败");
                    break;
            }
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        if (popupWindow2!=null && popupWindow2.isShowing()){
            popupWindow2.dismiss();
        }

        if (mReceiver!=null){
            unregisterReceiver(mReceiver);
        }
        super.onDestroy();
    }

    String deviceMac;
    /**
     * 处理扫描结果
     */
    String deviceName;
    public void handleDecode(Result result) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        String resultString = result.getText();

        try {
            if (TextUtils.isEmpty(resultString)) {
                Toast.makeText(QRScannerActivity.this, "扫描失败!", Toast.LENGTH_SHORT).show();
            } else {
                String content = resultString;
                if (!TextUtils.isEmpty(content)) {
                    if (Utils.isBase64(content)){
                        content = new String(Base64.decode(content, Base64.DEFAULT));
                        if (content.startsWith("shared")){
                            String[] s2=content.split("/");
                            long deviceId=Long.parseLong(s2[1].substring(9));
                            int deviceTimer=Integer.parseInt(s2[2].substring(12));
                            int deviceLinked=Integer.parseInt(s2[3].substring(13));
                            int deviceSwitch=Integer.parseInt(s2[4].substring(13));
                            int deviceAlarm=Integer.parseInt(s2[5].substring(12));
                            int deviceMap=Integer.parseInt(s2[6].substring(10));
                            int deviceControl=Integer.parseInt(s2[7].substring(14));
                            int deviceAnalog=Integer.parseInt(s2[8].substring(13));

                            deviceMac=s2[9].substring(10);
                            deviceName=s2[10].substring(11);
                            Device device=deviceDao.findDeviceByMac2(deviceMac);
                            if (device!=null){
                                ToastUtil.showShort(QRScannerActivity.this,"自己的设备不能分享给自己");
                                finish();
                                return;
                            }
                            int devicePowerOff=0;//掉电记忆
                            int deviceLock=0;//互锁设置
                            int deviceLineSwitch=0;//线路开关
                            int deviceInching=0;//点动设置
                            params.clear();
                            if (1==deviceControl){
                                devicePowerOff=1;
                                deviceLock=1;
                                deviceInching=1;
                                deviceLineSwitch=1;
                            }
                            params.put("deviceId",deviceId);
                            params.put("deviceTimer",deviceTimer);
                            params.put("deviceLinked",deviceLinked);
                            params.put("deviceSwitch",deviceSwitch);
                            params.put("deviceAlarm",deviceAlarm);
                            params.put("deviceMap",deviceMap);
                            params.put("devicePowerOff",devicePowerOff);
                            params.put("deviceLock",deviceLock);
                            params.put("deviceLineSwitch",deviceLineSwitch);
                            params.put("deviceInching",deviceInching);
                            params.put("deviceAnalog",deviceAnalog);
                            params.put("deviceSharerId",userId);
                            changeDialog();
                        }
                    }else {
                        rl_body4.setVisibility(View.GONE);
                        rl_body3.setVisibility(View.VISIBLE);
                        et_name.setText(content);
                        tv_wifi.setTextColor(Color.parseColor("#646464"));
                        tv_gprs.setTextColor(Color.parseColor("#09c585"));
                        et_name.setHint("请输入IMEI号");
                        et_pswd.setHint("请输入初始码");
                        et_orignal_code.setHint("");
                        et_orignal_code.setVisibility(View.GONE);
                        deviceModel=1;
                        addType=1;
                    }
//                    Toast.makeText(QRScannerActivity.this, "扫描成功!", Toast.LENGTH_SHORT).show();
//                    Intent intent=new Intent(this,DeviceListActivity.class);
//                    intent.putExtra("type",type);
//                    startActivity(intent);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //自定义点动时间
    ChangeDialog dialog;
    private void changeDialog(){
        rl_body4.setVisibility(View.GONE);
        if (dialog!=null && dialog.isShowing()){
            return;
        }
        dialog=new ChangeDialog(this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMode(0);
        dialog.setTitle("初始码");
        dialog.setTips("请输入初始码");
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
                String content=dialog.getContent();
                if (TextUtils.isEmpty(content)){
                    ToastUtil.showShort(QRScannerActivity.this,"请输入初始码");
                }else {
                    devicePassword=content;
                    params.put("devicePassword",devicePassword);
                    new ShareAsyncTask(QRScannerActivity.this).execute(params);
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
    Device device;
    class ShareAsyncTask extends BaseWeakAsyncTask<Map<String,Object>,Void,Integer,Activity>{

        public ShareAsyncTask(Activity activity) {
            super(activity);
        }

        @Override
        protected Integer doInBackground(Activity activity, Map<String, Object>... maps) {
            int code=0;
            try {
                Map<String,Object> params=maps[0];
                String url=HttpUtils.ipAddress+"/device/shareDevice";
                String result=HttpUtils.requestPost(url,params);
                Log.i("result","-->"+result);
                if (!TextUtils.isEmpty(result)){
                    JSONObject jsonObject=new JSONObject(result);
                    code=jsonObject.getInt("returnCode");
                    if (code==100){
                        long deviceId= (long) params.get("deviceId");
                        int deviceTimer= (int) params.get("deviceTimer");
                        int deviceLinked= (int) params.get("deviceLinked");
                        int deviceSwitch= (int) params.get("deviceSwitch");
                        int deviceAlarm= (int) params.get("deviceAlarm");
                        int deviceMap= (int) params.get("deviceMap");
                        int devicePowerOff= (int) params.get("devicePowerOff");
                        int deviceLock= (int) params.get("deviceLock");
                        int deviceLineSwitch= (int) params.get("deviceLineSwitch");
                        int deviceInching= (int) params.get("deviceInching");
                        int deviceAnalog= (int) params.get("deviceAnalog");


                        device=deviceDao.findDeviceByMac(deviceMac);
                        if (device==null){
//                            device=new Device(deviceId,deviceMac,deviceMac,devicePassword,"share",deviceAlarm,deviceMap,deviceLineSwitch,deviceAnalog,deviceSwitch,devicePowerOff,deviceInching)

                            device=new Device();
                            device.setDeviceId(deviceId);
                            device.setShare("share");
                            device.setDeviceOnlyMac(deviceMac);
                            device.setDevicePassword(devicePassword);
                            device.setDeviceAuthority_Alarm(deviceAlarm);
                            device.setDeviceAuthority_Analog(deviceAnalog);
                            device.setDeviceAuthority_Inching(deviceInching);
                            device.setDeviceAuthority_LineSwitch(deviceLineSwitch);
                            device.setDeviceAuthority_Lock(deviceLock);
                            device.setDeviceAuthority_Map(deviceMap);
                            device.setDeviceAuthority_Timer(deviceTimer);
                            device.setDeviceAuthority_Poweroff(devicePowerOff);
                            device.setDeviceAuthority_Switch(deviceSwitch);
                            device.setDeviceAuthority_Linked(deviceLinked);
                            device.setDeviceOnlyMac(deviceMac);
                            device.setDeviceName(deviceName);
                            deviceDao.insert(device);
                            insert=1;
                        }else {

                            device.setShare("share");
                            device.setDevicePassword(devicePassword);
                            device.setDeviceAuthority_Alarm(deviceAlarm);
                            device.setDeviceAuthority_Analog(deviceAnalog);
                            device.setDeviceAuthority_Inching(deviceInching);
                            device.setDeviceAuthority_LineSwitch(deviceLineSwitch);
                            device.setDeviceAuthority_Lock(deviceLock);
                            device.setDeviceAuthority_Map(deviceMap);
                            device.setDeviceAuthority_Timer(deviceTimer);
                            device.setDeviceAuthority_Poweroff(devicePowerOff);
                            device.setDeviceAuthority_Switch(deviceSwitch);
                            device.setDeviceAuthority_Linked(deviceLinked);
                            device.setDeviceOnlyMac(deviceMac);
                            device.setDeviceName(deviceName);
                            insert=0;
                            deviceDao.update(device);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return code;
        }

        @Override
        protected void onPostExecute(Activity activity, Integer integer) {
            if (integer==100){
//                Intent intent=new Intent(QRScannerActivity.this,DeviceListActivity.class);
//                startActivity(intent);
                Intent intent=new Intent();
                intent.putExtra("device",device);
                intent.putExtra("insert",insert);
                setResult(100,intent);
                finish();
            }else {
                ToastUtil.showShort(QRScannerActivity.this,"添加失败");
//                finish();
            }
        }
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
//            handler = new CaptureActivityHandler(QRScannerActivity.this, decodeFormats, characterSet);
            handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final MediaPlayer.OnCompletionListener beepListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };
    private static final int RC_CAMERA_AND_LOCATION=0;
    private boolean isNeedCheck=true;
    @AfterPermissionGranted(RC_CAMERA_AND_LOCATION)
    private void permissionGrantedSuccess(){
        String[] perms = {Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(this, perms)) {

        } else {
//             没有申请过权限，现在去申请
            if (isNeedCheck){
                EasyPermissions.requestPermissions(this, getString(R.string.camer),
                        RC_CAMERA_AND_LOCATION, perms);
            }
        }
    }
    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 把执行结果的操作给EasyPermissions
        System.out.println(requestCode);
        if (isNeedCheck){
            EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog
                    .Builder(this)
                    .setTitle("提示")
                    .setRationale("请点击\"设置\"打开相机权限。")
                    .setPositiveButton("设置")
                    .setNegativeButton("取消")
                    .build()
                    .show();
            isNeedCheck=false;
        }
    }

    private IEsptouchListener myListener = new IEsptouchListener() {

        @Override
        public void onEsptouchResultAdded(final IEsptouchResult result) {
            onEsptoucResultAddedPerform(result);
        }
    };
    private void onEsptoucResultAddedPerform(final IEsptouchResult result) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                String text = result.getBssid() + " is connected to the wifi";
//                Toast.makeText(AddDeviceActivity.this, text,
//                        Toast.LENGTH_LONG).show();
            }

        });
    }


    private static final String TAG = "Esptouch";
    private EspWifiAdminSimple mWifiAdmin;

    private IEsptouchTask mEsptouchTask;

    private class EsptouchAsyncTask3 extends AsyncTask<String, Void, List<IEsptouchResult>> {


        // without the lock, if the user tap confirm and cancel quickly enough,
        // the bug will arise. the reason is follows:
        // 0. task is starting created, but not finished
        // 1. the task is cancel for the task hasn't been created, it do nothing
        // 2. task is created
        // 3. Oops, the task should be cancelled, but it is running
        private final Object mLock = new Object();

        @Override
        protected void onPreExecute() {
//            popupWindow();
//            addDeviceDialog=new AddDeviceDialog(AddDeviceActivity.this);
//            addDeviceDialog.setCanceledOnTouchOutside(false);
//            addDeviceDialog.show();
//            mProgressDialog = new ProgressDialog(AddDeviceActivity.this);
//            mProgressDialog.setMessage("正在配置, 请耐心等待...");
//            mProgressDialog.setCanceledOnTouchOutside(false);
//            mProgressDialog.show();
//            CountTimer countTimer = new CountTimer(30000, 1000);
//            countTimer.start();
        }

        @Override
        protected List<IEsptouchResult> doInBackground(String... params) {
            int taskResultCount = -1;
            synchronized (mLock) {
                // !!!NOTICE
                String apSsid = mWifiAdmin.getWifiConnectedSsidAscii(params[0]);
                String apBssid = params[1];
                String apPassword = params[2];
                String taskResultCountStr = params[3];
                taskResultCount = Integer.parseInt(taskResultCountStr);
                mEsptouchTask = new EsptouchTask(apSsid, apBssid, apPassword, QRScannerActivity.this);
                mEsptouchTask.setEsptouchListener(myListener);
            }
            List<IEsptouchResult> resultList = mEsptouchTask.executeForResults(taskResultCount);
            return resultList;
        }

        @Override
        protected void onPostExecute(List<IEsptouchResult> result) {
            IEsptouchResult firstResult = result.get(0);
            // check whether the task is cancelled and no results received
            if (!firstResult.isCancelled()) {
                int count = 0;
                // max results to be displayed, if it is more than maxDisplayCount,
                // just show the count of redundant ones
                final int maxDisplayCount = 5;
                // the task received some results including cancelled while
                // executing before receiving enough results
                if (firstResult.isSuc()) {
                    StringBuilder sb = new StringBuilder();
                    try {
                        Log.i("IEsptouchResult", "-->" + result.size());
                        for (IEsptouchResult resultInList : result) {
                            //                String ssid=et_ssid.getText().toString();
                            String ssid = resultInList.getBssid();
                            sb.append("配置成功" + ssid);
                            if (!TextUtils.isEmpty(ssid)) {
                                Map<String,Object> params=new HashMap<>();
                                params.put("deviceOnlyMac",ssid);
                                params.put("devicePassword",orignalCode);
                                params.put("deviceUserId",userId);
                                params.put("deviceModel",1);
                                try {
                                    new AddDeivceAsync(QRScannerActivity.this).execute(params).get(5,TimeUnit.SECONDS);
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                                break;
                            }
                            count++;
                            if (count >= maxDisplayCount) {
                                break;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (count < result.size()) {
                        sb.append("\nthere's " + (result.size() - count)
                                + " more result(s) without showing\n");
                    }
                } else {
                    if (popupWindow2 != null && popupWindow2.isShowing()) {
                        isMatching=false;
                        wifiName="";
                        if (gifDrawable != null && gifDrawable.isPlaying()) {
                            gifDrawable.stop();

                            if (et_name != null) {
                                et_name.setEnabled(true);
                            }
                            if (et_pswd != null) {
                                et_pswd.setEnabled(true);
                            }
                            if (bt_add_finish != null) {
                                bt_add_finish.setEnabled(true);
                                ToastUtil.showShort(QRScannerActivity.this, "配置失败");
                            }

                            if (mEsptouchTask != null) {
                                mEsptouchTask.interrupt();
                            }
                        }
                        popupWindow2.dismiss();
                        backgroundAlpha(1f);

                    }
                }
            }
        }
    }


    private boolean isMatching=false;
    private String wifiName;
    private String wifiPassword;
    private boolean mReceiverRegistered = false;
    private boolean isSDKAtLeastP() {
        return Build.VERSION.SDK_INT >= 28;
    }
    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        if (isSDKAtLeastP()) {
            filter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
        }
        registerReceiver(mReceiver, filter);
        mReceiverRegistered = true;
    }
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) {
                return;
            }
            WifiManager wifiManager = (WifiManager) context.getApplicationContext()
                    .getSystemService(WIFI_SERVICE);
            assert wifiManager != null;
            switch (action) {
                case WifiManager.NETWORK_STATE_CHANGED_ACTION:
                    if (intent.hasExtra(WifiManager.EXTRA_WIFI_INFO)) {
                        wifiInfo = intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
                    } else {
                        wifiInfo = wifiManager.getConnectionInfo();
                    }
                    onWifiChanged(wifiInfo);
                    break;
                case LocationManager.PROVIDERS_CHANGED_ACTION:
                    wifiInfo=wifiManager.getConnectionInfo();
                    onWifiChanged(wifiInfo);
                    break;
            }
        }
    };
    String bSsid="";
    String wifiPswd="";
    WifiInfo wifiInfo;

    private void onWifiChanged(WifiInfo info) {
        if (info == null) {
            if (deviceModel==0){
                et_name.setText("");
                et_pswd.setText("");
                ToastUtil.showShort(QRScannerActivity.this,"WiFi已中断，请连接WiFi重新配置");
                if (mEsptouchTask != null) {
                    mEsptouchTask.interrupt();
                }
                if (popupWindow2 != null && popupWindow2.isShowing()) {
                    if (gifDrawable != null && gifDrawable.isRunning()) {
                        gifDrawable.stop();
                    }
                    et_name.setEnabled(true);
                    et_pswd.setEnabled(true);
                    et_orignal_code.setEnabled(true);
                    popupWindow2.dismiss();
                    backgroundAlpha(1f);
                }
            }
        } else {
            String apSsid = info.getSSID();
            bSsid=info.getBSSID();
            if (apSsid.startsWith("\"") && apSsid.endsWith("\"")) {
                apSsid = apSsid.substring(1, apSsid.length() - 1);
            }
            SharedPreferences wifi = getSharedPreferences("wifi", MODE_PRIVATE);
            if (wifi.contains(apSsid)) {
                wifiName=apSsid;
                String pswd = wifi.getString(apSsid, "");
                wifiPswd=pswd;
                et_name.setText(wifiPswd);
            } else {
                if ("<unknown ssid>".equals(apSsid)){
                    wifiName="";
                }else {
                    wifiName=apSsid;
                }
                wifiPswd="";
            }
            if (!TextUtils.isEmpty(wifiName)) {
                et_name.setText(wifiName);
                if (apSsid.contains("+") || apSsid.contains("/") ||apSsid.contains("#")) {
                    et_name.setText("");
                    wifiName="";
                    ToastUtil.showShort(QRScannerActivity.this, "WiFi名称为不含有+/#特殊符号的英文");
                }else {
                    char[] chars = apSsid.toCharArray();
                    for (char c : chars) {
                        if (IsChinese.isChinese(c)) {
                            ToastUtil.showShort(QRScannerActivity.this, "WiFi名称不能是中文");
                            wifiName="";
                            wifiPswd="";
                            break;
                        }
                    }
                }
            } else if (TextUtils.isEmpty(wifiName)){
                wifiName="";
                wifiPswd="";
            }
            if (deviceModel==0){
                et_pswd.setText(wifiName);
                et_pswd.setText(wifiPswd);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                int frequence = info.getFrequency();
                if (frequence > 4900 && frequence < 5900) {
                    // Connected 5G wifi. Device does not support 5G
                    wifiName="";
                    wifiPswd="";
                    if (addType==0){
                        et_name.setText("");
                        et_name.setHint("不支持5G WiFi");
                        et_pswd.setText("");
                    }
                }
            }
            if (isMatching && !TextUtils.isEmpty(wifiName) && !wifiName.equals(apSsid)){
                isMatching=false;
                wifiName="";
                ToastUtil.showShort(QRScannerActivity.this,"WiFi已切换,请重新配置");
                if (mEsptouchTask != null) {
                    mEsptouchTask.interrupt();
                }
                if (popupWindow2 != null && popupWindow2.isShowing()) {
                    if (gifDrawable != null && gifDrawable.isRunning()) {
                        gifDrawable.stop();
                    }
                    et_name.setEnabled(true);
                    et_pswd.setFocusable(false);
                    et_orignal_code.setEnabled(true);
                    bt_add_finish.setEnabled(true);
                    popupWindow2.dismiss();
                    backgroundAlpha(1f);
                }
            }
        }
    }
    private PopupWindow popupWindow2;
    GifImageView image_heater_help;
    GifDrawable gifDrawable;
    public void popupmenuWindow3() {
        if (popupWindow2 != null && popupWindow2.isShowing()) {
            return;
        }
        View view = View.inflate(this, R.layout.popup_help2, null);
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        image_heater_help = (GifImageView) view.findViewById(R.id.image_heater_help);
        try {
            gifDrawable = new GifDrawable(getResources(), R.mipmap.touxiang3);
        } catch (Exception e) {
            e.printStackTrace();
        }
        image_heater_help.setVisibility(View.VISIBLE);
        if (gifDrawable != null) {
            gifDrawable.start();
            image_heater_help.setImageDrawable(gifDrawable);
        }
        if (popupWindow2==null)
            popupWindow2 = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        //添加弹出、弹入的动画
        popupWindow2.setAnimationStyle(R.style.Popupwindow);
        backgroundAlpha(0.6f);
        popupWindow2.setFocusable(false);
        popupWindow2.setOutsideTouchable(false);
//        ColorDrawable dw = new ColorDrawable(0x30000000);
//        popupWindow.setBackgroundDrawable(dw);
//        popupWindow2.showAsDropDown(et_wifi, 0, -20);
        popupWindow2.showAtLocation(et_name, Gravity.CENTER, 0, 0);
        //添加按键事件监听
    }
}