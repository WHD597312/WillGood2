package com.peihou.willgood2.device.fragment;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.jwenfeng.library.pulltorefresh.BaseRefreshListener;
import com.jwenfeng.library.pulltorefresh.PullToRefreshLayout;
import com.jwenfeng.library.pulltorefresh.view.LoadMoreView;
import com.peihou.willgood2.R;
import com.peihou.willgood2.custom.MyHeadRefreshView;
import com.peihou.willgood2.custom.MyLoadMoreView;
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
import butterknife.Unbinder;


public class OperateDeviceFragment extends Fragment {


    int userId;
    private Unbinder unbinder;
    @BindView(R.id.rl_operate)
    RecyclerView rl_operate;
    private List<OperatorLog> logs = new ArrayList<>();//操作日志列表
    private OperaterLogAdapter logAdapter;
    @BindView(R.id.refersh_operate)
    PullToRefreshLayout refersh_operate;
    Map<String, Object> params = new HashMap<>();
    private int operate=1;
    public OperateDeviceFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_operate_device, container, false);
        unbinder=ButterKnife.bind(this,view);
        // Inflate the layout for this fragment
        Bundle bundle=getArguments();
        logs.clear();
        userId=bundle.getInt("userId");
        rl_operate.setLayoutManager(new LinearLayoutManager(getActivity()));
        logAdapter = new OperaterLogAdapter(getActivity(), logs);
        rl_operate.setAdapter(logAdapter);
        refersh_operate.setHeaderView(new MyHeadRefreshView(getActivity()));
        refersh_operate.setFooterView(new MyLoadMoreView(getActivity()));

        Log.i("operateSum","-->"+operate);
        params.put("userId", userId);
        params.put("deviceLogType", 1);
        params.put("pageNum", operate);
        new OperateAsync(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        refersh_operate.setRefreshListener(new BaseRefreshListener() {
            @Override
            public void refresh() {
                refersh_operate.finishRefresh();
            }

            @Override
            public void loadMore() {
                params.put("pageNum", operate++);
                new OperateAsync(OperateDeviceFragment.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);

            }
        });

        return view;
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        operateCallBack= (OperateCallBack) getActivity();
//    }
//
//    OperateCallBack operateCallBack;
//    public interface OperateCallBack{
//        public void setOpeate(int operate);
//    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder!=null){
            unbinder.unbind();
        }
    }

    class OperaterLogAdapter extends RecyclerView.Adapter<OperatorHolder> {

        private Context context;
        private List<OperatorLog> list;

        public OperaterLogAdapter(Context context, List<OperatorLog> list) {
            this.context = context;
            this.list = list;
        }

        @NonNull
        @Override
        public OperatorHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = View.inflate(context, R.layout.item_operate_log, null);
            return new OperatorHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull OperatorHolder holder, int position) {
            int last = list.size() - 1;
            if (position % 2 == 0) {
                if (position == 0) {
                    holder.rl_body.setBackgroundResource(R.drawable.shape_operate_share_top);
                } else {
                    holder.rl_body.setBackgroundColor(Color.parseColor("#F0F0F0"));
                }
            } else if (position % 2 != 0) {
                holder.rl_body.setBackgroundColor(Color.parseColor("#FFFFFF"));
//                if (position==last){
//                    holder.rl_body.setBackgroundResource(R.drawable.shape_operate_share_bottom);
//                }else {
//                    holder.rl_body.setBackgroundColor(Color.parseColor("#FFFFFF"));
//                }
            }


            OperatorLog log = list.get(position);
            String userPhone = log.getUserPhone();
            String deviceName = log.getDeviceName();
            String deviceLine = log.getDeviceLine();
            String deviceLogTime = log.getDeviceLogTime();
            int deviceControll = log.getDeviceControll();

            holder.tv_user.setText(userPhone+"     "+deviceName + "     " + deviceLine);
            holder.tv_timer.setText(deviceLogTime + "");

            if (deviceControll == 1) {
                holder.tv_device_state.setText("打开设备");
                holder.tv_device_state.setTextColor(Color.parseColor("#09c585"));
            } else {
                holder.tv_device_state.setText("关闭设备");
                holder.tv_device_state.setTextColor(Color.parseColor("#fe7918"));
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
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
    private int logSum=0;
    class OperateAsync extends BaseWeakAsyncTask<Map<String, Object>, Void, Integer, OperateDeviceFragment> {

        public OperateAsync(OperateDeviceFragment deviceRecordActivity) {
            super(deviceRecordActivity);
        }

        @Override
        protected Integer doInBackground(OperateDeviceFragment deviceRecordActivity, Map<String, Object>... maps) {
            int code = 0;
            try {
                Map<String, Object> map = maps[0];
                String url = HttpUtils.ipAddress + "data/getOperationLog";
                String result = HttpUtils.requestPost(url, map);
                if (!TextUtils.isEmpty(result)) {
                    JSONObject jsonObject = new JSONObject(result);
                    code = jsonObject.getInt("returnCode");
                    if (code == 100) {
                        JSONObject returnData = jsonObject.getJSONObject("returnData");
                        logSum = returnData.getInt("logSum");
                        Log.i("returnDataOperate",returnData.toString());
                        Log.i("logSum","-->logSum="+logSum+",logs="+logs.size());

                            JSONArray deviceOperationLogList = returnData.getJSONArray("deviceOperationLogList");
                            for (int i = 0; i < deviceOperationLogList.length(); i++) {
                                String s = deviceOperationLogList.getJSONObject(i).toString();
                                Gson gson = new Gson();
                                OperatorLog operatorLog = gson.fromJson(s, OperatorLog.class);
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
        protected void onPostExecute(OperateDeviceFragment deviceRecordActivity, Integer integer) {
            refersh_operate.finishLoadMore();
//            if (operateCallBack!=null){
//                operateCallBack.setOpeate(1);
//            }
            if (integer == 100) {
                logAdapter.notifyDataSetChanged();
            }
        }
    }

}
