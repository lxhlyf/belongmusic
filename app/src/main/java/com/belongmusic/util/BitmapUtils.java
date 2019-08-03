package com.belongmusic.util;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.belongmusic.app.MusicApplication;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by 枫暮晓 on 2018/7/31.
 *
 * 图片操作工具类
 */

public class BitmapUtils {
    /**
     * 通过一个输入流，按照用户需要的大小进行压缩返回一个Bitmap
     * @param is  输入流
     * @param width 图片的目标宽度
     * @param hight  图片的目标高度
     * @return  压缩过后的Bitmap
     */
    public static Bitmap loadBitmap(InputStream is, int width, int hight)throws IOException {
        //1.从输入流中，读取出bybt[]
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] bytes = new byte[10*1024];//10kb
        int len = 0;
        while ((len = is.read(bytes)) != -1){
            bos.write(bytes, 0, len);
            bos.flush();
        }
        //从输出流中得到bybe[],描述一个完整的Bitmap数据
         byte[] data =  bos.toByteArray();
        bos.close();
        //2.解析bybt[]，获取图片的原始宽与高
        BitmapFactory.Options opts = new BitmapFactory.Options();
        //仅仅加载图片的bounds（边界）属性
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, opts);
        int w = opts.outWidth / width;//宽度压缩比例
        int h = opts.outHeight / hight;//高度的压缩比例
        //3.根据用户的需要，计算出压缩的比例
       int scale = w > h ? w : h;
        //4.再此解析bybt[]获取压缩过后的图片
        opts.inJustDecodeBounds = false;
        opts.inSampleSize = scale;//压缩比例的设置
        Bitmap bitmap =  BitmapFactory.decodeByteArray(data,0, data.length, opts);
        return bitmap;
    }

    /**
     * 把bitmap压缩成。jpg格式保存到File文件中
     * @param bitmap
     * @param file
     */
    public static void save(Bitmap bitmap, File file)throws Exception{
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();//父目录不存在就创建父目录
        }
        FileOutputStream fos = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
    }

    public static Bitmap loadBitmap(File file) {
        if (!file.exists()){
            return null;
        }
        Bitmap b = BitmapFactory.decodeFile(file.getAbsolutePath());

        return b;
    }

    /**
     * 通过一个网络地址，加载一个图片
     */
    public static void loadBitmap(final String path, final int scale, final BitmapCallback callback){
        @SuppressLint("StaticFieldLeak") AsyncTask<String, String, Bitmap> task = new AsyncTask<String, String, Bitmap>() {
            //工作线程中执行，需要发请求，获取Bitmap
            @Override
            protected Bitmap doInBackground(String... strings) {
                try {
                    String filename = path.substring(path.lastIndexOf("/"));
                    File file = new File(MusicApplication.getApp().getCacheDir(),"images"+filename);
                    BitmapFactory.Options opts = new BitmapFactory.Options();
                    opts.inSampleSize = scale;
                    if (file.exists()){//文件已经存在
                        return BitmapFactory.decodeFile(file.getAbsolutePath(), opts);
                    }

                    //从服务端下载原始图片
                    InputStream is = HttpUtils.get(path);

                    Bitmap bitmap =  BitmapFactory.decodeStream(is);
                    //把bitmap存入文件缓存，供下次
                    save(bitmap,file);
                    //从文件中以压缩的方式读取图片
                    return  BitmapFactory.decodeFile(file.getAbsolutePath(), opts);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
               callback.onBitmapLoaded(bitmap);

            }
        };
        task.execute();

    }

    public interface BitmapCallback{
        void onBitmapLoaded(Bitmap bitmap);
    }


    /**
     * 异步在工作线程中执行图片模糊化处理
     * @param bitmap  需要被模糊的bitmap
     * @param r  要模糊的半径
     * @param callback   回调对象
     */
    public static void loadBluredBitmap(final Bitmap bitmap, final int r, final BitmapCallback callback){
        new AsyncTask<String, String, Bitmap>(){
            //工作线程中模糊化图片
            protected Bitmap doInBackground(String... params) {
                Bitmap b=createBlurBitmap(bitmap, r, new BitmapCallback() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap) {
                    }
                });
                return b;
            }
            protected void onPostExecute(Bitmap b) {
                callback.onBitmapLoaded(b);
            }
        }.execute();
    }

    /**
     * 传递bitmap  传递模糊半径 返回一个被模糊的bitmap
     * @param sentBitmap 原始图片
     * @param radius 要模糊的半径，半径越大，越模糊
     * @param callback
     * @return  bitmap 模糊后的图片
     */
    public static Bitmap createBlurBitmap(Bitmap sentBitmap, int radius, BitmapCallback callback) {
        Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
        if (radius < 1) {
            return (null);
        }
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);
        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;
        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];
        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);

        }
        yw = yi = 0;
        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;
        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];

                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];

                }

            }
            stackpointer = radius;
            for (x = 0; x < w; x++) {
                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];
                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;
                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];
                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];
                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);

                }
                p = pix[yw + vmin[x]];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];
                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;
                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];
                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];
                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];
                yi++;

            }
            yw += w;

        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;
                sir = stack[i + radius];
                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];
                rbs = r1 - Math.abs(i);
                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];

                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];

                }
                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16)
                        | (dv[gsum] << 8) | dv[bsum];
                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;
                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];
                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];
                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;

                }
                p = x + vmin[y];
                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];
                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];
                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;
                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];
                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];
                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];
                yi += w;

            }

        }
        bitmap.setPixels(pix, 0, w, 0, 0, w, h);
        return (bitmap);
    }
}
