package com.belongmusic.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;

import com.belongmusic.R;


import java.io.File;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 枫暮晓 on 2018/7/31.
 * 异步批量加载图片的工具类
 */

public class ImageLoader {

    private Context context;
    private static final int HANDLER_IMAGE_LOADED = 100;
    private Boolean isLoop = true;
    private ListView listView;
    //声明Handler
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case HANDLER_IMAGE_LOADED:
                    //给ImageView设置图片Bitmap
                    ImageLoadTask task =(ImageLoadTask)msg.obj;
                    ImageView ivAlbum = (ImageView) listView.findViewWithTag(task.path);
                    if (ivAlbum != null){//找到了对应的Imageviewkongjian
                        Bitmap b = task.bitmap;
                        if (b != null){//图片下载成功了
                            ivAlbum.setImageBitmap(b);
                        }else{//图片下载失败了
                            ivAlbum.setImageResource(R.drawable.selector_tab);
                        }
                    }
                    break;
            }
        }
    };

    //构造方法
    public ImageLoader(Context context, AbsListView listView) {
        this.context = context;
        this.listView = (ListView) listView;
        //线程只new一次，所以再构造方法中初始化线程
        workThread = new Thread(){
            /**
             *    不断轮循任务集合，从集合中获取每个任务，
             *    然后执行，下载图片
             */
            @Override
            public void run() {
                while (isLoop) {
                    if (!tasks.isEmpty()){//里面有任务
                        ImageLoadTask task = tasks.remove(0);
                        String path = task.path;
                        Bitmap bitmap = loadBitmap(path);
                        task.bitmap = bitmap;
                        //把bitmap设置到ImageView，需要在主线程进行。
                        Message msg = Message.obtain();
                        msg.what = HANDLER_IMAGE_LOADED;
                        msg.obj = task;
                        handler.sendMessage(msg);
                    }else{//里面没有任务，线程等待
                        try {
                            //为线程安全
                            synchronized (workThread){
                                workThread.wait();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        workThread.start();
    }
    //声明缓存
    private Map<String, SoftReference<Bitmap>> cache = new HashMap<>();
    //声明用于轮循的任务集合
    private List<ImageLoadTask> tasks = new ArrayList<>();
    //声明一个工作线程，用于轮循的任务集合
    private Thread workThread;

    /**
     * 从内存，或文件中获得图片
     * @param ivAlbum
     * @param path
     */
    public void display(ImageView ivAlbum, String path) {
        //把路径设置成ImageView的tag ,因为再handler中需要通过tag找到这个控件
        ivAlbum.setTag(path);
        //去内存缓存寻找，是否已经存过
        SoftReference<Bitmap> ref = cache.get(path);
        if (ref != null) {//以前存过
            Bitmap b = ref.get();
            if (b != null) {//bitmap还没有被销毁
                Log.i("info", "从内存缓存中读取Bitmap图片");
                ivAlbum.setImageBitmap(b);
                return;
            }
        }

        //去文件缓存中寻找是否有图片
        String filename = path.substring(path.lastIndexOf("/") + 1);
        File file = new File(context.getCacheDir(), "image/" + filename);
        Bitmap b = BitmapUtils.loadBitmap(file);
        if (b != null) {
            Log.i("info", "从文件缓存中读取Bitmap图片");
            ivAlbum.setImageBitmap(b);
            //把从文件中读取的bitmap存入内存缓存
            cache.put(path, new SoftReference<Bitmap>(b));
            return;
        }

        //创建ImageLoadTask对象，添加到任务集合中
        ImageLoadTask task = new ImageLoadTask();
        task.path = path;
        tasks.add(task);
        //把工作线程唤醒，起来干活。
        synchronized (workThread) {
            workThread.notify();
        }
    }
    /**
     * 就MusicAdapter自己用
     * 封装图片下载任务
     */
    class ImageLoadTask {
        String path;  //图片地址
        Bitmap bitmap; //根据图片地址下载到的图片
    }

    /**
     * 通过路径访问服务器
     * @param  path
     * @return
     */
    public Bitmap loadBitmap(String path) {
        try {
            InputStream is =  HttpUtils.get(path);
            //Bitmap b = BitmapFactory.decodeStream(is,);
            Bitmap b =  BitmapUtils.loadBitmap(is, 50, 50);//压缩成一个50x50的图片
            //把Bitmap存入内存缓存
            //从path中截取最后一段作为文件名，
            String filename = path.substring(path.lastIndexOf("/")+1);//截取包括起始位置（包前不包后）
            cache.put(path, new SoftReference<Bitmap>(b));
            //向文件缓存中存图片
            File file = new File(context.getCacheDir(), "iamges/"+filename);
            BitmapUtils.save(b, file);
            return b;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 停止子线程
     */
    public void stopThread(){
        isLoop = false;
        synchronized (workThread) {
            workThread.notify();
        }
    }
}


