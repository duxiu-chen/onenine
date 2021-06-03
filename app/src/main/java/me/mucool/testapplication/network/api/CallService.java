package me.mucool.testapplication.network.api;

import io.reactivex.Observable;
import me.mucool.testapplication.bean.BaseResponse;
import me.mucool.testapplication.bean.CallResponse;
import me.mucool.testapplication.bean.LoginResponse;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface CallService {
    /**
     * 服务生--获得呼叫请求列表
     * @param body
     * @return
     */
    @HTTP(method = "POST", path = "/jeeplus/a/web/yjMahjongHallWaiters/voicesList", hasBody = true)
    Observable<BaseResponse<CallResponse>> getCallList(@Body RequestBody body);

    /**
     * 服务生--完成呼叫请求
     * @param body
     * @return
     */
    @HTTP(method = "PUT", path = "/jeeplus/a/web/yjMahjongHallWaiters/completeCall", hasBody = true)
    Observable<BaseResponse> completeCall(@Body RequestBody body);

}
