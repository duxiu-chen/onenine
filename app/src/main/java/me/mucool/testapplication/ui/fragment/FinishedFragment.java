package me.mucool.testapplication.ui.fragment;


import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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

import java.util.ArrayList;
import java.util.List;

import me.mucool.testapplication.App;
import me.mucool.testapplication.R;
import me.mucool.testapplication.adapter.CallAdapter;
import me.mucool.testapplication.bean.BaseResponse;
import me.mucool.testapplication.bean.CallResponse;
import me.mucool.testapplication.mvp.contract.event.CallContract;
import me.mucool.testapplication.mvp.presenter.event.CallPresenter;
import me.mucool.testapplication.ui.activity.ServiceRecordFragment;
import me.mucool.testapplication.ui.base.BaseActivity;
import me.mucool.testapplication.ui.base.BaseFragment;
import me.mucool.testapplication.utils.SharedPreferenceManager;
import me.mucool.testapplication.utils.VoicePlay;

/**
 * Created by mucool on 2019/12/12.
 */

public class FinishedFragment extends BaseFragment implements CallContract.View, ServiceRecordFragment.UpdateNew {

    private List<CallResponse.DataBean> callList = new ArrayList<>();

    MediaPlayer player;

    private RecyclerView recyclerView;
    private View emptyView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CallAdapter callAdapter;

    private CallPresenter callPresenter;

    private int state = 1;

    private LinearLayoutManager linearLayoutManager;

    private int position =-1;

    private int intoFlag = 0;

    private View curView =null;

    private boolean firstClick = true;

    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case 1:
                    int timeBean = (int)msg.obj;
                    if (curView!=null){
                        TextView textView = curView.findViewById(R.id.tv_duration);
                        ImageView img_band = curView.findViewById(R.id.img_band);
                        if (textView!=null && timeBean>0)
                            textView.setText(callAdapter.getCourseTime(timeBean));
                        if (img_band!=null){
                            AnimationDrawable animation = (AnimationDrawable)img_band.getBackground();
                            if(!animation.isRunning())//是否正在运行？
                            {
                                animation.start();//启动
                            }
                        }


                    }

                    break;
                case 2:
                    callAdapter.notifyDataSetChanged();
                    if (curView!=null){
                        ImageView img_band = curView.findViewById(R.id.img_band);
                        AnimationDrawable animation = (AnimationDrawable)img_band.getBackground();
                        animation.stop();//停止
                    }
                    if (state==1){
                        showLoading();
                        callPresenter.responseCall(position, Integer.valueOf(callList.get(position).getId()));
                    }else
                        position=-1;
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
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && intoFlag >0){
            showLoading();
            position=-1;
            callPresenter.getCallList(SharedPreferenceManager.getUserPhone(),state);
            curView=null;
        }

        if (!isVisibleToUser){
            position=-1;
            if (handler!=null)
                handler.removeCallbacks(runnable);
            if (player!=null && player.isPlaying())
                player.stop();
            if (curView!=null){
                ImageView img_band = curView.findViewById(R.id.img_band);
                AnimationDrawable animation = (AnimationDrawable)img_band.getBackground();
                animation.stop();//停止
            }
            curView=null;
        }

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
        player = VoicePlay.getInstance(mContext);
        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        callPresenter = new CallPresenter(this);
        showLoading();
        callPresenter.getCallList(SharedPreferenceManager.getUserPhone(),state);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                position=-1;
                if (player.isPlaying())
                    player.stop();
                player.reset();
                handler.removeCallbacks(runnable);
                callPresenter.getCallList(SharedPreferenceManager.getUserPhone(),state);
            }
        });
        intoFlag++;
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

            if (getContext()!=null){
                callList = callResponseList.getData();
                emptyView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                callAdapter = new CallAdapter(callResponseList.getData(), getContext());
                recyclerView.setAdapter(callAdapter);
                if (state==1 || state==3)
                    callAdapter.setOnAdapterClickListener(new CallAdapter.AdapterClickListener() {
                        @Override
                        public void click(@NotNull String id) {
                            showLoading();
                            callPresenter.completeCall(SharedPreferenceManager.getUserPhone() ,Integer.valueOf(id));
                        }
                    });
                callAdapter.notifyDataSetChanged();
                callAdapter.setOnVoiceClickListener(new CallAdapter.VoiceClickListener() {
                    @Override
                    public void clickVoice(final int pos, @NotNull String dataUrl, View itemView) {
                        if (pos==position){

                        }else {
                            handler.removeCallbacks(runnable);
                            if (curView!=null){
                                ImageView img_band = curView.findViewById(R.id.img_band);
                                AnimationDrawable animation = (AnimationDrawable)img_band.getBackground();
                                if(animation.isRunning())//是否正在运行？
                                {
                                    animation.stop();//停止
                                }
                            }

                            curView = itemView;
                            position = pos;
                            try {
                                if (player.isPlaying())
                                    player.stop();
                                player.reset();
                                player.setDataSource(dataUrl);
                                player.prepare();
                                player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                    @Override
                                    public void onPrepared(MediaPlayer mp) {
                                        handler.post(runnable);
                                        player.start();//准备完成开始播放
                                        if (curView!=null){
                                            TextView textView = curView.findViewById(R.id.tv_duration);
                                            textView.setText(callAdapter.getCourseTime(0));
                                        }

                                    }
                                });
                                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mp) {
                                        handler.removeCallbacks(runnable);
                                        handler.sendEmptyMessage(2);
                                    }
                                });
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                        }

                    }
                });
            }
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
        hideLoading();
        callPresenter.getCallList(SharedPreferenceManager.getUserPhone(),state);
    }

    @Override
    public void completeCallFail(String msg) {
        hideLoading();
        App.getContext().showMessage(msg);
    }

    @Override
    public void responseCallSuccess(BaseResponse baseResponse, int pos) {
        position=-1;
        App.getContext().showMessage(baseResponse.getMsg());
        callPresenter.getCallList(SharedPreferenceManager.getUserPhone(),state);
    }

    @Override
    public void responseCallFail(String msg) {
        hideLoading();
        App.getContext().showMessage(msg);
    }

    @Override
    public void update() {
        showLoading();
        callPresenter.getCallList(SharedPreferenceManager.getUserPhone(),state);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Message message = Message.obtain();
            message.what = 1;
            message.obj = player.getCurrentPosition()/1000;
            Log.e("Runnable","Runnable:"+player.getCurrentPosition()/1000);
            handler.sendMessage(message);
            handler.postDelayed(runnable, 500);
        }
    };

}
