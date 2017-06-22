package com.example.a0_0.app_shixun.music;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;

import com.example.a0_0.app_shixun.Base.Encrypt;


/**
 * Created by 樱满集0_0 on 2017/6/22.
 */

public class MusicIconLoader {
    private static MusicIconLoader instance;

    private LruCache<String,Bitmap> cache;

    //获取MusicIconLoader的实例
    public synchronized static MusicIconLoader getInstance(){
        if(instance==null)
            instance=new MusicIconLoader();
        return instance;
    }

    //构造方法,初始化LruCache
    private MusicIconLoader(){
        int maxSize=(int)(Runtime.getRuntime().maxMemory()/8);
        cache=new LruCache<String, Bitmap>(maxSize){
            protected int sizeOf(String key,Bitmap value){
                return value.getByteCount();
            }
        };
    }

    //根据路径获取图片
    public Bitmap load(final String uri){
        if(uri==null)
            return null;
        final String key= Encrypt.md5(uri);
        Bitmap bmp=getFromCache(key);

        if(bmp!=null)
            return bmp;
        bmp= BitmapFactory.decodeFile(uri);
        addToCache(key,bmp);
        return bmp;
    }

    //内存中获取图片
    private Bitmap getFromCache(final String key){
        return cache.get(key);
    }
    //将图片缓存到内存中
    private void addToCache(final String key,final Bitmap bmp){
        if (getFromCache(key)==null&&key!=null&&bmp!=null)
            cache.put(key,bmp);
    }
}
