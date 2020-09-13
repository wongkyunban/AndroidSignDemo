package com.wong.sign;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * 手写板
 */
public class HandwritingBoardView extends View {


    private Paint mPaint;// 用于描绘路径的画笔
    private Canvas mCanvas;
    private Bitmap mBitmap;// 保存每一次绘制出来的图形
    private Path mPath;// 每一次画出来的路径
    private float mX, mY;// 记录当前点的坐标
    private static final float TOUCH_TOLERANCE = 4;// 触点之间的公差，此变量用于控制，当路径在绘制过程中，移动距离至少等于4才绘制，减轻频繁的绘制的情况。
    private DrawPath drawPath;// 记录Path路径的对象
    private static List<DrawPath> savePath;// 保存Path路径的集合,用List集合来模拟栈
    private int screenWidth, screenHeight;// 手写板的长宽
    private Paint mBitmapPaint;// 画布的画笔
    private int mPenSize = 10;// 画笔宽度 px
    private int mPenColor = Color.BLACK;// 画笔默认颜色
    private int mPanelColor = Color.TRANSPARENT;// 背景色（指最终签名结果文件的背景颜色，默认为透明色）


    public HandwritingBoardView(Context context) {
        super(context);
        init();
    }

    public HandwritingBoardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HandwritingBoardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);// 设置外边缘
        mPaint.setStrokeCap(Paint.Cap.ROUND);// 形状
        mPaint.setStrokeWidth(mPenSize);// 画笔宽度
        mPaint.setColor(mPenColor);
        savePath = new ArrayList<DrawPath>();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        screenWidth = getWidth();
        screenHeight = getHeight();
        /*创建跟view一样大的bitmap，用来保存签名(在控件大小发生改变时调用。)*/
        mBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);// 保存每一次绘制出来的图形
        mCanvas.drawColor(mPanelColor);
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawColor(mPanelColor);
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);// 将前面已经画过得显示出来
        if (mPath != null) {
            canvas.drawPath(mPath, mPaint);// 实时的显示
        }
    }

    /**
     * 描绘路径的起点
     *
     * @param x
     * @param y
     */
    private void touchStart(float x, float y) {
        mPath.moveTo(x, y);
        mX = x;// 记录当前x坐标
        mY = y;// 记录当前y坐标
    }

    /**
     * 描绘移动产生的路径
     *
     * @param x
     * @param y
     */
    private void touchMove(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(mY - y);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {// 横坐标或纵坐标移动的距离的绝对值大于或等于TOUCH_TOLERANCE时，才描绘路径
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);// 从x1,y1到x2,y2画一条贝塞尔曲线，更平滑(直接用mPath.lineTo也是可以的)
            mX = x;// 记录当前x坐标
            mY = y;// 记录当前y坐标
        }
    }

    /**
     * 路径结束时
     */
    private void touchUp() {
        mPath.lineTo(mX, mY);// 路径最后一点
        mCanvas.drawPath(mPath, mPaint);// 用mPaint在画布上绘制路径,将路径保存在Bitmap上
        savePath.add(drawPath);// 将一条完整的路径保存下来
        mPath = null;// 重新置空
    }

    /**
     * 撤销的核心思想就是将画布清空，
     * 将保存下来的Path路径最后一个移除掉，
     * 重新将路径画在画布上面。
     */
    public void undo() {
        mBitmap = Bitmap.createBitmap(screenWidth, screenHeight,
                Bitmap.Config.ARGB_8888);
        /*
         * 清空画布，但是如果图片有背景的话，则使用上面的重新初始化的方法，用该方法会将背景清空掉
         */
        mCanvas.setBitmap(mBitmap);// 重新设置画布，相当于清空画布
        if (savePath != null && savePath.size() > 0) {
            savePath.remove(savePath.size() - 1);// 移除最后一个path
            for (HandwritingBoardView.DrawPath DrawPath : savePath) {
                mCanvas.drawPath(DrawPath.path, DrawPath.paint);
            }
            invalidate();// 刷新
        }
    }

    /**
     * 重写，相当重新来过
     */
    public void redo() {
        savePath.clear();
        mCanvas.drawColor(mPanelColor, PorterDuff.Mode.CLEAR);
        invalidate();
    }

    public Bitmap getBitmap() {
        if(savePath == null || savePath.size() == 0){
            return null;
        }
        return mBitmap;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 获得当前点的坐标(x,y)
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            // 点击屏幕时触发
            case MotionEvent.ACTION_DOWN:
                // 每次点击时，就重新绘制一个新的路径Path
                mPath = new Path();
                // 每一次记录的路径对象是不一样的
                drawPath = new DrawPath();
                drawPath.path = mPath;
                drawPath.paint = mPaint;
                touchStart(x, y);
                // 更新视图
                invalidate();
                break;
            // 移动
            case MotionEvent.ACTION_MOVE:
                touchMove(x, y);
                // 更新视图
                invalidate();
                break;
            // 结束
            case MotionEvent.ACTION_UP:
                touchUp();
                // 更新视图
                invalidate();
                break;
        }
        return true;
    }

    /**
     * 设置笔头大小
     *
     * @param size
     */
    public void setPenSize(int size) {
        mPenSize = size > 0 ? size : 10;
        this.mPenSize = size;
        if(mPaint != null){
            mPaint.setStrokeWidth(mPenSize);
        }
    }

    /**
     * 设置面板的背景色
     *
     * @param bgColor
     */
    public void setPanelColor(@ColorInt int bgColor) {
        this.mPanelColor = bgColor;
    }


    /**
     * 设置画笔颜色
     *
     * @param mPenColor
     */
    public void setPenColor(int mPenColor) {
        this.mPenColor = mPenColor;
        if(mPaint != null) {
            mPaint.setColor(mPenColor);
        }
    }
    private static class DrawPath {
        public Path path;// 路径
        public Paint paint;// 画笔
    }
}
