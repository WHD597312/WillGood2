package com.peihou.willgood2.device;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Bundle;

import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.peihou.willgood2.CheckPermissionsActivity;
import com.peihou.willgood2.custom.DialogLoad;
import com.peihou.willgood2.daemon.DaemonHolder;
import com.peihou.willgood2.MyApplication;
import com.peihou.willgood2.R;
import com.peihou.willgood2.custom.ChangeDialog;
import com.peihou.willgood2.database.dao.impl.DeviceDaoImpl;
import com.peihou.willgood2.database.dao.impl.DeviceLineDaoImpl;
import com.peihou.willgood2.device.menu.AlermActivity;
import com.peihou.willgood2.device.menu.InterLockActivity;
import com.peihou.willgood2.device.menu.JogSetActivity;
import com.peihou.willgood2.device.menu.LinkedControlActivity;
import com.peihou.willgood2.device.menu.MoniCheckActivity;
import com.peihou.willgood2.device.menu.PowerLostMomoryActivity;
import com.peihou.willgood2.device.menu.RS485Activity;
import com.peihou.willgood2.device.menu.SwichCheckActivity;
import com.peihou.willgood2.device.menu.TimerTaskActivity;
import com.peihou.willgood2.location.LocationActivity;
import com.peihou.willgood2.pojo.Device;
import com.peihou.willgood2.pojo.DeviceLines;
import com.peihou.willgood2.pojo.DeviceMenu;
import com.peihou.willgood2.pojo.DeviceTrajectory;
import com.peihou.willgood2.pojo.Line2;
import com.peihou.willgood2.pojo.SwtichState;
import com.peihou.willgood2.service.MQService;
import com.peihou.willgood2.utils.DisplayUtil;
import com.peihou.willgood2.utils.TenTwoUtil;
import com.peihou.willgood2.utils.ToastUtil;
import com.peihou.willgood2.utils.WeakRefHandler;
import com.peihou.willgood2.utils.http.BaseWeakAsyncTask;
import com.peihou.willgood2.utils.http.HttpUtils;

import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import me.jessyan.autosize.internal.CustomAdapt;

public class DeviceItemActivity extends CheckPermissionsActivity implements View.OnTouchListener, CustomAdapt {

    Unbinder unbinder;
    /**
     * 线路以横向列表排列的控件
     */
    @BindView(R.id.img_switch2)
    ImageView img_switch2;//大开关2
    @BindView(R.id.img_all_close2)
    ImageView img_all_close2;//多关按钮2
    @BindView(R.id.img_all_open2)
    ImageView img_all_open2;//多开按钮2
    @BindView(R.id.img_all_jog2)
    ImageView img_all_jog2;//多点动2
    @BindView(R.id.tv_temp2)
    TextView tv_temp2;//温度
    @BindView(R.id.tv_hum2)
    TextView tv_hum2;//湿度
    @BindView(R.id.tv_state2)
    TextView tv_state2;//电流/电压/功率
    @BindView(R.id.tv_title)
    TextView tv_title;//标题
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    List<Line2> list = new ArrayList<>();
    @BindView(R.id.listview)
    ListView listview;
    List<DeviceMenu> menus = new ArrayList<>();//设备菜单
    MenuAdapter menuAdapter;//设备页面菜单
    @BindView(R.id.rv_lines)
    RecyclerView rv_lines;//设备线路以表格形式展示
    private LinesAadpter linesAadpter;
    @BindView(R.id.rl_body2)
    RelativeLayout rl_body2;//以横向列表布局的线路布局
    @BindView(R.id.tv_switch2)
    TextView tv_switch2;//以横向列表布局的线路开关
    int choicedLinesGrid = 0;//以网格排列的已多选的线路
    int choicedLines = 0;//以先行排列的多选的线路
    Map<String, Object> params = new HashMap<>();
    private DeviceDaoImpl deviceDao;
    private DeviceLineDaoImpl deviceLineDao;
    MyApplication application;
    long deviceId;
    Device device;
    String deviceMac;
    int mcuVersion;
    String topicName;
    private boolean bind;
    MessageReceiver receiver;
    String share;
    int deviceAuthority_LineSwitch;
    String search;
    private int click = 0;
    int init = 0;
    int userId;
    String lines;
    String checkLine;
    Map<Integer, Integer> mapChoiceLines = new HashMap<>();
    int width;
    int heigth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        application = (MyApplication) getApplication();
        application.addActivity(this);
        setContentView(R.layout.activity_device_item);
        DisplayMetrics dm = new DisplayMetrics();
        heigth = dm.heightPixels;
        width = dm.widthPixels;
        unbinder = ButterKnife.bind(this);

        initWindows();
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        //设置左上角的图标响应
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawer.setDrawerListener(toggle);
        toggle.syncState();
        toggle.setDrawerIndicatorEnabled(false);//修改DrawerLayout侧滑菜单图标
        //这样修改了图标，但是这个图标的点击事件会消失，点击图标不能打开侧边栏
        //所以还要加上如下代码
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(GravityCompat.START);
            }
        });

        init = 1;
        deviceDao = new DeviceDaoImpl(getApplicationContext());

        Intent intent = getIntent();
        userId = intent.getIntExtra("userId", 0);
        String name = intent.getStringExtra("name");
        deviceId = intent.getLongExtra("deviceId", 0);
        search = intent.getStringExtra("search");
        tv_title.setText(name);
        device = deviceDao.findDeviceById(deviceId);
        plMemory = device.getPlMemory();
        mcuVersion = device.getMcuVersion();
        deviceMac = device.getDeviceOnlyMac();
        topicName = "qjjc/gateway/" + deviceMac + "/server_to_client";
//        topicName = "qjjc/gateway/" + deviceMac + "/client_to_server";
        Intent service = new Intent(this, MQService.class);
        bind = bindService(service, connection, Context.BIND_AUTO_CREATE);
        IntentFilter intentFilter = new IntentFilter("DeviceItemActivity");
        intentFilter.addAction("offline");
        receiver = new MessageReceiver();
        registerReceiver(receiver, intentFilter);
        deviceLineDao = new DeviceLineDaoImpl(getApplicationContext());
        try {
            params.put("deviceId", deviceId);
            new GetDeviceLineAsync(DeviceItemActivity.this).execute(params).get(5, TimeUnit.SECONDS);

        } catch (Exception e) {
            e.printStackTrace();
        }

        img_all_close2.setOnTouchListener(this);
        img_all_open2.setOnTouchListener(this);
        img_all_jog2.setOnTouchListener(this);

        list = deviceLineDao.findDeviceOnlineLines(deviceId);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
//        rv_lines.setPadding(25,0,25,0);
//        rv_lines.addItemDecoration(new SpacesItemDecoration(25));
        rv_lines.setLayoutManager(layoutManager);
        linesAadpter = new LinesAadpter(list, this);
        rv_lines.setAdapter(linesAadpter);
//        setMode(device);
        share = device.getShare();
        deviceAuthority_LineSwitch = device.getDeviceAuthority_LineSwitch();
//        adapter2=new MyAdapter2(list,this);
//        grid_lines2.setAdapter(adapter2);

        menus.add(new DeviceMenu(0, "定时控制", R.mipmap.img_menu_timer));
        menus.add(new DeviceMenu(1, "联动控制", R.mipmap.img_menu_link));
        menus.add(new DeviceMenu(2, "开关量检测", R.mipmap.img_menu_switch));
        menus.add(new DeviceMenu(3, "报警设置", R.mipmap.img_menu_alerm));
        menus.add(new DeviceMenu(4, "地图定位", R.mipmap.img_menu_location));
        menus.add(new DeviceMenu(5, "模拟量检测", R.mipmap.img_menu_moni));
        menus.add(new DeviceMenu(6, "互锁模式", R.mipmap.img_menu_hs));
        menus.add(new DeviceMenu(7, "点动控制", R.mipmap.img_menu_jog));
        menus.add(new DeviceMenu(8, "掉电记忆", R.mipmap.img_menu_pd));
        menus.add(new DeviceMenu(9, "485接口", R.mipmap.img_menu_485));
        menus.add(new DeviceMenu(10, "控制语音", R.mipmap.img_menu_voice));

        Collections.sort(menus, new Comparator<DeviceMenu>() {
            @Override
            public int compare(DeviceMenu o1, DeviceMenu o2) {
                if (o1.getI() > o2.getI()) {
                    return 1;
                } else if (o1.getI() < o2.getI()) {
                    return -1;
                }
                return 0;
            }
        });
        menuAdapter = new MenuAdapter(menus, this);
        listview.setAdapter(menuAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String share = device.getShare();
                drawer.closeDrawer(Gravity.LEFT);
                switch (position) {
                    case 0:
                        int deviceAuthority_Timer = device.getDeviceAuthority_Timer();
                        if ("share".equals(share) && deviceAuthority_Timer == 0) {
                            ToastUtil.showShort(DeviceItemActivity.this, "你没有定时控制的权限");
                            break;
                        }
                        Intent timeIntent = new Intent(DeviceItemActivity.this, TimerTaskActivity.class);
                        timeIntent.putExtra("deviceId", deviceId);
                        timeIntent.putExtra("deviceMac", deviceMac);
                        timeIntent.putExtra("online", device.getOnline());
                        startActivityForResult(timeIntent, 1000);
                        break;
                    case 1:
                        int deviceAuthority_Linked = device.getDeviceAuthority_Linked();
                        if ("share".equals(share) && deviceAuthority_Linked == 0) {
                            ToastUtil.showShort(DeviceItemActivity.this, "你没有联动控制的权限");
                            break;
                        }
                        Intent linkIntent = new Intent(DeviceItemActivity.this, LinkedControlActivity.class);
                        linkIntent.putExtra("deviceId", deviceId);
                        linkIntent.putExtra("deviceMac", deviceMac);
                        linkIntent.putExtra("online", device.getOnline());
                        startActivityForResult(linkIntent, 1001);
                        break;
                    case 2:
                        int deviceAuthority_Switch = device.getDeviceAuthority_Switch();
                        if ("share".equals(share) && deviceAuthority_Switch == 0) {
                            ToastUtil.showShort(DeviceItemActivity.this, "你没有联动开关量检测的权限");
                            break;
                        }

                        Intent switchIntent = new Intent(DeviceItemActivity.this, SwichCheckActivity.class);
                        switchIntent.putExtra("deviceMac", deviceMac);
                        switchIntent.putExtra("deviceId", deviceId);
                        if (mqService != null) {
                            List<SwtichState> swtichStates = mqService.getSwitchName();
                            switchIntent.putExtra("swtichStates", (Serializable) swtichStates);
                        }

                        startActivity(switchIntent);
                        break;
                    case 3:
                        int deviceAuthority_Alarm = device.getDeviceAuthority_Alarm();
                        if ("share".equals(share) && deviceAuthority_Alarm == 0) {
                            ToastUtil.showShort(DeviceItemActivity.this, "你没有报警设置的权限");
                            break;
                        }
                        Intent alermIntent = new Intent(DeviceItemActivity.this, AlermActivity.class);
                        alermIntent.putExtra("deviceMac", deviceMac);
                        alermIntent.putExtra("deviceId", deviceId);
                        alermIntent.putExtra("mcuVersion", mcuVersion);
                        alermIntent.putExtra("online", device.getOnline());
                        startActivity(alermIntent);
                        break;
                    case 4:
                        int deviceAuthority_Map = device.getDeviceAuthority_Map();
                        if ("share".equals(share) && deviceAuthority_Map == 0) {
                            ToastUtil.showShort(DeviceItemActivity.this, "你没有地图定位的权限");
                            break;
                        }
                        Intent locationIntent = new Intent(DeviceItemActivity.this, LocationActivity.class);
                        locationIntent.putExtra("deviceMac", deviceMac);
                        locationIntent.putExtra("deviceId", deviceId);
                        locationIntent.putExtra("mcuVersion", mcuVersion);
                        locationIntent.putExtra("location",device.getLocation());
                        if (mqService != null) {
                            List<DeviceTrajectory> deviceTrajectories = mqService.getDeviceTrajectory();
                            locationIntent.putExtra("deviceTrajectories", (Serializable) deviceTrajectories);
                        }
                        startActivity(locationIntent);
                        break;
                    case 5:
                        int deviceAuthority_Analog = device.getDeviceAuthority_Analog();
                        if ("share".equals(share) && deviceAuthority_Analog == 0) {
                            ToastUtil.showShort(DeviceItemActivity.this, "你没有模拟量检测的权限");
                            break;
                        }
                        Intent moniIntent = new Intent(DeviceItemActivity.this, MoniCheckActivity.class);
                        moniIntent.putExtra("deviceMac", deviceMac);
                        moniIntent.putExtra("deviceId", deviceId);
                        startActivity(moniIntent);
                        break;
                    case 6:
                        int deviceAuthority_Lock = device.getDeviceAuthority_Lock();
                        if ("share".equals(share) && deviceAuthority_Lock == 0) {
                            ToastUtil.showShort(DeviceItemActivity.this, "你没有互锁模式的权限");
                            break;
                        }
                        Intent lockIntent = new Intent(DeviceItemActivity.this, InterLockActivity.class);
                        lockIntent.putExtra("deviceMac", deviceMac);
                        lockIntent.putExtra("deviceId", deviceId);
                        lockIntent.putExtra("online", device.getOnline());
                        startActivity(lockIntent);
                        break;
                    case 7:
                        int deviceAuthority_Inching = device.getDeviceAuthority_Inching();
                        if ("share".equals(share) && deviceAuthority_Inching == 0) {
                            ToastUtil.showShort(DeviceItemActivity.this, "你没有点动控制的权限");
                            break;
                        }
                        Intent jogIntent = new Intent(DeviceItemActivity.this, JogSetActivity.class);
                        jogIntent.putExtra("deviceId", deviceId);
                        jogIntent.putExtra("deviceMac", deviceMac);
                        jogIntent.putExtra("jog", device.getLineJog());
                        jogIntent.putExtra("mcuVersion", mcuVersion);
                        jogIntent.putExtra("online", device.getOnline());
                        startActivityForResult(jogIntent, 7000);
                        break;
                    case 8:
                        int deviceAuthority_Poweroff = device.getDeviceAuthority_Poweroff();
                        if ("share".equals(share) && deviceAuthority_Poweroff == 0) {
                            ToastUtil.showShort(DeviceItemActivity.this, "你没有掉电记忆的权限");
                            break;
                        }
                        Intent plIntent = new Intent(DeviceItemActivity.this, PowerLostMomoryActivity.class);
                        plIntent.putExtra("plMemory", plMemory);
                        plIntent.putExtra("deviceMac",deviceMac);
                        plIntent.putExtra("type", 1);
                        plIntent.putExtra("device",device);
                        startActivityForResult(plIntent, 8000);
                        break;
                    case 9:
                        Intent rs485Intent = new Intent(DeviceItemActivity.this, RS485Activity.class);
                        String res85 = device.getRe485();
                        rs485Intent.putExtra("res485", res85);
                        rs485Intent.putExtra("deviceMac", deviceMac);
                        startActivity(rs485Intent);
                        break;
                    case 10:
                        Intent voiceIntent = new Intent(DeviceItemActivity.this, PowerLostMomoryActivity.class);
                        voiceIntent.putExtra("type", 2);
                        voiceIntent.putExtra("voice", device.getVlice2());
                        voiceIntent.putExtra("deviceMac", device.getDeviceOnlyMac());
                        voiceIntent.putExtra("device",device);
                        startActivityForResult(voiceIntent, 9000);
                        break;
                }
            }
        });
    }

    CountTimer countTimer = new CountTimer(2000, 1000);

    @Override
    public boolean isBaseOnWidth() {
        return false;
    }

    @Override
    public float getSizeInDp() {
        return 640;
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
        }

        @Override
        public void onFinish() {
            if (dialogLoad != null && dialogLoad.isShowing()) {
                if (load == 1) {
                    if (mqService != null) {
                        mqService.connectMqtt(deviceMac);
                    }
                }
                load = 0;
                dialogLoad.dismiss();
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
//
//        //添加弹出、弹入的动画
//        popupWindow2.setAnimationStyle(R.style.Popupwindow);
//        popupWindow2.setFocusable(false);
//        popupWindow2.setOutsideTouchable(false);
//        backgroundAlpha(0.5f);
//        popupWindow2.setOnDismissListener(new PopupWindow.OnDismissListener() {
//            @Override
//            public void onDismiss() {
//                backgroundAlpha(1.0f);
//            }
//        });
////        ColorDrawable dw = new ColorDrawable(0x30000000);
////        popupWindow.setBackgroundDrawable(dw);
////        popupWindow2.showAsDropDown(et_wifi, 0, -20);
//        popupWindow2.showAtLocation(rv_lines, Gravity.CENTER, 0, 0);
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


    List<Line2> list2 = new ArrayList<>();
    private void setMode(Device device) {
        try {
            choicedLines = 0;
            list.clear();
            list2.clear();

            if (mqService != null) {
                list2 = mqService.getDeviceOnlineLiens(deviceMac);
            }

            Collections.sort(list2, new Comparator<Line2>() {
                @Override
                public int compare(Line2 o1, Line2 o2) {
                    if (o1.getDeviceLineNum()>o2.getDeviceLineNum()){
                        return 1;
                    }else if (o1.getDeviceLineNum()<o2.getDeviceLineNum()){
                        return -1;
                    }
                    return 0;
                }
            });
            Class<Device> clazz = (Class<Device>) Class.forName("com.peihou.willgood2.pojo.Device");
            for (int i = 0; i < list2.size(); i++) {
                Line2 line21 = list2.get(i);
                line21.setClick2(0);
                int deviceLineNum = line21.getDeviceLineNum();
                if (deviceLineNum == 1) {
                    double line0 = device.getLine();
                    line21.setSeconds(line0);
                    list2.set(i, line21);
                    if (init == 1) {
                        line21.setClick2(0);
                    }
                } else {
                    Method method = clazz.getDeclaredMethod("getLine" + deviceLineNum);
                    double seconds = (double) method.invoke(device);
                    line21.setSeconds(seconds);
                    if (init == 1) {
                        line21.setClick2(0);
                    }
                    list2.set(i, line21);
                }
            }
            if (init == 1) {
                init = 0;
            }
            list.addAll(list2);
            linesAadpter.notifyDataSetChanged();
            double temp = device.getTemp();
            double hum = device.getHum();
            double current = device.getCurrent();
            double votage = device.getVotage();

            String s = "" + String.format("%.1f", temp);
            tv_temp2.setText(s + "℃");
            String s3 = "" + String.format("%.1f", hum);
            tv_hum2.setText(s3 + "%");

            String s5 = "" + String.format("%.1f", current);
            String s7 = "" + String.format("%.1f", votage);
            double power = current * votage;
            String s9 = "" + String.format("%.1f", power);
            tv_state2.setText(s5 + "A/" + s7 + "V/" + s9 + "W");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
//        updateLines(deviceMac);
        int a=10;
        int b=0;
        if (!TextUtils.isEmpty(search)) {
            startActivity(new Intent(this, DeviceListActivity.class));
        } else {
            setResult(1002);
            super.onBackPressed();
        }
    }

//    public void updateLines(String deviceMac) {
//        List<Line2> list = deviceLineDao.findDeviceLines(deviceMac);
//        for (int i = 0; i < list.size(); i++) {
//            Line2 line2 = list.get(i);
//            line2.setOnClick(false);
//            line2.setClick(0);
//            line2.setClick2(0);
//            list.set(i, line2);
//        }
//    }

    private void initWindows() {
        Window window = getWindow();
        int color = Color.parseColor("#E0E0E0");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //设置状态栏颜色
            window.setStatusBarColor(color);
//            //设置导航栏颜色
//            window.setNavigationBarColor(color);
            ViewGroup contentView = ((ViewGroup) findViewById(android.R.id.content));
            View childAt = contentView.getChildAt(0);
            if (childAt != null) {
                childAt.setFitsSystemWindows(false);
            }

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            //设置contentview为fitsSystemWindows
            ViewGroup contentView = (ViewGroup) findViewById(android.R.id.content);
            View childAt = contentView.getChildAt(0);
            if (childAt != null) {
                childAt.setFitsSystemWindows(true);
            }
            //给statusbar着色
            View view = new View(this);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(this)));
            view.setBackgroundColor(color);
            contentView.addView(view);
        }
    }

    /**
     * 获取状态栏高度
     *
     * @param context context
     * @return 状态栏高度
     */
    private int getStatusBarHeight(Context context) {
        // 获得状态栏高度
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        int sss = DisplayUtil.px2dip(DeviceItemActivity.this, resourceId);
        Log.i("resourceId", "-->" + resourceId + "#####" + sss);
        return DisplayUtil.px2dip(DeviceItemActivity.this, resourceId);
    }

    public static boolean running = false;

    int load = 0;

    @Override
    protected void onStart() {
        super.onStart();
        DaemonHolder.startService();
//        boolean running2 = ServiceUtils.isServiceRunning(this, "com.peihou.willgood2.service.MQService");
//        if (!running2){
//            Intent intent=new Intent(this, MQService.class);
//            intent.putExtra("restart",1);
//            startService(intent);
////            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
////                startForegroundService(intent);
////            }else {
////                startService(intent);
////            }
//        }
        if (returnData==0 &&!running && mqService != null) {
            device=deviceDao.findDeviceByMac(deviceMac);
            setMode(device);
        }
        returnData=0;
        running = true;
        plMemory = device.getPlMemory();

    }

    @Override
    protected void onStop() {
        super.onStop();
        running = false;
        load = 0;
        returnData=0;

    }

    int returnData=0;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 7000) {
            double choices = data.getDoubleExtra("jog", 0);
            if (mqService != null) {
                device.setLineJog(choices);
            }
        } else if (resultCode == 8000) {
            int click=data.getIntExtra("click",0);
            returnData=1;
            if (click==1){
                plMemory = data.getIntExtra("plMemory", 0);
                device.setPlMemory(plMemory);
            }
        } else if (requestCode == 9000) {
            int voice = data.getIntExtra("voice", 0);
            if (mqService != null) {
                device.setVlice2(voice);
                mqService.updateDevice(device);
            }
            deviceDao.update(device);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialogLoad != null && dialogLoad.isShowing()) {
            dialogLoad.dismiss();
        }

        handler.removeCallbacksAndMessages(null);
        if (bind) {
            unbindService(connection);
        }
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }

    MQService mqService;
    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MQService.LocalBinder binder = (MQService.LocalBinder) service;
            mqService = binder.getService();
            Log.i("ServiceConnection", "-->ServiceConnection");
            mqService.getData(topicName, 0x11);
            countTimer.start();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String action = intent.getAction();
                if ("offline".equals(action)) {
                    String macAddress = intent.getStringExtra("macAddress");
                    if (intent.hasExtra("all") || macAddress.equals(deviceMac)) {
                        device.setOnline(false);
                        device.setTemp(0);
                        device.setHum(0);
                        device.setCurrent(0);
                        deviceDao.update(device);
                        device.setVotage(0);
                        setMode(device);
                    }
                } else {
                    String macAddress = intent.getStringExtra("macAddress");
//                    int funCode=intent.getIntExtra("funCode",0);
                    if (macAddress.equals(deviceMac)) {
                        Device device2 = (Device) intent.getSerializableExtra("device");
                        if (device2 != null) {
                            Log.i("ServiceConnection","-->ServiceConnection33");
                            device = device2;
//                            plMemory=device.getPlMemory();
                            setMode(device2);
                            lines = "";
                            lines = intent.getStringExtra("lines");
                            if (click == 1) {
                                handler.sendEmptyMessage(101);//关
                            } else if (click == 2) {
                                handler.sendEmptyMessage(102);//开
                            } else if (click == 3) {
                                handler.sendEmptyMessage(103);//其他
                            }
                        }
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    int way = 0;//0为默认以第一种展示线路 1为以第二种展示线路
    int allOpen = 0;//0表示 多开按钮为灰色，1为绿色
    int allClose = 0;//0表示 多关按钮为灰色，1为绿色
    int allJog = 0;//0表示 多点动按钮为灰色，1为绿色
    int singleSwitch = -1;//0表示 单个按钮为灰色，1为绿色
    int onKey = -1;

    /**
     * 设置单开关
     */
    private void setImageRes() {
        if (singleSwitch == 1) {
            img_switch2.setImageResource(R.mipmap.img_switch_close);
        } else if (singleSwitch == 0) {
            img_switch2.setImageResource(R.mipmap.img_switch_unclose);
        }
    }



    class AddOperationLogAsync extends BaseWeakAsyncTask<Map<String, Object>, Void, Integer, DeviceItemActivity> {

        public AddOperationLogAsync(DeviceItemActivity activity) {
            super(activity);
        }

        @Override
        protected Integer doInBackground(DeviceItemActivity activity, Map<String, Object>... maps) {
            Map<String, Object> params = maps[0];
            String url = HttpUtils.ipAddress + "data/addOperationLog";
            String result = HttpUtils.requestPost(url, params);
            Log.i("AddOperationLogAsync", "-->" + result);
            return null;
        }

        @Override
        protected void onPostExecute(DeviceItemActivity activity, Integer integer) {

        }
    }


    @OnClick({R.id.img_back, R.id.img_book, R.id.img_all_close2, R.id.img_all_open2, R.id.img_all_jog2, R.id.img_switch2})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
//                updateLines(deviceMac);
                if (!TextUtils.isEmpty(search)) {
                    startActivity(new Intent(this, DeviceListActivity.class));
                    break;
                }
                setResult(1002);
                finish();
                break;
            case R.id.img_book:
                int deviceAuthority_Lock = device.getDeviceAuthority_Lock();
                if ("share".equals(share) && deviceAuthority_Lock == 0) {
                    ToastUtil.showShort(this, "你没有线路互锁的权限");
                    break;
                }
                Intent intent = new Intent(this, DeviceInterLockActivity.class);
                intent.putExtra("deviceId", deviceId);
                intent.putExtra("deviceMac", deviceMac);
                intent.putExtra("online", device.getOnline());
                intent.putExtra("userId", userId);
                startActivity(intent);
                break;
            case R.id.img_all_close2:
                if (!device.getOnline()) {
                    mqService.getData(topicName, 0x11);
                    ToastUtil.showShort(this, "设备已离线");
                    break;
                }

                if ("share".equals(share) && deviceAuthority_LineSwitch == 0) {
                    ToastUtil.showShort(this, "你没有线路开关的权限");
                    break;
                }

                if (dialogLoad != null && dialogLoad.isShowing()) {
                    ToastUtil.showShort(this, "请稍后...");
                    break;
                }
                if (mqService != null) {
                    click = 1;
                    device.setPrelineswitch(0);
                    device.setLastlineswitch(0);
                    device.setPrelinesjog(0);
                    device.setLastlinesjog(0);
                    device.setDeviceState(0);
                    onKey = 1;
                    boolean success = mqService.sendBasic(topicName, device,0x01);
                    countTimer.start();

                }
                break;
            case R.id.img_all_open2:
                if (!device.getOnline()) {
                    mqService.getData(topicName, 0x11);
                    ToastUtil.showShort(this, "设备已离线");
                    break;
                }
                if ("share".equals(share) && deviceAuthority_LineSwitch == 0) {
                    ToastUtil.showShort(this, "你没有线路开关的权限");
                    break;
                }

                if (dialogLoad != null && dialogLoad.isShowing()) {
                    ToastUtil.showShort(this, "请稍后...");
                    break;
                }

                if (mqService != null) {
                    device.setPrelineswitch(255);
                    device.setLastlineswitch(255);
                    device.setDeviceState(1);
                    device.setPrelinesjog(0);
                    device.setLastlinesjog(0);
                    click = 2;
                    onKey = 2;
                    boolean success = mqService.sendBasic(topicName, device,0x01);
                    countTimer.start();
//                    if (success){
//                        linesAadpter.notifyDataSetChanged();
//                    }
                }

                break;
            case R.id.img_all_jog2:
                if (!device.getOnline()) {
                    mqService.getData(topicName, 0x11);
                    ToastUtil.showShort(this, "设备已离线");
                    break;
                }
                int deviceAuthority_Inching = device.getDeviceAuthority_Inching();
                if ("share".equals(share) && deviceAuthority_Inching == 0) {
                    ToastUtil.showShort(this, "你没有线路点动的权限");
                    break;
                }
                if (choicedLines < 1) {
                    ToastUtil.show(this, "请选择线路", Toast.LENGTH_SHORT);
                    break;
                }
                if (dialogLoad != null && dialogLoad.isShowing()) {
                    ToastUtil.showShort(this, "请稍后...");
                    break;
                }

                int[] preLines = new int[8];
                int[] lastLines = new int[8];
                for (int i = 0; i < list.size(); i++) {
                    Line2 line2 = list.get(i);

                    int deviceLineNum = line2.getDeviceLineNum();
                    if (line2.getClick2() == 1) {
                        if (deviceLineNum <= 8) {
                            preLines[deviceLineNum - 1] = 1;
                        } else {
                            lastLines[(deviceLineNum - 1) - 8] = 1;
                        }
                    }
                }
                int preLinesJog = TenTwoUtil.changeToTen2(preLines);
                int lastLinesJog = TenTwoUtil.changeToTen2(lastLines);
                Log.i("preLinesJog", "-->" + preLinesJog + "," + lastLinesJog);
                device.setPrelinesjog(preLinesJog);
                device.setLastlinesjog(lastLinesJog);

                if (mqService != null) {
                    boolean success = mqService.sendBasic(topicName, device,0x02);
                    countTimer.start();
                    click = 3;
//                    if (success){
//                        linesAadpter.notifyDataSetChanged();
//                    }
                }
                break;
            case R.id.img_switch2:
                if (!device.getOnline()) {
                    mqService.getData(topicName, 0x11);
                    ToastUtil.showShort(this, "设备已离线");
                    break;
                }
                if ("share".equals(share) && deviceAuthority_LineSwitch == 0) {
                    ToastUtil.showShort(this, "你没有线路开关的权限");
                    break;
                }
                Log.i("OnClickchoicedLinesGrid", "-->" + choicedLinesGrid);
                if (choicedLines < 1) {
                    ToastUtil.show(this, "请单独选择一路", Toast.LENGTH_SHORT);
                } else if (choicedLines > 1) {
                    ToastUtil.show(this, "请单独选择一路", Toast.LENGTH_SHORT);
                } else if (choicedLines == 1) {
                    if (dialogLoad != null && dialogLoad.isShowing()) {
                        ToastUtil.showShort(this, "请稍后...");
                        break;
                    }
                    int[] preLinesS = TenTwoUtil.changeToTwo(device.getPrelineswitch());
                    int[] lastLinesS = TenTwoUtil.changeToTwo(device.getLastlineswitch());
                    img_switch2.setImageResource(R.mipmap.img_switch_unclose);
                    for (int i = 0; i < list.size(); i++) {
                        Line2 line2 = list.get(i);
                        int deviceLineNum = line2.getDeviceLineNum();
                        boolean open = line2.getOpen();
                        if (line2.getClick2() == 1) {
                            if (deviceLineNum <= 8) {
                                if (open) {
                                    preLinesS[deviceLineNum - 1] = 0;
                                } else {
                                    preLinesS[deviceLineNum - 1] = 1;
                                }
                            } else if (deviceLineNum > 8) {
                                if (open) {
                                    lastLinesS[(deviceLineNum - 1) - 8] = 0;
                                } else {
                                    lastLinesS[(deviceLineNum - 1) - 8] = 1;
                                }
                            }
                            checkLine = "";
                            checkLine = deviceLineNum + "";

                            int state = open == true ? 0 : 1;
                            if (state == 0) {
                                click = 1;
                            } else if (state == 1) {
                                click = 2;
                            }
                            device.setDeviceState(state);
                            break;
                        }
                    }
                    int preLinesSwitch = TenTwoUtil.changeToTen2(preLinesS);
                    int lastLinesSwitch = TenTwoUtil.changeToTen2(lastLinesS);
                    onKey = 0;
                    device.setPrelineswitch(preLinesSwitch);
                    device.setLastlineswitch(lastLinesSwitch);
                    device.setPrelinesjog(0);
                    device.setLastlinesjog(0);
                    if (mqService != null) {
                        mqService.sendBasic(topicName, device,0x01);
                        countTimer.start();
                    }
                }
                break;

        }
    }

    PopupWindow popupWindow;

    private void popupNote(final int position, int type, View item, int xoff) {
        if (popupWindow != null && popupWindow.isShowing()) {
            return;
        }

        View view = null;
        if (type == 0) {
            view = View.inflate(this, R.layout.popup_note, null);
        } else if (type == 1) {
            view = View.inflate(this, R.layout.popup_note2, null);
        }

        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        //点击空白处时，隐藏掉pop窗口
        popupWindow.setFocusable(false);
        popupWindow.setOutsideTouchable(true);
        popupWindow.update();


//        popupWindow.showAtLocation(item,Gravity.START,0,0);
        int[] location = new int[2];
        item.getLocationOnScreen(location);
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int height = view.getTop();
        int popupWidth = view.getMeasuredWidth();    //  获取测量后的宽度
        int popupHeight = view.getMeasuredHeight();  //获取测量后的高度
        int ss = location[1] - popupHeight;
        Log.i("popupHeight", "-->" + popupHeight);
        if (type == 0) {
            popupWindow.showAsDropDown(item, xoff, 0);
        } else if (type == 1) {
            popupWindow.showAtLocation(item, Gravity.NO_GRAVITY, location[0] - 2 * popupWidth, location[1] + popupHeight / 10 + 8);
        }
        TextView tv_note = view.findViewById(R.id.tv_note);
        tv_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                changeDialog(position);
            }
        });
        //添加按键事件监听
        backgroundAlpha(0.6f);

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1.0f);
            }
        });
    }

    ChangeDialog dialog;
    private int updatePosition = -1;

    private void changeDialog(final int postion) {
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        dialog = new ChangeDialog(this);
        dialog.setCanceledOnTouchOutside(false);


        dialog.setMode(0);
        dialog.setTitle("备注");
        dialog.setTips("编辑内容");

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
                String content = dialog.getContent();
                if (TextUtils.isEmpty(content)) {
                    ToastUtil.show(DeviceItemActivity.this, "编辑内容不能为空", Toast.LENGTH_SHORT);
                } else {
                    try {
                        params.clear();
                        params.put("deviceId", deviceId);
                        params.put("lineName", content);
                        params.put("deviceLineNum", postion + 1);
                        updatePosition = postion;
                        new UpdateDeviceLineAsync().execute(params).get(3, TimeUnit.SECONDS);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

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

    //设置蒙版
    private void backgroundAlpha(float f) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = f;
        getWindow().setAttributes(lp);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.img_all_close2:
                if (!device.getOnline()) {
                    ToastUtil.showShort(this, "设备已离线");
                    break;
                }
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    img_all_close2.setImageResource(R.mipmap.img_all_close2);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    img_all_close2.setImageResource(R.mipmap.img_all_close);
                }
                break;
            case R.id.img_all_open2:
                if (!device.getOnline()) {
                    ToastUtil.showShort(this, "设备已离线");
                    break;
                }
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    img_all_open2.setImageResource(R.mipmap.img_all_open2);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    img_all_open2.setImageResource(R.mipmap.img_all_open);
                }
                break;
            case R.id.img_all_jog2:
                if (!device.getOnline()) {
                    ToastUtil.showShort(this, "设备已离线");
                    break;
                }
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    img_all_jog2.setImageResource(R.mipmap.img_jog2);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    img_all_jog2.setImageResource(R.mipmap.img_jog);
                }
                break;
        }
        return false;
    }

    class ViewHolder {
        @BindView(R.id.img_back)
        ImageView rl_item2;
        @BindView(R.id.rl_item)
        RelativeLayout rl_item;
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


    int firstItem = -1;

    class LinesAadpter extends RecyclerView.Adapter<LineHolder> {

        private List<Line2> list;
        private Context context;

        public LinesAadpter(List<Line2> list, Context context) {
            this.list = list;
            this.context = context;
        }

        @NonNull
        @Override
        public LineHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = View.inflate(context, R.layout.item_circle_change, null);
            return new LineHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull LineHolder holder, final int position) {
            final Line2 line2 = list.get(position);
            double seconds = line2.getSeconds();
            String name = line2.getName();
            Log.i("name----", "-->" + name);
            final boolean open = line2.isOpen();
            int click = line2.getClick2();
            final boolean online = line2.getOnline();
            boolean jog = line2.getJog();
            if (open) {
                if (click == 1) {
                    holder.img_line.setImageResource(R.mipmap.img_circle3);
                } else {
                    holder.img_line.setImageResource(R.mipmap.img_circle1);
                }
            } else {
                if (click == 1) {
                    holder.img_line.setImageResource(R.mipmap.img_circle2);
                } else {
                    holder.img_line.setImageResource(R.mipmap.img_circle4);
                }
            }


            if (choicedLines == 1) {
                for (int i = 0; i < list.size(); i++) {
                    Line2 line3 = list.get(i);
                    if (line3.getClick2() == 1) {
                        boolean open3 = line3.getOpen();
                        if (open3) {
                            tv_switch2.setText("关");
                            singleSwitch = 1;
                            setImageRes();
                        } else {
                            tv_switch2.setText("开");
                            singleSwitch = 1;
                            setImageRes();
                        }
                        break;
                    }
                }
            } else if (choicedLines == 0) {
                tv_switch2.setText("开");
                singleSwitch = 0;
                setImageRes();
            }

            if (jog) {
                holder.tv_line_value.setVisibility(View.GONE);
//                if (seconds > 0) {
//                    holder.tv_line_value.setVisibility(View.VISIBLE);
//                    String s = seconds + "";
//                    char s2 = s.charAt(s.indexOf(".") + 1);
//                    if (s2 == '0') {
//                        s = s.substring(0, s.indexOf("."));
//                    }
//                    holder.tv_line_value.setText("" + s);
//                }
            } else {
                holder.tv_line_value.setVisibility(View.GONE);
            }
            if (click == 1) {
                mapChoiceLines.put(position, line2.getDeviceLineNum());
            } else {
                if (mapChoiceLines.containsKey(position)) {
                    mapChoiceLines.remove(position);
                }
            }
            holder.rl_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!device.getOnline()) {
                        ToastUtil.showShort(DeviceItemActivity.this, "设备已离线");
                        return;
                    }
                    if (line2.getClick2() == 1) {
                        line2.setClick2(0);
                        if (choicedLines > 0) {
                            choicedLines--;
                        } else {
                            choicedLines = 0;
                        }
                    } else {
                        line2.setClick2(1);
                        choicedLines++;
                    }
                    list.set(position, line2);
                    notifyDataSetChanged();

                }
            });
            holder.rl_item.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    changeDialog(position);
                    return false;
                }
            });
            holder.tv_line.setText(name);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    class LineHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.rl_item)
        RelativeLayout rl_item;
        @BindView(R.id.img_line)
        ImageView img_line;
        @BindView(R.id.tv_line_value)
        TextView tv_line_value;
        @BindView(R.id.tv_line)
        TextView tv_line;

        public LineHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    int plMemory = 0;

    class MenuAdapter extends BaseAdapter {

        private List<DeviceMenu> list;
        private Context context;

        public MenuAdapter(List<DeviceMenu> list, Context context) {
            this.list = list;
            this.context = context;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public DeviceMenu getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            HolderView holderView = null;
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.item_device_menu, null);
                holderView = new HolderView(convertView);
                convertView.setTag(holderView);
            } else {
                holderView = (HolderView) convertView.getTag();
            }
            DeviceMenu deviceMenu = getItem(position);
            String name = deviceMenu.getName();
            int img = deviceMenu.getImg();
            int i = deviceMenu.getI();
            holderView.img_menu.setImageResource(img);
            holderView.tv_menu.setText(name);
            if (i == list.size() - 2) {
//                holderView.img_menu_switch.setVisibility(View.VISIBLE);
//                if (plMemory==0){
//                    holderView.img_menu_switch.setImageResource(R.mipmap.img_close);
//                }else if (plMemory==1){
//                    holderView.img_menu_switch.setImageResource(R.mipmap.img_open);
//                }
                holderView.img_menu_switch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String share = device.getShare();
                        int deviceAuthority_Poweroff = device.getDeviceAuthority_Poweroff();
                        if ("share".equals(share) && deviceAuthority_Poweroff == 0) {
                            ToastUtil.showShort(DeviceItemActivity.this, "你没有掉电记忆的权限");
                            return;
                        }
                        if (plMemory == 1) {
                            plMemory = 0;
                        } else if (plMemory == 0) {
                            plMemory = 1;
                        }
                        device.setPlMemory(plMemory);
                        notifyDataSetChanged();
                    }
                });
            } else {
                holderView.img_menu_switch.setVisibility(View.GONE);
            }
            return convertView;
        }
    }

    class HolderView {
        @BindView(R.id.img_menu)
        ImageView img_menu;
        @BindView(R.id.tv_menu)
        TextView tv_menu;
        @BindView(R.id.img_menu_switch)
        ImageView img_menu_switch;

        public HolderView(View view) {
            ButterKnife.bind(this, view);
        }
    }

    int back = 0;

    class GetDeviceLineAsync extends BaseWeakAsyncTask<Map<String, Object>, Void, Integer, DeviceItemActivity> {

        public GetDeviceLineAsync(DeviceItemActivity activity) {
            super(activity);
        }

        @Override
        protected Integer doInBackground(DeviceItemActivity activity, Map<String, Object>... maps) {
            int code = 0;
            Map<String, Object> params = maps[0];
            try {
                String url = HttpUtils.ipAddress + "device/getLineName";
                String result = HttpUtils.requestPost(url, params);
                if (TextUtils.isEmpty(result))
                    result = HttpUtils.requestPost(url, params);

                if (!TextUtils.isEmpty(result)) {
                    Log.i("GetDeviceLineAsync", "-->" + result);
                    JSONObject jsonObject = new JSONObject(result);
                    code = jsonObject.getInt("returnCode");
                    if (code == 100) {
                        String s = jsonObject.getJSONObject("returnData").toString();
                        Gson gson = new Gson();
                        DeviceLines deviceLines = gson.fromJson(s, DeviceLines.class);
                        Class<DeviceLines> clazz = (Class<DeviceLines>) Class.forName("com.peihou.willgood2.pojo.DeviceLines");
                        for (int i = 1; i <= 16; i++) {
                            Method method = clazz.getDeclaredMethod("getLineName" + i);
                            String lineName = (String) method.invoke(deviceLines);
                            Line2 line2 = deviceLineDao.findDeviceLine(deviceId, i);
                            if (line2 == null) {
                                line2 = new Line2(false, lineName, 0, false, i, deviceId, deviceMac);
                                deviceLineDao.insert(line2);
                            } else {
                                line2.setName(lineName);
                                deviceLineDao.update(line2);
                            }
                            list.add(line2);
                        }
                    }
                } else {
                    List<Line2> list2 = deviceLineDao.findDeviceLines(deviceId);
                    if ((list2 != null && list2.size() != 16)) {
                        deviceLineDao.deleteDeviceLines(list2);
                        for (int i = 1; i <= 16; i++) {
                            Line2 line2 = new Line2(false,  i+"路", 0, false, i, deviceId, deviceMac);
                            list.add(line2);
                        }
                        deviceLineDao.insertDeviceLines(list);
                        code = 100;

                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return code;
        }

        @Override
        protected void onPostExecute(DeviceItemActivity activity, Integer code) {

            switch (code) {
                case 100:
                    Log.i("ServiceConnection", "-->ServiceConnection2");
                    init = 1;
                    setMode(device);
                    Message msg = handler.obtainMessage();
                    msg.what = 0;
                    handler.sendMessageDelayed(msg, 500);
                    break;
            }
        }
    }

    Map<String, Object> operateLog = new HashMap<>();
    StringBuffer sb = new StringBuffer();
    Handler.Callback mCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (mqService != null) {
                        params.clear();
                        params.put("deviceId", deviceId);
                        params.put("deviceMac", deviceMac);
                        mqService.getSwitchName(params);
                    }
                    break;
                case 101:
                    if (mqService != null) {
                        click = 0;
                        String lines2 = "";
                        if (onKey == 0) {
                            lines2 = checkLine;
                        } else {
                            lines2 = lines;
                        }

                        Log.i("line2", "-->" + lines2);
                        onKey = -1;
                        mqService.starSpeech(deviceMac, 1);
                        operateLog.clear();
                        operateLog.put("deviceMac", deviceMac);
                        operateLog.put("deviceControll", 2);
                        operateLog.put("deviceLogType", 1);
                        operateLog.put("deviceLine", lines2);
                        operateLog.put("userId", userId);
                        new AddOperationLogAsync(DeviceItemActivity.this).execute(operateLog);
                    }
                    break;
                case 102:
                    if (mqService != null) {
                        click = 0;
                        mqService.starSpeech(deviceMac, 0);
                        String lines2 = "";
                        if (onKey == 0) {
                            lines2 = checkLine;
                        } else {
                            lines2 = lines;
                        }
                        Log.i("line2", "-->" + lines2);

                        onKey = -1;
                        operateLog.clear();
                        operateLog.put("deviceMac", deviceMac);
                        operateLog.put("deviceControll", 1);
                        operateLog.put("deviceLogType", 1);
                        operateLog.put("deviceLine", lines2);
                        operateLog.put("userId", userId);
                        new AddOperationLogAsync(DeviceItemActivity.this).execute(operateLog);
                    }
                    break;
                case 103:
                    if (mqService != null) {
                        click = 0;
                        mqService.starSpeech(deviceMac, 2);
                    }
                    break;
            }
            return true;
        }
    };
    Handler handler = new WeakRefHandler(mCallback);

    class UpdateDeviceLineAsync extends AsyncTask<Map<String, Object>, Void, Integer> {

        @Override
        protected Integer doInBackground(Map<String, Object>... maps) {
            int code = 0;
            Map<String, Object> params = maps[0];
            try {
                String url = HttpUtils.ipAddress + "device/changeLineName";
                String result = HttpUtils.requestPost(url, params);
                if (!TextUtils.isEmpty(result)) {
                    JSONObject jsonObject = new JSONObject(result);
                    code = jsonObject.getInt("returnCode");
                    if (code == 100) {
                        String lineName = (String) params.get("lineName");
                        int position = (int) params.get("deviceLineNum") - 1;
                        Line2 line2 = list.get(position);
                        line2.setName(lineName);
//                        deviceLineDao.update(line2);
                        if (mqService != null) {
                            mqService.updateLine(line2);
                        }
                        list.set(position, line2);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return code;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            switch (integer) {
                case 100:
                    linesAadpter.notifyDataSetChanged();
                    dialog.dismiss();
                    break;
            }
        }
    }
}
