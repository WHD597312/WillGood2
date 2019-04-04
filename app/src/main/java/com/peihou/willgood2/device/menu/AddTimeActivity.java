package com.peihou.willgood2.device.menu;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.peihou.willgood2.R;
import com.peihou.willgood2.BaseActivity;
import com.peihou.willgood2.custom.CustomDatePicker;
import com.peihou.willgood2.database.dao.impl.DeviceLineDaoImpl;
import com.peihou.willgood2.database.dao.impl.TimerTaskDaoImpl;

import com.peihou.willgood2.pojo.Line2;
import com.peihou.willgood2.pojo.TimerTask;
import com.peihou.willgood2.service.MQService;
import com.peihou.willgood2.utils.TenTwoUtil;
import com.peihou.willgood2.utils.ToastUtil;
import com.peihou.willgood2.utils.YearUtils;
import com.weigan.loopview.LoopView;
import com.weigan.loopview.OnItemSelectedListener;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 添加定时任务
 */
public class AddTimeActivity extends BaseActivity {

    @BindView(R.id.grid_line)
    GridView grid_line;//线路控件
    @BindView(R.id.tv_timer_single)
    TextView tv_timer_single;
    @BindView(R.id.tv_timer_loop)
    TextView tv_timer_loop;
    @BindView(R.id.tv_repeat)
    TextView tv_repeat;
    @BindView(R.id.linear_week)
    LinearLayout linear_week;
    @BindView(R.id.btn_control_open)
    TextView btnControlOpen;//开启定时
    @BindView(R.id.btn_control_close)
    TextView btnControlClose;//关闭定时
    @BindView(R.id.tv_1)
    TextView tv_1;//周一
    @BindView(R.id.tv_2)
    TextView tv_2;//周2
    @BindView(R.id.tv_3)
    TextView tv_3;//周3
    @BindView(R.id.tv_4)
    TextView tv_4;//周4
    @BindView(R.id.tv_5)
    TextView tv_5;//周5
    @BindView(R.id.tv_6)
    TextView tv_6;//周6
    @BindView(R.id.tv_7)
    TextView tv_7;//周日

    @BindView(R.id.tv_timer_set)
    TextView tv_timer_set;
    @BindView(R.id.tv_timer_value)
    TextView tv_timer_value;
    MyAdapter adapter;
    List<Line2> list = new ArrayList<>();//线路集合
    long deviceId;
    String deviceMac;
    private DeviceLineDaoImpl deviceLineDao;
    TimerTaskDaoImpl timerTaskDao;

    String[] hours=new String[24];
    String[] mins=new String[60];
    @Override
    public void initParms(Bundle parms) {
        deviceId = parms.getLong("deviceId");
        deviceMac = parms.getString("deviceMac");
    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_add_time;
    }

    int year;
    int month;
    int day;
    int hour;
    int min;

    int weeks[] = new int[8];
    String topicName;

    private boolean bind = false;

    @Override
    public void initView(View view) {


        deviceLineDao = new DeviceLineDaoImpl(getApplicationContext());
        list = deviceLineDao.findDeviceOnlineLines(deviceId);
        timerTaskDao = new TimerTaskDaoImpl(getApplicationContext());

        topicName = "qjjc/gateway/" + deviceMac + "/server_to_client";
//        topicName = "qjjc/gateway/" + deviceMac + "/client_to_server";
        Intent service = new Intent(this, MQService.class);
        bind = bindService(service, connection, Context.BIND_AUTO_CREATE);
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        min = calendar.get(Calendar.MINUTE);
        String time = "";
        String ss = "" + month;
        if (month < 10) {
            ss = "0" + month;
        }
        String day1=""+day;
        if (day<10){
            day1="0"+day;
        }
        String sss = "" + min;
        String ss1=hour+"";
        if (hour<10){
            ss1="0"+hour;
        }

        if (min < 10) {
            sss = "0" + min;
        }
        time = year + "-" + ss + "-" + day1 + " " + ss1 + ":" + sss;

        tv_timer_value.setText(time);
        tv_1.setTag(0);
        tv_2.setTag(0);
        tv_3.setTag(0);
        tv_4.setTag(0);
        tv_5.setTag(0);
        tv_6.setTag(0);
        tv_7.setTag(0);

        adapter = new MyAdapter(this, list);

        grid_line.setAdapter(adapter);
        grid_line.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Line2 line = list.get(position);
                boolean click = line.isOnClick();
                if (click) {
                    line.setOnClick(false);
                } else {
                    line.setOnClick(true);
                }
                list.set(position, line);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    int onClick = 0;
    int only = 0;//0为只有一次，1可以循环
    int open = 1;
    StringBuffer sb = new StringBuffer();

    @OnClick({R.id.img_back, R.id.img_add, R.id.tv_timer_single, R.id.rl_timer_perform, R.id.btn_control_open, R.id.btn_control_close, R.id.tv_timer_loop, R.id.tv_1, R.id.tv_2, R.id.tv_3, R.id.tv_4, R.id.tv_5, R.id.tv_6, R.id.tv_7})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.img_add:
                TimerTask timerTask = null;
                int preLine = 0;
                int lastLine = 0;
                if (only == 0) {
                    sb.setLength(0);
                    int[] preLines = new int[8];
                    int[] lastLines = new int[8];
                    for (int i = 0; i < list.size(); i++) {
                        Line2 line2 = list.get(i);
                        int deviceLineNum = line2.getDeviceLineNum() - 1;
                        String name = line2.getName();
                        if (deviceLineNum < 8) {
                            if (line2.isOnClick()) {
                                preLines[deviceLineNum] = 1;
                                sb.append(name + ",");
                            } else {
                                preLines[deviceLineNum] = 0;
                            }
                        } else if (deviceLineNum >= 8) {
                            if (line2.isOnClick()) {
                                lastLines[deviceLineNum - 8] = 1;
                                sb.append(name + ",");
                            } else {
                                lastLines[deviceLineNum - 8] = 0;
                            }
                        }
                    }

                    preLine = TenTwoUtil.changeToTen2(preLines);
                    lastLine = TenTwoUtil.changeToTen2(lastLines);
                    if (preLine+lastLine == 0) {
                        ToastUtil.showShort(this, "请选择线路");
                        break;
                    }
                    timerTask = new TimerTask(deviceMac, 0x11, year, month, day, hour, min, open, preLine, lastLine, 1);
                } else if (only == 1) {
                    sb.setLength(0);
                    int[] preLines = new int[8];
                    int[] lastLines = new int[8];
                    for (int i = 0; i < list.size(); i++) {
                        Line2 line2 = list.get(i);
                        String name = line2.getName();
                        if (i < 8) {
                            if (line2.isOnClick()) {
                                preLines[i] = 1;
                                sb.append(name + ",");
                            } else {
                                preLines[i] = 0;
                            }
                        } else if (i >= 8) {
                            if (line2.isOnClick()) {
                                lastLines[i - 8] = 1;
                                sb.append(name + ",");
                            } else {
                                lastLines[i - 8] = 0;
                            }
                        }
                    }

                    preLine = TenTwoUtil.changeToTen2(preLines);
                    lastLine = TenTwoUtil.changeToTen2(lastLines);
                    int week = TenTwoUtil.changeToTen2(weeks);
                    if (week == 0) {
                        ToastUtil.showShort(this, "请选择星期");
                        break;
                    }
                    if (preLine+lastLine == 0) {
                        ToastUtil.showShort(this, "请选择线路");
                        break;
                    }
                    timerTask = new TimerTask(deviceMac, 0x22, week, hour, min, open, preLine, lastLine, 1);
                }
                Intent intent = new Intent();
                intent.putExtra("timerTask", timerTask);
                setResult(1001, intent);
                finish();
                break;
            case R.id.btn_control_open:
                if (open == 1) {
                    break;
                }
                open = 1;
                setTimerState();

                break;
            case R.id.btn_control_close:
                if (open == 0) {
                    break;
                }
                open = 0;
                setTimerState();

                break;
            case R.id.rl_timer_perform:
                popupTimer();
                break;
            case R.id.tv_timer_single:
                if (only == 0) {
                    break;
                }
                only = 0;
                setOnlyOne();

                tv_timer_set.setText("设定时间");
                String timer = year + "-" + month + "-" + day + "  " + hour + ":" + min;
                tv_timer_value.setText(timer);
                linear_week.setVisibility(View.GONE);
                tv_repeat.setVisibility(View.GONE);
                break;
            case R.id.tv_timer_loop:
                if (only == 1) {
                    break;
                }
                only = 1;
                setOnlyOne();
                tv_timer_set.setText("循环时间");
                String timer2 = hour + ":" + min;
                tv_timer_value.setText(timer2);
                linear_week.setVisibility(View.VISIBLE);
                tv_repeat.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_1:
                onClick = (int) tv_1.getTag();
                setWeekView(tv_1, onClick);
                onClick = (int) tv_1.getTag();
                weeks[0] = onClick;

                break;
            case R.id.tv_2:
                onClick = (int) tv_2.getTag();
                setWeekView(tv_2, onClick);
                onClick = (int) tv_2.getTag();
                weeks[1] = onClick;
                break;
            case R.id.tv_3:
                onClick = (int) tv_3.getTag();
                setWeekView(tv_3, onClick);
                onClick = (int) tv_3.getTag();
                weeks[2] = onClick;
                break;
            case R.id.tv_4:
                onClick = (int) tv_4.getTag();
                setWeekView(tv_4, onClick);
                onClick = (int) tv_4.getTag();
                weeks[3] = onClick;
                break;
            case R.id.tv_5:
                onClick = (int) tv_5.getTag();
                setWeekView(tv_5, onClick);
                onClick = (int) tv_5.getTag();
                weeks[4] = onClick;
                break;
            case R.id.tv_6:
                onClick = (int) tv_6.getTag();
                setWeekView(tv_6, onClick);
                onClick = (int) tv_6.getTag();
                weeks[5] = onClick;
                break;
            case R.id.tv_7:
                onClick = (int) tv_7.getTag();
                setWeekView(tv_7, onClick);
                onClick = (int) tv_7.getTag();
                weeks[6] = onClick;
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bind) {
            unbindService(connection);
        }
    }

    MQService mqService;
    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MQService.LocalBinder binder = (MQService.LocalBinder) service;
            mqService = binder.getService();
//            mqService.getData(topicName, 0x11);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    public void setTimerState() {
        if (open == 1) {
            btnControlOpen.setTextColor(getResources().getColor(R.color.base_back));
            btnControlOpen.setBackground(getResources().getDrawable(R.drawable.shape_once));
            btnControlClose.setTextColor(getResources().getColor(R.color.gray2));
            btnControlClose.setBackground(getResources().getDrawable(R.drawable.shape_gray3));
        } else if (open == 0) {
            btnControlOpen.setTextColor(getResources().getColor(R.color.gray2));
            btnControlOpen.setBackground(getResources().getDrawable(R.drawable.shape_gray3));
            btnControlClose.setTextColor(getResources().getColor(R.color.base_back));
            btnControlClose.setBackground(getResources().getDrawable(R.drawable.shape_once));
        }
    }

    private void setOnlyOne() {
        if (only == 0) {
            tv_timer_single.setTextColor(getResources().getColor(R.color.base_back));
            tv_timer_single.setBackground(getResources().getDrawable(R.drawable.shape_once));
            tv_timer_loop.setTextColor(getResources().getColor(R.color.gray2));
            tv_timer_loop.setBackground(getResources().getDrawable(R.drawable.shape_gray3));
        } else if (only == 1) {
            tv_timer_single.setTextColor(getResources().getColor(R.color.gray2));
            tv_timer_single.setBackground(getResources().getDrawable(R.drawable.shape_gray3));
            tv_timer_loop.setTextColor(getResources().getColor(R.color.base_back));
            tv_timer_loop.setBackground(getResources().getDrawable(R.drawable.shape_once));
        }
        if (only == 0) {
            String time = "";
            String ss = "" + month;
            if (month < 10) {
                ss = "0" + month;
            }
            String day1=""+day;
            if (day<10){
                day1="0"+day;
            }
            String sss = "" + min;
            String ss1=hour+"";

            if (hour<10){
                ss1="0"+hour;
            }

            if (min < 10) {
                sss = "0" + min;
            }
            time = year + "-" + ss + "-" + day1 + " " + ss1 + ":" + sss;

            tv_timer_value.setText(time);
        }else {
            String time = "";
            String sss = "" + min;
            String ss=hour+"";
            if (hour<10){
                ss="0"+hour;
            }
            if (min < 10) {
                sss = "0" + min;
            }
            time=ss+":"+sss;
            tv_timer_value.setText(time);
        }
    }

    PopupWindow popupWindow;
    NumberPicker timerHour;
    NumberPicker timerMin;
    CustomDatePicker datePicker;

    private void popupTimer() {
        if (popupWindow != null && popupWindow.isShowing()) {
            return;
        }
        View view = View.inflate(this, R.layout.popup_timer, null);
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        //点击空白处时，隐藏掉pop窗口
        popupWindow.setFocusable(false);
        popupWindow.setOutsideTouchable(true);
        popupWindow.update();

        popupWindow.showAtLocation(btnControlOpen, Gravity.BOTTOM | Gravity.CENTER, 0, 0);


        TextView btn_cancel = view.findViewById(R.id.btn_cancel);
        TextView btn_ensure = view.findViewById(R.id.btn_ensure);
        datePicker = view.findViewById(R.id.datePicker);
        datePicker.setDividerColor(Color.WHITE);
        datePicker.setPickerMargin(1);
        TextView tv_year=view.findViewById(R.id.tv_year);
        TextView tv_month=view.findViewById(R.id.tv_month);
        TextView tv_day=view.findViewById(R.id.tv_day);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH));
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        min = calendar.get(Calendar.MINUTE);
        if (only==0){
            datePicker.setVisibility(View.VISIBLE);
            tv_year.setVisibility(View.VISIBLE);
            tv_month.setVisibility(View.VISIBLE);
            tv_day.setVisibility(View.VISIBLE);
            calendar.set(Calendar.YEAR, year + 50);
            datePicker.setMaxDate(calendar.getTimeInMillis());
            datePicker.setDate(year + "-" + month + "-" + day);
        }else {
            datePicker.setVisibility(View.GONE);
            tv_year.setVisibility(View.GONE);
            tv_month.setVisibility(View.GONE);
            tv_day.setVisibility(View.GONE);
        }



        timerHour = view.findViewById(R.id.timerHour);
//        timerHour.setDisplayedValues(hours);

        setNumberPickerDivider(timerHour);
        timerHour.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        timerHour.setMaxValue(23);
        timerHour.setMinValue(0);
        timerHour.setValue(hour);

        timerMin = view.findViewById(R.id.timerMin);

        setNumberPickerDivider(timerMin);
        timerMin.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

//        timerMin.setDisplayedValues(mins);
        timerMin.setMaxValue(59);
        timerMin.setMinValue(0);
        timerMin.setValue(min);
        if (only == 0) {
            String time = "";
            String ss = "" + month;
            if (month < 10) {
                ss = "0" + month;
            }
            String day1=""+day;
            if (day<10){
                day1="0"+day;
            }
            String sss = "" + min;
            String ss1=hour+"";

            if (hour<10){
                ss1="0"+hour;
            }

            if (min < 10) {
                sss = "0" + min;
            }
            time = year + "-" + ss + "-" + day1 + " " + ss1 + ":" + sss;

            tv_timer_value.setText(time);
        }else {
            String time = "";
            String sss = "" + min;
            String ss=hour+"";
            if (hour<10){
                ss="0"+hour;
            }
            if (min < 10) {
                sss = "0" + min;
            }
            time=ss+":"+sss;
            tv_timer_value.setText(time);
        }



        backgroundAlpha(0.6f);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1.0f);
            }
        });
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_cancel:
                        popupWindow.dismiss();
                        break;
                    case R.id.btn_ensure:
                        String date = datePicker.getDate();
                        if (!TextUtils.isEmpty(date)) {
                            String[] s = date.split("-");
                            year = Integer.parseInt(s[0]);
                            month = Integer.parseInt(s[1]);
                            day = Integer.parseInt(s[2]);
                        }
                        hour = timerHour.getValue();
                        min = timerMin.getValue();
                        if (only == 0) {
                            String time = "";
                            String ss = "" + month;
                            if (month < 10) {
                                ss = "0" + month;
                            }
                            String day1=""+day;
                            if (day<10){
                                day1="0"+day;
                            }
                            String sss = "" + min;
                            String ss1=hour+"";

                            if (hour<10){
                                ss1="0"+hour;
                            }

                            if (min < 10) {
                                sss = "0" + min;
                            }
                            time = year + "-" + ss + "-" + day1 + " " + ss1 + ":" + sss;

                            tv_timer_value.setText(time);
                        } else {
                            String time = "";
                            String sss = "" + min;
                            String ss=hour+"";
                            if (hour<10){
                                ss="0"+hour;
                            }
                            if (min < 10) {
                                sss = "0" + min;
                            }
                            time=ss+":"+sss;
                            tv_timer_value.setText(time);
                        }
                        Log.i("DateTime", "-->" + hour + "," + min);
                        popupWindow.dismiss();
                        break;
                }
            }
        };
        btn_ensure.setOnClickListener(clickListener);
        btn_cancel.setOnClickListener(clickListener);

    }

    //设置蒙版
    private void backgroundAlpha(float f) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = f;
        getWindow().setAttributes(lp);
    }

    private void setNumberPickerDivider(NumberPicker numberPicker) {
        final int count = numberPicker.getChildCount();
        for (int i = 0; i < count; i++) {
            try {
                Field dividerField = numberPicker.getClass().getDeclaredField("mSelectionDivider");
                dividerField.setAccessible(true);
                ColorDrawable colorDrawable = new ColorDrawable(
                        ContextCompat.getColor(this, R.color.white));
                dividerField.set(numberPicker, colorDrawable);
                numberPicker.invalidate();
            } catch (NoSuchFieldException | IllegalAccessException | IllegalArgumentException e) {
                Log.w("setNumberPickerTxtClr", e);
            }
        }
    }

    private void setWeekView(TextView tv, int onClick) {
        if (onClick == 1) {
            tv.setTag(0);
            tv.setTextColor(getResources().getColor(R.color.gray2));
            tv.setBackground(getResources().getDrawable(R.drawable.shape_week_gray));
        } else if (onClick == 0) {
            tv.setTag(1);
            tv.setTextColor(getResources().getColor(R.color.base_back));
            tv.setBackground(getResources().getDrawable(R.drawable.shape_week_gray));
        }
    }

    class MyAdapter extends BaseAdapter {

        private Context context;
        private List<Line2> list;

        public MyAdapter(Context context, List<Line2> list) {
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
                convertView = View.inflate(context, R.layout.item_line, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            Line2 line = getItem(position);
            String name = line.getName();
            boolean onClick = line.isOnClick();
            viewHolder.tv_line.setText(name + "");
            if (onClick) {
                viewHolder.tv_line.setBackground(getResources().getDrawable(R.drawable.shape_once));
                viewHolder.tv_line.setTextColor(getResources().getColor(R.color.base_back));
            } else {
                viewHolder.tv_line.setBackground(getResources().getDrawable(R.drawable.shape_gray3));
                viewHolder.tv_line.setTextColor(getResources().getColor(R.color.gray2));
            }

            return convertView;
        }

        class ViewHolder {
            @BindView(R.id.tv_line)
            TextView tv_line;

            public ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }
}
