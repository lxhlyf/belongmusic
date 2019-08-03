package com.belongmusic.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by 枫暮晓 on 2018/7/30.
 * 用于发送Http请求的工具类
 */

public class HttpUtils {

    /**
     * 向path地址发送Http请求
     * @param path 请求资源路径
     * @return
     * @throws Exception
     */
    public static InputStream get(String path)throws Exception{
         URL url = new URL(path);
        HttpURLConnection conn =(HttpURLConnection) url.openConnection();

        //Log.d("Get执行了吗？+",""+conn);
        conn.setRequestMethod("GET");
        InputStream is = conn.getInputStream();

        //Log.d("InputStream执行了吗？+",""+is);
        return is;
    }

    /**
     * 把输入流解析为json字符串
     */
    public static String isToString(InputStream is) throws IOException{
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line = null;
        StringBuffer sb = new StringBuffer();
        while ((line = reader.readLine()) != null){
            sb.append(line);
        }
        return sb.toString();
    }
}
