package com.peihou.willgood2.device;

import android.Manifest;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.peihou.willgood2.BaseActivity;
import com.peihou.willgood2.R;
import com.peihou.willgood2.utils.PlatformUtil;
import com.peihou.willgood2.utils.ToastUtil;
import com.peihou.willgood2.utils.Utils;
import com.peihou.willgood2.utils.ZXingUtils;

import java.io.UnsupportedEncodingException;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 彭涛:
 * shared/deviceId=0/devicePassword=0/deviceTimer=1/deviceLinked=0/deviceSwitch=0/deviceAlarm=0/deviceMap=0/deviceControl=0/deviceAnalog=0/deviceMac=
 *
 * 彭涛:
 * deviceControl:掉电记忆,互锁设置,点动设置,线路开关
 */
public class ShareDeviceQRCodeActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {

    @BindView(R.id.img_qrCode) ImageView img_qrCode;//二维码图片控件
    @BindView(R.id.tv_name) TextView tv_name;
    String name;
    @Override
    public void initParms(Bundle parms) {
        share=parms.getString("share");
        name=parms.getString("name");
    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_share_device_qrcode;
    }

    @Override
    public void initView(View view) {
        try {
            share=new String(Base64.encode(share.getBytes("utf-8"), Base64.NO_WRAP),"UTF-8");
            if (!TextUtils.isEmpty(name)){
                tv_name.setText("设备名称："+name);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        createQrCode();
    }

    @OnClick({R.id.back,R.id.rl_img})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.rl_img:
                popupShare();
                break;
        }
    }
    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        permissionGrantedSuccess();
    }

    private static final int EXTERNAL_STORAGE=0;
    private boolean isNeedCheck=true;
    private boolean openStorage=false;
    @AfterPermissionGranted(EXTERNAL_STORAGE)
    private void permissionGrantedSuccess(){
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            openStorage=true;
            // 已经申请过权限，做想做的事
        } else {
//             没有申请过权限，现在去申请
            if (isNeedCheck){
                EasyPermissions.requestPermissions(this, getString(R.string.save),
                        EXTERNAL_STORAGE, perms);
            }
        }
    }
    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        openStorage=true;
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog
                    .Builder(this)
                    .setTitle("提示")
                    .setRationale("请点击\"设置\"打开手机存储权限。")
                    .setPositiveButton("设置")
                    .setNegativeButton("取消")
                    .build()
                    .show();
            isNeedCheck=false;
        }
    }
    private PopupWindow popupWindow;

    /**
     * 分享图片popupview，可分享到微信，QQ，保存到本地
     */
    private void popupShare() {
        if (popupWindow != null && popupWindow.isShowing()) {
            return;
        }
        View view = View.inflate(this, R.layout.popup_share_device, null);
        TextView tv_save_phone = (TextView) view.findViewById(R.id.tv_save_phone);
        TextView tv_send_wechat = (TextView) view.findViewById(R.id.tv_send_wechat);
        TextView tv_send_qq = (TextView) view.findViewById(R.id.tv_send_qq);
        TextView tv_cancel = (TextView) view.findViewById(R.id.tv_cancel);


        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        //点击空白处时，隐藏掉pop窗口
        popupWindow.setFocusable(false);
        popupWindow.setOutsideTouchable(true);
        popupWindow.update();
        //添加弹出、弹入的动画
        popupWindow.setAnimationStyle(R.style.Popupwindow);


        popupWindow.showAtLocation(img_qrCode, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        backgroundAlpha(0.6f);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1.0f);
            }
        });
        //添加按键事件监听

        View.OnClickListener listener = new View.OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tv_save_phone:
                        ToastUtil.show(ShareDeviceQRCodeActivity.this,"保存成功",Toast.LENGTH_SHORT);
                        popupWindow.dismiss();
                        break;
                    case R.id.tv_send_wechat:
                        if (openStorage){
                            if (mBitmap!=null){
                                PlatformUtil.shareWechatFriend(ShareDeviceQRCodeActivity.this, mBitmap);
                                popupWindow.dismiss();
                            }
                        }else {
                            ToastUtil.show(ShareDeviceQRCodeActivity.this,"请打开手机存储权限",Toast.LENGTH_SHORT);
                        }

                        break;
                    case R.id.tv_send_qq:
                        if (openStorage){
                            if (mBitmap!=null){
                                PlatformUtil.shareImageToQQ(ShareDeviceQRCodeActivity.this,mBitmap);
                                popupWindow.dismiss();
                            }
                        }else {
                            ToastUtil.show(ShareDeviceQRCodeActivity.this,"请打开手机存储权限",Toast.LENGTH_SHORT);
                        }
                        break;
                    case R.id.tv_cancel:
                        popupWindow.dismiss();
                        break;

                }
            }
        };

        tv_save_phone.setOnClickListener(listener);
        tv_send_wechat.setOnClickListener(listener);
        tv_send_qq.setOnClickListener(listener);
        tv_cancel.setOnClickListener(listener);
    }
    //设置蒙版
    private void backgroundAlpha(float f) {
        WindowManager.LayoutParams lp =getWindow().getAttributes();
        lp.alpha = f;
        getWindow().setAttributes(lp);
    }
    private Bitmap mBitmap;//分享设备的图片
    private String share;//分享设备的内容信息
    /**生成二维码*/
    private void createQrCode(){
        Bitmap bitmap = ZXingUtils.createQRImage(share,1000, 1000);
        img_qrCode.setImageBitmap(bitmap);
        mBitmap=bitmap;
    }


}
