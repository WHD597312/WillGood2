package com.peihou.willgood2.device;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.peihou.willgood2.BaseActivity;
import com.peihou.willgood2.R;
import com.peihou.willgood2.database.dao.impl.DeviceDaoImpl;
import com.peihou.willgood2.pojo.Device;
import com.peihou.willgood2.utils.DisplayUtil;
import com.peihou.willgood2.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchDeviceActivity extends BaseActivity {


    @BindView(R.id.grid_list) GridView grid_list;
    @BindView(R.id.et_search) EditText et_search;
    private DeviceDaoImpl deviceDao;
    List<String> strings=new ArrayList<>();
    List<Device> list=new ArrayList<>();
    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_search_device;
    }

    MyAdapter adapter;
    List<Device> devices;
    @Override
    public void initView(View view) {
        deviceDao=new DeviceDaoImpl(getApplicationContext());
        devices=deviceDao.findAllDevice();


        adapter=new MyAdapter(list,this);
        grid_list.setAdapter(adapter);
        for(Device device:devices){
            String name=device.getDeviceName();
            strings.add(name);
        }
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String s2=s.toString();
                if (!TextUtils.isEmpty(s2) && !strings.isEmpty()){
                    for (int i = 0; i < strings.size(); i++) {
                        list.clear();
                        String s3=strings.get(i);
                        if (s3.contains(s2)){
                            Device device=devices.get(i);
                            list.add(device);
                        }
                    }
                }else {
                    list.clear();
                }
                adapter.notifyDataSetChanged();
            }
        });
    }


    @OnClick({R.id.img_back})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.img_back:
                finish();
                break;
        }
    }
    @Override
    public void doBusiness(Context mContext) {

    }

    class MyAdapter extends BaseAdapter {

        private List<Device> list;
        private Context context;

        public MyAdapter(List<Device> list, Context context) {
            this.list = list;
            this.context = context;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Device getItem(int position) {
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
                convertView=View.inflate(context,R.layout.item_device_list,null);
                viewHolder=new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            }else {
                viewHolder= (ViewHolder) convertView.getTag();
            }
            final Device device=getItem(position);
            boolean isOpen=device.isOpen();
            int deviceState=device.getDeviceState();
            String name=device.getDeviceName();
            String devicePassword=device.getDevicePassword();
            String share=device.getShare();
            int choice=device.getChoice();
            if (deviceState==1){
                viewHolder.img_lamp.setImageResource(R.mipmap.lamp_open);
            }else {
                viewHolder.img_lamp.setImageResource(R.mipmap.lamp_close);
            }
            if (choice==1){
                viewHolder.img_device_choice.setImageResource(R.mipmap.img_device_choice);
            }else {
                viewHolder.img_device_choice.setImageResource(R.mipmap.img_device_unchoice);
            }
            viewHolder.img_device_choice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int choice=device.getChoice();
                    if (choice==1){
                        device.setChoice(0);
                    }else {
                        device.setChoice(1);
                    }
                    list.set(position,device);
                    notifyDataSetChanged();
                }
            });

            viewHolder.rl_item2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Device device=list.get(position);
                    if (device.getOnline()){
                        long deviceId=device.getDeviceId();
                        String name=device.getDeviceName();
                        Intent intent=new Intent(SearchDeviceActivity.this,DeviceItemActivity.class);
                        intent.putExtra("deviceId",deviceId);
                        intent.putExtra("name",name);
                        intent.putExtra("search","search");
                        startActivity(intent);
                    }else {
                        ToastUtil.showShort(SearchDeviceActivity.this,"该设备离线");
                    }
                }
            });
            viewHolder.tv_name.setText(name);
            viewHolder.tv_imei.setText(devicePassword);
            if (TextUtils.isEmpty(share)){
                viewHolder.rl_item2.setImageResource(R.mipmap.device_back);
            }else {
                viewHolder.rl_item2.setImageResource(R.mipmap.share_back);
            }
            return convertView;
        }
    }
    class ViewHolder{
        @BindView(R.id.img_device_choice)
        ImageView img_device_choice;
        @BindView(R.id.img_back) ImageView rl_item2;
        @BindView(R.id.img_lamp) ImageView img_lamp;
        @BindView(R.id.tv_name)
        TextView tv_name;
        @BindView(R.id.tv_imei) TextView tv_imei;
        public ViewHolder(View view){
            ButterKnife.bind(this,view);
        }
    }
}
