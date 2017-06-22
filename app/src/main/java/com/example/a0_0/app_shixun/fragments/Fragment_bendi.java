package com.example.a0_0.app_shixun.fragments;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.DialogPreference;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.a0_0.app_shixun.Adapter.MusicListAdapter;
import com.example.a0_0.app_shixun.Base.L;
import com.example.a0_0.app_shixun.MainActivity;
import com.example.a0_0.app_shixun.R;
import com.example.a0_0.app_shixun.music.Music;
import com.example.a0_0.app_shixun.music.MusicUtils;

import org.w3c.dom.Text;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_bendi extends Fragment implements View.OnClickListener {
    private ListView lv_music;
    private ImageView musicIcon;
    private TextView tv_music_title;
    private TextView tv_music_artist;

    private ImageView preImageView;
    private ImageView playImageView;
    private ImageView nextImageView;

    private SeekBar musicProgress;

    private MusicListAdapter musicListAdapter=new MusicListAdapter();
    private MainActivity mActivity;
    private boolean isPause;

    public Fragment_bendi() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout=inflater.inflate(R.layout.bendi_music_item, null);
        setupViews(layout);
        return layout;
    }
    private void setupViews(View layout)
    {
        lv_music=(ListView)layout.findViewById(R.id.lv_music);
        musicIcon=(ImageView)layout.findViewById(R.id.iv_play_icon);
        tv_music_title=(TextView)layout.findViewById(R.id.tv_music_title);
        tv_music_artist=(TextView)layout.findViewById(R.id.tv_music_artist);

        preImageView=(ImageView)layout.findViewById(R.id.iv_pre);
        playImageView=(ImageView)layout.findViewById(R.id.iv_play);
        nextImageView=(ImageView)layout.findViewById(R.id.iv_next);

        musicProgress=(SeekBar) layout.findViewById(R.id.play_progress);
        lv_music.setAdapter(musicListAdapter);
        lv_music.setOnItemClickListener(musicItemClickListener);
        lv_music.setOnItemLongClickListener(itemLongClickListener);

        musicIcon.setOnClickListener(this);
        preImageView.setOnClickListener(this);
        playImageView.setOnClickListener(this);
        nextImageView.setOnClickListener(this);
    }
    private AdapterView.OnItemLongClickListener itemLongClickListener=
            new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    final int pos=position;

                    AlertDialog.Builder builder=new AlertDialog.Builder(mActivity);
                    builder.setTitle("删除该条目");
                    builder.setMessage("确认要删除该条目吗？");
                    builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Music music= MusicUtils.musicList.remove(pos);
                            musicListAdapter.notifyDataSetChanged();
                            if(new File(music.getUri()).delete()){
                                scanSDCard();
                            }
                        }
                    });
                    builder.setNegativeButton("取消",null);
                    builder.create().show();
                    return false;
                }
            };
    private AdapterView.OnItemClickListener musicItemClickListener=new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            play(position);
        }
    };
    private void scanSDCard() {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT) {
            //判断sdk版本是不是4.4或者高于4.4
            String[] paths=new String[]{
                    Environment.getExternalStorageDirectory().toString()
            };
            MediaScannerConnection.scanFile(mActivity,paths,null,null);
        }
        else
        {
            Intent intent=new Intent(Intent.ACTION_MEDIA_MOUNTED);
            intent.setClassName("com.android.providers.media","com.android.provider.media.MediaScannerReceiver");
            intent.setData(Uri.parse("file://"+MusicUtils.getMusicDir()));
            mActivity.sendBroadcast(intent);
        }
    }

    /***
     * 播放时高亮当前播放条目
     * 实现播放的歌曲条目可见,且实现指示标记可见
     * @param position
     */
    private void OnItemPlay(int position)
    {
        //将ListView列表滑动到播放的歌曲的位置,使播放的歌曲可见
        lv_music.smoothScrollToPosition(position);
        //获取上次播放的歌曲的position
        int prePlayingPosition=musicListAdapter.getPlayingPosition();
        //如果上次播放的位置在可视区域内
        //则手动设置invisible
        if(prePlayingPosition>=musicListAdapter.getFirstVisiblePosition()
                &&prePlayingPosition<=lv_music.getLastVisiblePosition())
        {
            int preItem=prePlayingPosition-lv_music.getFirstVisiblePosition();
            ((ViewGroup)lv_music.getChildAt(preItem)).getChildAt(0)
                    .setVisibility(View.INVISIBLE);
        }
        //设置新的播放位置
        musicListAdapter.setPlayingPosition(position);

        //如果新的播放位置不在可视区域
        //则直接返回
        if(lv_music.getLastVisiblePosition()<position||lv_music.getFirstVisiblePosition()>position)
        {
            return;
        }

        //如果在可视区域
        //手动设置item visible
        int currentItem=position-lv_music.getFirstVisiblePosition();
        ((ViewGroup)lv_music.getChildAt(currentItem)).getChildAt(0).setVisibility(View.VISIBLE);
    }

    /**
     * 播放音乐item
     *
     * @param position
     */
    private void play(int position) {
        int pos=mActivity.getPlayService().play(position);
        onPlay(pos);
    }
    public void onPlay(int position) {
        if(MusicUtils.musicList.isEmpty()||position<0)
            return ;
        //设置进度条的总长度
        musicProgress.setMax(mActivity.getPlayService().getDuration());
        onItemPlay(position);

        Music music=MusicUtils.musicList.get(position);
        Bitmap icon=musicIconLoader.getInstance().load(music,getImage());
        musicIcon.setImageBitmap(icon==null?ImageTools.scaleBitmap
                (R.drawable.ic_launcher):ImageTools
        .scaleBitmap(icon));
        tv_music_title.setText(music.getTitle());
        tv_music_artist.setText(music.getArtist());
        if(mActivity.getPlayService().isPlaying()){
            playImageView.setImageResource(android.R.drawable.ic_media_pause);
        }
        else
        {
            playImageView.setImageResource(android.R.drawable.ic_media_play);
        }
        //新启动一个线程更新通知栏,防止更新时间过长,导致界面卡顿
        new Thread(){
            public void run() {
                super.run();
                mActivity.getPlayService().setRemoteViews();
            }
        }.start();
    }
    /*private PagerAdapter bendiPageAdapter=new PagerAdapter() {
        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }
        */

    @Override
    public void onStart() {
        super.onStart();
        L.l("fragment","onViewCreated");
        mActivity.allowBindService();
    }

    /**预加载第二个界面的方法*//*
        public Object instantiateItem(ViewGroup container,int position)
        {
            container.addView()
        }
    };*/

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    /**
     * 主界面MainActivity.java中调用更新歌曲列表
     */
    public void onMusicListChanged(){
        musicListAdapter.notifyDataSetChanged();
    }
    //onAttach参数为Context
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity=(MainActivity) context;
    }

    @Override
    public void onResume() {
        isPause=true;
        super.onResume();
    }
    /**
     * stop时,回调通知activity解除绑定歌曲播放服务
     */
    @Override
    public void onStop() {
        super.onStop();
        L.l("fragment","onDestoryView");
        mActivity.allowUnbindService();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.iv_play_icon:
                startActivity(new Intent(mActivity,PlayActivity.class));
                break;
            case R.id.iv_play:
                if(mActivity.getPlayService().isPlaying()){
                    mActivity.getPlayService().pause();//暂停
                    playImageView
                            .setImageResource(android.R.drawable.ic_media_play);
                }
                else{
                    onPlay(mActivity.getPlayService());
                }
                break;
            case R.id.iv_next:
                mActivity.getPlayService().next();//下一曲
                break;
            case R.id.iv_pre:
                mActivity.getPlayService().pre();//上一曲
                break;
        }
    }

    /**
     * 设置进度条的进度(SeekBar)
     * @param progress
     */
    public void setProgress(int progress) {
        if(isPause)
            return;
        musicProgress.setProgress(progress);
    }
}
