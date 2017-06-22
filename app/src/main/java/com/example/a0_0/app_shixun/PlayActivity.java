package com.example.a0_0.app_shixun;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ShapeDrawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a0_0.app_shixun.Base.App;
import com.example.a0_0.app_shixun.Base.BaseActivity;
import com.example.a0_0.app_shixun.UI.ImageTools;
import com.example.a0_0.app_shixun.UI.Indicator;
import com.example.a0_0.app_shixun.UI.LrcView;
import com.example.a0_0.app_shixun.music.Music;
import com.example.a0_0.app_shixun.music.MusicIconLoader;
import com.example.a0_0.app_shixun.music.MusicUtils;

import java.util.ArrayList;

public class PlayActivity extends BaseActivity implements View.OnClickListener{

    private LinearLayout playContainer;
    private ImageView playBackImageView;//back button
    private TextView musicTitle;//music title
    private ViewPager viewPager;//cd or lrc
    private CDView cdView;//cd
    private SeekBar playSeekBar;//seekBar
    private ImageButton startPlayButton;//start or pause
    private TextView singerTextView;//singer
    private LrcView lrcViewOnFirstPage;//single line lrc
    private LrcView lrcViewOnSecondPage;//7 lines lrc
    private PagerIndicator pagerindicator;//indicator

    //cd view and lrc view
    private ArrayList<View> viewPagerContent=new ArrayList<View>(2);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_play);
        setupView();
    }

    private void setupView(){
        playContainer=(LinearLayout)findViewById(R.id.activity_play);
        playBackImageView=(ImageView)findViewById(R.id.iv_play_back);
        musicTitle=(TextView)findViewById(R.id.tv_music_title);
        viewPager=(ViewPager)findViewById(R.id.vp_play_container);
        playSeekBar=(SeekBar)findViewById(R.id.sb_play_progress);
        startPlayButton=(ImageButton)findViewById(R.id.ib_play_start);
        PagerIndicator=(pagerIndicator)findViewById(R.id.pi_play_indicator);

        //动态设置seekBar的margin
        ViewGroup.MarginLayoutParams p=(ViewGroup.MarginLayoutParams)playSeekBar
                .getLayoutParams();
        p.leftMargin=(int)(App.screenWidth*0.1);
        p.rightMargin=(int)(App.screenHeight*0.1);

        playSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);

        initViewPagerContent();
        //设置viewpager的切换动画
        viewPager.setPageTransformer(true,new PlayPageTransformer());
        pagerindicator.create(viewPagerContent.size());
        viewPager.addOnPageChangeListener(pageChangeListener);
        viewPager.setAdapter(pagerAdapter);

        playBackImageView.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        allowBindService();
    }

    @Override
    protected void onPause() {
        allowUnbindService();
        super.onPause();
    }
    private ViewPager.OnPageChangeListener pageChangeListener=
            new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }
                @Override
                public void onPageSelected(int position) {
                    if(position==0){
                        if(playService.isPlaying())
                            cdView.start();
                        else{
                            cdView.pause();
                        }
                        pagerIndicator.current(position);
                    }
                }
                @Override
                public void onPageScrollStateChanged(int state) {

                }
            };
    private SeekBar.OnSeekBarChangeListener seekBarChangeListener= new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            int progress=seekBar.getProgress();
            playService.seek(progress);
            lrcViewOnFirstPage.onDrag(progress);
            lrcViewOnSecondPage.onDrag(progress);
        }
    };
    private PagerAdapter pagerAdapter=new PagerAdapter() {
        @Override
        public int getCount() {
            return viewPagerContent.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        /**
         * 该方法是PageAdapter的预加载方法,系统调用 当显示第一个界面时,
         * 第二个界面已经预加载,此时调用的就是该方法
         * @param container
         * @param position
         * @return
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(viewPagerContent.get(position));
            return super.instantiateItem(container, position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager)container).removeView((View)object);
        }
    };

    /**
     * 初始化viewpager的内容
     */
    private void initViewPagerContent(){
        View cd=View.inflate(this,R.layout.play_pager_item_1,null);
        cdView=(CDView) cd.findViewById(R.id.play_cdview);
        singerTextView=(TextView)cd.findViewById(R.id.play_first_lrc);

        View lrcView=View.inflate(this,R.layout.play_pager_item_2,null);
        lrcViewOnSecondPage=(LrcView)lrcView
                .findViewById(R.id.play_first_lrc_2);

        viewPagerContent.add(cd);
        viewPagerContent.add(lrcView);
    }
    private void setBackground(int position){
        if(position==0){
            return ;
        }
        Bitmap bgBitmap=null;
        if(MusicUtils.musicList.size()!=0){
            Music currentMusic=MusicUtils.musicList.get(position);
            bgBitmap= MusicIconLoader.getInstance().load(
                    currentMusic.getImage()
            );
        }
        if(bgBitmap==null){
            bgBitmap= BitmapFactory.decodeResource(getResources(),
                    R.drawable.ic_launcher);
        }
        playContainer.setBackgroundResource(new ShapeDrawable(new PlayBgShape(bgBitmap)));
    }

    /**
     * 上一曲
     * @param view
     */
    public void pre(View view){
        playService.pre();//上一曲
    }
    public void play(View view){
        if(MusicUtils.musicList.isEmpty()){
            Toast.makeText(PlayActivity.this,"当前手机没有MP3文件",
                    Toast.LENGTH_LONG).show();
            return;
        }
        if(playService.isPlaying()){
            playService.pause();//暂停
            cdView.pause();
            startPlayButton
                    .setImageResource(R.drawable.player_btn_play_normal);
        }
        else {
            onPlay(playService.resume());//播放
        }
    }

    /**
     * 上一曲
     *
     * @param view
     */
    public void next(View view){
        playService.next();//上一曲
    }
    private void onPlay(int position){
        Bitmap bmp=null;
        if(!MusicUtils.musicList.isEmpty()){
            Music music=MusicUtils.musicList.get(position);

            musicTitle.setText(music.getTitle());
            singerTextView.setText(music.getArtist());
            playSeekBar.setMax(music.getLength());
            bmp=MusicIconLoader.getInstance().load(music.getImage());
        }
        if(bmp==null)
            bmp=BitmapFactory.decodeResource(getResources(),
                    R.drawable.ic_launcher);
        cdView.setImage(ImageTools.scaleBitmap(bmp,
                (int)(App.screenWidth*0.8)));
        if(playService.isPlaying()){
            cdView.start();
            startPlayButton
                    .setImageResource(R.drawable.player_btn_pause_normal);
        }
        else
        {
            cdView.pause();
            startPlayButton
                    .setImageResource(R.drawable.player_btn_play_normal);
        }
    }
    private  void setLrc(int position){
        if(MusicUtils.musicList.size()!=0){
            Music music=MusicUtils.musicList.get(position);
            String lrcPath=MusicUtils.getLrcDir()+music.getTitle()+".lrc";
            lrcViewOnFirstPage.setLrcPath(lrcPath);
            lrcViewOnSecondPage.setLrcPath(lrcPath);
        }
    }
    @Override
    public void onPublish(int progress) {
        playSeekBar.setProgress(progress);
        if(lrcViewOnFirstPage.hasLrc())
            lrcViewOnFirstPage.changeCurrent(progress);
        if(lrcViewOnSecondPage.hasLrc())
            lrcViewOnSecondPage.changeCurrent(progress);
    }
    @Override
    public void onChange(int position) {
        setBackground(position);
        onPlay(position);
        setLrc(position);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_play_back:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
