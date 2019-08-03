package com.belongmusic.util;



import android.os.Looper;
import android.util.Log;
import android.util.Xml;

import com.belongmusic.entity.Music;

import org.xml.sax.Parser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 枫暮晓 on 2018/7/30.
 * 解析xml的工具类
 */

public class Xmlparser {
    /**
     * 解析音乐列表，返回音乐集合,,pull解析
     * @param is
     * @return
     */
    public static List<Music> parseMusicList(InputStream is)throws Exception {
        List<Music> musics = new ArrayList<>();
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(is, "utf-8");
        int event = parser.getEventType();
        Music music = null;
        while(event != XmlPullParser.END_DOCUMENT){
            switch (event) {
                case XmlPullParser.START_TAG://开始标签
                    String tagName = parser.getName();
                    if (tagName.equals("song")) {
                         music = new Music();
                        musics.add(music);
                    }else if (tagName.equals("artist_id")) {
                               String   text = parser.nextText();
                                 music.setArtist_id(text);
                    }else if (tagName.equals("language")) {
                        String   text = parser.nextText();
                       music.setLanguage(text);
                    }else if (tagName.equals("pic_big")) {
                        String   text = parser.nextText();
                       music.setPic_big(text);
                   }else if (tagName.equals("pic_small")) {
                        String   text = parser.nextText();
                        music.setPic_small(text);
                   }else if (tagName.equals("lrclink")) {
                        String   text = parser.nextText();
                       music.setLrclink(text);
                   }else if (tagName.equals("all_artist_id")) {
                        String   text = parser.nextText();
                       music.setAll_artist_id(text);
                   }else if (tagName.equals("file_duration")) {
                        String   text = parser.nextText();
                       music.setFile_duration(text);
                   } else if (tagName.equals("song_id")) {
                        String  text = parser.nextText();
                       music.setSong_id(text);

                      // Log.i("song_id有什么问题：",""+text);
                   }else if (tagName.equals("title")) {
                        String  text = parser.nextText();
                       music.setTitle(text);
                   }else if (tagName.equals("author")) {
                        String   text = parser.nextText();
                       music.setAuthor(text);
                   }else if (tagName.equals("album_id")) {
                        String   text = parser.nextText();
                       music.setAlbum_id(text);
                   }else if (tagName.equals("album_title")) {
                        String   text = parser.nextText();
                       music.setAlbum_title(text);
                   }else if (tagName.equals("artist_name")) {
                        String   text = parser.nextText();
                       music.setArtist_name(text);
                   }
                    break;
//                case XmlPullParser.END_TAG:
//                    if("song".equals(parser.getName())){
//                        musics.add(music);
//                        music = null;
//                    }
//                    break;
//                default:
//                    break;
            }
                   event = parser.next();
        }
                  return musics;
    }
}
