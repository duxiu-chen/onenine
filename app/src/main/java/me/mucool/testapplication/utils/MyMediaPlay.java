package me.mucool.testapplication.utils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;

public class MyMediaPlay {
    private static MediaPlayer player;

    private MyMediaPlay(){

    }

    public synchronized static MediaPlayer  getInstance(Context context){
        if (player == null){
            try {
                player = new MediaPlayer();
                AssetManager assetManager;
                assetManager = context.getResources().getAssets();
                AssetFileDescriptor fileDescriptor = assetManager.openFd("sms2.wav");
                player.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(), fileDescriptor.getStartOffset());
                player.prepare();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return player;
    }
}
