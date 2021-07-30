package me.mucool.testapplication.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import me.mucool.testapplication.R;

import static android.content.Context.NOTIFICATION_SERVICE;

public class BadgeUtils {
    public static void setBadgeNum(Context context, int number) {//context对象，小红点的数量
        if (BackgroundUtil.isHuawei()) {//这个是华为的
            //华为的如果不想有小红点，传0就可以
            try {
                Bundle bunlde = new Bundle();
                bunlde.putString("package", context.getPackageName()); // 这个是包名
                bunlde.putString("class", context.getPackageManager().getLaunchIntentForPackage(context.getPackageName()).getComponent().getClassName()); // 这个是应用启动的页面路径
                bunlde.putInt("badgenumber", number);
                context.getContentResolver().call(Uri.parse("content://com.huawei.android.launcher.settings/badge/"), "change_badge", null, bunlde);
            } catch (Exception e) {
                Log.e("华为桌面图标", e.getMessage());
            }
        } else if (BackgroundUtil.isXiaomi()) {//这个是小米的
            //小米的需要发一个通知，进入应用后红点会自动消失，测试的时候进程只能在后台，否则没有效果
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
                String channelId = "default";
                String channelName = "默认通知";
                notificationManager.createNotificationChannel(new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH));
            }
            Intent intent = new Intent();//可以用intent设置点击通知后的页面
            Notification notification = new NotificationCompat.Builder(context, "default")
                    .setContentIntent(PendingIntent.getActivity(context, 0, intent, 0))
                    .setContentTitle("")
                    .setContentText("")
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.logo)
                    .setAutoCancel(true)
                    .build();
            try {
                Field field = notification.getClass().getDeclaredField("extraNotification");
                Object extraNotification = field.get(notification);
                Method method = extraNotification.getClass().getDeclaredMethod("setMessageCount", int.class);
                method.invoke(extraNotification, number);
            } catch (Exception e) {
                Log.e("小米桌面图标", e.getMessage());
            }
        }
    }
}
