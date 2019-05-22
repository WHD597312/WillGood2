package com.peihou.willgood2.device;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.peihou.willgood2.R;
import com.peihou.willgood2.pojo.TimerTask;
import com.peihou.willgood2.service.MQService;
import com.peihou.willgood2.utils.TenTwoUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class DemoActivity extends AppCompatActivity {

    Unbinder unbinder;
    TimerTask timerTask;
    @BindView(R.id.grid_list) GridView grid_list;
    MyAdapter adapter;

    private List<String> strings=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        unbinder=ButterKnife.bind(this);
        Intent intent=new Intent(this,MQService.class);
        bindService(intent,connection,Context.BIND_AUTO_CREATE);
        timerTask=new TimerTask();

        strings.add("基础数据");//0
        strings.add("单次定时");//1
        strings.add("循环定时");//2
        strings.add("温度联动");//3
        strings.add("湿度联动");//4
        strings.add("开关量联动");//5
        strings.add("电流联动");//6
        strings.add("电压联动");//7
        strings.add("模拟量电流1");//8
        strings.add("模拟量电压1");//9
        strings.add("删除定时");//10
        strings.add("删除温度");//11
        strings.add("删除湿度");//12
        strings.add("删除开关量");//13
        strings.add("删除电流");//14
        strings.add("删除电压");//15
        strings.add("删除模拟电流1");//16
        strings.add("删除模拟电压1");//17
        strings.add("地图定位");//18
        strings.add("开关量检测");//19
        strings.add("报警设置");//20
        strings.add("模拟量检测");//21
        strings.add("点动设置");//22
        strings.add("联动设置");//23
        strings.add("模拟量联动");//24
        strings.add("报警");//25
        strings.add("485数据");
        adapter=new MyAdapter(this,strings);

        grid_list.setAdapter(adapter);
        grid_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        sendBasic(topicName1,1);
                        sendBasic(topicName2,1);
                        break;
                    case 1:
                        sendTimer(topicName1,0,1);
                        break;
                    case 2:
                        sendTimer(topicName1,1,1);
                        break;
                    case 3:
                        sendLinkedSet(topicName1,0x34,1);
                        break;
                    case 4:
                        sendLinkedSet(topicName1,0x35,1);
                        break;
                    case 5:
                        sendLinkedSet(topicName1,0x36,1);
                        break;
                    case 6:
                        sendLinkedSet(topicName1,0x37,1);
                        break;
                    case 7:
                        sendLinkedSet(topicName1,0x38,1);
                        break;
                    case 8:
                        sendMoniLink(topicName1,0,0,1);
                        break;
                    case 9:
                        sendMoniLink(topicName1,1,4,1);
                        break;
                    case 10:
                        sendTimer(topicName1,0,5);
                        break;
                    case 11:
                        sendLinkedSet(topicName1,0x34,5);
                        break;
                    case 12:
                        sendLinkedSet(topicName1,0x35,5);
                        break;
                    case 13:
                        sendLinkedSet(topicName1,0x36,5);
                        break;
                    case 14:
                        sendLinkedSet(topicName1,0x37,5);
                        break;
                    case 15:
                        sendLinkedSet(topicName1,0x38,5);
                        break;
                    case 16:
                        sendMoniLink(topicName1,0,0,5);
                        break;
                    case 17:
                        sendMoniLink(topicName1,1,4,5);
                        break;
                    case 18:
                        sendLocation(topicName1);
                        break;
                    case 19:
                        sendSwitch(topicName1);
                        break;
                    case 20:
                        sendAlerm(topicName1);
                        break;
                    case 21:
                        sendMoni(topicName1);
                        break;
                    case 22:
                        sendJog(topicName1,0,5);
                        break;
                    case 23:
                        sendLinkedSwitch(topicName1,0,224);
                        break;
                    case 24:
                        sendMoniLinkSwitch(topicName1);
                        break;
                    case 25:
                        sendAlerm(topicName1,0x88);
                        break;
                    case 26:
                        send485(topicName1);
                        break;
//                    strings.add("开关量检测");//19
//                    strings.add("报警设置");//20
//                    strings.add("模拟量检测");//21
//                    strings.add("点动设置");//22
//                    strings.add("联动设置");//23
//                    strings.add("模拟量联动");//24
//                    strings.add("报警");//25
                }
            }
        });
    }
    class MyAdapter extends BaseAdapter{

        private Context context;
        private List<String> list;

        public MyAdapter(Context context, List<String> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public String getItem(int position) {
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
            String name=getItem(position);
            viewHolder.tv_line.setText(name);
            return convertView;
        }
    }
    class ViewHolder {
        @BindView(R.id.tv_line)
        TextView tv_line;

        public ViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }
    }

    String topicName1="qjjc/gateway/8655330313815/client_to_server";
    String topicName2="qjjc/gateway/865373040894557/client_to_server";
    int state=1;
    int count=0;
    int ss=1;
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
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
    private void sendBasic(String topicName,int state){
        try {
            byte[]bytes=new byte[59];
            bytes[0]= (byte) 0x3c;
            bytes[1]=0x11;
            bytes[2]=0;
            bytes[3]=0x0d;
            bytes[4]= (byte) state;
            bytes[5]= (byte) 255;
            bytes[6]= (byte) 255;
            bytes[7]=32;
            bytes[8]=32;
            bytes[9]=96;
            bytes[10]=32;
            bytes[11]=1;
            for (int i = 12; i <44 ; i++) {
                bytes[i]= (byte) i;
            }
            bytes[44]=0x04;
            bytes[45]= (byte) 0xA6;
            bytes[46]=0x02;
            bytes[47]= (byte) 0x80;
            bytes[48]=0;
            bytes[49]=0x7d;
            bytes[50]=0;
            bytes[51]=0x7d;
            int sum=0;
            for (int i = 0; i <bytes.length ; i++) {
                sum+=bytes[i];
            }
            bytes[57]= (byte) (sum%256);
            bytes[58]=0x46;
            boolean success=mqService.publish(topicName,1,bytes);
            Log.i("DemoActivity","-->"+success+"#"+topicName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void sendTimer(String topicName1,int only,int state){
        byte[] bytes=new byte[23];
        bytes[0]=0x3c;
        bytes[1]=0x22;
        bytes[2]=0;
        bytes[3]=0x11;

        if (only==0){
            bytes[4]=0x11;
            bytes[5]=7;
            bytes[6]= (byte) 226;
            bytes[7]=3;
            bytes[8]=13;
            bytes[9]= (byte) 224;
            bytes[10]=14;
            bytes[11]=12;
            bytes[12]= (byte) 224;
            bytes[13]=32;
            bytes[14]=1;
            bytes[15]= (byte) state;
        }else {
            bytes[4]=0x22;
            bytes[10]=14;
            bytes[11]=12;
            bytes[12]= (byte) 224;
            bytes[13]=32;
            bytes[14]=1;
            bytes[15]= (byte) state;
        }

        int sum=0;
        for (int i = 0; i <bytes.length ; i++) {
            sum+=bytes[i];
        }
        bytes[21]= (byte) (sum%256);
        bytes[22]=0x46;
        boolean success=mqService.publish(topicName1,1,bytes);
        Log.i("DemoActivity","-->"+success+"#"+topicName1);

    }

    int line=1;
    private void sendAlerm(String topicName,int alerm){
        byte[] bytes=new byte[13];
        bytes[0]=0x3c;
        bytes[1]= (byte) 0x99;
        bytes[2]=0;
        bytes[3]=0x07;
        if (count2==0){
            bytes[4]=0x11;
        }else if (count2==1){
            bytes[4]=0x22;
        }else  if (count2==2){
            bytes[4]=0x33;
        }else if (count2==3){
            bytes[4]=0x44;
        }else if (count2==4){
            bytes[4]=0x55;
        }else if (count2==5){
            bytes[4]=0x66;
        }else if (count2==6){
            bytes[4]=0x77;
        }else if (count2==7){
            bytes[4]= (byte) 0x88;
        }
        bytes[5]= (byte) line;
        int sum=0;
        for (int i = 0; i <bytes.length ; i++) {
            sum+=bytes[i];
        }
        bytes[11]= (byte) (sum%256);
        bytes[12]=0x46;
        boolean success=mqService.publish(topicName,1,bytes);
        if (line>=16)
            line=1;
        else
            line++;
        if (count2==7){
            count2=0;
        }else {
            count2++;
        }
        Log.i("DemoActivity","-->"+success+"#"+topicName1);
    }
    public void sendSwitch(String topicName){
        byte[] bytes=new byte[13];
        bytes[0]=0x3c;
        bytes[1]=0x55;
        bytes[2]=0;
        bytes[3]=0x07;
        bytes[4]= (byte) 224;
        bytes[5]= (byte) 224;
        int sum=0;
        for (int i = 0; i <bytes.length ; i++) {
            sum+=bytes[i];
        }
        bytes[11]= (byte) (sum%256);
        bytes[12]=0x46;
        boolean success=mqService.publish(topicName,1,bytes);
    }
    public void sendMoni(String topicName){
        byte[] bytes=new byte[27];
        bytes[0]=0x3c;
        bytes[1]= (byte) 0x88;
        bytes[2]=0;
        bytes[3]=0x15;
        for (byte i = 4; i <20 ; i++) {
            bytes[i]=1;
        }
        int sum=0;
        for (int i = 0; i <bytes.length ; i++) {
            sum+=bytes[i];
        }
        bytes[25]= (byte) (sum%256);
        bytes[26]=0x46;
        boolean success=mqService.publish(topicName,1,bytes);
        Log.i("DemoActivity","-->"+success+"#"+topicName1);
    }
    public void sendOffline(){
        String topicName1="qjjc/gateway/8655330313815/lwt";
//        boolean success=mqService.publish(topicName1,1,"offline");
//        Log.i("DemoActivity","-->"+success+"#"+topicName1);
    }
    public boolean sendLinkedSwitch(String topicName, int mcuVerion, int data) {
        boolean success = false;
        try {
            byte[] bytes = new byte[12];
            bytes[0] = (byte) 0x3c;
            bytes[1] = 0x33;
            bytes[2] = (byte) mcuVerion;
            bytes[3] = 0x06;
            bytes[4] = (byte) data;
            int sum = 0;
            for (int i = 0; i < bytes.length; i++) {
                sum += bytes[i];
            }
            bytes[10] = (byte) (sum % 256);
            bytes[11] = 0x46;
            success = mqService.publish(topicName, 1, bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return success;
    }
    public boolean sendLinkedSet(String topicName,int funCode,int state) {
        boolean success = false;
        try {
            int mcuVersion = 0;
            int condition = 1;//触发条件
            int triState = 1;//触发条件状态
            int conditionState =11;//控制状态
            int preLines = 192;
            int lastLines = 64;
            int triType = 1;//0单次触发，1循环触发
            int[] x = new int[8];
            int type=2;
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

            if (triType == 1) {
                x[3] = 1;
            } else {
                x[2] = 0;
            }

            int triType2 = TenTwoUtil.changeToTen(x);
            byte[] bytes = new byte[16];
            bytes[0] = 0x3c;
            bytes[1] = (byte)funCode ;
            bytes[2] = (byte) mcuVersion;
            bytes[3] = 0x0a;
            bytes[4] = (byte) condition;
            bytes[5] = (byte) preLines;
            bytes[6] = (byte) lastLines;
            bytes[7] = (byte) triType2;
            bytes[8] = (byte) state;
            int sum = 0;
            for (int i = 0; i < bytes.length; i++) {
                sum += bytes[i];
            }
            int checkCode = sum % 256;
            bytes[14] = (byte) checkCode;
            bytes[15] = 0x46;

            success = mqService.publish(topicName, 1, bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean sendMoniLinkSwitch(String topicName) {
        try {
            byte[] bytes = new byte[12];
            bytes[0] = (byte) 0x3c;
            bytes[1] = 0x3a;
            bytes[2] = (byte) 1;
            bytes[3] = 0x06;
            int x = 0xC0;
            bytes[4] = (byte) x;
            int sum = 0;
            for (int i = 0; i < bytes.length; i++) {
                sum += bytes[i];
            }
            bytes[10] = (byte) (sum % 256);
            bytes[11] = 0x46;
            boolean success = mqService.publish(topicName, 1, bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean sendMoniLink(String topicName,int type,int num,int state) {
        boolean success = false;
        try {
            int headCode = 0x3c;
            int funCode = 0x39;
            int mcuVersion =0;
            int length = 0x0a;
            int contition =41;
            int triState =1;
            int preLine = 192;
            int lastLine =192;
            int controlState = 1;
            int triType = 1;

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
            bytes[15] = 0x46;
            success = mqService.publish(topicName, 1, bytes);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean sendJog(String topicName, int mcuVerion, int jog) {
        boolean success = false;
        try {
            int jogHigh = (jog / 256);
            int jogLow = (jog % 256);

            byte[] bytes = new byte[13];
            bytes[0] = 0x3c;
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
            bytes[12] = 0x56;
            success = mqService.publish(topicName, 1, bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return success;
    }
    public boolean sendInterLine(String topicName) {
        boolean success = false;
        try {
            byte[] bytes = new byte[27];
            bytes[0] = (byte) 0x3c;
            bytes[1] = 0x46;
            bytes[2] = 0;
            bytes[3] = 0x15;
            bytes[4]=1;
            bytes[5]=3;
            bytes[6]=7;
            bytes[7]=12;
            bytes[8]=5;
            bytes[9]=10;
            int sum = 0;
            for (int i = 0; i < bytes.length; i++) {
                sum += bytes[i];
            }
            bytes[25] = (byte) (sum % 256);
            bytes[26] = 0x46;
            success = mqService.publish(topicName, 1, bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
    int count2=0;
    public boolean sendAlerm(String topicName) {
        boolean success = false;
        try {
            byte[] bytes = new byte[28];
            bytes[0] = (byte) 0x90;
            bytes[1] = 0x66;
            bytes[2] =0;
            bytes[3] = 0x16;
            bytes[4]= (byte) 224;
            bytes[5]=0;
            bytes[6]=125;
            bytes[7]=0x11;
            bytes[8]= 0;
            bytes[9]= (byte) 131;
            bytes[10]=0x11;
            bytes[11]=0;
            bytes[12]= (byte) 163;
            bytes[13]=0x11;
            bytes[14]=0;
            bytes[15]=4;
            bytes[16]=0x11;
            bytes[17]=0;
            bytes[18]= (byte) 256;
            bytes[19]=0x11;
            bytes[20]=1;
            int sum = 0;
            for (int i = 0; i < bytes.length; i++) {
                sum += bytes[i];
            }
            bytes[26] = (byte) (sum % 256);
            bytes[27] = 0x09;
            success = mqService.publish(topicName, 1, bytes);
            count2++;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void send485(String topicName1){
        byte[] bytes=new byte[26];
        bytes[0]=0x3c;
        bytes[1]= (byte) 0xaa;
        bytes[2]=0x01;
        bytes[3]=0x15;
        bytes[4]=0x01;
        bytes[5]=0x02;
        bytes[6]=0x03;
        bytes[7]=0x04;
        bytes[7]=0x05;
        bytes[8]=0x06;
        bytes[9]=0x07;
        bytes[10]=0x08;
        bytes[11]=0x09;
        bytes[12]=0x0a;
        bytes[13]=0x0b;
        bytes[14]=0x0c;
        bytes[15]=0x0d;
        bytes[16]=0x0e;
        bytes[17]=0x0f;
        bytes[18]=0x10;
        int sum=0;
        for (int i = 0; i <bytes.length ; i++) {
            sum+=bytes[i];
        }
        bytes[24]= (byte) (sum%256);
        bytes[25]=0x46;
        boolean success = mqService.publish(topicName1, 1, bytes);

    }


    public void sendLocation(String topicName1){
        byte[] bytes=new byte[22];
        bytes[0]=0x3c;
        bytes[1]=0x77;
        bytes[2]=0;
        bytes[3]=10;
        bytes[4]=0x14;
        bytes[5]=0x11;
        bytes[6]= (byte) 0xA0;
        bytes[7]=0x1D;
        bytes[8]=0x38;
        bytes[9]=0x45;
        bytes[10]=0x48;
        bytes[11]=0x6A;
        bytes[12]=0x2D;
        bytes[13]=0x12;
        bytes[14]=0x60;
        int sum=0;
        for (int i = 0; i <bytes.length ; i++) {
            sum+=bytes[i];
        }
        bytes[20]= (byte) (sum%256);
        bytes[21]=0x46;
        boolean success = mqService.publish(topicName1, 1, bytes);

    }
}
