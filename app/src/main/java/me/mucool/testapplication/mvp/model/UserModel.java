package me.mucool.testapplication.mvp.model;

import android.text.TextUtils;

import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.mucool.testapplication.bean.BaseResponse;
import me.mucool.testapplication.bean.LoginResponse;
import me.mucool.testapplication.bean.StringResponse;
import me.mucool.testapplication.bean.UserInfoResponse;
import me.mucool.testapplication.network.api.UserService;
import me.mucool.testapplication.network.util.ServiceUtil;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.http.Field;

public class UserModel {

    UserService userService = ServiceUtil.getUserService();

    //获取验证码
    public void authCode(String mobile, Observer<BaseResponse> observer) {
        userService.authCode(mobile, 1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }


    //登录
    public void mobileLogin(String mobile, String captcha, Observer<BaseResponse<LoginResponse>> observer) {
        JsonObject json = new JsonObject();
        json.addProperty("phoneNumber",mobile);
        json.addProperty("captcha",captcha);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json.toString());

        userService.mobileLogin(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    /***
     * 修改头像
     * @param base64Data
     * @param observer
     */
    public void uploadAvatar(String base64Data, Observer<BaseResponse<StringResponse>> observer){
        JsonObject json = new JsonObject();
        json.addProperty("base64Data",base64Data);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json.toString());

        userService.uploadAvatar(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    /**
     * 修改服务生状态
     * @param receiveCall  是否接收语音推送 0-不接收 1-接收
     * @param workState  工作状态 0-休息中 1-工作中
     * @param observer
     */
    public void updateStatus(int receiveCall, int workState, Observer<BaseResponse<StringResponse>> observer){
        JsonObject json = new JsonObject();
        json.addProperty("receiveCall",receiveCall);
        json.addProperty("workState",workState);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json.toString());

        userService.updateStatus(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void getUserInfo(Observer<BaseResponse<UserInfoResponse>> observer){
        userService.getUserInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

}
