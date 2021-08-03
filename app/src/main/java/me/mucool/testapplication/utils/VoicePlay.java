package me.mucool.testapplication.utils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;

public class VoicePlay {

    private static MediaPlayer player;

    private VoicePlay(){

    }

    public synchronized static MediaPlayer getInstance(Context context){
        if (player == null){
            try {
                player = new MediaPlayer();
                player.setLooping(false);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return player;
    }


    public static void release(){
        if (player != null){
            player.release();
            player = null;
        }
    }

}
