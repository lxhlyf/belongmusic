package com.belongmusic.model;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.AbsListView;

import com.belongmusic.app.MusicApplication;
import com.belongmusic.entity.Music;
import com.belongmusic.entity.SongInfo;
import com.belongmusic.entity.SongUrl;
import com.belongmusic.fragment.HotMusicListFragment;
import com.belongmusic.util.HttpUtils;
import com.belongmusic.util.JSONParser;
import com.belongmusic.util.UrlFactory;
import com.belongmusic.util.Xmlparser;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by 枫暮晓 on 2018/7/30.
 * 封装执行音乐相关业务
 */

public class MusicModel {


    /**
     * 获取新歌榜列表，需发送http请求
     * 该操作需要在线程中执行
     * @param offset 起始位置
     * @param size 查询数量
     */
    public void getNewMusicList( final int offset, final int size, final MusicListCallback callback) {//局部变量要用，就是final

             @SuppressLint("StaticFieldLeak") AsyncTask<String, String, List<Music>> task = new AsyncTask<String, String, List<Music>>() {
            @Override
            public List<Music> doInBackground(String...params) {

                try {
                    //获得地址
                    String url = UrlFactory.getNewMusicListUrl(offset,size);
                    //发送Http请求
                    InputStream is = HttpUtils.get(url);
                    //解析is中的xml数据，获取LIstanbul<Music>
                    List<Music> musics =  Xmlparser.parseMusicList(is);
                    return musics;
                } catch (Exception e) {
                    //Log.i("这有问题吗？", ""+e.getLocalizedMessage());
                    e.printStackTrace();
                }
                return null;
            }

            /**当doInBackground执行完毕后，将会在主线程中执行onPostExecute*/
            @Override
           public void onPostExecute(List<Music> musics) {
                //Log.i("info", ""+musics.toString());
                callback.onMusicListLoaded(musics);
            }
        };
        //启动异步任务
        task.execute();

    }

    /**
     * 获取热歌榜列表，需发送http请求
     * 该操作需要在线程中执行
     * @param offset 起始位置
     * @param size 查询数量
     */
    public void getHotMusicList(final int offset, final int size, final MusicListCallback callback) {
        @SuppressLint("StaticFieldLeak") AsyncTask<String, String, List<Music>> task = new AsyncTask<String, String, List<Music>>() {
            @Override
            public List<Music> doInBackground(String... strings) {
                String path = UrlFactory.getHotMusicListUrl(offset, size);
                try {
                    InputStream is = HttpUtils.get(path);
                    List<Music> musics = Xmlparser.parseMusicList(is);
                    return musics;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
            @Override
            public void onPostExecute(List<Music> music) {
                  callback.onMusicListLoaded(music);
            }
        };
        task.execute();
    }

    /**
     * 获取音乐列表的回调接口
     */
    public interface MusicListCallback {
        /**
         * 回调方法，当音乐列表加载完毕后，
         * 将会调用该回掉方法
         * 把音乐列表结果交给调用者
         * 执行后续任务
         * @param musics
         */
        void onMusicListLoaded(List<Music> musics);
    }

    /**
     * 通过songId查询音乐播放地址及信息
     * @param songId
     * @param callback
     */
    public void loadSongInfoBySongId(final String songId, final SongInfoCallBack callback){
          @SuppressLint("StaticFieldLeak") AsyncTask<String, String, Music> task = new AsyncTask<String, String, Music>() {
             /**发送http请求，通过songId获取基本信息*/
             @Override
             public Music doInBackground(String... params) {

                 String path = UrlFactory.getSongInfoUrl(songId);
                 try {
                     InputStream is =  HttpUtils.get(path);
                     //把输入流中的数据解析为Json字符串，
                     String json = HttpUtils.isToString(is);//json要先拿到字符串才能解析
                     //解析json字符串
                     JSONObject obj = new JSONObject(json);
                     //JSONArray urlArry = obj.getJSONObject("songurl").getJSONArray("url");
                     //把urlArry解析成集合，
                     //List<SongUrl> urls = JSONParser.parseUrls(urlArry);
                     JSONObject bitObj = obj.getJSONObject("bitrate");
                     JSONObject infoObj = obj.getJSONObject("songinfo");
                     //把infoObj解析成SongInfo对象
                     SongInfo info = JSONParser.parserSongInfo(infoObj);
                     SongUrl url = JSONParser.parserBitrate(bitObj);
                     Music m = new Music();
                     //m.setUrls(urls);
                     m.setInfo(info);
                     m.setUrl(url);
                     //Log.i("到底是谁的问题，是你的吗？++：",""+info.getAlbum_id());
                     //Log.i("到底是谁的问题，是你的吗？++：",""+urls.getFile_link());
                     return m;
                 } catch (Exception e) {
                     e.printStackTrace();
                     //Log.i("到底是谁的问题，是你的吗？++：",""+e.getLocalizedMessage());
                 }
                 return  null;//music对象，但是不含数据，可以确保不返回null。
             }

             @Override
             public void onPostExecute(Music music) {
                 //进行空判断，否则会崩
                 if (music != null) {
                     callback.onSongInfoLoaded(music.getUrl(), music.getInfo());
                     //Log.i("到底是谁的问题，是你的吗？++：",""+music.getUrls().get(0).getFile_link());
                 }
             }
         };
         task.execute();
    }

    /**
     * 通过songId获取音乐基本信息的回调接口
     */
    public interface SongInfoCallBack {
        /**
         * 当音乐基本信息加载完毕后
         * @param url 封装了url列表
         * @param Info 封装了音乐的详细信息
         */
        void onSongInfoLoaded(SongUrl url, SongInfo Info);
    }

    /**
     * 通过歌词路径加载歌词，并解析歌词，
     * 把一整篇歌词内容都封装到HaspMap
     * @param lrcPath
     * @param lrcCallback
     */
    public void loadLrc(final String lrcPath, final LrcCallback lrcCallback){
        @SuppressLint("StaticFieldLeak") AsyncTask<String, String, HashMap<String, String>> task = new AsyncTask<String, String, HashMap<String, String>>() {
            /** 发送http请求下载歌词*/
            @Override
            protected HashMap<String, String> doInBackground(String... strings) {

                try {
                    //声明缓存文件
                    if (lrcPath == null && lrcPath.equals("")){
                        //歌词不存在
                        return null;
                    }
                    String filename = lrcPath.substring(lrcPath.lastIndexOf("/"));//当lrcPath为空时，截取不到返回-1，会出现数组下标越界
                    File file = new File(MusicApplication.getApp().getCacheDir(),"lrc"+filename);
                    PrintWriter out = null;//一个将歌词写入文件的输出流
                    InputStream is = null;//通过httputils得到的输入流，用于BufferedReader，从网络读取歌词
                    boolean isnewFile = true;
                    if (file.exists()){
                         is = new FileInputStream(file);//如果文件存在就直接用一个输入流读取
                         isnewFile = false;
                    }else{
                        if (!file.getParentFile().exists()){
                            file.getParentFile().mkdirs();
                            isnewFile = true;
                        }
                          out = new PrintWriter(file);//高级的一个输出流
                         is = HttpUtils.get(lrcPath);
                    }
                    HashMap<String, String> map = new HashMap<>();
                   BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                   String line = null;
                   while((line = reader.readLine() )!= null) {//一行一行的读
                       //每读取一行向文件中写一行
                       if (isnewFile){
                           out.println(line);
                           out.flush();
                       }
                       //line:
                      //line:[title]
                       //line:[00:00.90]微微一笑很倾城
                       if("".equals(line.trim())){//去了空字符串啥都没有
                           continue;//继续循环下一行
                       }
                       if (!line.contains(".")){//不包含点,不符合格式的
                           continue;
                       }
                       String time = line.substring(1,6);
                       String content = line.substring(10);
                       map.put(time, content);
                   }
                   if(out != null) {
                       out.close();//输出流要关
                   }
                    return map;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
            @Override
            protected void onPostExecute(HashMap<String,String> map) {
                 lrcCallback.onMusicLrcLoaded(map);
                //Log.i("你是什么情况？",""+map.get("00:00"));
            }
        };
        task.execute();
    }

    /**
     * 获取歌词的回调接口
     */
    public interface LrcCallback {
        /**
         * 当音乐歌词下载解析完毕后执行
         * @param lrc
         */
        void onMusicLrcLoaded(HashMap<String,String> lrc);
    }

    /**
     * 搜索音乐列表
     * @param keyword 关键字
     * @param callback 当音乐结果加载完毕后通过回调返回音乐列表
     */
   public void searchMusicList(final String keyword, final MusicListCallback callback) {
       @SuppressLint("StaticFieldLeak") AsyncTask<String, String, List<Music>> task = new AsyncTask<String, String, List<Music>>() {
           @Override
           protected List<Music> doInBackground(String... strings) {

               try {
                   //获取歌曲地址
                   String path = UrlFactory.getSearchMusicUrl(keyword);
                   //通过地址发送http请求，获取一个输入流
                   InputStream is = HttpUtils.get(path);
                   //获得json字符串并解析
                   String json = HttpUtils.isToString(is);
                   //获取一个json字符串
                   JSONObject obj = new JSONObject(json);
                   JSONArray ary = obj.getJSONArray("song_list");
                   List<Music> musics = JSONParser.parserSearResult(ary);
                   return musics;
               } catch (Exception e) {
                   e.printStackTrace();
               }
               return null;
           }

           @Override
            protected void onPostExecute(List<Music> musics) {
                    callback.onMusicListLoaded(musics);
           }
       };
       task.execute();
   }


}
