package me.mucool.testapplication.ui.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ywl5320.bean.TimeBean;
import com.ywl5320.libmusic.WlMusic;
import com.ywl5320.listener.OnCompleteListener;
import com.ywl5320.listener.OnInfoListener;
import com.ywl5320.listener.OnPreparedListener;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

import butterknife.BindView;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import me.mucool.testapplication.App;
import me.mucool.testapplication.R;
import me.mucool.testapplication.adapter.CallAdapter;
import me.mucool.testapplication.bean.BaseResponse;
import me.mucool.testapplication.bean.CallResponse;
import me.mucool.testapplication.mvp.contract.event.CallContract;
import me.mucool.testapplication.mvp.presenter.event.CallPresenter;
import me.mucool.testapplication.network.util.RetrofitUtil;
import me.mucool.testapplication.ui.activity.ServiceRecordActivity;
import me.mucool.testapplication.ui.base.BaseActivity;
import me.mucool.testapplication.ui.base.BaseFragment;
import me.mucool.testapplication.utils.SharedPreferenceManager;

/**
 * Created by mucool on 2019/12/12.
 */

public class FinishedFragment extends BaseFragment implements CallContract.View, ServiceRecordActivity.UpdateNew {

    WlMusic wlMusic = WlMusic.getInstance();

    private RecyclerView recyclerView;
    private View emptyView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CallAdapter callAdapter;

    private CallPresenter callPresenter;

    private int state = 1;

    private LinearLayoutManager linearLayoutManager;

    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case 1:
                    TimeBean timeBean = (TimeBean) msg.obj;
                    View child = recyclerView.getChildAt(msg.arg1);
                    if (child!=null){
                        TextView textView = child.findViewById(R.id.tv_duration);
                        if (textView!=null && timeBean.getCurrSecs()>0)
                            textView.setText(callAdapter.getCourseTime(timeBean.getCurrSecs()));
                    }

                    break;
                case 2:
                    callAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    };


    public static FinishedFragment getInstance(int state) {
        FinishedFragment finishedFragment = new FinishedFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("state", state);
        finishedFragment.setArguments(bundle);
        return finishedFragment;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        emptyView = root.findViewById(R.id.empty_view);
        recyclerView = root.findViewById(R.id.recyclerView);
        swipeRefreshLayout = root.findViewById(R.id.refreshLayout);
    }

    @Override
    protected void initData() {
        super.initData();
        if (null != getArguments()) {
            state = getArguments().getInt("state");
        }
        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        callPresenter = new CallPresenter(this);
        showLoading();
        callPresenter.getCallList(SharedPreferenceManager.getUserPhone(),state);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                callPresenter.getCallList(SharedPreferenceManager.getUserPhone(),state);
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_finished;
    }

    private BaseActivity activity;

    private void showLoading() {
        if (getActivity() instanceof BaseActivity) {
            activity = (BaseActivity) getActivity();
        }
        if (null != activity) {
            activity.showWaitingDialog("加载中...", true);
        }
    }

    private void hideLoading() {
        if (getActivity() instanceof BaseActivity) {
            activity = (BaseActivity) getActivity();
        }
        if (null != activity) {
            activity.closeWaitingDialog();
        }
    }

    @Override
    public void getCallListSuccess(CallResponse callResponseList) {
        swipeRefreshLayout.setRefreshing(false);
        if (callResponseList== null || callResponseList.getData().size() == 0){
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }else {
            emptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            callAdapter = new CallAdapter(callResponseList.getData(), getContext());
            recyclerView.setAdapter(callAdapter);
            if (state==1)
                callAdapter.setOnAdapterClickListener(new CallAdapter.AdapterClickListener() {
                    @Override
                    public void click(@NotNull String id) {
                        showLoading();
                        callPresenter.completeCall(SharedPreferenceManager.getUserPhone() ,Integer.valueOf(id));
                    }
                });
            callAdapter.setOnVoiceClickListener(new CallAdapter.VoiceClickListener() {
                @Override
                public void clickVoice(final int pos, @NotNull String dataUrl) {
                    wlMusic.stop();
                    wlMusic.setSource(dataUrl);
                    wlMusic.prePared();
                    wlMusic.setOnPreparedListener(new OnPreparedListener() {
                        @Override
                        public void onPrepared() {
                            wlMusic.start(); //准备完成开始播放
                        }
                    });
                    wlMusic.setOnInfoListener(new OnInfoListener() {
                        @Override
                        public void onInfo(TimeBean timeBean) {
                            Log.e("timeBean", "curr:" + timeBean.getCurrSecs() + ", total:" + timeBean.getTotalSecs());
                            Message message = Message.obtain();
                            message.obj = timeBean;
                            message.arg1 = pos;
                            message.what = 1;
                            handler.sendMessage(message);
                        }
                    });
                    wlMusic.setOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete() {
                            handler.sendEmptyMessage(2);
                        }
                    });
                }
            });
        }

        hideLoading();
    }

    @Override
    public void getCallListFail(String msg) {
        emptyView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        hideLoading();
        App.getContext().showMessage(msg);
    }

    @Override
    public void completeCallSuccess(BaseResponse baseResponse) {
        App.getContext().showMessage(baseResponse.getMsg());
        callPresenter.getCallList(SharedPreferenceManager.getUserPhone(),state);
    }

    @Override
    public void completeCallFail(String msg) {
        hideLoading();
        App.getContext().showMessage(msg);
    }

    @Override
    public void onStop() {
        super.onStop();
        wlMusic.stop();
    }

    @Override
    public void update() {
        callPresenter.getCallList(SharedPreferenceManager.getUserPhone(),state);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                callPresenter.getCallList(SharedPreferenceManager.getUserPhone(),state);
            }
        });
    }
}
