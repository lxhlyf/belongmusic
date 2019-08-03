package com.belongmusic.app;

import android.app.Application;

import com.belongmusic.entity.Music;

import java.util.List;

/**
 * Created by 枫暮晓 on 2018/8/1.
 */

public class MusicApplication extends Application {

    private List<Music> musics;
    private int position;
    private static MusicApplication app;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;//把当前对象存入静态变量app

    }

    //静态变量可以直接用类名调用
    public static MusicApplication getApp(){
        return app;
    }

    public void setMusics(List<Music> musics) {
        this.musics = musics;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPosition(){
        return position;
    }

    /**
     * 获取当前正在播放的音乐对象
     */
    public Music getCurrentMusic(){
        return  this.musics.get(this.position);
    }

    /**
     * 跳转到上一首歌
     */
    public void preMusic() {
        position = position == 0 ? musics.size()-1 : position-1;
    }
    /**
     * 跳转到下一首歌
     */
    public void nextMusic() {
        position = position == musics.size()-1 ? 0 : position+1;
    }

    /**
     * 循环播放
     */


}

