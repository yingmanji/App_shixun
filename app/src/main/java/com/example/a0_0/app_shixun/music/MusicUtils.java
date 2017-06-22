package com.example.a0_0.app_shixun.music;

import android.os.Environment;

import com.example.a0_0.app_shixun.Base.App;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by 樱满集0_0 on 2017/6/20.
 */

public class MusicUtils {
    //存放歌曲列表
    public static ArrayList<Music> musicList=new ArrayList<Music>();

    public static void initMusicList()
    {
            //获取歌曲列表
            musicList.clear();
            musicList.addAll(BendiMusicUtils.queryMusic(getBaseDir()));
    }
    /**
     * 获取内存卡根
     */
    public static String getBaseDir()
    {
        String dir=null;
        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_UNMOUNTED))
        {
            dir=Environment.getExternalStorageDirectory()+ File.separator;
        }
        else
        {
            dir= App.context.getFilesDir()+File.separator;
        }
        return dir;
    }
    /**
     * 获取应用程序使用的本地目录
     */
    public static String getAppLocalDir()
    {
        String dir=null;
        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_UNMOUNTED))
        {
            dir=Environment.getExternalStorageDirectory()+ File.separator
            +"liteplayer"+File.separator;
        }
        else
        {
            dir=App.context.getFilesDir()+File.separator
                    +"liteplayer"+File.separator;
        }
        return mkdir(dir);
    }
    /**
     * 获取音乐存放目录
     */
    public static String getMusicDir()
    {
        String musicDir=getAppLocalDir()+"Music"+File.separator;
        return mkdir(musicDir);
    }
    /**
     * 获取歌词存放目录
     */
    public static String getFileDir()
    {
        String lrcDir=getAppLocalDir()+"lrc"+File.separator;
        return mkdir(lrcDir);
    }
    /**
     * 创建文件夹
     */
    public static String mkdir(String dir)
    {
        File f=new File(dir);
        if(!f.exists())
        {
            for(int i=0;i<5;i++)
            {
                if(f.mkdir())
                    return dir;
            }
            return null;
        }
        return dir;
    }
}
