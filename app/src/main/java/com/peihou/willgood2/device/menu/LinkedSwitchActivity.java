package com.peihou.willgood2.device.menu;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.peihou.willgood2.R;
import com.peihou.willgood2.BaseActivity;
import com.peihou.willgood2.database.dao.impl.DeviceLineDaoImpl;
import com.peihou.willgood2.database.dao.impl.DeviceLinkDaoImpl;
import com.peihou.willgood2.pojo.Line2;
import com.peihou.willgood2.pojo.Linked;
import com.peihou.willgood2.service.MQService;
import com.peihou.willgood2.utils.TenTwoUtil;
import com.peihou.willgood2.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 *开关量联动设置
 */
public class LinkedSwitchActivity extends BaseActivity {


    @BindView(R.id.gv_line)
    GridView gv_line;//线路网格布局
    private List<Line2> lines=new ArrayList<>();//线路集合
    @BindView(R.id.btn_once) TextView btn_once;//单次触发
    @BindView(R.id.btn_loop) TextView btn_loop;//循环触发
    @BindView(R.id.btn_switch_close) TextView btn_switch_close;//开关量闭合
    @BindView(R.id.btn_switch_open) TextView btn_switch_open;//开关量断开
    @BindView(R.id.btn_open) TextView btn_open;//打开
    @BindView(R.id.btn_close) TextView btn_close;//关闭
    LinesAdapter adapter;
    private DeviceLineDaoImpl deviceLineDao;//设备线路表操作对象
    private DeviceLinkDaoImpl deviceLinkDao;//设备联动表操作对象
    private int type=2;
    long deviceId;//设备Id
    String deviceMac;//设备的mac地址
    private String topicName;

    @Override
    public void initParms(Bundle parms) {
        deviceId=parms.getLong("deviceId");
        deviceMac=parms.getString("deviceMac");
    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_linked_switch;
    }

    private boolean bind=false;
    @Override
    public void initView(View view) {

        int a=0;
        int b=a;
        Log.i("bbb","-->"+b);

        topicName="qjjc/gateway/"+deviceMac+"/server_to_client";
//        topicName = "qjjc/gateway/" + deviceMac + "/client_to_server";
        deviceLineDao=new DeviceLineDaoImpl(getApplicationContext());
        deviceLinkDao=new DeviceLinkDaoImpl(getApplicationContext());
        lines=deviceLineDao.findDeviceOnlineLines(deviceId);

        adapter=new LinesAdapter(this,lines);
        gv_line.setAdapter(adapter);
        Intent service=new Intent(this,MQService.class);
        bind=bindService(service,connection,Context.BIND_AUTO_CREATE);
        gv_line.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Line2 line=lines.get(position);
                if (line.isOnClick()){
                    line.setOnClick(false);
                }else {
                    line.setOnClick(true);
                }
                lines.set(position,line);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    String s="开关量";
    StringBuffer sb=new StringBuffer();

    @OnClick({R.id.img_back,R.id.btn_switch_close,R.id.btn_switch_open,R.id.btn_close,R.id.btn_open,R.id.btn_once,R.id.btn_loop,R.id.img_ensure})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.img_back:
                finish();
                break;
            case R.id.img_ensure:
                sb.setLength(0);
                List<Linked> linkeds=deviceLinkDao.findLinkeds(deviceMac,type);
                if (linkeds!=null && !linkeds.isEmpty()){
                    int size=linkeds.size();
                    s=s+size;
                }else if (linkeds==null || linkeds.isEmpty()){
                    s=s+1;
                }
                int preLines=0;
                int lastLines=0;
                int[] pre=new int[8];
                int[] last=new int[8];
                for (int i = 0; i < lines.size(); i++) {
                    Line2 line2=lines.get(i);
                    int deviceLineNum=line2.getDeviceLineNum()-1;
                    String name=line2.getName();
                    if (deviceLineNum<8){
                        if (line2.isOnClick()){
                            pre[deviceLineNum]=1;
                            sb.append(name+",");
                        }else {
                            pre[deviceLineNum]=0;
                        }
                    }else if (deviceLineNum>=8){
                        if (line2.isOnClick()){
                            last[deviceLineNum-8]=1;
                            sb.append(name+",");
                        }else {
                            last[deviceLineNum-8]=0;
                        }
                    }
                }
                preLines=TenTwoUtil.changeToTen2(pre);
                lastLines=TenTwoUtil.changeToTen2(last);
                Linked linked=new  Linked(deviceMac, 2, s, condition,open,1, preLines, lastLines, touch);
                Intent intent=new Intent();
                intent.putExtra("linked",linked);
                setResult(1000,intent);
                finish();

                break;
            case R.id.btn_switch_close:
                if (condition==1){
                    break;
                }
                condition=1;
                setCaseLimit();
                break;
            case R.id.btn_open:
                if (open==1){
                    break;
                }
                open=1;
                setSwitch();
                break;
            case R.id.btn_close:
                if (open==0){
                    break;
                }
                open=0;
                setSwitch();
                break;
            case R.id.btn_switch_open:
                if (condition==0){
                    break;
                }
                condition=0;
                setCaseLimit();
                break;
            case R.id.btn_once:
                if (touch==0){
                    break;
                }
                touch=0;
                setTouchMode();
                break;
            case R.id.btn_loop:
                if (touch==1){
                    break;
                }
                touch=1;
                setTouchMode();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bind){
            unbindService(connection);
        }
    }

    MQService mqService;
    ServiceConnection connection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MQService.LocalBinder binder= (MQService.LocalBinder) service;
            mqService=binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    int touch=0;//为0是单次触发，1为多次触发
    private void setTouchMode(){
        if (touch==0){
            btn_loop.setTextColor(getResources().getColor(R.color.white));
            btn_loop.setBackgroundColor(0);
            btn_once.setTextColor(getResources().getColor(R.color.base_back));
            btn_once.setBackground(getResources().getDrawable(R.drawable.shape_once));
        }else if (touch==1){
            btn_loop.setTextColor(getResources().getColor(R.color.base_back));
            btn_loop.setBackground(getResources().getDrawable(R.drawable.shape_once));
            btn_once.setTextColor(getResources().getColor(R.color.white));
            btn_once.setBackgroundColor(0);
        }
    }
    int condition=1;//条件 0为闭合，1为断开
    private void setCaseLimit(){
        if (condition==1){
            btn_switch_close.setTextColor(getResources().getColor(R.color.base_back));
            btn_switch_close.setBackground(getResources().getDrawable(R.drawable.shape_once));
            btn_switch_open.setTextColor(getResources().getColor(R.color.white));
            btn_switch_open.setBackgroundColor(0);
        }else if (condition==0){
            btn_switch_close.setTextColor(getResources().getColor(R.color.white));
            btn_switch_close.setBackgroundColor(0);
            btn_switch_open.setTextColor(getResources().getColor(R.color.base_back));
            btn_switch_open.setBackground(getResources().getDrawable(R.drawable.shape_once));
        }
    }
    int open=1;
    private void setSwitch(){
        if (open==1){
            btn_open.setTextColor(getResources().getColor(R.color.base_back));
            btn_open.setBackground(getResources().getDrawable(R.drawable.shape_once));
            btn_close.setTextColor(getResources().getColor(R.color.white));
            btn_close.setBackgroundColor(0);
        }else if (open==0){
            btn_open.setTextColor(getResources().getColor(R.color.white));
            btn_open.setBackgroundColor(0);
            btn_close.setTextColor(getResources().getColor(R.color.base_back));
            btn_close.setBackground(getResources().getDrawable(R.drawable.shape_once));
        }
    }
    class LinesAdapter extends BaseAdapter {

        private Context context;
        private List<Line2> list;

        public LinesAdapter(Context context, List<Line2> list) {
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
            ViewHolder viewHolder=null;
            if (convertView==null){
                convertView=View.inflate(context,R.layout.item_line2,null);
                viewHolder=new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            }else {
                viewHolder= (ViewHolder) convertView.getTag();
            }
            Line2 line=getItem(position);
            boolean onClick=line.isOnClick();
            String name=line.getName();
            viewHolder.tv_line.setText(name+"");
            if (onClick){
                viewHolder.tv_line.setTextColor(getResources().getColor(R.color.base_back));
                viewHolder.tv_line.setBackground(getResources().getDrawable(R.drawable.shape_once));
            }else {
                viewHolder.tv_line.setTextColor(getResources().getColor(R.color.gray2));
                viewHolder.tv_line.setBackgroundColor(0);
            }
            return convertView;
        }
    }
    class ViewHolder{
        @BindView(R.id.tv_line)
        TextView tv_line;
        public ViewHolder(View itemView){
            ButterKnife.bind(this ,itemView);
        }
    }
}

