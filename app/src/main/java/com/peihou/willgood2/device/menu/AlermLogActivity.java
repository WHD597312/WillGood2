package com.peihou.willgood2.device.menu;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.peihou.willgood2.R;
import com.peihou.willgood2.BaseActivity;
import com.peihou.willgood2.pojo.OperatorLog;
import com.peihou.willgood2.utils.NoFastClickUtils;
import com.peihou.willgood2.utils.ToastUtil;
import com.peihou.willgood2.utils.http.BaseWeakAsyncTask;
import com.peihou.willgood2.utils.http.HttpUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 报警日志
 */
public class AlermLogActivity extends BaseActivity {

    @BindView(R.id.list_log) RecyclerView list_log;
    private List<OperatorLog> list=new ArrayList<>();
    MyAdapter adapter;

    String deviceMac;
    int userId;
    @Override
    public void initParms(Bundle parms) {
        deviceMac=parms.getString("deviceMac");
        userId=parms.getInt("userId",0);
    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_alerm_log;
    }

    Map<String,Object> params=new HashMap<>();
    @Override
    public void initView(View view) {

        adapter=new MyAdapter(this,list);
        list_log.setLayoutManager(new LinearLayoutManager(this));
        list_log.setAdapter(adapter);
        params.put("userId",userId);
        params.put("deviceLogType",2);
        params.put("pageNum",1);
        new AlermAsync(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
    }
    @OnClick({R.id.img_back,R.id.img_log})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.img_back:
                finish();
                break;
            case R.id.img_log:
               if (NoFastClickUtils.isFastClick()){
                   params.clear();
                   params.put("deviceMac",deviceMac);
                   params.put("deviceLogType",2);
                   new DeleteAlermAsync(this).execute(params);
               }
                break;
        }
    }

    @Override
    public void doBusiness(Context mContext) {

    }
    class MyAdapter extends RecyclerView.Adapter<ViewHolder>{

        private Context context;
        private List<OperatorLog> list;

        public MyAdapter(Context context, List<OperatorLog> list) {
            this.context = context;
            this.list = list;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view=View.inflate(context,R.layout.item_log,null);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            OperatorLog log=list.get(position);
            String deviceLine=log.getDeviceLine();
            String deviceLogTime=log.getDeviceLogTime();
            int deviceControll=log.getDeviceControll();

            holder.tv_name.setText(deviceLine);
            if (deviceControll==1){
                holder.tv_type.setText("来电报警");
            }else if (deviceControll==2){
                holder.tv_type.setText("断电报警报警");
            }else if (deviceControll==3){
                holder.tv_type.setText("温度报警");
            }else if (deviceControll==4){
                holder.tv_type.setText("湿度报警");
            }else if (deviceControll==5){
                holder.tv_type.setText("电流报警");
            }else if (deviceControll==6){
                holder.tv_type.setText("电压报警");
            }else if (deviceControll==7){
                holder.tv_type.setText("功率报警");
            }else if (deviceControll==8){
                holder.tv_type.setText("开关量报警");
            }

            holder.tv_timer.setText(deviceLogTime);
            if (position==list.size()-1){
                holder.view.setVisibility(View.GONE);
            }else {
                holder.view.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }
    class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.tv_name) TextView tv_name;
        @BindView(R.id.tv_type) TextView tv_type;
        @BindView(R.id.tv_timer) TextView tv_timer;
        @BindView(R.id.view) View view;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    class AlermAsync extends BaseWeakAsyncTask<Map<String,Object>,Void,Integer,AlermLogActivity> {

        public AlermAsync(AlermLogActivity deviceRecordActivity) {
            super(deviceRecordActivity);
        }

        @Override
        protected Integer doInBackground(AlermLogActivity deviceRecordActivity, Map<String, Object>... maps) {
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
                            list.add(operatorLog);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return code;
        }

        @Override
        protected void onPostExecute(AlermLogActivity deviceRecordActivity, Integer integer) {
            if (integer==100){
               adapter.notifyDataSetChanged();
            }
        }
    }

    class DeleteAlermAsync extends BaseWeakAsyncTask<Map<String,Object>,Void,Integer,AlermLogActivity> {

        public DeleteAlermAsync(AlermLogActivity alermLogActivity) {
            super(alermLogActivity);
        }

        @Override
        protected Integer doInBackground(AlermLogActivity alermLogActivity, Map<String, Object>... maps) {
            int returnCode=0;
            try {
                Map<String,Object> param=maps[0];
                String url=HttpUtils.ipAddress+"data/deleteOperationLog";
                String result=HttpUtils.requestPost(url,param);
                if (!TextUtils.isEmpty(result)){
                    JSONObject jsonObject=new JSONObject(result);
                    returnCode=jsonObject.getInt("returnCode");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return returnCode;
        }

        @Override
        protected void onPostExecute(AlermLogActivity alermLogActivity, Integer integer) {
            switch (integer){
                case 100:
                    ToastUtil.showShort(AlermLogActivity.this,"清除该设备的报警日志成功");
                    list.clear();
                    adapter.notifyDataSetChanged();
                    break;
                    default:
                        ToastUtil.showShort(AlermLogActivity.this,"清除该设备的报警日志失败");
                        break;
            }
        }
    }
}
