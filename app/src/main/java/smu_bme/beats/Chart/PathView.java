package smu_bme.beats.Chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bme-lab2 on 5/14/16.
 */
public class PathView extends CardiographView {

    private int i = 0;
    public int Width = mWidth;
    //    private int y = 0;
//    private int previousX = 0;
//    private int previousY = mHeight / 2;
//    public List<Integer> whiteNoise = new ArrayList<>();
    public List<Integer> data = new ArrayList<>();
    public int previous = mHeight / 2;
//    public Thread thread;
//    private Canvas canvas;

    //    private MainActivity activity =
    public PathView(Context context) {
        this(context, null);
    }

    public PathView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PathView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mPath = new Path();
        new Thread(){
            @Override
            public void run() {
                super.run();
            }
        }.run();
    }

    private void drawPath(Canvas canvas) {
        // 重置path
        //用path模拟一个心电图样式
//        mPath.moveTo(mWidth,mHeight/2);
        Log.d("DEBUGGING", "data.size1"  + data.size());
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(mLineColor);
        mPaint.setStrokeWidth(5);
        if (data.size() > 1) {
            for (i = 0; i < data.size() - 1; i += 1) {
                canvas.drawLine(mWidth + i * 2 , (mHeight / 2) + data.get(i), mWidth + (i + 1) * 2, (mHeight / 2) + data.get(i + 1), mPaint);
            }
        }
        //设置画笔style
    }

    @Override
    protected void onDraw(Canvas canvas) {
        scrollBy(1, 0);
        drawPath(canvas);
        Log.d("DEBUGGING", "data.size2"  + data.size());
        postInvalidateDelayed(10);
    }


    public void setPoint(int num) {
        if (data.size() > mWidth) {
            data.remove(0);
        }
        data.add(num);
//        Log.d("DEBUGGING", "num = " + num);
    }
}
