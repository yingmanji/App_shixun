package com.example.a0_0.app_shixun.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.SparseArray;
import android.webkit.DownloadListener;
import android.widget.RemoteViews;

import com.example.a0_0.app_shixun.music.MusicUtils;

public class DownloadService extends Service {
    private SparseArray<Download> downloads=new SparseArray<Download>();
    private RemoteViews remoteViews;
    public DownloadService() {
    }
    public class DownloadBinder extends Binder{
        public DownloadService getService(){
            return DownloadService.this;
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
       /* // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");*/
        return new DownloadBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
    /*public void download(final int id,final String url,final String name){
        L.l("download",url);
        Download d=new Download(id,url, MusicUtils.getMusicDir()+name);
        d.setOnDownloadListener(DownloadListener).start(false);
        downloads.put(id,d);
    }*/

}
