package com.peihou.willgood2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;

public class RS485Activity extends BaseActivity {


    @BindView(R.id.tv_rs485) TextView tv_rs485;
    @BindView(R.id.btn_485_10) Button btn_485_10;
    @BindView(R.id.btn_485_2) Button btn_485_2;
    @BindView(R.id.btn_485_16) Button btn_485_16;
    @BindView(R.id.btn_485_ASCII) Button btn_485_ASCII;
    String rs485;
    String deviceMac;
    @Override
    public void initParms(Bundle parms) {
        deviceMac=parms.getString("deviceMac");
        rs485=parms.getString("rs485");
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_rs485;
    }

    @Override
    public void initView(View view) {
//        rs485="1 2 3 4 5 6 4 8 91 2 3 4 5 6 4 8 9 1 2 3 4 5 6 4 8 9 1 2 3 4 5 6 4 8 9/";
        if (!TextUtils.isEmpty(rs485)){
            tv_rs485.setText(rs485+"");
        }
        receiver=new MessageReceiver();
        IntentFilter filter=new IntentFilter("RS485Activity");
        registerReceiver(receiver,filter);
    }
    public static  boolean running=false;

    @Override
    protected void onStart() {
        super.onStart();
        running=true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        running=false;
    }

    int key=0;
    @OnClick({R.id.back,R.id.btn_485_10,R.id.btn_485_2,R.id.btn_485_16,R.id.btn_485_ASCII})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.btn_485_10:
                if (key==0){
                    break;
                }
                key=0;
                btn_485_10.setTextColor(getResources().getColor(R.color.base_back));
                btn_485_2.setTextColor(getResources().getColor(R.color.color_blue));
                btn_485_16.setTextColor(getResources().getColor(R.color.color_blue));
                btn_485_ASCII.setTextColor(getResources().getColor(R.color.color_blue));
                if (!TextUtils.isEmpty(rs485)){
                    tv_rs485.setText(rs485+"");
                }
                break;
            case R.id.btn_485_2:
                if (key==1){
                    break;
                }
                btn_485_10.setTextColor(getResources().getColor(R.color.color_blue));
                btn_485_2.setTextColor(getResources().getColor(R.color.base_back));
                btn_485_16.setTextColor(getResources().getColor(R.color.color_blue));
                btn_485_ASCII.setTextColor(getResources().getColor(R.color.color_blue));
                if (!TextUtils.isEmpty(rs485)){
                    String s=toBinary(rs485);
                    tv_rs485.setText(s);
                    key=1;

                }
                break;
            case R.id.btn_485_16:
                if (key==2){
                    break;
                }
                key=2;
                btn_485_10.setTextColor(getResources().getColor(R.color.color_blue));
                btn_485_2.setTextColor(getResources().getColor(R.color.color_blue));
                btn_485_16.setTextColor(getResources().getColor(R.color.base_back));
                btn_485_ASCII.setTextColor(getResources().getColor(R.color.color_blue));
                if (!TextUtils.isEmpty(rs485)){
                    String s=strTo16(rs485);
                    tv_rs485.setText(s);
                }
                break;
            case R.id.btn_485_ASCII:
                if (key==3){
                    break;
                }
                key=3;
                btn_485_10.setTextColor(getResources().getColor(R.color.color_blue));
                btn_485_2.setTextColor(getResources().getColor(R.color.color_blue));
                btn_485_16.setTextColor(getResources().getColor(R.color.color_blue));
                btn_485_ASCII.setTextColor(getResources().getColor(R.color.base_back));
                if (!TextUtils.isEmpty(rs485)){
                    String s=stringToAscii(rs485);
                    tv_rs485.setText(s);
                }
                break;

        }
    }

    public String toBinary(String str){
        char[] strChar=str.toCharArray();
        String result="";
        for(int i=0;i<strChar.length;i++){
            result +=Integer.toBinaryString(strChar[i])+ " ";
        }
        return result;
    }

    public  String strTo16(String s) {
        String str = "";
        for (int i = 0; i < s.length(); i++) {
            int ch = (int) s.charAt(i);
            String s4 = Integer.toHexString(ch);
            if (i==s.length()-1){
                str = str + s4;
            }else {
                str = str + s4+",";
            }
        }
        return str;
    }
    public  String stringToAscii(String value)
    {
        StringBuffer sbu = new StringBuffer();
        char[] chars = value.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if(i != chars.length - 1)
            {
                sbu.append((int)chars[i]).append(",");
            }
            else {
                sbu.append((int)chars[i]);
            }
        }
        return sbu.toString();
    }
    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver!=null){
            unregisterReceiver(receiver);
        }
    }

    MessageReceiver receiver;
    class MessageReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String macAddress=intent.getStringExtra("macAddress");
                if (macAddress.equals(deviceMac)){
                    rs485=intent.getStringExtra("rs485");
                    if (!TextUtils.isEmpty(rs485)){
                        if (key==0){
                            tv_rs485.setText(rs485);
                        }else if (key==1){
                            String s=toBinary(rs485);
                            tv_rs485.setText(s);
                        }else if (key==2){
                            String s=strTo16(rs485);
                            tv_rs485.setText(s);
                        }else if (key==3){
                            String s=stringToAscii(rs485);
                            tv_rs485.setText(s);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
