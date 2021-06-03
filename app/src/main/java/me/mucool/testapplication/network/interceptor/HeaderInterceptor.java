package me.mucool.testapplication.network.interceptor;


import java.io.IOException;

import me.mucool.testapplication.utils.SharedPreferenceManager;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class HeaderInterceptor implements Interceptor {


    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Request.Builder requestBuilder = original.newBuilder().header("Authorization", SharedPreferenceManager.getToken());
        Request request = requestBuilder.build();
        return chain.proceed(request);
    }

}
