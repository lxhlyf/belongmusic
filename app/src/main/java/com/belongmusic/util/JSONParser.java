package com.belongmusic.util;

import android.support.annotation.NonNull;
import android.util.Log;

import com.belongmusic.entity.Music;
import com.belongmusic.entity.SongInfo;
import com.belongmusic.entity.SongUrl;
import com.belongmusic.fragment.NewMusicListFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by 枫暮晓 on 2018/7/31.
 *
 * 解析json字符串的工具类
 */

public class JSONParser {

    /**
     * 解析Urls
     * @param urlArry
     * @return
     */
    public static List<SongUrl> parseUrls(JSONArray urlArry) throws Exception{
         List<SongUrl> urls = new ArrayList<>();
         for (int i = 0; i<urlArry.length(); i++){
             JSONObject o = urlArry.getJSONObject(i);
             SongUrl url =new SongUrl(
                     o.getString("show_link"),
                     o.getString("file_extension"),
                     o.getString("file_link"),
                     o.getInt("song_file_id"),
                     o.getInt("file_bitrate"),
                     o.getInt("file_size"),
                     o.getInt("file_duration"));
             urls.add(url);
             Music music = new Music();
             Log.i("file_link有什么问题："+music.getSong_id()+"=",""+ o.getString("file_link"));
         }

        return urls;
    }

    /**
     * 解析bitrate
     */
    public static SongUrl parserBitrate(JSONObject bitObj) throws IOException,JSONException{


        SongUrl url =new SongUrl(
                bitObj.getString("show_link"),
                bitObj.getString("file_extension"),
                bitObj.getString("file_link"),
                bitObj.getInt("song_file_id"),
                bitObj.getInt("file_bitrate"),
                bitObj.getInt("file_size"),
                bitObj.getInt("file_duration"));


         //Log.i("这有问题吗？", ""+ bitObj.getString("file_link"));
        return url;
    }


    /**
     * 解析SongInfo
     * @param infoObj
     * @return
     */
    public static SongInfo parserSongInfo(JSONObject infoObj) throws IOException,JSONException {


        SongInfo info = new SongInfo(
                infoObj.getString("title"),
                infoObj.getString("pic_radio"),
                infoObj.getString("lrclink"),
                infoObj.getString("pic_huge"),
                infoObj.getString("song_id"),
                infoObj.getString("pic_big"),
                infoObj.getString("album_id")
        );

       // Log.i("这有问题吗？", ""+infoObj.getString("pic_big"));

        return info;
    }

    /**
     * 解析搜索结果的音乐列表json
     * @param ary [{}{}{}]
     * @return
     */
    public static List<Music> parserSearResult(JSONArray ary)throws Exception {
        List<Music> musics = new ArrayList<>();

        for (int i=0;i<ary.length();i++){
            JSONObject o = ary.getJSONObject(i);
            Music m = new Music();
            m.setTitle(o.getString("title"));
            m.setSong_id(o.getString("song_id"));
            m.setAuthor(o.getString("author"));
            m.setArtist_id(o.getString("artist_id"));
            m.setAlbum_title(o.getString("album_title"));
            musics.add(m);
        }
        //Log.i("hahahaahahahah",""+musics.get(0).getSong_id());
        return musics;
    }
}
