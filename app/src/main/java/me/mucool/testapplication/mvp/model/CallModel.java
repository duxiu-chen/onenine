package me.mucool.testapplication.mvp.model;

import com.google.gson.JsonObject;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.mucool.testapplication.bean.BaseResponse;
import me.mucool.testapplication.bean.CallResponse;
import me.mucool.testapplication.network.api.CallService;
import me.mucool.testapplication.network.util.ServiceUtil;
import me.mucool.testapplication.utils.SharedPreferenceManager;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class CallModel {
    CallService eventService = ServiceUtil.getEventService();

    public void getCallList(int state, Observer<BaseResponse<CallResponse>> observer){
        JsonObject json = new JsonObject();
        json.addProperty("state",state);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json.toString());
        eventService.getCallList(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }


    public void completeCall(int id, Observer<BaseResponse> observer){
        JsonObject json = new JsonObject();
        json.addProperty("id",id);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json.toString());
        eventService.completeCall(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

}
