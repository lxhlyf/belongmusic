package com.belongmusic.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.annotation.Nullable;

import com.belongmusic.R;
import com.belongmusic.entity.SongUrl;
import com.belongmusic.util.GlobalConsts;
import com.belongmusic.util.HttpUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Created by 枫暮晓 on 2018/8/4.
 */

public class DownService extends IntentService {



    //需要无参的构造方法，但是IntentService只有一个有参的构造方法。
    public DownService() {
        super("yifeng");
    }

    /**该方法在工作线程中执行，可以直接编写成耗时代码
    * 我们需要在该方法中发送http请求
    * 完成下载业务（边都去边保存）
     */
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        //工作线程可以直接发请求
        //获取Activity传递过来的参数
        String path = intent.getStringExtra("path");
        String title = intent.getStringExtra("title");
        int bitrate = intent.getIntExtra("bitrate",0);
        int filesize = intent.getIntExtra("filesize",0);

        try {
            if (path.equals("")&&path == null){//没有路径
                return;
            }
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC),
                    "_"+bitrate+"/"+title+".mp3");
            if (!file.getParentFile().exists()){
                file.getParentFile().mkdirs();
            }
            //边读取，边保存在sd卡中
            InputStream is = HttpUtils.get(path);
            FileOutputStream fos = new FileOutputStream(file);
            byte[] buffer = new byte[1024*200];
            int len = 0;
            //准备开始下载，发送通知
            sendNotification("音乐下载","音乐开始下载","音乐下载呀！");
            int progress = 0;
            while ((len = is.read(buffer))!=-1){
                fos.write(buffer,0,len);
                fos.flush();
                //保存的过程中发通知，提示进度
                progress += len;
                String jindu = Math.floor(100.0*progress/filesize)+"%";
                sendNotification("音乐下载","音乐开始下载","下载进度："+jindu);
            }
            fos.close();
            //下载完毕，发送通知
            clearNotification();
            sendNotification("音乐下载","音乐下载好了","音乐下载好了");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发通知
     * @param title
     */
    public void sendNotification(String title, String tiker, String text) {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(this);
        builder.setTicker(tiker)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(text)
                .setContentTitle(title);
        Notification nf = builder.build();
        manager.notify(GlobalConsts.NOTIFICATION_ID,nf);
    }

    /**
     * 清除通知
     */
    public void clearNotification(){
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(GlobalConsts.NOTIFICATION_ID);
    }
}
