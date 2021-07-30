package me.mucool.testapplication;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.annotation.StringRes;
import androidx.multidex.MultiDex;

import com.tencent.bugly.Bugly;

import cn.jpush.android.api.JPushInterface;
import me.mucool.testapplication.utils.SharedPreferenceManager;

import static android.content.ContentValues.TAG;

public class App extends Application {

    private static App sContext;

    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        sContext = this;
        //如果是Android7.0以上，还会存在不能访问路径问题
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static App getContext() {
        return sContext;
    }

    public Handler getHandler() {
        return handler;
    }

    public void showMessage(final String message) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (TextUtils.isEmpty(message)) {
                    return;
                }
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showMessage(@StringRes final int message) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

    }
}
