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

import com.peihou.willgood2.R;
import com.peihou.willgood2.pojo.TimerTask;
import com.peihou.willgood2.service.MQService;
import com.peihou.willgood2.utils.TenTwoUtil;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class DemoActivity extends AppCompatActivity {

    Unbinder unbinder;
    TimerTask timerTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        unbinder=ButterKnife.bind(this);
        Intent intent=new Intent(this,MQService.class);
        bindService(intent,connection,Context.BIND_AUTO_CREATE);
        timerTask=new TimerTask();

    }

    String topicName1="qjjc/gateway/8655330313815/client_to_server";
    int state=1;
    int count=0;
    int ss=1;
    @OnClick({R.id.button,R.id.button2,R.id.button3,R.id.button4,R.id.button5,R.id.button6,R.id.button7,
            R.id.button8,R.id.button9,R.id.button10,R.id.button11,R.id.button12,R.id.button13,R.id.button14,
            R.id.button15,R.id.button16,R.id.button17,R.id.button18,R.id.button19,R.id.button20
    })
    public void onClick(View view){
        switch (view.getId()){
            case R.id.button:
                if (mqService!=null){
                    sendBasic(topicName1,ss);
                    ss = ss== 1 ? 0 : 1;
                }
                break;
            case R.id.button2:
                if (mqService!=null){
                    sendTimer(topicName1,0,1);
                }
                break;
            case R.id.button3:
                if (mqService!=null){
                    sendTimer(topicName1,1,1);
                }
                break;
            case R.id.button4:
                if (mqService!=null){
                    sendTimer(topicName1,0,2);
                }
                break;
            case R.id.button5:
                if (mqService!=null){
                    sendTimer(topicName1,1,2);
                }
                break;
            case R.id.button6:
                int alerm=0;
                if (count==0)
                    alerm=0x11;
                else if (count==1)
                    alerm=0x22;
                else if (count==2)
                    alerm=0x33;
                else if (count==3)
                    alerm=0x44;
                else if (count==4)
                    alerm=0x55;
                else if (count==5)
                    alerm=0x66;
                else if (count==6)
                    alerm=0x77;
                else if (count==7)
                    alerm=0x88;
                sendAlerm(topicName1,alerm);
                if (count>=8){
                    count=0;
                }else {
                    count++;
                }
                break;
            case R.id.button7:
                sendSwitch(topicName1);
                break;
            case R.id.button8:
                sendMoni(topicName1);
                break;
            case R.id.button9:
                sendOffline();
                break;
            case R.id.button10:
                sendLinkedSwitch(topicName1,0,232);
                break;
            case R.id.button11:
                sendLinkedSet(topicName1,0x34);
                break;
            case R.id.button12:
                sendLinkedSet(topicName1,0x36);
                break;
            case R.id.button13:
                sendLinkedSet(topicName1,0x37);
                break;
            case R.id.button14:
                sendLinkedSet(topicName1,0x38);
                break;
            case R.id.button15:
                sendMoniLinkSwitch(topicName1);
                break;
            case R.id.button16:
                sendMoniLink(topicName1,0,0);
                break;
            case R.id.button17:
                sendMoniLink(topicName1,1,4);
                break;
            case R.id.button18:
                sendJog(topicName1,0,30);
                break;
            case R.id.button19:
                sendInterLine(topicName1);
                break;
            case R.id.button20:
                sendAlerm(topicName1);
                break;


        }
    }
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
            byte[]bytes=new byte[61];
            bytes[0]=0x3c;
            bytes[1]=0x11;
            bytes[2]=0x01;
            bytes[3]=0x37;
            bytes[4]=0x00;
            bytes[5]= (byte) 0xF0;
            bytes[8]=0x40;
            bytes[44]=0x32;
            bytes[45]=0x32;
            bytes[46]=0x46;
            bytes[47]=0x32;
            bytes[48]=0x46;
            bytes[49]=0x32;
            bytes[50]=0x46;
            bytes[51]=0x32;
            bytes[52]=0x46;
            bytes[53]=0x32;
            bytes[54]=0x01;
            bytes[55]=0x02;
            bytes[56]=0x03;
            bytes[57]=0x04;
            bytes[59]=0x03;
            bytes[60]=0x46;

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
        bytes[4]= (byte) alerm;
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
        boolean success=mqService.publish(topicName1,1,"offline");
        Log.i("DemoActivity","-->"+success+"#"+topicName1);
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
    public boolean sendLinkedSet(String topicName,int funCode) {
        boolean success = false;
        try {
            int mcuVersion = 0;
            int condition = 1;//触发条件
            int triState = 1;//触发条件状态
            int conditionState =11;//控制状态
            int preLines = 192;
            int lastLines = 64;
            int triType = 1;//0单次触发，1循环触发
            int state = 1;
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
            bytes[0] = (byte) 0x30;
            bytes[1] = 0x3a;
            bytes[2] = (byte) 0;
            bytes[3] = 0x06;
            int x = 224;
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
    public boolean sendMoniLink(String topicName,int type,int num) {
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
            int state = 1;

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
    public boolean sendAlerm(String topicName) {
        boolean success = false;
        try {
            byte[] bytes = new byte[28];
            bytes[0] = (byte) 0x90;
            bytes[1] = 0x66;
            bytes[2] =0;
            bytes[3] = 0x11;
            bytes[4]= (byte) 224;
            bytes[5]=0;
            bytes[6]=125;
            bytes[7]=0x11;
            bytes[8]= 0;
            bytes[9]= (byte) 131;
            bytes[10]=0x11;
            bytes[11]=0;
            bytes[12]= (byte) 163;
            bytes[13]=0x22;
            bytes[14]=0;
            bytes[15]=4;
            bytes[16]=0x11;
            bytes[17]=0;
            bytes[18]= (byte) 256;
            bytes[19]=0x22;
            bytes[20]=1;
            int sum = 0;
            for (int i = 0; i < bytes.length; i++) {
                sum += bytes[i];
            }
            bytes[26] = (byte) (sum % 256);
            bytes[27] = 0x09;
            success = mqService.publish(topicName, 1, bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }



}
