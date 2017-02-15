package unc.edu.kewang.sensorplot;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.provider.CalendarContract;
import android.support.v4.util.CircularArray;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;

public class SensorPlotView extends View {
    private final static int MAX_DATA_POINT = 150;
    int mTotalDataCount = 0;
    float sum_0 = 0;
    float sum_1 = 0;
    float sum_2 = 0;
    ArrayList<Float> mData;
    ArrayList<Float> mMean;
    ArrayList<Float> mStd;
    Paint mDataPaint;
    Paint mMeanPaint;
    Paint mStdPaint;


    public SensorPlotView(Context context) {
        super(context);
        mData = new ArrayList<Float>();
        mMean = new ArrayList<Float>();
        mStd = new ArrayList<Float>();
        mDataPaint = new Paint();
        mDataPaint.setColor(Color.BLUE);
        mDataPaint.setStyle(Paint.Style.STROKE);
        mMeanPaint = new Paint();
        mMeanPaint.setColor(Color.CYAN);
        mMeanPaint.setStyle(Paint.Style.STROKE);
        mStdPaint = new Paint();
        mStdPaint.setColor(Color.DKGRAY);
        mStdPaint.setStyle(Paint.Style.STROKE);
    }

    public void addData(float data) {
        if (mData.size() >= MAX_DATA_POINT) {
            int exceedSize = mData.size() - MAX_DATA_POINT;
            for (int i = 0; i < exceedSize; ++i) {
                mData.remove(0);
                mMean.remove(0);
                mStd.remove(0);
            }
        }
        sum_0 += 1;
        sum_1 += data;
        sum_2 += (data * data);
        mData.add(data);
        mMean.add(sum_1 / sum_0);
        mStd.add((float) Math.sqrt((sum_0 * sum_2 - sum_1 * sum_1) / (sum_0 * (sum_0 - 1))));
    }

    // TODO: set the scale accordingly
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();


        for (int i = 1; i < mData.size(); ++i) {
            canvas.drawLine(
                    (i - 1) * 5 + width / 10,
                    height - mData.get(i - 1),
                    i * 5 + width / 10,
                    height - mData.get(i),
                    mDataPaint);
            canvas.drawLine(
                    (i - 1) * 5 + width / 10,
                    height - mMean.get(i - 1),
                    i * 5 + width / 10,
                    height - mMean.get(i),
                    mMeanPaint);
            canvas.drawLine(
                    (i - 1) * 5 + width / 10,
                    height - mStd.get(i - 1),
                    i * 5 + width / 10,
                    height - mStd.get(i),
                    mStdPaint);
        }
        invalidate();
    }
}
