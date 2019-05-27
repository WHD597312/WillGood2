package com.peihou.willgood2.login;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.peihou.willgood2.BaseActivity;
import com.peihou.willgood2.R;
import com.peihou.willgood2.database.dao.impl.DeviceDaoImpl;
import com.peihou.willgood2.device.DeviceListActivity;
import com.peihou.willgood2.pojo.Device;
import com.peihou.willgood2.utils.Mobile;
import com.peihou.willgood2.utils.ToastUtil;
import com.peihou.willgood2.utils.http.BaseWeakAsyncTask;
import com.peihou.willgood2.utils.http.HttpUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class ResetPswdActivity extends BaseActivity {

    @BindView(R.id.et_phone) EditText et_phone;//输入手机号
    @BindView(R.id.et_code) EditText et_code;//输入验证码
    @BindView(R.id.et_pswd) EditText et_pswd;//输入密码
    @BindView(R.id.btn_code) Button btn_code;//验证码按钮
    @BindView(R.id.btn_finish) Button btn_finish;//完成按钮

    private DeviceDaoImpl deviceDao;

    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_reset_pswd;
    }

    @Override
    public void initView(View view) {

        deviceDao=new DeviceDaoImpl(getApplicationContext());
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        SMSSDK.registerEventHandler(eventHandler);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(eventHandler);
    }

    Map<String,Object> map=new HashMap<>();
    String phone;
    private int running=0;
    @OnClick({R.id.btn_code,R.id.btn_finish})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.btn_code:
                if (running==1){
                    ToastUtil.showShort(this, "请稍后...");
                    break;
                }
                phone=et_phone.getText().toString();
                if (TextUtils.isEmpty(phone)) {
                    ToastUtil.showShort(this, "手机号码不能为空");
                    break;
                }
                if (!Mobile.isMobile(phone)){
                    ToastUtil.showShort(this, "不合法的手机号码");
                    break;
                }
                SMSSDK.getVerificationCode("86", phone);
                CountTimer countTimer = new CountTimer(60000, 1000);
                countTimer.start();
                break;
            case R.id.btn_finish:
                if (running==1){
                    ToastUtil.showShort(this, "请稍后...");
                    break;
                }
                map.clear();
                String phone=et_phone.getText().toString();
                if (TextUtils.isEmpty(phone)) {
                    ToastUtil.showShort(this, "手机号码不能为空");
                    break;
                }
                if (!Mobile.isMobile(phone)){
                    ToastUtil.showShort(this, "不合法的手机号码");
                    break;
                }
                String code=et_code.getText().toString();
                if (TextUtils.isEmpty(code)){
                    ToastUtil.showShort(this, "验证码不能为空");
                    break;
                }
                String password=et_pswd.getText().toString();
                if (TextUtils.isEmpty(password)){
                    ToastUtil.showShort(this, "密码不能为空");
                    break;
                }

                running=1;
                map.put("phone",phone);
                map.put("password",password);
                map.put("code",code);
                new UpdateUserAsync(ResetPswdActivity.this).execute(map);
                break;
        }
    }

    private EventHandler eventHandler = new EventHandler() {
        @Override
        public void afterEvent(int event, int result, Object data) {
            super.afterEvent(event, result, data);
            if (result == SMSSDK.RESULT_COMPLETE) {
                //回调完成
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    //提交验证码成功
                    @SuppressWarnings("unchecked") HashMap<String, Object> phoneMap = (HashMap<String, Object>) data;
                    String country = (String) phoneMap.get("country");
                    String phone = (String) phoneMap.get("phone");
                    Log.d(TAG, "提交验证码成功--country=" + country + "--phone" + phone);
                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    //获取验证码成功
                    Log.d(TAG, "获取验证码成功");
                } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                    //返回支持发送验证码的国家列表
                }
            } else {
                ((Throwable) data).printStackTrace();
            }
        }
    };
    class CountTimer extends CountDownTimer {
        public CountTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        /**
         * 倒计时过程中调用
         *
         * @param millisUntilFinished
         */
        @Override
        public void onTick(long millisUntilFinished) {
            Log.e("Tag", "倒计时=" + (millisUntilFinished / 1000));
            if (btn_code!=null){
                btn_code.setText(millisUntilFinished / 1000 + "s后重新发送");
                //设置倒计时中的按钮外观
                btn_code.setClickable(false);//倒计时过程中将按钮设置为不可点击
            }

//            btn_get_code.setBackgroundColor(Color.parseColor("#c7c7c7"));
//            btn_get_code.setTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.black));
//            btn_get_code.setTextSize(16);
        }

        /**
         * 倒计时完成后调用
         */
        @Override
        public void onFinish() {
            Log.e("Tag", "倒计时完成");
            //设置倒计时结束之后的按钮样式
//            btn_get_code.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), android.R.color.holo_blue_light));
//            btn_get_code.setTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.white));
//            btn_get_code.setTextSize(18);
            if (btn_code != null) {
                btn_code.setText("重新发送");
                btn_code.setClickable(true);
            }
        }
    }
    class UpdateUserAsync extends BaseWeakAsyncTask<Map<String,Object>,Void,Integer,ResetPswdActivity> {

        public UpdateUserAsync(ResetPswdActivity activity) {
            super(activity);
        }

        @Override
        protected Integer doInBackground(ResetPswdActivity activity,Map<String, Object>... maps) {
            int code=0;
            try {
                Map<String,Object> params=maps[0];
                String url=HttpUtils.ipAddress+"user/forgetPassword";
                String result=HttpUtils.requestPost(url,params);
                if (!TextUtils.isEmpty(result)){
                    JSONObject jsonObject=new JSONObject(result);
                    code=jsonObject.getInt("returnCode");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return code;
        }

        @Override
        protected void onPostExecute(ResetPswdActivity activity,Integer integer) {
            try {
//                super.onPostExecute(integer);
                running=0;
                if (integer==10003){
                    ToastUtil.showShort(ResetPswdActivity.this,"验证码错误");
                }else if (integer==100) {
                    ToastUtil.showShort(ResetPswdActivity.this, "修改成功,请重新登录");
                    setResult(1001);
                    finish();
                }else {
                    ToastUtil.showShort(ResetPswdActivity.this,"修改失败");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    class LoadDeviceListAsync extends AsyncTask<Map<String,Object>,Void,Integer>{


        @Override
        protected Integer doInBackground(Map<String, Object>... maps) {
            Map<String,Object> params=maps[0];
            int code=0;
            try {
                String url=HttpUtils.ipAddress+"device/getDeviceList";
                String result=HttpUtils.requestPost(url,params);
                Log.i("LoadDeviceListAsync","-->"+result);
                if (TextUtils.isEmpty(result)){
                    result=HttpUtils.requestPost(url,params);
                }
                if (!TextUtils.isEmpty(result)){
                    deviceDao.deleteAll();
                    JSONObject jsonObject=new JSONObject(result);
                    code=jsonObject.getInt("returnCode");
                    if (code==100){
                        JSONObject returnData=jsonObject.getJSONObject("returnData");
                        JSONArray deviceList=returnData.getJSONArray("deviceList");
                        for (int i = 0; i < deviceList.length(); i++) {
                            String s=deviceList.getJSONObject(i).toString();
                            Gson gson=new Gson();
                            Device device=gson.fromJson(s,Device.class);
                            deviceDao.insert(device);
                        }
                        JSONArray deviceShareList=returnData.getJSONArray("deviceShareList");
                        for (int i = 0; i <deviceShareList.length() ; i++) {
                            String s2=deviceShareList.getJSONObject(i).toString();
                            Gson gson=new Gson();
                            Device device=gson.fromJson(s2,Device.class);
                            Device device2=deviceDao.findDeviceById(device.getDeviceId());
                            if (device2==null){
                                device.setShare("share");
                                deviceDao.insert(device);
                            }else {
                                device.setShare("share");
                                deviceDao.update(device);
                            }
                        }
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return code;
        }

        @Override
        protected void onPostExecute(Integer code) {
            super.onPostExecute(code);
            switch (code){
                case 100:
                    ToastUtil.showShort(ResetPswdActivity.this,"修改成功");
                    startActivity(DeviceListActivity.class);
                    break;
                default:
                    ToastUtil.showShort(ResetPswdActivity.this,"修改成功");
                    break;
            }
        }
    }
}
