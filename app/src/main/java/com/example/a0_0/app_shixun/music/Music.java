package com.example.a0_0.app_shixun.music;

/**
 * Created by 樱满集0_0 on 2017/6/20.
 */

public class Music {
    private int id;//音乐id
    private String title;//音乐标题
    private String artist;//艺术家
    private String uri;//音乐路径
    private int Length;//长度
    private String image;//icon
    public void setId(int id)
    {
        this.id=id;
    }

    public int getId() {
        return id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getArtist() {
        return artist;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }

    public void setLength(int length) {
        Length = length;
    }

    public int getLength() {
        return Length;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
