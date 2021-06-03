package me.mucool.testapplication.network.api;




import io.reactivex.Observable;
import me.mucool.testapplication.bean.BaseResponse;
import me.mucool.testapplication.bean.LoginResponse;
import me.mucool.testapplication.bean.StringResponse;
import me.mucool.testapplication.bean.UserInfoResponse;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface UserService {
    @FormUrlEncoded
    @POST("/jeeplus/android/sendWaiterCaptcha")
    Observable<BaseResponse> authCode(@Query("phoneNumber") String phoneNumber, @Field("version") int version);

    @POST("/jeeplus/a/web/yjMahjongHallWaiters/login")
    Observable<BaseResponse<LoginResponse>> mobileLogin(@Body RequestBody body);

    @FormUrlEncoded
    @POST("/jeeplus/a/web/yjMahjongHallWaiters/login")
    Observable<BaseResponse<LoginResponse>> mobileLogin(@Field("captcha") String token);

    /***
     * 上传头像
     * @return
     */
    @POST("/jeeplus/a/web/yjMahjongHallWaiters/uploadAvatar")
    Observable<BaseResponse<StringResponse>> uploadAvatar(@Body RequestBody body);

    /***
     * 服务生--修改属性（是否接收呼叫、上班状态等）
     * @return
     */
    @HTTP(method = "PUT", path = "/jeeplus/a/web/yjMahjongHallWaiters/updateMahjongHallWaiter", hasBody = true)
    Observable<BaseResponse<StringResponse>> updateStatus(@Body RequestBody body);


    /***
     * 获取用户信息
     * @return
     */
    @GET("/jeeplus/a/web/yjMahjongHallWaiters/getAndroidMahjongHallWaiterInfo")
    Observable<BaseResponse<UserInfoResponse>> getUserInfo();
}
