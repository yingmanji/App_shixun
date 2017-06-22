package com.example.a0_0.app_shixun.UI;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import com.example.a0_0.app_shixun.Base.App;

/**
 * Created by 樱满集0_0 on 2017/6/22.
 */

public class ImageTools {
    /**
     * 缩放图片
     * @param bmp
     * @return
     */
    public static Bitmap scaleBitmap(Bitmap bmp){
        return scaleBitmap(bmp,(int)(App.screenWidth*0.13));
    }

    /**
     * 缩放图片
     * @param bmp
     * @param size
     * @return
     */
    public static Bitmap scaleBitmap(Bitmap bmp,int size){
        return Bitmap.createScaledBitmap(bmp,size,size,true);
    }

    /**
     * 根据文件uri缩放图片
     * @param uri
     * @param size
     * @return
     */
    public static Bitmap scaleBitmap(String uri,int size){
        return scaleBitmap(BitmapFactory.decodeFile(uri),size);
    }

    /**
     * 缩放资源图片
     * @param res
     * @return
     */
    public static Bitmap scaleBitmap(int res){
        return scaleBitmap(BitmapFactory.decodeResource(
                App.context.getResources(),res
        ));
    }

    /**
     * 创建圆形图片
     * @param src
     * @return
     */
    private static Bitmap createCircleBitmap(Bitmap src){
        int size=(int)(App.screenWidth*0.13);

        Paint paint=new Paint();
        paint.setAntiAlias(true);
        paint.setARGB(255,241,239,229);

        Bitmap target=Bitmap.createBitmap(size,size,Bitmap.Config.ARGB_8888);
        Canvas canvas=new Canvas(target);
        canvas.drawCircle(size/2,size/2,size/2,paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
        canvas.drawBitmap(src,0,0,paint);

        return target;
    }

    /**
     *
     * @param uri
     * @return
     */
    private static Bitmap createCircleBitmap(String uri){
        return createCircleBitmap(BitmapFactory.decodeFile(uri));
    }
    private static Bitmap createCirleBitmap(int res){
        return createCircleBitmap(BitmapFactory.decodeResource
                (App.context.getResources(),res));
    }
}
