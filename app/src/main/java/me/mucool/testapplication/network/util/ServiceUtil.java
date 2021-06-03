package me.mucool.testapplication.network.util;


import me.mucool.testapplication.network.api.CallService;
import me.mucool.testapplication.network.api.UserService;
import retrofit2.Retrofit;

public class ServiceUtil {

    static Retrofit retrofit = RetrofitUtil.getRetrofit();

    public static UserService getUserService() {
        return retrofit.create(UserService.class);
    }

    public static CallService getEventService() {
        return retrofit.create(CallService.class);
    }

}
