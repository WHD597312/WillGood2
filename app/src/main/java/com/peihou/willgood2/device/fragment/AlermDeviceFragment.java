package com.peihou.willgood2.device.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

public class AlermDeviceFragment extends Fragment {

    private Unbinder unbinder;
    @BindView(R.id.rl_operate)
    RecyclerView rl_operate;
    @BindView(R.id.refersh_operate)
    PullToRefreshLayout refersh_operate;
    private List<OperatorLog> alerms = new ArrayList<>();
    int userId;
    Map<String, Object> params = new HashMap<>();
    private int operate = 1;
    private int logAlerm = 0;

    public AlermDeviceFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private AlermLogAdapter alermAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_alerm_device, container, false);
        unbinder = ButterKnife.bind(this, view);
        operate = 1;
        alerms.clear();
        Bundle bundle = getArguments();
        userId = bundle.getInt("userId");
        rl_operate.setLayoutManager(new LinearLayoutManager(getActivity()));
        alermAdapter = new AlermLogAdapter(getActivity(), alerms);
        rl_operate.setAdapter(alermAdapter);

        refersh_operate.setHeaderView(new MyHeadRefreshView(getActivity()));
        refersh_operate.setFooterView(new MyLoadMoreView(getActivity()));

        params.put("userId", userId);
        params.put("deviceLogType", 2);
        params.put("pageNum", operate);
        new AlermAsync(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        refersh_operate.setRefreshListener(new BaseRefreshListener() {
            @Override
            public void refresh() {
                refersh_operate.finishRefresh();

            }

            @Override
            public void loadMore() {
                params.put("pageNum", operate++);
                new AlermAsync(AlermDeviceFragment.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }


    class AlermLogAdapter extends RecyclerView.Adapter<OperatorHolder> {

        private Context context;
        private List<OperatorLog> list;

        public AlermLogAdapter(Context context, List<OperatorLog> list) {
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
//                    holder.rl_body.setBackgroundResource(R.drawable.shape_operate_share_top);
//                }else {
//                    holder.rl_body.setBackgroundColor(Color.parseColor("#FFFFFF"));
//                }
            }
            OperatorLog log = list.get(position);

            String deviceName = log.getDeviceName();
            String deviceLine = log.getDeviceLine();
            String deviceLogTime = log.getDeviceLogTime();
            int deviceControll = log.getDeviceControll();

            holder.tv_user.setText(deviceName + "     " + deviceLine);
            holder.tv_timer.setText(deviceLogTime + "");
            holder.tv_device_state.setTextColor(Color.parseColor("#fe7918"));
            if (deviceControll == 1) {
                holder.tv_device_state.setText("来电报警");
            } else if (deviceControll == 2) {
                holder.tv_device_state.setText("断电报警报警");
            } else if (deviceControll == 3) {
                holder.tv_device_state.setText("温度报警");
            } else if (deviceControll == 4) {
                holder.tv_device_state.setText("湿度报警");
            } else if (deviceControll == 5) {
                holder.tv_device_state.setText("电流报警");
            } else if (deviceControll == 6) {
                holder.tv_device_state.setText("电压报警");
            } else if (deviceControll == 7) {
                holder.tv_device_state.setText("功率报警");
            } else if (deviceControll == 8) {
                holder.tv_device_state.setText("开关量报警");
            } else {
                holder.tv_device_state.setText("来电报警");
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

    class AlermAsync extends BaseWeakAsyncTask<Map<String, Object>, Void, Integer, AlermDeviceFragment> {

        public AlermAsync(AlermDeviceFragment deviceRecordActivity) {
            super(deviceRecordActivity);
        }

        @Override
        protected Integer doInBackground(AlermDeviceFragment deviceRecordActivity, Map<String, Object>... maps) {
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
                        logAlerm = returnData.getInt("logSum");

                        JSONArray deviceOperationLogList = returnData.getJSONArray("deviceOperationLogList");
                        for (int i = 0; i < deviceOperationLogList.length(); i++) {
                            String s = deviceOperationLogList.getJSONObject(i).toString();
                            Gson gson = new Gson();
                            OperatorLog operatorLog = gson.fromJson(s, OperatorLog.class);
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
        protected void onPostExecute(AlermDeviceFragment deviceRecordActivity, Integer integer) {
//            if (alermCallBack!=null){
//                alermCallBack.setAlerm(operate);
//            }
            if (refersh_operate != null) {
                refersh_operate.finishLoadMore();
            }

            if (integer == 100 && alermAdapter != null) {
                alermAdapter.notifyDataSetChanged();
            }
        }
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        alermCallBack= (AlermCallBack) getActivity();
//    }
//
//    AlermCallBack alermCallBack;
//    public interface AlermCallBack{
//        public void setAlerm(int alerm);
//    }
}
