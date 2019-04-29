package com.peihou.willgood2.device.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.donkingliang.groupedadapter.adapter.GroupedRecyclerViewAdapter;
import com.donkingliang.groupedadapter.holder.BaseViewHolder;
import com.google.gson.Gson;
import com.jwenfeng.library.pulltorefresh.BaseRefreshListener;
import com.jwenfeng.library.pulltorefresh.PullToRefreshLayout;
import com.jwenfeng.library.pulltorefresh.view.HeadRefreshView;
import com.jwenfeng.library.pulltorefresh.view.LoadMoreView;
import com.peihou.willgood2.R;
import com.peihou.willgood2.custom.MyHeadRefreshView;
import com.peihou.willgood2.custom.MyLoadMoreView;
import com.peihou.willgood2.device.DeviceRecordActivity;
import com.peihou.willgood2.pojo.Device;
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
import butterknife.Unbinder;

public class ShareDeviceFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    int userId;

    Unbinder unbinder;
    @BindView(R.id.rl_share)
    RecyclerView rl_share;
    private List<String> usersInfo = new ArrayList<>();
    private List<List<Device>> devices = new ArrayList<>();
    @BindView(R.id.refersh_operate)
    PullToRefreshLayout refersh_operate;
    private DeviceSharedAdapter sharedAdapter;

    public ShareDeviceFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_share_device, container, false);
        unbinder = ButterKnife.bind(this, view);
        Bundle bundle = getArguments();
        userId = bundle.getInt("userId");
        usersInfo.clear();
        devices.clear();
        sharedAdapter = new DeviceSharedAdapter(getActivity(), usersInfo, devices);

        rl_share.setLayoutManager(new LinearLayoutManager(getActivity()));
        rl_share.setAdapter(sharedAdapter);

        params.clear();
        params.put("userId", userId);
        refersh_operate.setHeaderView(new MyHeadRefreshView(getActivity()));
        refersh_operate.setFooterView(new MyLoadMoreView(getActivity()));
        new SharedDeviceAsync(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);

        refersh_operate.setRefreshListener(new BaseRefreshListener() {
            @Override
            public void refresh() {
                refersh_operate.finishRefresh();
            }

            @Override
            public void loadMore() {
                params.clear();
                params.put("userId", userId);
                new SharedDeviceAsync(ShareDeviceFragment.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);

            }
        });
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    Map<String, Object> params = new HashMap<>();

    class DeviceSharedAdapter extends GroupedRecyclerViewAdapter {

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
            try {
                int size=devices.get(groupPosition).size();
                return size;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 0;
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
            String name = userInfos.get(groupPosition);
            TextView tv_user = holder.itemView.findViewById(R.id.tv_user);
            String name2 = name.substring(name.indexOf("&") + 1);
            Log.i("UserNameName", "-->" + name2);
            tv_user.setText("用户:" + name2);
        }

        @Override
        public void onBindFooterViewHolder(BaseViewHolder holder, int groupPosition) {

        }

        @Override
        public void onBindChildViewHolder(BaseViewHolder holder, final int groupPosition, final int childPosition) {
            RelativeLayout rl_body = holder.itemView.findViewById(R.id.rl_body);
            if (childPosition % 2 == 0) {
                if (childPosition == 0) {
                    rl_body.setBackgroundResource(R.drawable.shape_operate_share_top);
                } else {
                    rl_body.setBackgroundColor(Color.parseColor("#F0F0F0"));
                }
            } else if (childPosition % 2 != 0) {
                rl_body.setBackgroundColor(Color.parseColor("#FFFFFF"));
            }
            final Device device = devices.get(groupPosition).get(childPosition);
            if (device != null) {
                String deviceName = device.getDeviceName();
                TextView tv_name = holder.itemView.findViewById(R.id.tv_name);
                tv_name.setText(deviceName);
            }
            TextView tv_unbind = holder.itemView.findViewById(R.id.tv_unbind);
            tv_unbind.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String userInfo = userInfos.get(groupPosition);
                    String ss = userInfo.substring(0, userInfo.indexOf("&"));
                    long deviceId = device.getDeviceId();
                    params.clear();
                    params.put("deviceId", deviceId);
                    params.put("deviceSharerId", ss);
                    params.put("groupPosition", groupPosition);
                    params.put("childPosition", childPosition);
                    new UnbindDeviceAsync(ShareDeviceFragment.this).execute(params);
                }
            });
        }
    }

    class OperatorHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.rl_body)
        RelativeLayout rl_body;
        @BindView(R.id.tv_user)
        TextView tv_user;//操作用户
        @BindView(R.id.tv_timer)
        TextView tv_timer;//操作时间
        @BindView(R.id.tv_device_state)
        TextView tv_device_state;//设备开关状态

        public OperatorHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class SharedDeviceAsync extends BaseWeakAsyncTask<Map<String, Object>, Void, Integer, ShareDeviceFragment> {

        public SharedDeviceAsync(ShareDeviceFragment deviceRecordActivity) {
            super(deviceRecordActivity);
        }

        @Override
        protected Integer doInBackground(ShareDeviceFragment deviceRecordActivity, Map<String, Object>... maps) {
            int code = 0;
            try {
                Map<String, Object> map = maps[0];
                String url = HttpUtils.ipAddress + "device/getSharedDeviceList";
                String result = HttpUtils.requestPost(url, map);
                Log.i("SharedDeviceAsync", "-->" + result);
                if (!TextUtils.isEmpty(result)) {
                    JSONObject jsonObject = new JSONObject(result);
                    code = jsonObject.getInt("returnCode");
                    if (code == 100) {
                        devices.clear();
                        usersInfo.clear();
                        JSONArray returnData = jsonObject.getJSONArray("returnData");
                        for (int i = 0; i < returnData.length(); i++) {
                            JSONObject jsonObject2 = returnData.getJSONObject(i);
                            int sharerId = jsonObject2.getInt("sharerId");
                            String sharerName = jsonObject2.getString("sharerName");
                            String name = sharerId + "&" + sharerName;
                            usersInfo.add(name);
                            JSONArray deviceShareList = jsonObject2.getJSONArray("deviceShareList");
                            List<Device> list = new ArrayList<>();
                            for (int j = 0; j < deviceShareList.length(); j++) {
                                String s = deviceShareList.getJSONObject(j).toString();
                                Gson gson = new Gson();
                                Device device = gson.fromJson(s, Device.class);
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
        protected void onPostExecute(ShareDeviceFragment deviceRecordActivity, Integer integer) {

            try {
                if (refersh_operate != null) {
                    refersh_operate.finishLoadMore();

                }
                if (integer == 100 && sharedAdapter != null) {
                    sharedAdapter.notifyDataSetChanged();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    class UnbindDeviceAsync extends BaseWeakAsyncTask<Map<String, Object>, Void, Integer, ShareDeviceFragment> {

        public UnbindDeviceAsync(ShareDeviceFragment fragment) {
            super(fragment);
        }

        @Override
        protected Integer doInBackground(ShareDeviceFragment fragment, Map<String, Object>... maps) {
            int code = 0;
            try {
                int groupPosition = (int) params.get("groupPosition");
                int childPosition = (int) params.get("childPosition");
                Map<String, Object> params = maps[0];
                params.remove("groupPosition");
                params.remove("childPosition");
                String url = HttpUtils.ipAddress + "device/deleteShareDevice";
                String result = HttpUtils.requestPost(url, params);
                if (!TextUtils.isEmpty(result)) {
                    JSONObject jsonObject = new JSONObject(result);
                    code = jsonObject.getInt("returnCode");
                    if (code == 100) {
                        int groupDeviceSize=devices.get(groupPosition).size();
                        if (groupDeviceSize==1){
                            usersInfo.remove(groupPosition);
                            devices.remove(groupPosition);
                        }else {
                            devices.get(groupPosition).remove(childPosition);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return code;
        }

        @Override
        protected void onPostExecute(ShareDeviceFragment fragment, Integer integer) {
            if (integer == 100) {
                ToastUtil.showShort(getActivity(), "解除成功");
                sharedAdapter.notifyDataSetChanged();
            } else {
                ToastUtil.showShort(getActivity(), "解除失败");
            }
        }
    }

}
