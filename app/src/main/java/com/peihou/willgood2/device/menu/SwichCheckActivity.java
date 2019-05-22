package com.peihou.willgood2.device.menu;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.view.CropImageView;
import com.peihou.willgood2.R;
import com.peihou.willgood2.BaseActivity;
import com.peihou.willgood2.custom.ChangeDialog;
import com.peihou.willgood2.custom.DialogLoad;
import com.peihou.willgood2.pojo.SwitchCheck;
import com.peihou.willgood2.pojo.SwtichState;

import com.peihou.willgood2.service.MQService;
import com.peihou.willgood2.utils.BitmapUtils;
import com.peihou.willgood2.utils.GlideCircleTransform;
import com.peihou.willgood2.utils.ToastUtil;
import com.peihou.willgood2.utils.http.BaseWeakAsyncTask;
import com.peihou.willgood2.utils.http.HttpUtils;
import com.yancy.gallerypick.config.GalleryConfig;
import com.yancy.gallerypick.config.GalleryPick;
import com.yancy.gallerypick.inter.IHandlerCallBack;
import com.yancy.gallerypick.inter.ImageLoader;
import com.yancy.gallerypick.widget.GalleryImageView;

import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 开关量测试
 */
public class SwichCheckActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tv_title;
    @BindView(R.id.list_linked)
    RecyclerView list_linked;//开关量检测视图列表
    private List<SwtichState> list = new ArrayList<>();
    MyAdapter adapter;
    long deviceId;
    Map<String, Object> params = new HashMap<>();
    String deviceMac;
    int voice;

    @Override
    public void initParms(Bundle parms) {
        deviceId = parms.getLong("deviceId");
        deviceMac = parms.getString("deviceMac");
//        list = (List<SwtichState>) parms.getSerializable("swtichStates");
        voice = parms.getInt("voice");
    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_linked_control;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mqService != null) {
            mqService.getData(topicName, 0x55);
            countTimer.start();
        }
    }

    private String topicName;

    @Override
    public void initView(View view) {

        tv_title.setText("开关量检测");
        list_linked.setLayoutManager(new LinearLayoutManager(this));
        params.put("deviceId",deviceId);
        new GetSwichAsync(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,params);
//        if (list.size() != 8) {
//            list.add(new SwtichState(0, "开关量1", "", 0));
//            list.add(new SwtichState(0, "开关量2", "", 0));
//            list.add(new SwtichState(0, "开关量3", "", 0));
//            list.add(new SwtichState(0, "开关量4", "", 0));
//            list.add(new SwtichState(0, "开关量5", "", 0));
//            list.add(new SwtichState(0, "开关量6", "", 0));
//            list.add(new SwtichState(0, "开关量7", "", 0));
//            list.add(new SwtichState(0, "开关量8", "", 0));
//        }
        adapter = new MyAdapter(this, list);
        list_linked.setAdapter(adapter);

        topicName = "qjjc/gateway/" + deviceMac + "/server_to_client";
        receiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter("SwichCheckActivity");
        registerReceiver(receiver, filter);
        Intent service = new Intent(this, MQService.class);
        bind = bindService(service, connection, Context.BIND_AUTO_CREATE);
        initPermissions();
        initGallery();
        initImage();
    }

    @OnClick({R.id.img_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                if (mqService != null) {
                    mqService.updateSwitchCheck();
                }
                finish();
                break;
        }
    }
    class GetSwichAsync extends BaseWeakAsyncTask<Map<String, Object>, Void, Integer, SwichCheckActivity> {

        public GetSwichAsync(SwichCheckActivity activity) {
            super(activity);
        }

        @Override
        protected Integer doInBackground(SwichCheckActivity activity, Map<String, Object>... maps) {
            int code = 0;
            Map<String, Object> params = maps[0];
            String url = HttpUtils.ipAddress + "device/getSwitchName";
            try {
                String result = HttpUtils.requestPost(url, params);
                if (TextUtils.isEmpty(result)) {
                    result = HttpUtils.requestPost(url, params);
                }
                Log.i("GetSwichAsync","-->"+result);
                if (!TextUtils.isEmpty(result)) {
                    Log.i("GetSwichAsync", "-->" + result);
                    JSONObject jsonObject = new JSONObject(result);
                    code = jsonObject.getInt("returnCode");
                    if (code == 100) {
                        list.clear();
                        JSONObject returnData = jsonObject.getJSONObject("returnData");
                        Gson gson = new Gson();
                        SwitchCheck switchCheck = gson.fromJson(returnData.toString() + "", SwitchCheck.class);
                        if (switchCheck != null) {
                            Class<SwitchCheck> clazz = (Class<SwitchCheck>) Class.forName("com.peihou.willgood2.pojo.SwitchCheck");
                            for (int i = 1; i <= 8; i++) {
                                Method method = clazz.getDeclaredMethod("getSwitchName" + i);
                                Method method2 = clazz.getDeclaredMethod("getSwitchPic" + i);
                                String switchName = (String) method.invoke(switchCheck);
                                String switchPic = (String) method2.invoke(switchCheck);
                                list.add(new SwtichState(0, switchName, switchPic, 0));
                            }
                        }
                    }
                }
                params.put("deviceMac", deviceMac);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return code;
        }

        @Override
        protected void onPostExecute(SwichCheckActivity activity, Integer code) {
           if (code==100){
               adapter.notifyDataSetChanged();
           }
            if (mqService != null) {
                mqService.getData(topicName, 0x55);
                countTimer.start();
            }
        }

    }

    @Override
    public void onBackPressed() {
        if (mqService != null) {
            mqService.updateSwitchCheck();
        }
        super.onBackPressed();
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    private boolean isNeedCheck = true;
    private static final int RC_CAMERA_AND_LOCATION = 0;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "同意授权");
                GalleryPick.getInstance().setGalleryConfig(galleryConfig).open(SwichCheckActivity.this);
                isNeedCheck = false;
            } else {
                Log.i(TAG, "拒绝授权");
            }
        }
    }

    class MyAdapter extends RecyclerView.Adapter<ViewHolder> {
        private Context context;
        private List<SwtichState> list;

        public MyAdapter(Context context, List<SwtichState> list) {
            this.context = context;
            this.list = list;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = View.inflate(context, R.layout.item_sa, null);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
            SwtichState state = list.get(position);
            String name = state.getName();
            int state2 = state.getState();
            holder.tv_name.setText(name);
            String pic = state.getPic();
            File file = new File(pic);
            if (file.exists()) {
                Glide.with(context).load(file).error(R.mipmap.img_sa).transform(new GlideCircleTransform(getApplicationContext())).into(holder.img_sa);
            } else {
                Glide.with(context).load(pic).error(R.mipmap.img_sa).transform(new GlideCircleTransform(getApplicationContext())).into(holder.img_sa);
            }
            if (state2 == 2) {
//                holder.tv_state.setText("异常");
                holder.tv_state.setText("断开");
                holder.img_state.setImageResource(R.mipmap.img_bad);
            } else if (state2 == 1) {
//                holder.tv_state.setText("正常");
                holder.tv_state.setText("闭合");
                holder.img_state.setImageResource(R.mipmap.img_right);
            } else if (state2 == 0) {
                holder.tv_state.setText("无效");
                holder.img_state.setImageResource(R.mipmap.img_invalid);
            }
            holder.rl_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow(position);
                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.rl_item)
        RelativeLayout rl_item;
        @BindView(R.id.img_sa)
        ImageView img_sa;
        @BindView(R.id.tv_name)
        TextView tv_name;
        @BindView(R.id.tv_state)
        TextView tv_state;
        @BindView(R.id.img_state)
        ImageView img_state;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private void initPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "需要授权 ");
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Log.i(TAG, "拒绝过了");
                Toast.makeText(this, "请在 设置-应用管理 中开启此应用的储存授权。", Toast.LENGTH_SHORT).show();
            } else {
                Log.i(TAG, "进行授权");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        } else {
            Log.i(TAG, "不需要授权 ");
            isNeedCheck = true;
            GalleryPick.getInstance().setGalleryConfig(galleryConfig).open(SwichCheckActivity.this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialogLoad!=null && dialogLoad.isShowing()){
            dialogLoad.dismiss();
        }

        if (receiver != null) {
            unregisterReceiver(receiver);
        }
        if (bind) {
            unbindService(connection);
        }
    }

    private boolean bind;
    MQService mqService;
    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MQService.LocalBinder binder = (MQService.LocalBinder) service;
            mqService = binder.getService();

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    CountTimer countTimer = new CountTimer(2000, 1000);

    class CountTimer extends CountDownTimer {

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public CountTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            setLoadDialog();
        }

        @Override
        public void onFinish() {
            if (dialogLoad != null && dialogLoad.isShowing()) {
                dialogLoad.dismiss();
            }
        }
    }
    DialogLoad dialogLoad;
    private void setLoadDialog() {
        if (dialogLoad != null && dialogLoad.isShowing()) {
            return;
        }

        dialogLoad = new DialogLoad(this);
        dialogLoad.setCanceledOnTouchOutside(false);
        dialogLoad.setLoad("正在加载,请稍后");
        dialogLoad.show();
    }
//    private PopupWindow popupWindow2;

//    public void popupmenuWindow3() {
//        if (popupWindow2 != null && popupWindow2.isShowing()) {
//            return;
//        }
//        View view = View.inflate(this, R.layout.progress, null);
//        TextView tv_load = view.findViewById(R.id.tv_load);
//        tv_load.setTextColor(getResources().getColor(R.color.white));
//
//            popupWindow2 = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
//        //添加弹出、弹入的动画
//        popupWindow2.setAnimationStyle(R.style.Popupwindow);
//        popupWindow2.setFocusable(false);
//        popupWindow2.setOutsideTouchable(false);
//        backgroundAlpha(0.6f);
//        popupWindow2.setOnDismissListener(new PopupWindow.OnDismissListener() {
//            @Override
//            public void onDismiss() {
//                backgroundAlpha(1.0f);
//            }
//        });
////        ColorDrawable dw = new ColorDrawable(0x30000000);
////        popupWindow.setBackgroundDrawable(dw);
////        popupWindow2.showAsDropDown(et_wifi, 0, -20);
//        popupWindow2.showAtLocation(list_linked, Gravity.CENTER, 0, 0);
//        //添加按键事件监听
//    }

    MessageReceiver receiver;

    class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String macAddress = intent.getStringExtra("macAddress");
                if (macAddress.equals(deviceMac)) {
                    int switchState1 = intent.getIntExtra("switchState1", 0);
                    int switchState2 = intent.getIntExtra("switchState2", 0);
                    int switchState3 = intent.getIntExtra("switchState3", 0);
                    int switchState4 = intent.getIntExtra("switchState4", 0);
                    int switchState5 = intent.getIntExtra("switchState5", 0);
                    int switchState6 = intent.getIntExtra("switchState6", 0);
                    int switchState7 = intent.getIntExtra("switchState7", 0);
                    int switchState8 = intent.getIntExtra("switchState8", 0);
                    if (list != null && list.size() == 8) {
                        SwtichState swtichState = list.get(0);
                        SwtichState swtichState2 = list.get(1);
                        SwtichState swtichState3 = list.get(2);
                        SwtichState swtichState4 = list.get(3);
                        SwtichState swtichState5 = list.get(4);
                        SwtichState swtichState6 = list.get(5);
                        SwtichState swtichState7 = list.get(6);
                        SwtichState swtichState8 = list.get(7);
                        swtichState.setState(switchState1);
                        swtichState2.setState(switchState2);
                        swtichState3.setState(switchState3);
                        swtichState4.setState(switchState4);
                        swtichState5.setState(switchState5);
                        swtichState6.setState(switchState6);
                        swtichState7.setState(switchState7);
                        swtichState8.setState(switchState8);

                        list.set(0, swtichState);
                        list.set(1, swtichState2);
                        list.set(2, swtichState3);
                        list.set(3, swtichState4);
                        list.set(4, swtichState5);
                        list.set(5, swtichState6);
                        list.set(6, swtichState7);
                        list.set(7, swtichState8);
                        adapter.notifyDataSetChanged();
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public int load = 0;
    public static boolean running = false;

    @Override
    protected void onStart() {
        super.onStart();
        running = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        running = false;
    }

    private PopupWindow popupWindow;

    //底部popupWindow
    public void popupWindow(final int position) {
        if (popupWindow != null && popupWindow.isShowing()) {
            return;
        }

        if (popupWindow == null) {
            load = 1;
        }
        if (load == 1) {
            initImagePicker();
        }

        View view = View.inflate(SwichCheckActivity.this, R.layout.changepicture, null);
        Button camera = view.findViewById(R.id.camera);
        Button gallery = view.findViewById(R.id.gallery);
        TextView cancel = view.findViewById(R.id.cancel);
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        //点击空白处时，隐藏掉pop窗口
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        //添加弹出、弹入的动画
        popupWindow.setAnimationStyle(R.style.Popupwindow);

//        ColorDrawable dw = new ColorDrawable(0x30000000);
//        popupWindow.setBackgroundDrawable(dw);
        popupWindow.showAtLocation(tv_title, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        //添加按键事件监听
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.camera:
                        popupWindow.dismiss();
                        changeDialog(position);
                        break;
                    case R.id.gallery:
                        //打开选择,本次允许选择的数量
                        popupWindow.dismiss();
                        if (params.containsKey("switchName")) {
                            params.remove("switchName");
                        }

                        params.put("deviceSwitchNum", position + 1);
                        GalleryPick.getInstance().setGalleryConfig(galleryConfig).open(SwichCheckActivity.this);
                        break;
                    case R.id.cancel:
                        popupWindow.dismiss();
                        break;
                }
            }
        };
        camera.setOnClickListener(onClickListener);
        gallery.setOnClickListener(onClickListener);
        cancel.setOnClickListener(onClickListener);
        backgroundAlpha(0.4f);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1.0f);
            }
        });
    }

    private final int PERMISSIONS_REQUEST_READ_CONTACTS = 8;
    private GalleryConfig galleryConfig;

    private void initImage() {
        galleryConfig = new GalleryConfig.Builder()
                .imageLoader(new ImageLoader() {
                    @Override
                    public void displayImage(Activity activity, Context context, String path, GalleryImageView galleryImageView, int width, int height) {
                        Glide.with(context)
                                .load(path)
                                .placeholder(R.mipmap.gallery_pick_photo)
                                .centerCrop()
                                .into(galleryImageView);
                    }

                    @Override
                    public void clearMemoryCache() {

                    }
                })    // ImageLoader 加载框架（必填）
                .iHandlerCallBack(iHandlerCallBack)     // 监听接口（必填）
                .provider("com.peihou.willgood2.fileprovider2")   // provider(必填)
                .multiSelect(false)                      // 是否多选   默认：false
                .multiSelect(false, 9)                   // 配置是否多选的同时 配置多选数量   默认：false ， 9
                .maxSize(9)                             // 配置多选时 的多选数量。    默认：9
                .crop(true)                             // 快捷开启裁剪功能，仅当单选 或直接开启相机时有效
                .crop(true, 1, 1, 500, 500)             // 配置裁剪功能的参数，   默认裁剪比例 1:1
                .isShowCamera(true)                     // 是否现实相机按钮  默认：false
                .filePath("/Gallery/Pictures")          // 图片存放路径
                .build();
    }

    private IHandlerCallBack iHandlerCallBack;

    private void initGallery() {
        iHandlerCallBack = new IHandlerCallBack() {
            @Override
            public void onStart() {
                Log.i(TAG, "onStart: 开启");
            }

            @Override
            public void onSuccess(List<String> photoList) {
                Log.i(TAG, "onSuccess: 返回数据");
                try {
                    if (photoList != null && !photoList.isEmpty()) {
                        String path = photoList.get(0);
                        File file = new File(path);
                        params.put("deviceId", deviceId);
                        new UpImageAysnc().execute(file).get(3, TimeUnit.SECONDS);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancel() {
                Log.i(TAG, "onCancel: 取消");
            }

            @Override
            public void onFinish() {
                Log.i(TAG, "onFinish: 结束");
            }

            @Override
            public void onError() {
                Log.i(TAG, "onError: 出错");
            }
        };

    }


    //设置蒙版
    private void backgroundAlpha(float f) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = f;
        getWindow().setAttributes(lp);
    }

    ChangeDialog dialog;

    private void changeDialog(final int postion) {
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        dialog = new ChangeDialog(this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setTitle("修改名称");
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
                    ToastUtil.show(SwichCheckActivity.this, "编辑内容不能为空", Toast.LENGTH_SHORT);
                } else {
                    try {
                        params.put("deviceId", deviceId);
                        params.put("switchName", content);
                        params.put("deviceSwitchNum", postion + 1);
                        new ChangeSwitchNameAsync().execute(params).get(3, TimeUnit.SECONDS);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    dialog.dismiss();
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

    private ArrayList<ImageItem> selImageList = new ArrayList<>(); //当前选择的所有图片

    private void initImagePicker() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());   //设置图片加载器
        imagePicker.setShowCamera(true);                   //显示拍照按钮
        imagePicker.setCrop(true);                            //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true);                   //是否按矩形区域保存
        imagePicker.setSelectLimit(1);              //选中数量限制
        imagePicker.setMultiMode(false);                      //多选
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
        imagePicker.setFocusWidth(800);                       //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800);                      //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(1000);                         //保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);                         //保存文件的高度。单位像素
    }

    public static final int IMAGE_ITEM_ADD = -1;
    public static final int REQUEST_CODE_SELECT = 100;
    public static final int REQUEST_CODE_PREVIEW = 101;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            //添加图片返回
            if (data != null && requestCode == REQUEST_CODE_SELECT) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (!images.isEmpty()) {
                    try {

                        String newPath = BitmapUtils.compressImageUpload(images.get(0).path);
                        File file = new File(newPath);
                        params.put("deviceId", deviceId);
                        new UpImageAysnc().execute(file).get(3, TimeUnit.SECONDS);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } else if (resultCode == ImagePicker.RESULT_CODE_BACK) {
            //预览图片返回
            if (data != null && requestCode == REQUEST_CODE_PREVIEW) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_IMAGE_ITEMS);

            }
        }
    }

    /**
     * 修改开关量名称
     */
    class ChangeSwitchNameAsync extends AsyncTask<Map<String, Object>, Void, Integer> {

        @Override
        protected Integer doInBackground(Map<String, Object>... maps) {
            int code = 0;
            try {
                Map<String, Object> params = maps[0];
                String url = HttpUtils.ipAddress + "device/changeSwitchName";
                String result = HttpUtils.requestPost(url, params);
                if (TextUtils.isEmpty(result))
                    result = HttpUtils.requestPost(url, params);
                if (!TextUtils.isEmpty(result)) {
                    JSONObject jsonObject = new JSONObject(result);
                    code = jsonObject.getInt("returnCode");
                    if (code == 100) {
                        int deviceSwitchNum = (int) params.get("deviceSwitchNum");
                        String switchName = (String) params.get("switchName");
                        SwtichState swtichState = list.get(deviceSwitchNum - 1);
                        swtichState.setName(switchName);
                        list.set(deviceSwitchNum - 1, swtichState);
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
            if (integer == 100) {
                ToastUtil.showShort(SwichCheckActivity.this, "修改成功");
                adapter.notifyDataSetChanged();
            } else {
                ToastUtil.showShort(SwichCheckActivity.this, "修改失败");
            }
        }
    }

    /**
     * 上传开关量图片
     */
    class UpImageAysnc extends AsyncTask<File, Void, Integer> {

        @Override
        protected Integer doInBackground(File... files) {

            int code = 0;
            try {
                File file = files[0];
                String url = HttpUtils.ipAddress + "device/updateSwitchPic";
                String result = HttpUtils.uploadFile(params, file);
                if (TextUtils.isEmpty(result)) {
                    result = HttpUtils.uploadFile(params, file);
                }
                if (!TextUtils.isEmpty(result)) {
                    JSONObject jsonObject = new JSONObject(result);
                    code = jsonObject.getInt("returnCode");
                    int deviceSwitchNum = (int) params.get("deviceSwitchNum");
                    SwtichState swtichState = list.get(deviceSwitchNum - 1);
                    swtichState.setPic(file.getPath());
                    list.set(deviceSwitchNum - 1, swtichState);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return code;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if (integer == 100) {
                ToastUtil.showShort(SwichCheckActivity.this, "修改成功");
                adapter.notifyDataSetChanged();
            } else {
                ToastUtil.showShort(SwichCheckActivity.this, "修改失败");
            }
        }
    }
}
