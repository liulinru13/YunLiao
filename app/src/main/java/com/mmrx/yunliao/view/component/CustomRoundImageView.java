package com.mmrx.yunliao.view.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.mmrx.yunliao.R;


/**
 * Created by mmrx on 2015/4/16.
 */
public class CustomRoundImageView extends ImageView {

    private Context mContext;
    private Bitmap mBitmap;
    //图片画笔和边框画笔
    private Paint mBitMapPaint;
    private Paint mBorderPaint;
    //边框颜色
    private int mBorderColor = Color.WHITE;
    //边框宽度
    private float mBorderWidth = 0f;
    //渲染器
    private BitmapShader mBitMapShader;
    //图片拉伸方式 默认为按比例缩放
    private int mImageScale = 1;
    //控件的长宽
    private int mWidth;
    private int mHeight;
    //边框和圆形图片的半径
    private float mBorderRadius;
    private float mDrawableRadius;
    //矩形
    private RectF mDrawableRect;
    private RectF mBorderRect;
    //变换矩形
    private Matrix mBitMapMatrix;
    public CustomRoundImageView(Context mContext){
        super(mContext);
        this.mContext = mContext;
    }

    public CustomRoundImageView(Context mContext,AttributeSet attr){
        this(mContext, attr, R.attr.CustomImageView03Style);

        this.mContext = mContext;
    }

    public CustomRoundImageView(Context mContext,AttributeSet attr,int defSytle){
        super(mContext,attr,defSytle);
        TypedArray ta = mContext.obtainStyledAttributes(attr,R.styleable.CustomRoundImageView,
                defSytle, R.style.CustomizeStyle03);
        int count = ta.getIndexCount();
        for(int i=0;i<count;i++){
            int index = ta.getIndex(i);
            switch (index){
                case R.styleable.CustomRoundImageView_cborderColor:
                    mBorderColor = ta.getColor(index,Color.WHITE);
                    break;
                case R.styleable.CustomRoundImageView_cborderWidth:
                    mBorderWidth = ta.getDimension(index,0f);
                    break;
                case R.styleable.CustomRoundImageView_image:
                    mBitmap = BitmapFactory.decodeResource(getResources(),ta.getResourceId(index,0));
                    break;
                case R.styleable.CustomRoundImageView_imageScaleType:
                    mImageScale = ta.getInt(index,0);
                    break;
                default:
                    break;
            }
        }
        ta.recycle();
        mBorderPaint = new Paint();
        mBitMapPaint = new Paint();
        mDrawableRect = new RectF();
        mBorderRect = new RectF();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int heightMod = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int widthMod = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        //march_parent & exactly dimen
        if(heightMod == MeasureSpec.EXACTLY){
            mHeight = heightSize;
        }
        //wrap_content & others
        else{
            mHeight = mBitmap.getHeight();
        }

        if(widthMod == MeasureSpec.EXACTLY){
            mWidth = widthSize;
        }
        //wrap_content & others
        else{
            mWidth = mBitmap.getWidth();
        }
/*
* 下面注释掉的代码在Fragment的布局文件不适用，如果需要在Activity中使用，请取消下面的注释
* */
//        Log.v("--wrap_content--dimen1", "width: " + mWidth + " height: " + mHeight);
//        //获得屏幕的宽高,代码里面获取和设置的都是像素作为单位
//        DisplayMetrics dm = new DisplayMetrics();
//        int screenWidth = 0,screenHeight = 0;
//        if(this.getParent() instanceof Fragment)
//            ((Fragment)this.getParent()).getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
//        else
//            ((Activity)mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
//        screenWidth = dm.widthPixels;
//        screenHeight = dm.heightPixels;
//        //比较图片的尺寸和屏幕的尺寸，尺寸最大不得超出屏幕
//        mWidth = mWidth<=screenWidth? mWidth : screenWidth;
//        mHeight = mHeight<=screenHeight? mHeight : screenHeight;
//        Log.v("--wrap_content--dimen2","screenHeight: "+ screenHeight + " screenWidth: " + screenWidth);
//        Log.v("--wrap_content--dimen1","width: "+ mWidth + " height: " + mHeight);
        mWidth = Math.min(mWidth,mHeight);
        mHeight = Math.min(mWidth,mHeight);

        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
        if(mBitmap == null)
            return;
        //设置渲染器
        mBitMapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        //抗锯齿
        mBitMapPaint.setAntiAlias(true);
        //设置渲染器
        mBitMapPaint.setShader(mBitMapShader);
        //设置外框的相关参数
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setStrokeWidth(mBorderWidth);

        //边框矩形
        mBorderRect.set(0,0,getWidth(),getHeight());
        //边框半径,考虑线条的宽度,如果没有考虑线条宽度，显示会把线条的一部分遮蔽
        mBorderRadius = Math.min((mBorderRect.width()-mBorderWidth)/2,(mBorderRect.height()-mBorderWidth)/2);
        //图片的矩形,因为图片是在外边框内部，所以位置矩形的坐标要考虑到边框的宽度
        mDrawableRect.set(mBorderWidth,mBorderWidth,
                mBorderRect.width()-mBorderWidth,mBorderRect.height()-mBorderWidth);
        //圆形图片的半径
        mDrawableRadius = mBorderRadius-mBorderWidth;
        //设置图片的缩放
        setBitMapScale();

        canvas.drawCircle(getWidth()/2,getHeight()/2,mDrawableRadius,mBitMapPaint);
        if(mBorderWidth != 0)
            canvas.drawCircle(getWidth()/2,getHeight()/2,mBorderRadius,mBorderPaint);
    }
    //根据控件的尺寸和设置的图片缩放模式，来对图片进行缩放
    private void setBitMapScale(){

        float scaleX = 0,scaleY = 0;
        //获得圆形的直径和图片的尺寸
        float diameter = mDrawableRadius*2;
        float mBitMapWidth = mBitmap.getWidth();
        float mBitMapHeight = mBitmap.getHeight();
        mBitMapMatrix = new Matrix();
        mBitMapMatrix.set(null);
        //fillXY 宽高单独缩放
        if(mImageScale == 0){
            scaleX = diameter/mBitMapWidth;
            scaleY = diameter/mBitMapHeight;
        }
        //center 等比例缩放
        else{
            float scale = 0;
            scaleX = diameter/mBitMapWidth;
            scaleY = diameter/mBitMapHeight;
            //如果宽度和高度至少有一个需要放大
            if(scaleX > 1 || scaleY > 1){
                scale = Math.max(scaleX,scaleY);
            }
            else{
                scale = Math.min(scaleX, scaleY);
            }
            scaleX = scale;
            scaleY = scale;
        }
        mBitMapMatrix.setScale(scaleX,scaleY);
        mBitMapShader.setLocalMatrix(mBitMapMatrix);
    }
    //设置图片
    public void setImage(Bitmap bm){
        this.mBitmap = bm;
        invalidate();
    }
    //设置图片
    public void setImage(int rid){
        this.mBitmap = BitmapFactory.decodeResource(getResources(),rid);
        invalidate();
    }
    //像素转换为dp
//    private int px2dip(Context context, float pxValue){
//        final float scale = context.getResources().getDisplayMetrics().density;
//        return (int)(pxValue * scale + 0.5f);
//    }
}