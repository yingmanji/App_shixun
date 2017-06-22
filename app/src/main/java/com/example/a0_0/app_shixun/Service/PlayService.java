package com.example.a0_0.app_shixun.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.widget.RemoteViews;

import com.example.a0_0.app_shixun.Base.Constants;
import com.example.a0_0.app_shixun.Base.L;
import com.example.a0_0.app_shixun.Base.SqUtils;
import com.example.a0_0.app_shixun.PlayActivity;
import com.example.a0_0.app_shixun.R;
import com.example.a0_0.app_shixun.UI.ImageTools;
import com.example.a0_0.app_shixun.music.MusicIconLoader;
import com.example.a0_0.app_shixun.music.MusicUtils;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by 樱满集0_0 on 2017/6/20.
 */

public class PlayService extends Service implements MediaPlayer.OnCompletionListener{
    private static final String TAG=
            PlayService.class.getSimpleName();
    private SensorManager sensorManager;
    private MediaPlayer player;
    private OnMusicEventListener eventListener;
    private int playingPosition;//当前正在播放
    private PowerManager.WakeLock wakeLock=null;//获取设备电源锁,防止锁屏后服务停止
    private boolean isShaking;
    private Notification notification;//通知栏
    private RemoteViews remoteViews;//通知栏布局
    private NotificationManager notificationManager;
    //单线程池
    private ExecutorService progressUpdateListener= Executors
            .newSingleThreadExecutor();
    public class PlayBinder extends Binder{
        public PlayService getService(){
            return PlayService.this;
        }
    }

    public IBinder onBind(Intent intent)
    {
        sensorManager.registerListener(sensorEventListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME);
        return new PlayBinder();
    }

    /**
     * 音乐播放完毕 自动下一曲
     * @param mp
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        next();
    }


    @Override
    public void onCreate() {
        super.onCreate();
        acquireWakeLock();//获取设备电源锁
        sensorManager=(SensorManager)
                getSystemService(Context.SENSOR_SERVICE);
        MusicUtils.initMusicList();
        playingPosition=(Integer)
                SqUtils.get(this, Constants.PLAY_POS,0);
        Uri uri= Uri.parse(MusicUtils.musicList.get(getPlayingPosition()).getUri());
        player=MediaPlayer.create(PlayService.this,uri);
        player.setOnCompletionListener(this);
        //开始更新进度的线程
        progressUpdateListener.execute(publishProgressRunable);

        PendingIntent pendingIntent=PendingIntent.getActivity(PlayService.this,
                0,new Intent(PlayService.this,PlayActivity.class),0
        );
        remoteViews=new RemoteViews(getPackageName(),
                R.layout.play_notification);
        notification=new Notification(R.drawable.ic_launcher,
                "歌曲正在播放",System.currentTimeMillis());
        notification.contentIntent=pendingIntent;
        notification.contentView=remoteViews;
        //标记位,设置通知栏一直存在
        notification.flags=Notification.FLAG_ONGOING_EVENT;

        Intent intent=new Intent(PlayService.class.getSimpleName());
        intent.putExtra("BUTTON_NOTI",1);
        PendingIntent preIntent=PendingIntent.getBroadcast(
                PlayService.this, 1,intent,PendingIntent.FLAG_UPDATE_CURRENT
        );
        remoteViews.setOnClickPendingIntent(R.id.music_play_pre,preIntent);

        intent.putExtra("BUTTON_NOTI",2);
        PendingIntent pauseIntent=PendingIntent.getBroadcast(
                PlayService.this,2,intent,pendingIntent.FLAG_UPDATE_CURRENT
        );
        remoteViews.setOnClickPendingIntent(R.id.music_play_pause,pauseIntent);
        intent.putExtra("BUTTON_NOTI",3);
        PendingIntent nextIntent=PendingIntent.getBroadcast(
                PlayService.this,3,intent,pendingIntent.FLAG_UPDATE_CURRENT
        );
        remoteViews.setOnClickPendingIntent(R.id.music_play_next,nextIntent);
        intent.putExtra("BUTTON_NOTI",4);
        PendingIntent exit=PendingIntent.getBroadcast(
                PlayService.this,4,intent,pendingIntent.FLAG_UPDATE_CURRENT
        );
        remoteViews.setOnClickPendingIntent(R.id.music_play_notifi_exit,exit);
        notificationManager=(NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        setRemoteViews();

        IntentFilter filter=new IntentFilter(
                PlayService.class.getSimpleName()
        );
        MyBroadCastReceiver receiver=new MyBroadCastReceiver();
        registerReceiver(receiver,filter);
    }

    public void setRemoteViews() {
        L.l(TAG,"进入-->setRemoteViews()");
        remoteViews.setTextViewText(R.id.music_name,
                MusicUtils.musicList.get(
                        getPlayingPosition()
                ).getTitle()
        );
        remoteViews.setTextViewText(R.id.music_author,
                MusicUtils.musicList.get(
                        getPlayingPosition()
                ).getArtist()
        );
        Bitmap icon= MusicIconLoader.getInstance().load(
          MusicUtils.musicList.get(
                  getPlayingPosition()
          ).getImage());
        remoteViews.setImageViewBitmap(R.id.music_icon,icon==null?
                ImageTools.scaleBitmap(
                        R.drawable.ic_launcher
                ):ImageTools.scaleBitmap(icon)
        );
        if(isPlaying()){
            remoteViews.setImageViewResource(R.id.music_play_pause,
                    R.drawable.btn_notification_player_stop_normal);
        }
        else
        {
            remoteViews.setImageViewResource(R.id.music_play_pause,
            R.drawable.btn_notification_player_play_normal);
        }
        //通知栏更新
        notificationManager.notify(5,notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(0,notification);//让服务前台运行
        return Service.START_STICKY;
    }

    /**
     * 感应器的时间监听器
     */
    private SensorEventListener sensorEventListener=
            new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    if(isShaking)
                        return;
                    if(Sensor.TYPE_ACCELEROMETER==event.sensor.getType()){
                        float[] values=event.values;
                        /**
                         * 监听三个方向上的变化,数据变化剧烈,next()方法播放下一首歌曲
                         */
                        if(Math.abs(values[0])>8&&Math.abs(values[1])>8&&
                                Math.abs(values[2])>8){
                            isShaking=true;
                            next();
                            //延迟200毫秒 防止都懂的给
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    isShaking=false;
                                }
                            },200);
                        }
                    }
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {

                }
            };
    /**
     * 更新进度的线程
     */
    private Runnable publishProgressRunable=new Runnable() {
        @Override
        public void run() {
            while (true){
                if(player!=null&&player.isPlaying()
                        &&eventListener!=null){
                    eventListener.onPublish(player.getCurrentPosition());
                }
            }
        }
    };

    /**
     * 设置回调
     *
     * @param l
     */
    public void setOnMusicEventListener(OnMusicEventListener l){
        eventListener=l;
    }

    /**
     * 播放
     *
     * @param position
     * 音乐列表播放的位置
     * @return 当前播放的位置
     */
    public int play(int position){
        L.l(TAG,"play(int position)方法");
        if(position<0)
            position=0;
        if(position>=MusicUtils.musicList.size())
            position=MusicUtils.musicList.size()-1;

        try {
            player.reset();
            player.setDataSource(MusicUtils.musicList.get(position).getUri());
            player.prepare();

            start();
            if(eventListener!=null)
                eventListener.onChange(position);
        }
        catch (Exception e){
           e.printStackTrace();
        }

        playingPosition=position;
        SqUtils.put(Constants.PLAY_POS,playingPosition);
        setRemoteViews();
        return playingPosition;
    }

    /**
     * 继续播放
     *
     * @return
     */
    public int resume(){
        if(isPlaying())
            return -1;
        player.start();
        setRemoteViews();
        return playingPosition;
    }

    /**
     * 暂停播放
     *
     * @return
     */
    public int pause(){
        if(isPlaying())
            return -1;
        player.pause();
        setRemoteViews();
        return playingPosition;
    }

    /**
     * 下一曲
     *
     * @return 当前播放的位置
     */
    public int next(){
        if(playingPosition>=MusicUtils.musicList.size()-1){
            return play(0);
        }
        return play(playingPosition+1);
    }

    /**
     * 上一曲
     *
     * @return 当前播放的位置
     */
    public int pre(){
        if(playingPosition<=0){
            return play(MusicUtils.musicList.size()-1);
        }
        return play(playingPosition-1);
    }

    /**
     * 是否正在播放
     *
     * @return
     */
    public boolean isPlaying(){
        return null!=player && player.isPlaying();
    }

    /**
     * 获取正在播放的歌曲在歌曲列表的位置
     *
     * @return
     */
    public int getPlayingPosition(){
        return playingPosition;
    }

    /**
     * 获取当前正在播放音乐的总时长
     *
     * @return
     */
    public int getDuration(){
        if(isPlaying())
            return 0;
        return player.getDuration();
    }

    /**
     * 拖放到指定位置进行播放
     *
     * @param mssc
     */
    public void seek(int mssc){
        if(!isPlaying())
            return;
        player.seekTo(mssc);
    }

    /**
     * 开始播放
     */
    private void start(){
        player.start();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        L.l("play service","unbind");
        sensorManager.unregisterListener(sensorEventListener);
        return true;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        if(eventListener!=null)
            eventListener.onChange(playingPosition);
    }

    @Override
    public void onDestroy() {
        L.l(TAG,"PlayService.java的onDestory()方法调用");
        release();
        stopForeground(true);
        sensorManager.unregisterListener(sensorEventListener);
        super.onDestroy();
    }

    /**
     * 服务销毁时,释放各种控件
     */
    private void release(){
        if(!progressUpdateListener.isShutdown())
            progressUpdateListener.shutdown();
        progressUpdateListener=null;
        //释放设备电源锁
        releaseWakeLock();
        if(player!=null)
            player.release();
        player=null;
    }
    private void acquireWakeLock(){
        L.l(TAG,"正在申请电源锁");
        if(null==wakeLock){
            PowerManager pm=(PowerManager)this
                    .getSystemService(Context.POWER_SERVICE);
            wakeLock=pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK
            |PowerManager.ON_AFTER_RELEASE,"");
            if(null!=wakeLock){
                wakeLock.acquire();
                L.l(TAG,"电源锁申请成功");
            }
        }
    }
    //释放设备电源锁
    private void releaseWakeLock(){
        L.l(TAG,"正在释放电源锁");
        if(null!=wakeLock) {
            wakeLock.release();
            wakeLock = null;
            L.l(TAG, "电源锁释放成功");
        }
    }
    private class MyBroadCastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(
                    PlayService.class.getSimpleName()
            ))
            {
                L.l(TAG,"MyBroadCastReceiver类-->onReceiver()");
                L.l(TAG,"button_noti-->"
                +intent.getIntExtra("BUTTON_NOTI",0));
                switch (intent.getIntExtra("BUTTON_NOTI",0)){
                    case 1:
                        pre();
                        break;
                    case 2:
                        if(isPlaying()){
                            pause();//暂停
                        }
                        else
                        {
                            resume();//播放
                        }
                        break;
                    case 3:
                        next();
                        break;
                    case 4:
                        if(isPlaying()){
                            pause();
                        }
                        //取消通知栏
                        notificationManager.cancel(5);
                        break;
                    default:
                        break;
                }
            }
            if(eventListener!=null){
                eventListener.onChange(getPlayingPosition());
            }
        }
    }
    public interface OnMusicEventListener {
        public void onPublish(int parcent);

        public void onChange(int position);
    }
}

