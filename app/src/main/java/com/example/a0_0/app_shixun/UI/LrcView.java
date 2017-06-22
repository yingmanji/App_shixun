package com.example.a0_0.app_shixun.UI;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;

import com.example.a0_0.app_shixun.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by 樱满集0_0 on 2017/6/22.
 */

public class LrcView extends View{
    private static final int SCROLL_TIME=500;
    private static final String DEFAULT_TEXT="暂无歌词";

    private List<String> lrcs=new ArrayList<String>();//存放歌词
    private List<Long> times=new ArrayList<Long>();//存放时间

    private long nextTime=01;//保存下一句开始的时间
    private int viewWidth;//view的宽度
    private int lrcHeight;//lrc界面的高度
    private int rows;//多少行
    private int currentLine=0;//当前行
    private int offsetY;//y上的偏移
    private int maxScroll;//最大滑动距离=一行歌词高度+歌词间距

    private float textSize;//字体
    private float dividerHeight;//行间距

    private Rect textBounds;

    private Paint normalPaint;//常规的字体
    private Paint currentPaint;//当前歌词的大小

    private Bitmap background;

    private Scroller scroller;

    public LrcView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LrcView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        scroller=new Scroller(context,new LinearInterpolator());
        inflateAttributes(attrs);
    }
    private void inflateAttributes(AttributeSet attrs){
        TypedArray ta=getContext().obtainStyledAttributes(attrs,
                R.styleable.Lrc);
        textSize=ta.getDimension(R.styleable.Lrc_textSize,50.0f);
        rows=ta.getInteger(R.styleable.Lrc_rows,5);
        dividerHeight=ta.getDimension(R.styleable.Lrc_dividerHeight,0.0f);

        int normalTextColor=ta.getColor(R.styleable.Lrc_normalTextColor,
                0xffffffff);
        int currentTextColor=ta.getColor(R.styleable.Lrc_currentTextColor,
                0xff00ffde);
        ta.recycle();

        //计算lrc面板的高度
        lrcHeight=(int)(textSize+dividerHeight)*rows+5;
        normalPaint=new Paint();
        currentPaint=new Paint();

        //初始化paint
        normalPaint.setTextSize(textSize);
        normalPaint.setColor(normalTextColor);
        normalPaint.setAntiAlias(true);
        currentPaint.setTextSize(textSize);
        currentPaint.setColor(currentTextColor);
        currentPaint.setAntiAlias(true);

        textBounds=new Rect();
        currentPaint.getTextBounds(DEFAULT_TEXT,0,DEFAULT_TEXT.length(),
                textBounds);
        maxScroll=(int)(textBounds.height()+dividerHeight);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //重新设置view的高度
        int measuredHeightSpec=MeasureSpec.makeMeasureSpec(lrcHeight,
        MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec,measuredHeightSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //获取view宽度
        viewWidth=getMeasuredWidth();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(background!=null){
            canvas.drawBitmap(Bitmap.createScaledBitmap(
                    background,viewWidth,lrcHeight,true
            ),new Matrix(),null);
        }
        float centerY=(getMeasuredHeight()+
        textBounds.height()-dividerHeight)/2;
        if(lrcs.isEmpty()||times.isEmpty()){
            canvas.drawText(DEFAULT_TEXT,
                    (viewWidth-currentPaint.measureText(DEFAULT_TEXT))/2,
            centerY,currentPaint);
            return;
        }
        float fOffsetY=textBounds.height()+dividerHeight;
        String currentLrc=lrcs.get(currentLine);
        float currentX=(viewWidth-currentPaint.measureText(currentLrc))/2;
        //画当前行
        canvas.drawText(currentLrc,currentX,centerY-offsetY,currentPaint);

        int firstLine=currentLine-rows/2;
        int lastLine=currentLine+rows/2+2;
        lastLine=lastLine>=lrcs.size()-1?lrcs.size()-1:lastLine;

        //画当前行上面的
        for(int i=currentLine-1,j=1;i>=firstLine;i--,j++){
            String lrc=lrcs.get(i);
            float x=(viewWidth-normalPaint.measureText(lrc))/2;
            canvas.drawText(lrc,x,centerY-j*fOffsetY-offsetY,
            normalPaint);
        }
        //画当前行下面的
        for(int i=currentLine+1,j=1;i<=lastLine;i++,j++){
            String lrc=lrcs.get(i);
            float x=(viewWidth-normalPaint.measureText(lrc))/2;
            canvas.drawText(lrc,x,centerY+j*fOffsetY-offsetY,
                    normalPaint);
        }
    }

    @Override
    public void computeScroll() {
        if(scroller.computeScrollOffset()){
            offsetY=scroller.getCurrY();
            if(scroller.isFinished()){
                int cur=scroller.getCurrX();
                currentLine=cur<=1?0:cur-1;
                offsetY=0;
            }
            postInvalidate();
        }
    }
    //解析时间
    private Long parseTime(String time){
        //03:02.12
        String[] min=time.split(":");
        String[] sec=min[1].split("\\.");

        long minInt=Long.parseLong(min[0].replaceAll("\\D+","")
        .replaceAll("\r","").replaceAll("\n","").trim());
        long secInt=Long.parseLong(sec[0].replaceAll("\\D+","")
        .replaceAll("\r","").replaceAll("\n","").trim());
        long milInt=Long.parseLong(sec[1].replaceAll("\\D+","")
        .replaceAll("\r","").replaceAll("\n","").trim());

        return minInt*60*1000+secInt*1000+milInt*10;
    }
    //解析每行
    private String[] parseLine(String line){
        Matcher matcher= Pattern.compile("\\[\\d.+\\].+").matcher(line);
        //如果形如：[xxx]后面为空的,则return空
        if(!matcher.matches()){
            System.out.println("throws"+line);
            return null;
        }
        line=line.replaceAll("\\[","");
        String[] result=line.split("\\]");
        result[0]=String.valueOf(parseTime(result[0]));

        return result;
    }
    //外部提供方法
    //传入当前播放时间
    public synchronized void changeCurrent(long time){
        //如果当前时间小于下一句开始的时间
        //直接return
        if(nextTime>time){
            return;
        }

        //每次进来都遍历存放的时间
        for(int i=0;i<times.size();i++){
            //发现这个时间大于传进来的时间
            //那么现在就应该显示这个时间前面的对应的哪一行
            //每次都重新显示,是不是要判断:现在正在显示就不刷新了
            if(times.get(i)>time){
                nextTime=times.get(i);
                scroller.abortAnimation();
                scroller.startScroll(i,0,0,maxScroll,SCROLL_TIME);
                nextTime=times.get(i);
                currentLine=i<=1?0:i-1;
                postInvalidate();
                return;
            }
        }
    }
    public void onDrag(int progress){
        for(int i=0;i<times.size();i++){
            if(Integer.parseInt(times.get(i).toString())>progress){
                nextTime=i==0?0:times.get(i-1);
                return;
            }
        }
    }
    //外部提供方法
    //设置lrc的路径
    public void setLrcPath(String path){
        reset();
        File file=new File(path);
        if(!file.exists()){
            postInvalidate();
            return;
        }
        BufferedReader reader=null;
        try {
            reader=new BufferedReader(new InputStreamReader(new
                    FileInputStream(file)));
            String line="";
            String[] arr;
            while (null!=(line=reader.readLine())){
                arr=parseLine(line);
                if(arr==null)
                    continue;

                //如果解析出来只有一个
                if(arr.length==1){
                    String last=lrcs.remove(lrcs.size()-1);
                    lrcs.add(last+arr[0]);
                    continue;
                }
                times.add(Long.parseLong(arr[0]));
                lrcs.add(arr[1]);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if(reader!=null){
                try{
                    reader.close();
                }
                catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }
    private void reset(){
        lrcs.clear();
        times.clear();
        currentLine=0;
        nextTime=0;
    }
    //是否设置歌词
    public boolean hasLrc(){
        return lrcs!=null&&!lrcs.isEmpty();
    }

    //外部提供方法
    //设置背景图片
    public void setBackground(Bitmap bmp){
        background=bmp;
    }
}
