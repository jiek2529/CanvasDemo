package com.example.jiek.canvasdemo.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.jiek.canvasdemo.view.CanvasView.DrawType.DRAW_CLOCK;
import static com.example.jiek.canvasdemo.view.CanvasView.DrawType.DRAW_POINTS;

/**
 * 实验Canvas
 *
 * @author https://www.gitbook.com/@jiek
 */
public class CanvasView extends View {

    /**
     * save this View as an jpeg image.
     *
     * @param file
     */
    public void saveView(File file) {
        saveView(file, getViewBitmap());
//        saveView(file, getCacheBitmap());
    }

    public void saveView(File file, Bitmap bitmap) {
        if (file.exists()) {
            if (!file.canWrite()) {
                Toast.makeText(getContext(), "can't to write : " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            }
        }
        try {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "save to file failure.", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap getViewBitmap() {
        int w = this.getWidth();
        int h = this.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);/** 设置canvas背景白色，否则透明 */
        this.layout(0, 0, w, h);
        this.draw(canvas);
        return bitmap;
    }

    /**
     * 缓存中获取Bitmap
     *
     * @return
     */
    private Bitmap getCacheBitmap() {
        setDrawingCacheEnabled(true);
        buildDrawingCache();  //启用DrawingCache并创建位图
        Bitmap bitmap = Bitmap.createBitmap(getDrawingCache()); //创建一个DrawingCache的拷贝，因为DrawingCache得到的位图在禁用后会被回收
        setDrawingCacheEnabled(false);  //禁用DrawingCahce否则会影响性能
        return bitmap;
    }

    public enum DrawType {
        DRAW_POINTS, DRAW_CLOCK,
    }

    Paint paint;
    private ArrayList<PointF> graphics = new ArrayList<PointF>();
    DrawType mDrawType = DRAW_CLOCK;

    public CanvasView(Context context) {
        super(context);
        initPaint();
    }

    public CanvasView(Context context, DrawType mDrawType) {
        this(context);
        this.mDrawType = mDrawType;
        if (mDrawType == DRAW_CLOCK) {
            new Timer().schedule(timerTask, 1000, 1000);
        }
    }

    int second = 25;
    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            second += 1;
            second %= 60;
            CanvasView.this.post(new Runnable() {
                @Override
                public void run() {
                    invalidate();
                }
            });
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mDrawType == DRAW_POINTS) {
            graphics.add(new PointF(event.getX(), event.getY()));
            invalidate(); //重新绘制区域
            return true;
        }
        return false;
    }

    //在这里我们将测试canvas提供的绘制图形方法
    @Override
    protected void onDraw(Canvas canvas) {
        switch (mDrawType) {
            case DRAW_POINTS:
                drawDragPoint(canvas);
                break;
            case DRAW_CLOCK:
                drawClock(canvas);
                break;
            default:
                break;
        }
    }

    private void drawDragPoint(Canvas canvas) {
        for (PointF point : graphics) {
            canvas.drawPoint(point.x, point.y, paint);
        }
    }

    private int dp2px(int value) {
        return (int) getResources().getDisplayMetrics().density * value;
    }

    private int sp2px(int value) {
        return (int) getResources().getDisplayMetrics().scaledDensity * value;
    }

    private void initPaint() {
        paint = new Paint(); //设置一个笔刷大小是3sp的红色的画笔
        paint.setColor(Color.RED);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(dp2px(1));
    }

    private void drawClock(Canvas canvas) {

        /**
         * 画布中心点
         */
        int canvasX = canvas.getWidth() / 2, canvasY = canvas.getHeight() / 2;

        /**
         * 大圆半径
         */
        int bigCircleRadius = dp2px(100);
        /**
         * 画布中心点偏移
         */
        canvas.translate(canvasX, canvasY);
        canvas.rotate(360 / 60 * 35);
        canvas.save();

        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(0, 0, bigCircleRadius, paint); //画圆圈

        //使用path绘制路径文字
        Path path = new Path();
        path.addArc(new RectF(0, 0, dp2px(150), dp2px(150)), -30, 359); //X轴正侧为0度，sweepAngle是扫过角度
        Paint sitePaint = new Paint(paint);
        sitePaint.setTextSize(sp2px(14));
        sitePaint.setStyle(Paint.Style.FILL);
        sitePaint.setStrokeWidth(dp2px(1));
        canvas.translate(dp2px(-75), dp2px(-75));//中心点向左上角移动
        canvas.drawTextOnPath("https://www.gitbook.com/@jiek", path, sp2px(16), 0, sitePaint);

//        sitePaint.setStyle(Paint.Style.STROKE);
//        canvas.drawPath(path, sitePaint);
        canvas.restore();//恢复到save时的状态

        Paint tmpPaint = new Paint(paint); //小刻度画笔对象
        tmpPaint.setTextSize(sp2px(12));
        tmpPaint.setStyle(Paint.Style.FILL);
        tmpPaint.setStrokeWidth(dp2px(1));

        float y = dp2px(100);
        int count = 60; //总刻度数
//        canvas.rotate(360 / 60 * 35, 0f, 0f); //旋转画纸
        for (int i = 0; i < count; i++) {
            if (i % 5 == 0) {
                canvas.drawLine(0f, y, 0, y + dp2px(12), paint);
                canvas.drawText(String.valueOf(i / 5 + 1), dp2px(-4), y + dp2px(25), tmpPaint);

            } else {
                canvas.drawLine(0f, y, 0f, y + dp2px(5), tmpPaint);
            }
            canvas.rotate(360 / count, 0f, 0f); //旋转画纸
        }

        //绘制指针
        tmpPaint.setColor(Color.GRAY);
        tmpPaint.setStrokeWidth(dp2px(1));
        tmpPaint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(0, 0, dp2px(25), tmpPaint);

        tmpPaint.setColor(Color.BLUE);

        canvas.rotate(second % 60 * 6, 0f, 0f);
        canvas.drawCircle(0, dp2px(-45), dp2px(4), tmpPaint);
        canvas.drawLine(0, dp2px(15), 0, dp2px(-70), paint);
        canvas.drawCircle(0, dp2px(0), dp2px(2), tmpPaint);
    }
}
