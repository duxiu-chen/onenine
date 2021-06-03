package me.mucool.testapplication.jpush;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.util.Log;
import org.json.JSONObject;

import cn.jpush.android.api.CmdMessage;
import cn.jpush.android.api.NotificationMessage;
import cn.jpush.android.service.JPushMessageReceiver;
import me.mucool.testapplication.ui.activity.ServiceRecordActivity;
import me.mucool.testapplication.utils.MyMediaPlay;
import me.mucool.testapplication.utils.SharedPreferenceManager;

public class JReceiver extends JPushMessageReceiver {

    private static final String TAG = "PushMessageReceiver";

    @Override
    public void onNotifyMessageOpened(Context context, NotificationMessage message) {
        Log.e(TAG,"[onNotifyMessageOpened] "+message);
        try{
            String extras = message.notificationContent;
            if (!TextUtils.isEmpty(extras)){
                    Intent intent = new Intent(context, ServiceRecordActivity.class);
                    intent.putExtra("hasNew", true);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
                    context.startActivity(intent);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onNotifyMessageArrived(Context context, NotificationMessage message) {
        Log.e(TAG,"[onNotifyMessageArrived] "+message);
        try {
            MediaPlayer player = MyMediaPlay.getInstance(context);
            if (!player.isPlaying() && SharedPreferenceManager.getLoginResponse()!=null && SharedPreferenceManager.getLoginResponse().getData().getReceiveCall()==1){
                player.start();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onNotifyMessageDismiss(Context context, NotificationMessage message) {
        Log.e(TAG,"[onNotifyMessageDismiss] "+message);
    }

    @Override
    public void onRegister(Context context, String registrationId) {
        Log.e(TAG,"[onRegister] "+registrationId);
    }

    @Override
    public void onConnected(Context context, boolean isConnected) {
        Log.e(TAG,"[onConnected] "+isConnected);
    }

    @Override
    public void onCommandResult(Context context, CmdMessage cmdMessage) {
        Log.e(TAG,"[onCommandResult] "+cmdMessage);
    }

}
