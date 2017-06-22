package com.example.a0_0.app_shixun.UI;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.example.a0_0.app_shixun.R;

/**
 * Created by 樱满集0_0 on 2017/6/20.
 */

public class Indicator extends LinearLayout{
    private Paint paint;//画指示符的paint

    private int top;//指示符的top
    private int left;//指示符的left
    private int width;//指示符的width
    private int height;//指示符的高度
    private int color;//指示符的颜色
    private int childCount;//字item的个数,用于计算指示符的宽度

    public Indicator(Context context, AttributeSet attrs){
        super(context,attrs);
        setBackgroundColor(Color.TRANSPARENT);//如果是在onDraw绘制Indicator,必须设置背景,否则onDraw不执行
        //如果是在dispatchDraw()则不需要

        //获取自定义属性,指示符的颜色
        TypedArray ta=context.obtainStyledAttributes(attrs, R.styleable.Indicator,0,0);
        color=ta.getColor(R.styleable.Indicator_mColor,0xff00ff00);
        height=(int)ta.getDimension(R.styleable.Indicator_mHeight,2);
        ta.recycle();

        //初始化paint
        paint=new Paint();
        paint.setColor(color);
        paint.setAntiAlias(true);
    }
    protected void onFinishInflate() {
        super.onFinishInflate();
        childCount=getChildCount();//获取子item的个数
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        top=getMeasuredHeight();//测量的高度即指示符的顶部位置
        int totalWidth=getMeasuredWidth();//获取测量的总宽度
        int totalHeight=top+height;//重新定义一下测量的高度
        width=width/childCount;//指示符的宽度为总宽度/item的个数

        setMeasuredDimension(totalWidth,totalHeight);
    }

    /**
     * 指示符滚动
     * @param position 现在的位置
     * @param offset 偏移量 0`1
     */
    public void scroll(int position,float offset)
    {
        left=(int)((position+offset)*width);
        invalidate();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        //圈出一个矩形
        Rect rect=new Rect(left,top,left+width,top+height);
        canvas.drawRect(rect,paint);//绘制该矩形
        super.dispatchDraw(canvas);
    }
}
