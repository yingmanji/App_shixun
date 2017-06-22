package com.example.a0_0.app_shixun.Base;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.example.a0_0.app_shixun.Service.DownloadService;
import com.example.a0_0.app_shixun.Service.PlayService;

/**
 * Created by 樱满集0_0 on 2017/6/20.
 */

public abstract class BaseActivity extends FragmentActivity {
    protected PlayService playService;
    protected DownloadService DownloadService;
    private final String TAG=BaseActivity.class.getSimpleName();

    private ServiceConnection playServiceConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            playService=((PlayService.PlayBinder) service).getService();
            playService.setOnMusicEventListener(MusicEventListener);
            onChange(playService.getPlayingPosition());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            L.l(TAG,"play-->onServiceDisconnected");
            playService=null;
        }
    };
    private ServiceConnection DownloadServiceConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            DownloadService=((DownloadService.DownloadBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

            L.l(TAG,"download-->onServiceDisconnected");
            DownloadService=null;
        }
    };
    /**
     * 音乐播放服务回调接口的实现类
     */
    private PlayService.OnMusicEventListener MusicEventListener=
            new PlayService.OnMusicEventListener(){
                public void onPublish(int progress) {
                    BaseActivity.this.onPublish(progress);
                }
                public void onChange(int position) {
                    BaseActivity.this.onChange(position);
                }
            };
    /**
     * Fragment的view加载完成后回调
     *
     * 注意：
     * allowBindService()使用绑定的方式启动歌曲
     * allowUnbindService()方法解除绑定
     *
     * 在SplashActivity.java中使用startService()方法启动过该音乐播放服务了
     * 该服务不会因为调用allowUnbindService()方法解除绑定而停止
     */
    public void allowBindService(){
        getApplicationContext().bindService(new Intent(this,PlayService.class),
        playServiceConnection,Context.BIND_AUTO_CREATE);
    }

    /**
     * fragment的view消失后回调
     */
    public void allowUnbindService()
    {
        getApplicationContext().unbindService(playServiceConnection);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //绑定下载服务
        bindService(new Intent(this,DownloadService.class),DownloadServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        unbindService(DownloadServiceConnection);
        super.onDestroy();
    }

    public DownloadService getDownloadService() {
        return DownloadService;
    }
    /**
     * 更新进度
     * 抽象方法由子类实现
     * 实现service与主界面通信
     */
    public abstract void onPublish(int progress);
    /**
     * 切换歌曲
     * 抽象方法由子类实现
     * 实现service与主界面通信
     */
    public abstract void onChange(int prosition);
}
