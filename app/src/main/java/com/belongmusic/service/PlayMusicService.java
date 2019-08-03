package com.belongmusic.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import android.support.annotation.Nullable;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.belongmusic.R;
import com.belongmusic.app.MusicApplication;
import com.belongmusic.entity.Music;
import com.belongmusic.util.GlobalConsts;

import java.io.IOException;

/**
 * Created by 枫暮晓 on 2018/8/1.
 */

public class PlayMusicService extends Service {

    private MediaPlayer player = new MediaPlayer();
    private Boolean isLoop = true;
    private String time;
    private int position;

    //只执行一次
    @Override
    public void onCreate() {
        super.onCreate();
        //为MediaPlayer设置监听
        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            //当prepareAsync()准备完毕后执行
            @Override
            public void onPrepared(MediaPlayer mp) {
                player.start();

                //发送一个音乐已经开始播放的广播
                Intent intent = new Intent(GlobalConsts.ACTION_MUSIC_STARTED);
                sendBroadcast(intent);
            }
        });
        //启动一个工作线程，每1秒发送一次广播,程序一开始，线程就被唤醒，但只有当音乐正在播放时才会执行。
        new Thread(){
            @Override
            public void run() {
                while (isLoop){
                    try {
                        Thread.sleep(1000);
                        //发送广播
                        if (player.isPlaying()){
                            int total = player.getDuration();
                            int current = player.getCurrentPosition();
                            Intent intent = new Intent(GlobalConsts.ACTION_UPDATA_MUSIC_STARTED);
                            intent.putExtra("total", total);
                            intent.putExtra("current", current);
                            sendBroadcast(intent);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return new MusicBinder();
    }


    @Override
    public void onDestroy() {
        //解开音乐文件。
        player.release();
        //停止工作线程
        isLoop = false;
        super.onDestroy();
    }
    /**
     * 在binder中声明客户端调用的方法
     */
    public class MusicBinder extends Binder{
        //暂停或播放
       public void startOrPause(){
           if (player.isPlaying()) {
               player.pause();
           }else {
               player.start();
           }
       }
        //播放音乐
        public void playMusic(String url){
            try {
                player.reset();
                player.setDataSource(url);
                player.prepareAsync();
                //必须等准备好了之后才能播放
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        public void seekTO(int position){
            player.seekTo(position);
        }
    }


}
