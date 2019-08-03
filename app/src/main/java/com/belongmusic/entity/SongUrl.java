package com.belongmusic.entity;

/**
 * Created by 枫暮晓 on 2018/7/31.
 *
 * 保存音乐的url信息
 */

public class SongUrl {

    private int song_file_id;
    private int file_size;
    private int file_duration;
    private int file_bitrate;
    private String show_link;
    private String file_extension;
    private String file_link;



    public SongUrl(String show_link, String file_extension, String file_link, int song_file_id, int file_bitrate, int file_size,
                   int file_duration) {
    this.show_link = show_link;
    this.file_extension = file_extension;
    this.file_link = file_link;
    this.song_file_id = song_file_id;
    this.file_bitrate = file_bitrate;
    this.file_size = file_size;
    this.file_duration = file_duration;
    }


    public int getSong_file_id() {
        return song_file_id;
    }

    public void setSong_file_id(int song_file_id) {
        this.song_file_id = song_file_id;
    }

    public int getFile_size() {
        return file_size;
    }

    public void setFile_size(int file_size) {
        this.file_size = file_size;
    }

    public int getFile_duration() {
        return file_duration;
    }

    public void setFile_duration(int file_duration) {
        this.file_duration = file_duration;
    }

    public int getFile_bitrate() {
        return file_bitrate;
    }

    public void setFile_bitrate(int file_bitrate) {
        this.file_bitrate = file_bitrate;
    }

    public String getShow_link() {
        return show_link;
    }

    public void setShow_link(String show_link) {
        this.show_link = show_link;
    }

    public String getFile_extension() {
        return file_extension;
    }

    public void setFile_extension(String file_extension) {
        this.file_extension = file_extension;
    }

    public String getFile_link() {
        return file_link;
    }

    public void setFile_link(String file_link) {
        this.file_link = file_link;
    }



}
