package com.peihou.willgood2.device;

import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.donkingliang.groupedadapter.adapter.GroupedRecyclerViewAdapter;
import com.donkingliang.groupedadapter.holder.BaseViewHolder;
import com.google.gson.Gson;
import com.peihou.willgood2.BaseActivity;
import com.peihou.willgood2.R;
import com.peihou.willgood2.device.fragment.AlermDeviceFragment;
import com.peihou.willgood2.device.fragment.OperateDeviceFragment;
import com.peihou.willgood2.device.fragment.ShareDeviceFragment;
import com.peihou.willgood2.pojo.Device;
import com.peihou.willgood2.pojo.OperatorLog;
import com.peihou.willgood2.utils.NoFastClickUtils;
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

    @BindView(R.id.tv_share)
    TextView tv_share;//分享操作
    @BindView(R.id.tv_operate_log)
    TextView tv_operate_log;//操作日志
    @BindView(R.id.tv_alerm_log)
    TextView tv_alerm_log;//报警日志


    private FragmentManager fragmentManager;//碎片管理者
    FragmentTransaction fragmentTransaction;

    private ShareDeviceFragment shareDeviceFragment;
    private OperateDeviceFragment operateDeviceFragment;
    private AlermDeviceFragment alermDeviceFragment;
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


    @Override
    public void initView(View view) {

        preferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        userId = preferences.getInt("userId", 0);

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();//开启碎片事务
        shareDeviceFragment=new ShareDeviceFragment();
        operateDeviceFragment=new OperateDeviceFragment();
        alermDeviceFragment=new AlermDeviceFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("userId",userId);
        shareDeviceFragment.setArguments(bundle);
        operateDeviceFragment.setArguments(bundle);
        alermDeviceFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.layout,shareDeviceFragment).commit();
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    int operator = 0;//0为分享操作，1为操作日志，2为报警日志

    @OnClick({R.id.img_back, R.id.tv_share, R.id.tv_operate_log, R.id.tv_alerm_log})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.tv_share:
                if (NoFastClickUtils.isFastClick2()){
                    if (operator == 0) {
                        break;
                    }
                    tv_share.setTextSize(22);
                    tv_operate_log.setTextSize(16);
                    tv_alerm_log.setTextSize(16);
                    operator=0;
                    Bundle bundle = new Bundle();
                    bundle.putInt("userId",userId);
                    shareDeviceFragment.setArguments(bundle);
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.layout,shareDeviceFragment).commit();
                }else {
                    ToastUtil.showShort(this,"请稍后...");
                }

                break;
            case R.id.tv_operate_log:
                if (NoFastClickUtils.isFastClick2()){
                    if (operator == 1) {
                        break;
                    }
                    tv_share.setTextSize(16);
                    tv_operate_log.setTextSize(22);
                    tv_alerm_log.setTextSize(16);
                    operator=1;

                    Bundle bundle = new Bundle();
                    bundle.putInt("userId",userId);
                    operateDeviceFragment.setArguments(bundle);
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.layout,operateDeviceFragment).commit();
                }else {
                    ToastUtil.showShort(this,"请稍后...");
                }
                operator=1;
                break;
            case R.id.tv_alerm_log:
                if (NoFastClickUtils.isFastClick2()){
                    if (operator == 2) {
                        break;
                    }
                    tv_share.setTextSize(16);
                    tv_operate_log.setTextSize(16);
                    tv_alerm_log.setTextSize(22);
                    operator=2;
                    Bundle bundle = new Bundle();

                    bundle.putInt("userId",userId);
                    bundle.putInt("load",1);

                    alermDeviceFragment.setArguments(bundle);
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.layout,alermDeviceFragment).commit();
                }else {
                    ToastUtil.showShort(this,"请稍后...");
                }
                operator=2;
                break;
        }
    }


//    private int operateDevice=1;
//    @Override
//    public void setOpeate(int operate) {
//        operateDevice=operate;
//        Log.i("DeiviceRecordoperate","-->"+operate);
//    }
//
//    private int alermDevice=1;
//    @Override
//    public void setAlerm(int alerm) {
//        alermDevice=alerm;
//        Log.i("DeiviceRecordalerm","-->"+alerm);
//
//    }
}
