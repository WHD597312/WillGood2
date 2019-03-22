package com.peihou.willgood2.device.menu;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.peihou.willgood2.BaseActivity;
import com.peihou.willgood2.R;
import com.peihou.willgood2.custom.ChangeDialog;
import com.peihou.willgood2.database.dao.impl.DeviceAnalogDaoImpl;
import com.peihou.willgood2.pojo.AnalogName;
import com.peihou.willgood2.pojo.Linked;
import com.peihou.willgood2.pojo.Table;
import com.peihou.willgood2.service.MQService;
import com.peihou.willgood2.utils.ToastUtil;
import com.peihou.willgood2.utils.Utils;
import com.peihou.willgood2.utils.http.HttpUtils;


import org.json.JSONObject;

import java.lang.reflect.Method;
import java.math.BigDecimal;
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

public class MoniCheckActivity extends BaseActivity {

    @BindView(R.id.table)
    ListView table;
    List<Table> tables = new ArrayList<>();
    MyAdapter adapter;
    private DeviceAnalogDaoImpl deviceAnalogDao;
    Map<String,Object> params=new HashMap<>();
    private long deviceId;
    private String deviceMac;
    MessageReceiver receiver;
    public static boolean running=false;
    private boolean bind;
    @Override
    public void initParms(Bundle parms) {
        deviceId=parms.getLong("deviceId");
        deviceMac=parms.getString("deviceMac");
    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_moni_check;
    }


    String topicName;
    @Override
    public void initView(View view) {
        deviceAnalogDao=new DeviceAnalogDaoImpl(getApplicationContext());
        topicName="qjjc/gateway/"+deviceMac+"/server_to_client";
        tables=deviceAnalogDao.findDeviceAnalogs(deviceId);
        adapter=new MyAdapter(tables,this);
        table.setAdapter(adapter);

        Intent service=new Intent(this,MQService.class);
        bind=bindService(service,connection,Context.BIND_AUTO_CREATE);
        receiver=new MessageReceiver();
        IntentFilter intentFilter=new IntentFilter("MoniCheckActivity");
        registerReceiver(receiver,intentFilter);

    }

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

    @OnClick({R.id.img_back})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.img_back:
                finish();
                break;
        }
    }
    ChangeDialog dialog;

    /**
     *
     * @param row 行
     * @param col 列
     * @param num 1为数字，0为任何字符
     */
    private void changeDialog(final int row, final int col, final int num){
        if (dialog!=null && dialog.isShowing()){
            return;
        }
        dialog=new ChangeDialog(this);
        dialog.setCanceledOnTouchOutside(false);
        if (num==1){
            dialog.setInputType(2);
        }else {
            dialog.setInputType(0);
        }
        if (col==0){
            dialog.setTitle("修改名称");
        }else if (col==2){
            dialog.setTitle("修改系数");
        }else if (col==4){
            dialog.setTitle("修改单位");
        }
        dialog.setMode(0);
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
                    ToastUtil.show(MoniCheckActivity.this, "编辑内容不能为空", Toast.LENGTH_SHORT);
                } else {
                    Table table=tables.get(row);
                    if (col==0){
                        try {
                            dialog.dismiss();
                            table.setName(content);
                            params.clear();
                            params.put("deviceId",deviceId);
                            params.put("analogName",content);
                            params.put("deviceAnalogNum",row+1);
                            new ChangeAnalogAsync().execute(params).get(3,TimeUnit.SECONDS);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }else if (col==2){
                        if (Utils.isNumeric(content)){
                            dialog.dismiss();
                            double data=Double.parseDouble(content);
                            BigDecimal b = new BigDecimal(data);
                            data = b.setScale(2, BigDecimal.ROUND_HALF_DOWN).doubleValue();
                            table.setFactor(data);
                            deviceAnalogDao.update(table);
                            tables.set(row,table);
                            adapter.notifyDataSetChanged();
                        }else {
                            ToastUtil.showShort(MoniCheckActivity.this,"不合法的数字");
                        }
                    }else if (col==4){
                        dialog.dismiss();
                        table.setUnit(content);
                        deviceAnalogDao.update(table);
                        tables.set(row,table);
                        adapter.notifyDataSetChanged();
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
    public void doBusiness(Context mContext) {

    }


    class ChangeAnalogAsync extends AsyncTask<Map<String,Object>,Void,Integer>{

        @Override
        protected Integer doInBackground(Map<String, Object>... maps) {
            int code=0;
            try {
                Map<String,Object> params=maps[0];
                String url=HttpUtils.ipAddress+"device/changeAnalogName";
                String result=HttpUtils.requestPost(url,params);
                if (TextUtils.isEmpty(result)){
                    result=HttpUtils.requestPost(url,params);
                }
                if (!TextUtils.isEmpty(result)){
                    JSONObject jsonObject=new JSONObject(result);
                    code=jsonObject.getInt("returnCode");
                    if (code==100){
                        int deviceAnalogNum= (int) params.get("deviceAnalogNum");
                        String analogName= (String) params.get("analogName");
                        Table table=tables.get(deviceAnalogNum-1);
                        table.setName(analogName);
                        deviceAnalogDao.update(table);
                        tables.set(deviceAnalogNum-1,table);
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
            if (integer==100){
                ToastUtil.showShort(MoniCheckActivity.this,"修改成功");
                adapter.notifyDataSetChanged();
            }else {
                ToastUtil.showShort(MoniCheckActivity.this,"修改失败");
            }
        }
    }
    class MyAdapter extends BaseAdapter{

        private List<Table> list;
        private Context context;

        public MyAdapter(List<Table> list, Context context) {
            this.list = list;
            this.context = context;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Table getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder=null;
            if (convertView==null){
                convertView=View.inflate(context,R.layout.item_moni,null);
                viewHolder=new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            }else {
                viewHolder= (ViewHolder) convertView.getTag();
            }
            Table table=getItem(position);
            String name=table.getName();
            double data=table.getData();
            double factor=table.getFactor();
            double result=data*factor;
            String unit=table.getUnit();
            BigDecimal b = new BigDecimal(data);
            data = b.setScale(2, BigDecimal.ROUND_HALF_DOWN).doubleValue();
            viewHolder.tv_col.setText(name);
            String s=data+" "+unit;
            BigDecimal b1= new BigDecimal(factor);
            factor = b1.setScale(2, BigDecimal.ROUND_HALF_DOWN).doubleValue();

            BigDecimal b2 = new BigDecimal(result);
            result = b2.setScale(2, BigDecimal.ROUND_HALF_DOWN).doubleValue();

            SpannableStringBuilder style=new SpannableStringBuilder(s);
            style.setSpan(new ForegroundColorSpan(Color.parseColor("#898989")),s.indexOf(" "),s.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            viewHolder.tv_col2.setText(style);
            viewHolder.tv_col3.setText(""+factor);
            viewHolder.tv_col4.setText(""+result);
            viewHolder.tv_col5.setText(unit);
            View.OnClickListener onClickListener=new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()){
                        case R.id.tv_col:
                            changeDialog(position,0,0);
                            break;
                        case R.id.tv_col3:
                            changeDialog(position,2,1);
                            break;
                        case R.id.tv_col5:
                            changeDialog(position,4,0);
                            break;
                    }
                }
            };
            viewHolder.tv_col.setOnClickListener(onClickListener);
            viewHolder.tv_col3.setOnClickListener(onClickListener);
            viewHolder.tv_col5.setOnClickListener(onClickListener);
            return convertView;
        }
    }
    class ViewHolder{
        @BindView(R.id.linear) LinearLayout linear;
        @BindView(R.id.tv_col) TextView tv_col;
        @BindView(R.id.tv_col2) TextView tv_col2;
        @BindView(R.id.tv_col3) TextView tv_col3;
        @BindView(R.id.tv_col4) TextView tv_col4;
        @BindView(R.id.tv_col5) TextView tv_col5;
        public ViewHolder(View view){
            ButterKnife.bind(this,view);
        }
    }

    MQService mqService;
    ServiceConnection connection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MQService.LocalBinder binder= (MQService.LocalBinder) service;
            mqService=binder.getService();
            if (mqService!=null){
                mqService.getData(topicName,0x88);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    class MessageReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String macAddress=intent.getStringExtra("macAddress");
                List<Table> tables2= (List<Table>) intent.getSerializableExtra("table");
                if (macAddress.equals(deviceMac) && !tables2.isEmpty() && tables2.size()==9){
                    tables.clear();
                    tables.addAll(tables2);
                    adapter.notifyDataSetChanged();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        if (bind){
            unbindService(connection);
        }
    }
}
