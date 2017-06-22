package com.example.a0_0.app_shixun.music;

import android.database.Cursor;
import android.provider.MediaStore;

import com.example.a0_0.app_shixun.Base.App;

import java.util.ArrayList;

/**
 * Created by 樱满集0_0 on 2017/6/20.
 */

public class BendiMusicUtils {
    /**
     * 根据id获取歌曲uri
     */
    public static String queryMusicById(int musicId)
    {
        String result=null;
        Cursor cursor=App.context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Media.DATA},
                MediaStore.Audio.Media._ID+"=?",
                new String[]{String.valueOf(musicId)},
                null
        );
        for(cursor.moveToFirst();!cursor.isAfterLast();)
        {
            result=cursor.getString(0);
            break;
        }
        cursor.close();
        return result;
    }
    /**
     * 获取目录的歌曲
     */
    public static ArrayList<Music> queryMusic(String dirName)
    {
        ArrayList<Music> results=new ArrayList<Music>();
        Cursor cursor= App.context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,
                MediaStore.Audio.Media.DATA+"like ?",
                new String[]{dirName+"%"},
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER
        );
        if(cursor==null)
            return results;
        //id title single data time image
        Music music;
        for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext())
        {
            //如果不是音乐
            String isMusic=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.IS_MUSIC));
            if(isMusic!=null&&isMusic.equals(""))
                continue;
            String title=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
            String artist=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
            if(isRepeat(title,artist))
                continue;
            music=new Music();
            music.setId(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)));
            music.setTitle(title);
            music.setArtist(artist);
            music.setUri(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
            music.setLength(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)));
            results.add(music);
        }
        cursor.close();
        return  results;
    }
    /**
     * 根据音乐名称和艺术家来判断是否重复包含了
     */
    private static boolean isRepeat(String title,String artist){
        for(Music music:MusicUtils.musicList)
        {
            if(title.equals(music.getTitle()))
                return true;
        }
        return false;
    }

}
