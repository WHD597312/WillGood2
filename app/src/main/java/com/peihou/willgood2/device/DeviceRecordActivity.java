package com.peihou.willgood2.device;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.donkingliang.groupedadapter.adapter.GroupedRecyclerViewAdapter;
import com.donkingliang.groupedadapter.holder.BaseViewHolder;
import com.google.gson.Gson;
import com.peihou.willgood2.BaseActivity;
import com.peihou.willgood2.R;
import com.peihou.willgood2.pojo.Device;
import com.peihou.willgood2.pojo.OperatorLog;
import com.peihou.willgood2.utils.ToastUtil;
import com.peihou.willgood2.utils.http.BaseWeakAsyncTask;
import com.peihou.willgood2.utils.http.HttpUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.media.CamcorderProfile.get;

public class DeviceRecordActivity extends BaseActivity {

    @BindView(R.id.tv_share) TextView tv_share;//分享操作
    @BindView(R.id.tv_operate_log) TextView tv_operate_log;//操作日志
    @BindView(R.id.tv_alerm_log) TextView tv_alerm_log;//报警日志
    @BindView(R.id.rl_share) RecyclerView rl_share;//分享操作记录列表

    @BindView(R.id.rl_operate) RecyclerView rl_operate;//分享操作记录列表
    @BindView(R.id.rl_operate2) RecyclerView rl_operate2;//报警日志列表
    private OperaterLogAdapter logAdapter;//操作日志适配器
    private List<OperatorLog> logs=new ArrayList<>();//操作日志列表
    private AlermLogAdapter alermAdapter;//报警日志适配器
    private List<OperatorLog> alerms=new ArrayList<>();
    private List<List<Device>> devices=new ArrayList<>();
    private DeviceSharedAdapter sharedAdapter;


    private List<String> usersInfo=new ArrayList<>();
    @Override
    public void initParms(Bundle parms) {

    }

    int userId;
    SharedPreferences preferences;
    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_device_record;
    }

    Map<String,Object> params=new HashMap<>();
    @Override
    public void initView(View view) {


        preferences=getSharedPreferences("userInfo",Context.MODE_PRIVATE);
        userId=preferences.getInt("userId",0);

        sharedAdapter=new DeviceSharedAdapter(this,usersInfo,devices);

        rl_share.setLayoutManager(new LinearLayoutManager(this));
        rl_share.setAdapter(sharedAdapter);


        rl_operate.setLayoutManager(new LinearLayoutManager(this));
        logAdapter=new OperaterLogAdapter(this,logs);
        rl_operate.setAdapter(logAdapter);

        rl_operate2.setLayoutManager(new LinearLayoutManager(this));
        alermAdapter=new AlermLogAdapter(this,alerms);
        rl_operate2.setAdapter(alermAdapter);
        params.put("userId",userId);
        new SharedDeviceAsync(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);

    }

    @Override
    public void doBusiness(Context mContext) {

    }
    int operator=0;//0为分享操作，1为操作日志，2为报警日志
    @OnClick({R.id.img_back,R.id.tv_share,R.id.tv_operate_log,R.id.tv_alerm_log})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.img_back:
                finish();
                break;
            case R.id.tv_share:
                if (operator==0){
                    break;
                }
                operator=0;
                tv_share.setTextSize(22);
                tv_operate_log.setTextSize(16);
                tv_alerm_log.setTextSize(16);
                rl_share.setVisibility(View.VISIBLE);
                rl_operate.setVisibility(View.GONE);
                rl_operate2.setVisibility(View.GONE);

                break;
            case R.id.tv_operate_log:
                if (operator==1){
                    break;
                }
                operator=1;
                tv_share.setTextSize(16);
                tv_operate_log.setTextSize(22);
                tv_alerm_log.setTextSize(16);
                rl_share.setVisibility(View.GONE);
                rl_operate.setVisibility(View.VISIBLE);
                rl_operate2.setVisibility(View.GONE);
                break;
            case R.id.tv_alerm_log:
                if (operator==2){
                    break;
                }
                operator=2;
                tv_share.setTextSize(16);
                tv_operate_log.setTextSize(16);
                tv_alerm_log.setTextSize(22);
                rl_share.setVisibility(View.GONE);
                rl_operate.setVisibility(View.GONE);
                rl_operate2.setVisibility(View.VISIBLE);
                break;
        }
    }



    class DeviceSharedAdapter extends GroupedRecyclerViewAdapter{

        private Context context;
        private List<String> userInfos;
        List<List<Device>> devices;

        public DeviceSharedAdapter(Context context, List<String> userInfos, List<List<Device>> devices) {
            super(context);
            this.context = context;
            this.userInfos = userInfos;
            this.devices = devices;
        }

        @Override
        public int getGroupCount() {
            return userInfos.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return devices.get(groupPosition).size();
        }

        @Override
        public boolean hasHeader(int groupPosition) {
            return true;
        }

        @Override
        public boolean hasFooter(int groupPosition) {
            return false;
        }

        @Override
        public int getHeaderLayout(int viewType) {
            return R.layout.item_operate_share_header;
        }

        @Override
        public int getFooterLayout(int viewType) {
            return 0;
        }

        @Override
        public int getChildLayout(int viewType) {
            return R.layout.item_operate_share_body;
        }

        @Override
        public void onBindHeaderViewHolder(BaseViewHolder holder, int groupPosition) {
            String name=userInfos.get(groupPosition);
            TextView tv_user=holder.itemView.findViewById(R.id.tv_user);
            String name2=name.substring(name.indexOf("&")+1);
            Log.i("UserNameName","-->"+name2);
            tv_user.setText("用户:"+name2);
        }

        @Override
        public void onBindFooterViewHolder(BaseViewHolder holder, int groupPosition) {

        }

        @Override
        public void onBindChildViewHolder(BaseViewHolder holder, final int groupPosition, final int childPosition) {
            RelativeLayout rl_body=holder.itemView.findViewById(R.id.rl_body);
            if (childPosition%2==0){
                if (childPosition==0){
                    rl_body.setBackgroundResource(R.drawable.shape_operate_share_top);
                }else {
                    rl_body.setBackgroundColor(Color.parseColor("#F0F0F0"));
                }
            }else if (childPosition%2!=0){
                rl_body.setBackgroundColor(Color.parseColor("#FFFFFF"));
            }
            final Device device=devices.get(groupPosition).get(childPosition);
            if (device!=null){
                String deviceName=device.getDeviceName();
                TextView tv_name=holder.itemView.findViewById(R.id.tv_name);
                tv_name.setText(deviceName);
            }
            TextView tv_unbind=holder.itemView.findViewById(R.id.tv_unbind);
            tv_unbind.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String userInfo=userInfos.get(groupPosition);
                    String ss=userInfo.substring(0,userInfo.indexOf("&"));
                    long deviceId=device.getDeviceId();
                    params.clear();
                    params.put("deviceId",deviceId);
                    params.put("deviceSharerId",ss);
                    params.put("groupPosition",groupPosition);
                    params.put("childPosition",childPosition);
                    new UnbindDeviceAsync(DeviceRecordActivity.this).execute(params);
                }
            });
        }
    }
    class OperaterLogAdapter extends RecyclerView.Adapter<OperatorHolder>{

        private Context context;
        private List<OperatorLog> list;

        public OperaterLogAdapter(Context context, List<OperatorLog> list) {
            this.context = context;
            this.list = list;
        }

        @NonNull
        @Override
        public OperatorHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view=View.inflate(context,R.layout.item_operate_log,null);
            return new OperatorHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull OperatorHolder holder, int position) {
            int last=list.size()-1;
            if (position%2==0){
                if (position==0){
                    holder.rl_body.setBackgroundResource(R.drawable.shape_operate_share_top);
                }else {
                    holder.rl_body.setBackgroundColor(Color.parseColor("#F0F0F0"));
                }
            }else if (position%2!=0){
                holder.rl_body.setBackgroundColor(Color.parseColor("#FFFFFF"));
//                if (position==last){
//                    holder.rl_body.setBackgroundResource(R.drawable.shape_operate_share_bottom);
//                }else {
//                    holder.rl_body.setBackgroundColor(Color.parseColor("#FFFFFF"));
//                }
            }


            OperatorLog log=list.get(position);
            String userPhone=log.getUserPhone();
            String deviceName=log.getDeviceName();
            String deviceLine=log.getDeviceLine();
            String deviceLogTime=log.getDeviceLogTime();
            int deviceControll=log.getDeviceControll();

            holder.tv_user.setText(userPhone+"       "+deviceName+"     "+deviceLine);
            holder.tv_timer.setText(deviceLogTime+"");

            if (deviceControll==1){
                holder.tv_device_state.setText("打开设备");
                holder.tv_device_state.setTextColor(Color.parseColor("#09c585"));
            }else {
                holder.tv_device_state.setText("关闭设备");
                holder.tv_device_state.setTextColor(Color.parseColor("#fe7918"));
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }
    class AlermLogAdapter extends RecyclerView.Adapter<OperatorHolder>{

        private Context context;
        private List<OperatorLog> list;

        public AlermLogAdapter(Context context, List<OperatorLog> list) {
            this.context = context;
            this.list = list;
        }

        @NonNull
        @Override
        public OperatorHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view=View.inflate(context,R.layout.item_operate_log,null);
            return new OperatorHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull OperatorHolder holder, int position) {
            int last=list.size()-1;
            if (position%2==0){
                if (position==0){
                    holder.rl_body.setBackgroundResource(R.drawable.shape_operate_share_top);
                }else {
                    holder.rl_body.setBackgroundColor(Color.parseColor("#F0F0F0"));
                }
            }else if (position%2!=0){
                holder.rl_body.setBackgroundColor(Color.parseColor("#FFFFFF"));
//                if (position==last){
//                    holder.rl_body.setBackgroundResource(R.drawable.shape_operate_share_top);
//                }else {
//                    holder.rl_body.setBackgroundColor(Color.parseColor("#FFFFFF"));
//                }
            }
            OperatorLog log=list.get(position);

            String deviceName=log.getDeviceName();
            String deviceLine=log.getDeviceLine();
            String deviceLogTime=log.getDeviceLogTime();
            int deviceControll=log.getDeviceControll();

            holder.tv_user.setText(deviceName+"     "+deviceLine);
            holder.tv_timer.setText(deviceLogTime+"");
            holder.tv_device_state.setTextColor(Color.parseColor("#fe7918"));
            if (deviceControll==1){
                holder.tv_device_state.setText("来电报警");
            }else if (deviceControll==2){
                holder.tv_device_state.setText("断电报警报警");
            }else if (deviceControll==3){
                holder.tv_device_state.setText("温度报警");
            }else if (deviceControll==4){
                holder.tv_device_state.setText("湿度报警");
            }else if (deviceControll==5){
                holder.tv_device_state.setText("电流报警");
            }else if (deviceControll==6){
                holder.tv_device_state.setText("电压报警");
            }else if (deviceControll==7){
                holder.tv_device_state.setText("功率报警");
            }else if (deviceControll==8){
                holder.tv_device_state.setText("开关量报警");
            }else {
                holder.tv_device_state.setText("来电报警");
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    class OperatorHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.rl_body) RelativeLayout rl_body;
        @BindView(R.id.tv_user) TextView tv_user;//操作用户
        @BindView(R.id.tv_timer) TextView tv_timer;//操作时间
        @BindView(R.id.tv_device_state) TextView tv_device_state;//设备开关状态
        public OperatorHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    class SharedDeviceAsync extends BaseWeakAsyncTask<Map<String,Object>,Void,Integer,DeviceRecordActivity>{

        public SharedDeviceAsync(DeviceRecordActivity deviceRecordActivity) {
            super(deviceRecordActivity);
        }

        @Override
        protected Integer doInBackground(DeviceRecordActivity deviceRecordActivity, Map<String, Object>... maps) {
            int code=0;
            try {
                Map<String,Object> map=maps[0];
                String url=HttpUtils.ipAddress+"device/getSharedDeviceList";
                String result=HttpUtils.requestPost(url,map);
                Log.i("SharedDeviceAsync","-->"+result);
                if (!TextUtils.isEmpty(result)){
                    JSONObject jsonObject=new JSONObject(result);
                    code=jsonObject.getInt("returnCode");
                    if (code==100){
                        JSONArray returnData=jsonObject.getJSONArray("returnData");
                        for (int i = 0; i <returnData.length(); i++) {
                            JSONObject jsonObject2=returnData.getJSONObject(i);
                            int sharerId=jsonObject2.getInt("sharerId");
                            String sharerName=jsonObject2.getString("sharerName");
                            String name=sharerId+"&"+sharerName;
                            usersInfo.add(name);
                            JSONArray deviceShareList=jsonObject2.getJSONArray("deviceShareList");
                            List<Device> list=new ArrayList<>();
                            for (int j = 0; j <deviceShareList.length() ; j++) {
                                String s=deviceShareList.getJSONObject(j).toString();
                                Gson gson=new Gson();
                                Device device=gson.fromJson(s,Device.class);
                                list.add(device);
                            }
                            devices.add(list);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return code;
        }

        @Override
        protected void onPostExecute(DeviceRecordActivity deviceRecordActivity, Integer integer) {
            if (integer==100){
                sharedAdapter.notifyDataSetChanged();
            }
            try {
                params.put("deviceLogType",1);
                params.put("pageNum",1);
                new OperateAsync(DeviceRecordActivity.this).execute(params);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    class OperateAsync extends  BaseWeakAsyncTask<Map<String,Object>,Void,Integer,DeviceRecordActivity>{

        public OperateAsync(DeviceRecordActivity deviceRecordActivity) {
            super(deviceRecordActivity);
        }

        @Override
        protected Integer doInBackground(DeviceRecordActivity deviceRecordActivity, Map<String, Object>... maps) {
            int code=0;
            try {
                Map<String,Object> map=maps[0];
                String url=HttpUtils.ipAddress+"data/getOperationLog";
                String result=HttpUtils.requestPost(url,map);
                if (!TextUtils.isEmpty(result)){
                    JSONObject jsonObject=new JSONObject(result);
                    code=jsonObject.getInt("returnCode");
                    if (code==100){
                        JSONObject returnData=jsonObject.getJSONObject("returnData");
                        JSONArray deviceOperationLogList=returnData.getJSONArray("deviceOperationLogList");
                        for (int i = 0; i <deviceOperationLogList.length() ; i++) {
                            String s=deviceOperationLogList.getJSONObject(i).toString();
                            Gson gson=new Gson();
                            OperatorLog operatorLog=gson.fromJson(s,OperatorLog.class);
                            logs.add(operatorLog);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return code;
        }

        @Override
        protected void onPostExecute(DeviceRecordActivity deviceRecordActivity, Integer integer) {
            if (integer==100){
                logAdapter.notifyDataSetChanged();
            }
            params.put("deviceLogType",2);
            try {
                new AlermAsync(DeviceRecordActivity.this).execute(params);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class AlermAsync extends  BaseWeakAsyncTask<Map<String,Object>,Void,Integer,DeviceRecordActivity>{

        public AlermAsync(DeviceRecordActivity deviceRecordActivity) {
            super(deviceRecordActivity);
        }

        @Override
        protected Integer doInBackground(DeviceRecordActivity deviceRecordActivity, Map<String, Object>... maps) {
            int code=0;
            try {
                Map<String,Object> map=maps[0];
                String url=HttpUtils.ipAddress+"data/getOperationLog";
                String result=HttpUtils.requestPost(url,map);
                if (!TextUtils.isEmpty(result)){
                    JSONObject jsonObject=new JSONObject(result);
                    code=jsonObject.getInt("returnCode");
                    if (code==100){
                        JSONObject returnData=jsonObject.getJSONObject("returnData");
                        JSONArray deviceOperationLogList=returnData.getJSONArray("deviceOperationLogList");
                        for (int i = 0; i <deviceOperationLogList.length() ; i++) {
                            String s=deviceOperationLogList.getJSONObject(i).toString();
                            Gson gson=new Gson();
                            OperatorLog operatorLog=gson.fromJson(s,OperatorLog.class);
                            alerms.add(operatorLog);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return code;
        }

        @Override
        protected void onPostExecute(DeviceRecordActivity deviceRecordActivity, Integer integer) {
            if (integer==100){
                alermAdapter.notifyDataSetChanged();
            }
        }
    }

    class UnbindDeviceAsync extends BaseWeakAsyncTask<Map<String,Object>,Void,Integer,DeviceRecordActivity>{

        public UnbindDeviceAsync(DeviceRecordActivity deviceRecordActivity) {
            super(deviceRecordActivity);
        }

        @Override
        protected Integer doInBackground(DeviceRecordActivity deviceRecordActivity, Map<String, Object>... maps) {
            int code=0;
            try {
                int groupPosition= (int) params.get("groupPosition");
                int childPosition= (int) params.get("childPosition");
                Map<String,Object> params=maps[0];
                params.remove("groupPosition");
                params.remove("childPosition");
                String url=HttpUtils.ipAddress+"device/deleteShareDevice";
                String result=HttpUtils.requestPost(url,params);
                if (!TextUtils.isEmpty(result)){
                    JSONObject jsonObject=new JSONObject(result);
                    code=jsonObject.getInt("returnCode");
                    if (code==100){
                        devices.get(groupPosition).remove(childPosition);
                        if (devices.get(groupPosition).isEmpty()){
                            usersInfo.remove(groupPosition);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return code;
        }

        @Override
        protected void onPostExecute(DeviceRecordActivity deviceRecordActivity, Integer integer) {
            if (integer==100){
                ToastUtil.showShort(DeviceRecordActivity.this,"解除成功");
                sharedAdapter.notifyDataSetChanged();
            }else {
                ToastUtil.showShort(DeviceRecordActivity.this,"解除失败");
            }
        }
    }
}
