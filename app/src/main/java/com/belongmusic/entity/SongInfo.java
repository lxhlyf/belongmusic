package com.belongmusic.entity;

/**
 * Created by 枫暮晓 on 2018/7/31.
 *
 * 保存音乐的详细基本信息
 */

public class SongInfo {

    private String title;
    private String pic_radio;
    private String pic_huge;
    private String song_id;
    private String pic_big;
    private String album_id;
    private String  album_title;
    private String author;
    private String all_artist_id;
    private String file_duration;
    private String publishtime;
    private String artist_1000_1000;
    private String artist_id;
    private String lrclink;
    private String pic_small;
    private String pic_premium;

    public SongInfo(String title, String pic_radio, String lrclink, String pic_huge,
                    String song_id, String pic_big, String album_id) {
        this.title = title;
        this.pic_radio = pic_radio;
        this.pic_huge = pic_huge;
        this.song_id = song_id;
        this.pic_big = pic_big;
        this.album_id = album_id;
        this.lrclink = lrclink;

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPic_radio() {
        return pic_radio;
    }

    public void setPic_radio(String pic_radio) {
        this.pic_radio = pic_radio;
    }

    public String getPic_huge() {
        return pic_huge;
    }

    public void setPic_huge(String pic_huge) {
        this.pic_huge = pic_huge;
    }

    public String getSong_id() {
        return song_id;
    }

    public void setSong_id(String song_id) {
        this.song_id = song_id;
    }

    public String getPic_big() {
        return pic_big;
    }

    public void setPic_big(String pic_big) {
        this.pic_big = pic_big;
    }

    public String getAlbum_id() {
        return album_id;
    }

    public void setAlbum_id(String album_id) {
        this.album_id = album_id;
    }



    public String getAlbum_title() {
        return album_title;
    }

    public void setAlbum_title(String album_title) {
        this.album_title = album_title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAll_artist_id() {
        return all_artist_id;
    }

    public void setAll_artist_id(String all_artist_id) {
        this.all_artist_id = all_artist_id;
    }

    public String getFile_duration() {
        return file_duration;
    }

    public void setFile_duration(String file_duration) {
        this.file_duration = file_duration;
    }

    public String getPublishtime() {
        return publishtime;
    }

    public void setPublishtime(String publishtime) {
        this.publishtime = publishtime;
    }

    public String getArtist_1000_1000() {
        return artist_1000_1000;
    }

    public void setArtist_1000_1000(String artist_1000_1000) {
        this.artist_1000_1000 = artist_1000_1000;
    }

    public String getArtist_id() {
        return artist_id;
    }

    public void setArtist_id(String artist_id) {
        this.artist_id = artist_id;
    }

    public String getLrclink() {
        return lrclink;
    }

    public void setLrclink(String lrclink) {
        this.lrclink = lrclink;
    }

    public String getPic_small() {
        return pic_small;
    }

    public void setPic_small(String pic_small) {
        this.pic_small = pic_small;
    }

    public String getPic_premium() {
        return pic_premium;
    }

    public void setPic_premium(String pic_premium) {
        this.pic_premium = pic_premium;
    }


}
