package com.belongmusic.util;

/**
 * Created by 枫暮晓 on 2018/7/30.
 * 用于生产url地址字符串
 */

public class UrlFactory {

    /**
     * 获取新歌表地址
     * @param offset 起始位置
     * @param size 请求数量
     * @return
     */
    public static String getNewMusicListUrl(int offset, int size){
        String url = "http://tingapi.ting.baidu.com/v1/restserver/ting?from=qianqian&version=2.1.0&method=baidu.ting.billboard.billList&format=xml&type=1&offset="+offset+"&size="+size;

        //String url ="http://tingapi.ting.baidu.com/v1/restserver/ting?method=baidu.ting.billboard.billList&type=1&size=3&offset=0";
        return url;
    }

    /**
     * 通过songId获取songInfo的url的地址
     * @return
     */
    public static String getSongInfoUrl(String songId) {
         //String url = "http://tingapi.ting.baidu.com/v1/restserver/ting?from=qianqian&version=2.1.0&method=baidu.ting.song.getInfos&format=json&songid="+songId+"&ts=1408284347323&e=JoN56kTXnnbEpd9MVczkYJCSx%2FE1mkLx%2BPMIkTcOEu4&3D&nw=2&ucf=1&res=1";
        String url ="http://tingapi.ting.baidu.com/v1/restserver/ting?method=baidu.ting.song.play&&songid="+songId;
        return url;
    }

    public static String getHotMusicListUrl(int offset, int size) {
        String url = "http://tingapi.ting.baidu.com/v1/restserver/ting?from=qianqian&version=2.1.0&method=baidu.ting.billboard.billList&format=xml&type=2&offset="+offset+"&size="+size;
      return url;
    }

    public static String getSearchMusicUrl(String keyword) {
        //String url = "http://tingapi.ting.baidu.com/v1/restserver/ting?method=baidu.ting.search.common&query="+keyword+"&page_size=30&page_no=1&format=json";
        String url = "http://tingapi.ting.baidu.com/v1/restserver/ting?from=qianqian&version=2.1.0&method=baidu.ting.search.common&format=json&query="+keyword+"&page_no=1&page_size=30";
        return url;
    }
}
