package me.mucool.testapplication.network.util;

import java.util.concurrent.TimeUnit;

import me.mucool.testapplication.BuildConfig;
import me.mucool.testapplication.network.factory.BaseConverterFactory;
import me.mucool.testapplication.network.interceptor.HeaderInterceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

public class RetrofitUtil {

    public static String BASE_URL = "http://47.110.49.157:8090/";

    private static final int DEFAULT_TIMEOUT = 60;

    public static Retrofit getRetrofit() {

        // 创建一个OkHttpClient
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        builder.readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        builder.writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        //  builder.sslSocketFactory(SSLSocketClient.getSSLSocketFactory());
        // builder.hostnameVerifier(SSLSocketClient.getHostnameVerifier());

        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor();
        if (BuildConfig.DEBUG) {
            logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            logInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        }
          builder.addInterceptor(new HeaderInterceptor());
        builder.addInterceptor(logInterceptor);

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(builder.build())
                .addConverterFactory(BaseConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }


}
